package zjut.alan.cameracapture;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * 捕獲視頻
 */
public class VideoCaptureActivity extends AppCompatActivity implements View.OnClickListener{
    private static int VIDEO_CAPTURED = 1;
    private Button captureVideoButton, playVideoButton, saveVideoButton;
    private VideoView videoView;
    private Uri videoFileUri;
    private EditText titleEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_capture);
        captureVideoButton = findViewById(R.id.CaptureVideoButton);
        playVideoButton = findViewById(R.id.PlayVideoButton);
        saveVideoButton = findViewById(R.id.SaveVideoButton);
        titleEditText = findViewById(R.id.TitleEditText);
        captureVideoButton.setOnClickListener(this);
        playVideoButton.setOnClickListener(this);
        saveVideoButton.setOnClickListener(this);
        playVideoButton.setEnabled(false);
        videoView = findViewById(R.id.VideoView);
        //在Android 6.0之後需要進行動態權限申請
        permissionRequest();
    }

    private void permissionRequest() {
        if(ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                1);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == captureVideoButton){
            Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(captureIntent, VIDEO_CAPTURED);
        }else if(view == playVideoButton){
            videoView.setVideoURI(videoFileUri);
            videoView.start();
        }else if(view == saveVideoButton){
            //存儲MediaStore更改元數據
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.MediaColumns.TITLE, titleEditText.getText().toString());
            if(getContentResolver().update(videoFileUri, values, null, null) == 1){
                Toast t = Toast.makeText(this, "Updated" +
                titleEditText.getText().toString(), Toast.LENGTH_LONG);
                t.show();
            }else{
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == VIDEO_CAPTURED){
            if(null != data){
                videoFileUri = data.getData();
                playVideoButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("CAPTURE","申請成功");
            } else {
                Log.i("CAPTURE","申請失敗");
            }
        }
    }


}
