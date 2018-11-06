package zjut.alan.cameracapture;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.OutputStream;

public class ChoosePicDrawActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{
    private ImageView chooseImageView;
    private Button choosePicture, savePicture;

    private Bitmap bitmap;
    private Bitmap alteredBitmap;
    private Canvas canvas;
    private Paint paint;
    private Matrix matrix;

    private float downx = 0;
    private float downy = 0;
    private float upx = 0;
    private float upy = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_pic_draw);
        chooseImageView = findViewById(R.id.ChoosenImageView);
        choosePicture = findViewById(R.id.ChoosePictureBtn);
        savePicture = findViewById(R.id.SavePictureBtn);
        choosePicture.setOnClickListener(this);
        chooseImageView.setOnTouchListener(this);
        savePicture.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == choosePicture){
            Intent choosePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(choosePictureIntent, 0);
        }else if(view == savePicture){
            if(alteredBitmap != null){
                Uri mediaFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new ContentValues());
                try {
                    OutputStream imageFileOS = getContentResolver().openOutputStream(mediaFileUri);
                    //将位图转成jpeg，并指定质量为90，写入到输出流
                    alteredBitmap.compress(Bitmap.CompressFormat.JPEG, 90, imageFileOS);
                    Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    Log.v("EXCEPTION", e.getMessage());
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri imageFileUri = data.getData();
            Display currentDIsplay = getWindowManager().getDefaultDisplay();
            float dw = currentDIsplay.getWidth();
            float dh = currentDIsplay.getHeight();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri),
                        null, options);
                int heightRatio = (int) Math.ceil(options.outHeight / dh);
                int widthRatio = (int) Math.ceil(options.outWidth / dw);
                if(heightRatio > 1 && widthRatio > 1){
                    if(heightRatio > widthRatio){
                        options.inSampleSize = heightRatio;
                    }else{
                        options.inSampleSize = widthRatio;
                    }
                }
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri),
                        null, options);
                //创建可变的位图对象，并进行绘制
                alteredBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                        bitmap.getConfig());
                canvas = new Canvas(alteredBitmap);
                paint = new Paint();
                paint.setColor(Color.GREEN);
                paint.setStrokeWidth(5);
                matrix = new Matrix();
                canvas.drawBitmap(bitmap, matrix, paint);
                chooseImageView.setImageBitmap(alteredBitmap);
                chooseImageView.setOnTouchListener(this);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:{
                downx = motionEvent.getX();
                downy = motionEvent.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                upx = motionEvent.getX();
                upy = motionEvent.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                chooseImageView.invalidate();
                downx = upx;
                downy = upy;
                break;
            }
            case MotionEvent.ACTION_UP:{
                upx = motionEvent.getX();
                upy = motionEvent.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                chooseImageView.invalidate();
                break;
            }
            case MotionEvent.ACTION_CANCEL: break;
            default:{
                break;
            }
        }
        return true;
    }
}
