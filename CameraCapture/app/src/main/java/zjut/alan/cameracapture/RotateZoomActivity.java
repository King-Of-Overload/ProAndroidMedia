package zjut.alan.cameracapture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 图像合成
 */
public class RotateZoomActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PICKED_ONE = 0;
    private static final int PICKED_TWO = 1;

    private boolean onePicked = false;
    private boolean twoPicked = false;

    private Button choosePicture1, choosePicture2;

    private ImageView compositeImageview;
    private Bitmap bmp1, bmp2;

    private Canvas canvas;
    private Paint paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_zoom);
        compositeImageview = findViewById(R.id.CompositeImageView);
        choosePicture1 = findViewById(R.id.ChoosePictureButton1);
        choosePicture2 = findViewById(R.id.ChoosePictureButton2);
        choosePicture1.setOnClickListener(this);
        choosePicture2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int which = -1;
        if(view == choosePicture1){
            which = PICKED_ONE;
        }else if(view == choosePicture2){
            which = PICKED_TWO;
        }
        Intent choosePictureIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(choosePictureIntent, which);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri imageFileUri = data.getData();
            if(requestCode == PICKED_ONE){
                bmp1 = loadBitmap(imageFileUri);
                onePicked = true;
            }else if(requestCode == PICKED_TWO){
                bmp2 = loadBitmap(imageFileUri);
                twoPicked = true;
            }
            //两图片准备完成开始合成
            if(onePicked && twoPicked){
                Bitmap drawingBitmap = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(),
                        bmp1.getConfig());
                canvas = new Canvas(drawingBitmap);
                paint = new Paint();
                canvas.drawBitmap(bmp1, 0, 0, paint);
                //设置Paint对象上的过渡模式, 将每个位置的两个像素相乘，除以255
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
                canvas.drawBitmap(bmp2, 0, 0, paint);
                compositeImageview.setImageBitmap(drawingBitmap);
            }
        }
    }


    private Bitmap loadBitmap(Uri imageFileUri){
        Display currentDisplay = getWindowManager().getDefaultDisplay();
        float dw = currentDisplay.getWidth();
        float dh = currentDisplay.getHeight();
        //期望是ARGB_4444
        Bitmap returnBmp = Bitmap.createBitmap((int)dw, (int)dh, Bitmap.Config.ARGB_4444);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            returnBmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri),
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
            returnBmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri),
                     null, options);
        } catch (IOException e) {
            Log.v("ERROR", e.toString());
        }
        return returnBmp;
    }
}
