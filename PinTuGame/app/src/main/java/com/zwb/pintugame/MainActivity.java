package com.zwb.pintugame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private GridLayout gl;
    private int rowCount = 3;//行数
    private int columnCount = 3;//列数
    private ImageView[][] imageViews = new ImageView[rowCount][columnCount];
    private ImageView blankImageview;//空白块

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
                imageView.setTag(new GameData(i, j, tempBitmap));
                imageViews[i][j] = imageView;
                gl.addView(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView srcImageView = (ImageView) v;
                        GameData gameData = (GameData) srcImageView.getTag();
                        GameData blankGameData = (GameData) blankImageview.getTag();
                        boolean nearBlank = gameData.nearBlank(blankGameData);
//                        Toast.makeText(MainActivity.this, "--nearBlank---" + nearBlank, Toast.LENGTH_SHORT).show();
                        if (nearBlank) {
                            changePosition(srcImageView);
                        }
                        if (isGameOver()) {
                            Toast.makeText(MainActivity.this, "恭喜你完成拼图", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        setImageViewNullBitmap(imageViews[rowCount - 1][columnCount - 1]);
        randomPosition();
    }

    /**
     * 设置某一个imageView为空
     *
     * @param imageView 空白图快
     */
    private void setImageViewNullBitmap(ImageView imageView) {
        imageView.setImageBitmap(null);
        blankImageview = imageView;
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

    /**
     * 与空白块交换位置位置
     *
     * @param srcImageView 要交换位置的imageView
     */
    private void changePosition(ImageView srcImageView) {
        GameData srcData = (GameData) srcImageView.getTag();
        GameData dstData = (GameData) blankImageview.getTag();
        Bitmap bitmap = ((BitmapDrawable) srcImageView.getDrawable()).getBitmap();
        dstData.setBitmap(bitmap);
        srcData.setBitmap(null);
        int tempX = srcData.getP_x();
        int tempY = srcData.getP_y();
        srcData.setP_x(dstData.getP_x());
        srcData.setP_y(dstData.getP_y());
        dstData.setP_x(tempX);
        dstData.setP_y(tempY);
        blankImageview.setImageBitmap(bitmap);
        srcImageView.setImageBitmap(null);
        blankImageview = srcImageView;
    }

    /**
     * 是否完成平图
     *
     * @return
     */
    private boolean isGameOver() {
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                GameData gameData = (GameData) imageViews[i][j].getTag();
                if (!gameData.isRightPosition()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 打乱顺序
     */
    private void randomPosition() {
        //打乱的次数
        int count = 50;
        for (int i = 0; i < count; i++) {
            int x = new Random().nextInt(rowCount);
            int y = new Random().nextInt(columnCount);
            GameData blankData = (GameData) blankImageview.getTag();
            //随机一个imageView与空白块交换位置，如果当前是空白块就不交换位置了
            if (x != blankData.getX() || y != blankData.getY()) {
                changePosition(imageViews[x][y]);
            }
        }
    }
}
