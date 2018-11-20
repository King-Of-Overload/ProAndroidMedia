package zjut.alan.cameracapture;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 *合成音播放
 */
public class AudioSynthesisActivity extends AppCompatActivity implements View.OnClickListener{
    private Button startSound, endSound;
    private boolean keepGoing = false;

    private AudioSynthesisTask audioSynthesisTask;
    private float synth_frequency = 440;//440HZ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_synthesis);
        startSound = findViewById(R.id.StartSound);
        endSound = findViewById(R.id.EndSound);
        startSound.setOnClickListener(this);
        endSound.setOnClickListener(this);
        endSound.setEnabled(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        keepGoing = false;
        endSound.setEnabled(false);
        startSound.setEnabled(true);
    }

    @Override
    public void onClick(View view){
        if(view == startSound){
            keepGoing = true;
            audioSynthesisTask = new AudioSynthesisTask();
            audioSynthesisTask.execute();
            endSound.setEnabled(true);
            startSound.setEnabled(false);
        }else if(view == endSound){
            keepGoing = false;
            endSound.setEnabled(false);
            startSound.setEnabled(true);
        }

    }

    private class AudioSynthesisTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            final int SAMPLE_RATE = 11025;//採樣率
            int minSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,minSize,
                    AudioTrack.MODE_STREAM);
            audioTrack.play();
            //定義波形
//            short[] buffer = {
//                    8130,15752,22389,27625,31134,32695,32210,29711,25354,19410,12253,4329,-3865,-11818,-19032,-25055,-29511,-32121,-32722,-31276,-27874,-22728,-16160,-8582,-466
//            };
            //生成正弦波
            short[] buffer = new short[minSize];
            float angular_frequency = (float)(2*Math.PI) *synth_frequency / SAMPLE_RATE;
            float angle = 0;
            while (keepGoing){
                for(int i = 0; i < buffer.length; i++){
                    buffer[i] = (short)(Short.MAX_VALUE * (float)Math.sin(angle));
                    angle += angular_frequency;
                }
                audioTrack.write(buffer, 0, buffer.length);
            }
            return null;
        }
    }
}
