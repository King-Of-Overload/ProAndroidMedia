package zjut.alan.cameracapture;

import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements Camera.PictureCallback{
    private SurfaceView cameraView;
    private SurfaceHolder surfaceHolder;
    @SuppressWarnings("deprecation")
    private Camera camera;

    public static final int LARGEST_WIDTH = 200;
    public static final int LARGEST_HEIGHT = 200;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //默认camera预览是竖屏，切换成横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横向模式
        setContentView(R.layout.activity_camera);
        cameraView = findViewById(R.id.CameraView);
        cameraView.setFocusable(true);
        cameraView.setFocusableInTouchMode(true);
        cameraView.setClickable(true);
        cameraView.setOnClickListener(new MyCameraViewClickListener());
        surfaceHolder = cameraView.getHolder();
        //表明该Surface不包含原生数据，Surface用到的数据由其他对象提供
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(new MyCallBack());

    }



    /**
     * SurfaceHolder回调
     */
    private class MyCallBack implements SurfaceHolder.Callback {
        @SuppressWarnings("deprecation")
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            //创建Surface时会调用
            camera = Camera.open();
            try {
                //设置参数
                setCameraParameters();
                setSolarizeCamera();
                //setCameraPreviewSize();
                //预览设置
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
                camera.release();
            }
            camera.startPreview();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int w, int h) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            camera.stopPreview();
            camera.release();
        }

    }


    /**
     * 设置摄像头的参数
     */
    private void setCameraParameters(){
        //实际上，该方法修改的横竖屏仅仅是修改EXIF中的显示方式，摄像头并没有旋转
        Camera.Parameters parameters = camera.getParameters();
        //判断当前横竖屏状态
        if(getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
            //设置成竖屏
            parameters.set("orientation", "portrait");
            //camera.setDisplayOrientation(90);//2.2以上版本
        }else{
            //设置成横屏
            parameters.set("orientation", "landscape");
            //camera.setDisplayOrientation(0);
        }
        camera.setParameters(parameters);
    }

    /**
     * 设置摄像头过度曝光
     */
    private void setSolarizeCamera(){
        Camera.Parameters parameters = camera.getParameters();
        List<String> colorEffects = parameters.getSupportedColorEffects();//获取所有支持的效果
        Iterator<String> cei = colorEffects.iterator();
        while(cei.hasNext()){
            String currentEffect = cei.next();
            if(currentEffect.equals(Camera.Parameters.EFFECT_SOLARIZE)){
                parameters.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
                break;
            }
        }
        camera.setParameters(parameters);
    }

    /**
     * 设置摄像头预览大小
     */
    private void setCameraPreviewSize(){
        Camera.Parameters parameters = camera.getParameters();
        int bestWidth = 0;
        int bestHeight = 0;
        //获取所有支持的预览尺寸
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        if(previewSizes.size() > 1){
            Iterator<Camera.Size> cei = previewSizes.iterator();
            while(cei.hasNext()){
                Camera.Size aSize = cei.next();
                if(aSize.width > bestWidth && aSize.width <= LARGEST_WIDTH &&
                        aSize.height > bestHeight && aSize.height <= LARGEST_HEIGHT){
                    bestHeight = aSize.height;
                    bestWidth = aSize.width;
                }
            }
            if(bestHeight != 0 && bestHeight != 0){
                parameters.setPreviewSize(bestWidth, bestHeight);
                cameraView.setLayoutParams(new LinearLayout.LayoutParams(bestWidth, bestHeight));
            }
        }
        camera.setParameters(parameters);
    }

    /**
     * 捕获图像
     */
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        try {
            Uri imageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
            OutputStream imageFileOS = getContentResolver().openOutputStream(imageFileUri);
            imageFileOS.write(data);
            imageFileOS.flush();
            imageFileOS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //由于截屏之后会停止预览，需要重新启动
        camera.startPreview();
    }

    private class MyCameraViewClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            camera.takePicture(null, null, null, CameraActivity.this);
        }
    }
}
