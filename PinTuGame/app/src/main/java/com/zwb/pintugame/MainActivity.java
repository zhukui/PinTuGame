package com.zwb.pintugame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private GridLayout gl;
    private int rowCount = 3;//行数
    private int columnCount = 3;//列数
    private ImageView[][] imageViews = new ImageView[rowCount][columnCount];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gl = (GridLayout) findViewById(R.id.gl);
        initImageViews();
    }

    /**
     * 初始化图片
     */
    private void initImageViews() {
        Bitmap bitmap = scaleBitmapToScreen();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int tempWidth = width / columnCount;
        int tempHeight = height / rowCount;
        ImageView imageView;
        Bitmap tempBitmap;
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                tempBitmap = Bitmap.createBitmap(bitmap, j * tempWidth, i * tempHeight, tempWidth, tempHeight);
                imageView = new ImageView(this);
                imageView.setPadding(2, 2, 2, 2);
                imageView.setImageBitmap(tempBitmap);
                imageViews[i][j] = imageView;
                gl.addView(imageView);
            }
        }
        setImageViewNullBitmap(imageViews[rowCount - 1][columnCount - 1]);
    }

    /**
     * 设置某一个imageView为空
     *
     * @param imageView
     */
    private void setImageViewNullBitmap(ImageView imageView) {
        imageView.setImageBitmap(null);
    }

    /**
     * 把bitmap缩放到与屏幕一样宽
     */
    private Bitmap scaleBitmapToScreen() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pintu);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //减去这12是因为有3行3列，每一个imageView设置的padding是2，减去之后图片才能完全显示
        int screenW = getResources().getDisplayMetrics().widthPixels - columnCount * 4;
        int screenH = getResources().getDisplayMetrics().heightPixels - rowCount * 4;
        float scale = Math.min(screenH * 1.0f / height, screenW * 1.0f / width);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }
}
