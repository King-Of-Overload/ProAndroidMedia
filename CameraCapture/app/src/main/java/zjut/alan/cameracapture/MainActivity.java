package zjut.alan.cameracapture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private final static int CAMERA_RESULT = 0;

    private ImageView imv;
    private String imageFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageFilePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/mypicture.jpg";
        File imageFile = new File(imageFilePath);
        Uri imageFileUri = Uri.fromFile(imageFile);

        //获得摄像头的意图对象
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //追加保存路径
        i.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        //启动意图对象调用摄像头并请求返回结果
        startActivityForResult(i, CAMERA_RESULT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK){
            //Bundle extras = intent.getExtras();
            //Bitmap bitmap = (Bitmap) extras.get("data");//附加值名称为data
            imv = findViewById(R.id.ReturnedImageView);
            Display currentDisplay = getWindowManager().getDefaultDisplay();
            int dw = currentDisplay.getWidth();
            int dh = currentDisplay.getHeight();
            //加载图像的尺寸而不是图像本身
            BitmapFactory.Options options = new BitmapFactory.Options();
            int heightRatio = (int) Math.ceil(options.outHeight / (float)dh);
            int widthRatio = (int) Math.ceil(options.outWidth / (float)dw);
            Log.v("HEIGHTRATIO", heightRatio+"");
            Log.v("WIDTHRATIO", ""+widthRatio);
            //如果两个比例都大于1，表示边大于屏幕
            if(heightRatio > 1 && widthRatio > 1){
                if(heightRatio > widthRatio){
                    //若高度更大，则根据高度缩放
                    options.inSampleSize = heightRatio;//该属性表示将图片按1/heightRatio缩放
                }else{
                    options.inSampleSize = widthRatio;
                }
            }
            //对其进行真正的解码
            options.inJustDecodeBounds = false;
            Bitmap bmp = BitmapFactory.decodeFile(imageFilePath, options);
            //这里需要注意的是，默认返回的是一个缩略图，这是为了内存着想
            imv.setImageBitmap(bmp);
        }
    }
}
