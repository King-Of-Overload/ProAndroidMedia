package zjut.alan.cameracapture;

import android.content.ContentValues;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 定时器摄像头程序
 * 构建时间推移摄影
 */
public class TimeSnapShotActivity extends AppCompatActivity implements Camera.PictureCallback{
    private SurfaceView cameraView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;

    private Button startStopButton;
    private TextView countdownTextView;

    private Handler timeUpdateHandler;
    private boolean timelapseRunning = false;
    private int currentTime = 0;
    public static final int SECONDS_BETWEEN_PHOTOS = 60;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_snap_shot);
        cameraView = findViewById(R.id.CameraView);
        surfaceHolder = cameraView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new MySurfaceCallback());
        countdownTextView = findViewById(R.id.CountDownTextView);
        startStopButton = findViewById(R.id.CountDownButton);
        startStopButton.setOnClickListener(new StartBtnClickListener());
        timeUpdateHandler = new Handler();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Uri imageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new ContentValues());
        try {
            OutputStream imageFileOS = getContentResolver().openOutputStream(imageFileUri);
            imageFileOS.write(data);
            imageFileOS.flush();
            imageFileOS.close();
            Toast.makeText(this, "保存完毕", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        camera.startPreview();
    }



    private class MySurfaceCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            camera = Camera.open();
            try {
                camera.setPreviewDisplay(surfaceHolder);
                Camera.Parameters parameters = camera.getParameters();
                if(getResources().getConfiguration().orientation !=
                        Configuration.ORIENTATION_LANDSCAPE){
                    parameters.set("orientation", "portrait");
                    //2.2以上版本
                    camera.setDisplayOrientation(90);
                    parameters.setRotation(90);
                }
                camera.setParameters(parameters);
            } catch (IOException e) {
                e.printStackTrace();
                camera.release();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            camera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            camera.stopPreview();
            camera.release();
        }
    }
    private Runnable runnable;
    private class StartBtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(!timelapseRunning){
                timelapseRunning = true;
                startStopButton.setText("停止");
                timeUpdateHandler.post(runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(currentTime < SECONDS_BETWEEN_PHOTOS){
                            currentTime++;
                        }else{
                            //调用摄像头拍照
                            camera.takePicture(null, null, TimeSnapShotActivity.this);
                            currentTime = 0;
                        }
                        timeUpdateHandler.postDelayed(runnable, 1000);
                        countdownTextView.setText("" + currentTime);
                    }
                });
            }else{
                //正在运行
                startStopButton.setText("开始");
                timelapseRunning = false;
                timeUpdateHandler.removeCallbacks(runnable);
            }
        }
    }


}
