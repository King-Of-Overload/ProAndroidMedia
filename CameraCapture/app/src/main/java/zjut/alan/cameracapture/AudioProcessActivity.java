package zjut.alan.cameracapture;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import ca.uol.aig.fftpack.RealDoubleFFT;

/**
 * 音頻分析
 * 聲音通過某種物質的震動，振動可被麥克風接收，麥克風生成不斷變化的電流
 * 該聲音會被數字化，特定大小的振幅樣本被每秒鐘採樣多次，這個數據流稱爲PCM(脈衝編碼調製)流
 * 採樣率越高，表示越準確，捕獲的音頻頻率越高
 *
 * 可視化頻率：
 * 將音頻信號轉化爲分量頻率的技術採用了一個數學變換，稱爲傅里葉變換DFT
 * 開源：www.netlib.org/fftpack   常用jfftpack.tgz
 */
public class AudioProcessActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView imageView;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private Button startStopButton;
    private boolean started = false;

    private RealDoubleFFT transfomer;//傅里葉變換
    private int blockSize = 256;
    private RecordAudio recordTask;

    private int frequency = 8000;
    private int channelCOnfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_process);
        startStopButton = findViewById(R.id.StartStopButton);
        startStopButton.setOnClickListener(this);
        transfomer = new RealDoubleFFT(blockSize);
        imageView = findViewById(R.id.ImageView01);
        bitmap = Bitmap.createBitmap((int)256, (int)100, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View view) {
        if(started){
            started = false;
            startStopButton.setText("Start");
            recordTask.cancel(true);
        }else{

            started = true;
            startStopButton.setText("Stop");
            recordTask = new RecordAudio();
            recordTask.execute();
        }
    }

    private class RecordAudio extends AsyncTask<Void,double[],Void>{

        @Override
        protected Void doInBackground(Void... params) {
            int bufferSize = AudioRecord.getMinBufferSize(frequency,channelCOnfiguration,
                    audioEncoding);
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    frequency, channelCOnfiguration,audioEncoding,bufferSize);
            short[] buffer = new short[blockSize];
            double[] toTransform = new double[blockSize];
            audioRecord.startRecording();
            while (started){
                int bufferReadResult = audioRecord.read(buffer,0,blockSize);
                for(int i = 0; i < blockSize && i < bufferReadResult; i++){
                    toTransform[i] = (double)buffer[i] / 32768.0;//有符號16位
                }
                transfomer.ft(toTransform);
                publishProgress(toTransform);
            }
            audioRecord.stop();
            return null;
        }

        @Override
        protected void onProgressUpdate(double[]... toTransform) {
            super.onProgressUpdate(toTransform);
            canvas.drawColor(Color.BLACK);
            for(int i = 0; i < toTransform[0].length; i++){
                int x = i;
                int downy = (int)(100 - (toTransform[0][i]*10));
                int upy = 100;
                canvas.drawLine(x,downy,x,upy,paint);
            }
            imageView.invalidate();
        }
    }
}
