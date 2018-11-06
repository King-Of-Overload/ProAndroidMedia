package zjut.alan.cameracapture;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CameraMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_main);
    }


    public void showBigPicClick(View view){
        startActivity(new Intent(this, MainActivity.class));
    }

    public void mediaStoreClick(View v){
        startActivity(new Intent(this, MediaStoreEditActivity.class));
    }

    public void mediaStoreFindClick(View v){
        startActivity(new Intent(this, MediaStoreSearchActivity.class));
    }

    public void cameraClick(View v){
        startActivity(new Intent(this, CameraActivity.class));
    }

    public void timeSnapClick(View v){
        startActivity(new Intent(this, TimeSnapShotActivity.class));
    }

    public void choosePicClick(View v){
        startActivity(new Intent(this, ChoosePicActivity.class));
    }

    public void rotateZoomClick(View v){
        startActivity(new Intent(this, RotateZoomActivity.class));
    }

    public void drawImageClick(View v){
        startActivity(new Intent(this, DrawImageActivity.class));
    }

    public void choosePicDrawClick(View v){
        startActivity(new Intent(this, ChoosePicDrawActivity.class));
    }


}
