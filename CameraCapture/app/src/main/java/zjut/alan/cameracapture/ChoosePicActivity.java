package zjut.alan.cameracapture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
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

/**
 * 选择图片
 * 在位图上绘制位图
 * 图像旋转与缩放
 */
public class ChoosePicActivity extends AppCompatActivity {
    private ImageView chosenImageView, alteredImageView;
    private Button choosePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_pic);
        chosenImageView = findViewById(R.id.ChooseImageView);
        alteredImageView = findViewById(R.id.AlteredImageView);
        choosePicture = findViewById(R.id.ChoosePicture);
        choosePicture.setOnClickListener(new ChosePicClickListener());
    }

    private class ChosePicClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent choosePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(choosePictureIntent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK){
            Uri imageFileUri = intent.getData();
            //由于加载的图象比较大，进行大小更改
            Display currentDisplay = getWindowManager().getDefaultDisplay();
            int dw = currentDisplay.getWidth();
            int dh = currentDisplay.getHeight() / 2 - 100;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri),
                        null, options);
                int heightRatio = (int) Math.ceil(options.outHeight / (float)dh);
                int widthRatio = (int) Math.ceil(options.outWidth / (float)dw);
                if(heightRatio > 1 && widthRatio > 1){
                    if(heightRatio > widthRatio){
                        options.inSampleSize = heightRatio;
                    }else{
                        options.inSampleSize = widthRatio;
                    }
                }
                options.inJustDecodeBounds = false;
                Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri),
                        null, options);
                //drawBitmapOnBitmap(bmp);
                rotateMatrix(bmp);
                chosenImageView.setImageBitmap(bmp);
                //drawBitmapOnBitmap(bmp);
            } catch (FileNotFoundException e) {
                Log.v("ERROR", e.toString());
            }

        }
    }

    /**
     * 在位图上绘制位图
     */
    private void drawBitmapOnBitmap(Bitmap bmp){
        Bitmap alteredBitmap = Bitmap.createBitmap(bmp.getWidth(),
                bmp.getHeight(), bmp.getConfig());//获得可变的bitmap
        Canvas canvas = new Canvas(alteredBitmap);
        Paint paint = new Paint();
        canvas.drawBitmap(bmp,0, 0, paint);
        alteredImageView.setImageBitmap(alteredBitmap);
    }

    /**
     * 矩阵变换原理：
     * 1 0 0     x = 1x + 0y + 0z
     * 0 1 0     y = 0x + 1y + 0z
     * 0 0 1     z = 0x + 0y + 1z
     * 分别对应三维坐标系的坐标
     * 单位矩阵不会造成任何变换
     */
    private void rotateMatrix(Bitmap bmp){
        Bitmap alteredBitmap = Bitmap.createBitmap(bmp.getWidth()*2,
                bmp.getHeight(), bmp.getConfig());//获得可变的bitmap
        Canvas canvas = new Canvas(alteredBitmap);
        Paint paint = new Paint();
        //矩阵操作
        Matrix matrix = new Matrix();
        matrix.setValues(new float[]{
                1, .5f, 0,
                0, 1, 0,
                0, 0, 1
        });
        canvas.drawBitmap(bmp, matrix, paint);
        alteredImageView.setImageBitmap(alteredBitmap);
    }


    //以下为Matrix类方法

    /**
     * 旋转操作
     * 默认点是图像的左上方
     */
    private void martixRotate(){
        Matrix matrix = new Matrix();
        matrix.setRotate(15);
        matrix.setRotate(15, 20,20);//可以指定角度与旋转点
    }

    /**
     *缩放
     */
    private void martixScale(){
        Matrix matrix = new Matrix();
        matrix.setScale(1.5f, 1);//参数一：x轴缩放比例  参数二：y轴缩放比例

        matrix.setTranslate(1.5f, -10);//平移：参数一在x轴上的移动数量 参数二y（负数向下）

        matrix.setScale(-1, 1);// x轴负数向左绘制，翻转
        matrix.postTranslate(12,0 );//因为翻转的时候向左绘制
    }

    /**
     * 图像处理
     *
     * 默认的ColorMatrix对象就是所谓的标识
     * 1 0 0 0 0
     * 0 1 0 0 0
     * 0 0 1 0 0
     * 0 0 0 1 0
     * 每一行分别代表  红 绿 蓝 Alpha  最后一个数字不会与任何值相乘
     * 假设现在有一个像素 红色值128 蓝色128 绿色128， Alpha为0（不透明）
     * 红色值 = 1 * 128 + 0*128 + 。。。 + 0*0 + 0
     *
     * 改变亮度与对比度：可以将每个颜色通道的强度加强
     * 改变饱和度： cm.setSaturation(.5f)
     */
    private void dealWithImage(){
        Canvas canvas = new Canvas();
        ColorMatrix colorMatrix = new ColorMatrix();
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
    }









}
