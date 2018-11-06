package zjut.alan.cameracapture;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * 画布绘图
 */
public class DrawImageActivity extends AppCompatActivity implements View.OnTouchListener{
    private ImageView imageView;
    private Bitmap bitmap;

    private Canvas canvas;
    private Paint paint;

    private float downx = 0;
    private float downy = 0;
    private float upx = 0;
    private float upy = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_image);
        imageView = findViewById(R.id.ImageView);
        Display currentDisplay = getWindowManager().getDefaultDisplay();
        float dh = currentDisplay.getHeight();
        float dw = currentDisplay.getWidth();
        bitmap = Bitmap.createBitmap((int)dw, (int)dh, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        imageView.setImageBitmap(bitmap);
        imageView.setOnTouchListener(this);
    }

    private void drawKindsOfImages(){
        //imageView = findViewById(R.id.ImageView);
        //1. 位图配置
        //ARG_8888:为每个颜色通道分配8位，包括Alpha通道
        //2. 创建画布对象
        Bitmap bitmap = Bitmap.createBitmap(getWindowManager().getDefaultDisplay()
                        .getWidth(), getWindowManager().getDefaultDisplay().getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        //3. 使用Paint对象
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);//仅绘制形状的轮廓而不填充
        paint.setStrokeWidth(10);
        //绘制形状
        //点
        canvas.drawPoint(199, 201, paint);
        canvas.setBitmap(bitmap);
        imageView.setImageBitmap(bitmap);
        //直线
        int startX = 50;
        int startY = 100;
        int endX = 150;
        int endY = 210;
        canvas.drawLine(startX, startY, endX, endY, paint);
        //矩形
        float leftX = 20;
        float topY = 20;
        float rightX = 50;
        float bottomY = 100;
        canvas.drawRect(leftX, topY, rightX, bottomY, paint);
        //或者
        RectF rectangle = new RectF(leftX, topY, rightX, bottomY);
        canvas.drawRect(rectangle, paint);
        //椭圆
        RectF ovalBounds = new RectF(leftX, topY, rightX, bottomY);
        canvas.drawOval(ovalBounds, paint);
        //圆
        float x = 50;
        float y = 50;
        float radius = 20;
        canvas.drawCircle(x, y, radius, paint);
        //路径
        Path path = new Path();
        //没有初始的moveTo，默认从(0,0)开始
        path.moveTo(20, 20);
        path.lineTo(100, 200);
        path.lineTo(200, 100);
        path.lineTo(20, 20);
        canvas.drawPath(path, paint);
        //绘制文本
        float text_x = 120;
        float text_y = 120;
        paint.setTypeface(Typeface.DEFAULT_BOLD);//设置字体
        //加载外部字体
        Typeface chops = Typeface.createFromAsset(getAssets(), "填写ttf文件.ttf");
        paint.setTypeface(chops);
        canvas.drawText("幼儿缘", text_x, text_y, paint);//起始坐标

        //路径上的文本
        Path p = new Path();
        p.moveTo(20, 20);
        p.lineTo(100, 150);
        p.lineTo(200, 220);
        canvas.drawTextOnPath("Hello this is a text", p, 0, 0, paint);
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
                imageView.invalidate();
                downx = upx;
                downy = upy;
                break;
            }
            case MotionEvent.ACTION_UP:{
                upx = motionEvent.getX();
                upy = motionEvent.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                imageView.invalidate();
                break;
            }
            case MotionEvent.ACTION_CANCEL:{
                break;
            }
            default:{
                break;
            }
        }
        return true;
    }
}
