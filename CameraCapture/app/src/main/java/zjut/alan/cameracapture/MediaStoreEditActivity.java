package zjut.alan.cameracapture;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MediaStoreEditActivity extends AppCompatActivity {
    private static final int CAMERA_RESULT = 0;
    private Uri imageFileUri;

    private ImageView returnedImageView;

    private LinearLayout section;

    private Button saveMetaBtn, takePicBtn;
    private EditText titleEditText, descriptionEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_store_edit);
        returnedImageView = findViewById(R.id.ReturnedImageView);
        section = findViewById(R.id.section);
        saveMetaBtn = findViewById(R.id.btn_save_meta);
        takePicBtn = findViewById(R.id.btn_take_pic);
        titleEditText = findViewById(R.id.TitleEditText);
        descriptionEditText = findViewById(R.id.DescriptionEditText);
        //一开始隐藏编辑区与保存按钮
        section.setVisibility(View.GONE);
        saveMetaBtn.setVisibility(View.GONE);

    }

    /**
     * 拍照片
     * @param v
     */
    public void takePicClick(View v){
        //添加一条不带位图的新纪录
        //返回新纪录的Uri
        imageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        //启动camera应用程序
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        startActivityForResult(i, CAMERA_RESULT);
    }

    /**
     * 保存元数据
     * @param v
     */
    public void saveMetaDataClick(View v){
        //更新MediaStore中记录的标题与描述
        ContentValues contentValues = new ContentValues(3);
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, titleEditText.getText().toString());
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, descriptionEditText.getText().toString());
        getContentResolver().update(imageFileUri, contentValues, null, null);
        //通知用户
        Toast bread = Toast.makeText(this, "记录更新", Toast.LENGTH_LONG);
        bread.show();
        //回到初始状态，设置拍照按钮可见
        //隐藏其他元素
        saveMetaBtn.setVisibility(View.GONE);
        section.setVisibility(View.GONE);
        takePicBtn.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK){
            //camera应用程序已经返回
            //显示其他UI元素
            section.setVisibility(View.VISIBLE);
            //隐藏拍照按钮
            takePicBtn.setVisibility(View.GONE);
            //缩放图像
            int dw = 200;
            int dh = 200;
            try{
                //加载图像的尺寸而非图像本身
                BitmapFactory.Options options = new BitmapFactory.Options();
                int heightRatio = (int)Math.ceil(options.outHeight / (float)dh);
                int widthRatio = (int)Math.ceil((options.outWidth / (float)dw));
                //如果两个比率都D大于1,那么一条边将大于屏幕
                if(heightRatio > 1 && widthRatio > 1){
                    if(heightRatio > widthRatio){
                        //按高度缩放
                        options.inSampleSize = heightRatio;
                    }else{
                        options.inSampleSize = widthRatio;
                    }
                }
                //对其进行真正的解码
                options.inJustDecodeBounds = false;
                Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, options);
                //显示图像
                returnedImageView.setImageBitmap(bmp);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
