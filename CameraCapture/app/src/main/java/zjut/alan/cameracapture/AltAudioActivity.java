package zjut.alan.cameracapture;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 音頻捕獲與播放
 */
public class AltAudioActivity extends AppCompatActivity {
    private Button startRecordingButton, stopRecordingButton, startPlaybackButton,stopPlaybackButton;
    private TextView statusText;
    private File recordingFile;

    private boolean isRecording = false;
    private boolean isPlaying = false;

    private int frequency = 11025;
    private int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    int audioEecording = AudioFormat.ENCODING_PCM_16BIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alt_audio);
    }

    //播放音頻內部類
    private class PlayAudio extends AsyncTask<Void, Integer, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            isPlaying = true;
            int bufferSize = AudioTrack.getMinBufferSize(frequency, channelConfiguration,audioEecording);
            short[] audioData = new short[bufferSize / 4];//一個short2位，一半除以4
            try {
                DataInputStream dis = new DataInputStream(new FileInputStream(recordingFile));
                AudioTrack audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC, frequency,
                        channelConfiguration, audioEecording, bufferSize,
                        AudioTrack.MODE_STREAM);
                audioTrack.play();
                while(isPlaying && dis.available() > 0){
                    int i = 0;
                    while(dis.available() > 0 && i < audioData.length){
                        audioData[i] = dis.readShort();
                        i++;
                    }
                    audioTrack.write(audioData, 0, audioData.length);
                }
                dis.close();
                startPlaybackButton.setEnabled(false);
                stopPlaybackButton.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //錄製類
    private class RecordAudio extends AsyncTask<Void, Integer, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            isRecording = true;
            try {
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(recordingFile));
                int bufferSize = AudioRecord.getMinBufferSize(frequency,channelConfiguration,audioEecording);
                AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        frequency, channelConfiguration, audioEecording, bufferSize);
                short[] buffer = new short[bufferSize];
                audioRecord.startRecording();
                int r = 0;
                while (isRecording){
                    int bufferReadResult = audioRecord.read(buffer,0,bufferSize);
                    for(int i = 0; i < bufferReadResult; i++){
                        dos.write(buffer[i]);
                    }
                    publishProgress(r);
                    r++;
                }
                audioRecord.stop();
                dos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            statusText.setText(progress[0].toString());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            startPlaybackButton.setEnabled(true);
            stopPlaybackButton.setEnabled(false);
            startRecordingButton.setEnabled(true);
        }
    }


}
