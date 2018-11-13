package zjut.alan.cameracapture;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

/**
 * 异步音频
 */
public class AudioHTTPActivity extends AppCompatActivity implements View.OnClickListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnPreparedListener{
    private MediaPlayer mediaPlayer;
    private Button startButton, stopButton;
    private TextView statusTextView, bufferValueTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_http);
        startButton = findViewById(R.id.StartButton);
        stopButton = findViewById(R.id.EndButton);
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        startButton.setEnabled(false);
        stopButton.setEnabled(false);
        bufferValueTextView = findViewById(R.id.BufferValueTextView);
        statusTextView = findViewById(R.id.StatusDisplayTextView);
        statusTextView.setText("onCreate");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);
        statusTextView.setText("媒体播放器已创建");
        //使用音频URL调用设置数据源
        try {
            mediaPlayer.setDataSource("https://www.mobvcasting.com/android/audio/goodmorningandroid.mp3");
            statusTextView.setText("设置数据源完毕");
            statusTextView.setText("Calling PrepareAsync");
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if(view == stopButton){
            mediaPlayer.pause();
            statusTextView.setText("pause called");
            startButton.setEnabled(true);
        }else if(view == startButton){
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
            statusTextView.setText("start called");
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        }
    }


    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        statusTextView.setText("onError Called");
        switch (what){
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:{
                break;
            }
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:{
                break;
            }
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:{
                break;
            }
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        statusTextView.setText("onCompletion called");
        stopButton.setEnabled(false);
        startButton.setEnabled(true);
    }

    //正在緩衝時，該方法調用
    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
        bufferValueTextView.setText("" + percent + "%");
    }

    //prepareAsync完成後，該方法調用
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        statusTextView.setText("onPrepared called");
        startButton.setEnabled(true);
    }
}
