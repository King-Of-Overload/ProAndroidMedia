package zjut.alan.cameracapture;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

public class MediaStoreSearchActivity extends AppCompatActivity {

    public final static int DISPLAYWIDTH = 200;
    public final static int DISPLAYHEIGHT = 200;

    private TextView titleTextView;
    private ImageButton imageButton;

    private Cursor cursor;
    private Bitmap bmp;
    private String imageFilePath;
    private int fileColumn;
    private int titleColumn;
    private int displayColumn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_store_search);
        titleTextView = findViewById(R.id.TitleTextView);
        imageButton = findViewById(R.id.ImageButton);
        imageButton.setOnClickListener(new MyImageBtnClick());
        String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DISPLAY_NAME};
        cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
        fileColumn = cursor.getColumnIndexOrThrow(columns[0]);
        titleColumn = cursor.getColumnIndexOrThrow(columns[1]);
        displayColumn = cursor.getColumnIndexOrThrow(columns[2]);
        if(cursor.moveToFirst()){
            titleTextView.setText(cursor.getString(displayColumn));
            imageFilePath = cursor.getString(fileColumn);
            bmp = getBitmap(imageFilePath);
            //获取位图图像
            imageButton.setImageBitmap(bmp);


        }
    }

    /**
     * 获取EXIF数据的方式
     */
    private void getEXIFData(){
        try {
            ExifInterface exifInterface = new ExifInterface(imageFilePath);
            String imageDescription = exifInterface.getAttribute("ImageDescription");
            if(imageDescription != null){
                Log.v("EXIF", imageDescription);
            }
            //以下是保存
            exifInterface.setAttribute("ImageDescription", "幼儿缘");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private class MyImageBtnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(cursor.moveToNext()){
                titleTextView.setText(cursor.getString(displayColumn));
                imageFilePath = cursor.getString(fileColumn);
                bmp = getBitmap(imageFilePath);
                imageButton.setImageBitmap(bmp);
            }
        }
    }

    private Bitmap getBitmap(String imageFilePath){
        //加载图像尺寸
        BitmapFactory.Options options = new BitmapFactory.Options();
        int heightRatio = (int) Math.ceil(options.outHeight / (float)DISPLAYHEIGHT);
        int widthRatio = (int) Math.ceil(options.outWidth / (float)DISPLAYWIDTH);
        //若两个比例都大于1，那么图像的一条边大于屏幕
        if(heightRatio > 1 && widthRatio > 1){
            if(heightRatio > widthRatio){
                options.inSampleSize = heightRatio;
            }else{
                options.inSampleSize = widthRatio;
            }
        }
        //进行解码
        options.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(imageFilePath, options);
        return bmp;
    }
}
