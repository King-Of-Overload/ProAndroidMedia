package zjut.alan.cameracapture;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class CustomAudioActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,View.OnClickListener,View.OnTouchListener{
    private MediaPlayer mediaPlayer;
    private View theView;
    private Button stopButton, startButton;
    private int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_audio);
        stopButton = findViewById(R.id.StopButton);
        startButton = findViewById(R.id.StartButton);
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        theView = findViewById(R.id.theView);
        theView.setOnTouchListener(this);
        mediaPlayer = MediaPlayer.create(this, R.raw.a);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.start();
    }



    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        mediaPlayer.seekTo(position);
    }

    @Override
    public void onClick(View view) {
        if(view == stopButton){
            mediaPlayer.pause();
        }else if(view == startButton){
            mediaPlayer.start();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_MOVE){
            if(mediaPlayer.isPlaying()){
                position = (int) (motionEvent.getX() * mediaPlayer.getDuration()/theView.getWidth());
                Log.v("SEEK", ""+position);
                mediaPlayer.seekTo(position);
            }
        }
        return true;
    }
}
