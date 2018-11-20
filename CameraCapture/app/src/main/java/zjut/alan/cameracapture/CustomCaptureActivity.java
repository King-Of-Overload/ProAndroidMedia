package zjut.alan.cameracapture;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class CustomCaptureActivity extends AppCompatActivity implements View.OnClickListener,
        SurfaceHolder.Callback{
    private MediaRecorder recorder;
    private SurfaceHolder holder;

    private boolean recording = false;
    public static final String TAG = "VIDEOCAPTURE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        recorder = new MediaRecorder();
        initRecoder();
        setContentView(R.layout.activity_custom_capture);
        SurfaceView cameraView = findViewById(R.id.CameraView);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraView.setClickable(true);
        cameraView.setOnClickListener(this);
    }

    private void initRecoder(){
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        recorder.setOutputFile("/sdcard/videosample.mp4");
        recorder.setMaxDuration(50000);
        recorder.setMaxFileSize(5000000);
    }

    private void prepareRecoder(){
        recorder.setPreviewDisplay(holder.getSurface());
        try{
            recorder.prepare();
        }catch (Exception e){
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        prepareRecoder();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(recording){
            recorder.stop();
            recording = false;
        }
        recorder.release();
        finish();
    }

    @Override
    public void onClick(View view) {
        if(recording){
            recorder.stop();
            recording = false;
            //调用初始化方法,record
            initRecoder();
            prepareRecoder();
        }else{
            recording = true;
            recorder.start();
        }
    }
}
