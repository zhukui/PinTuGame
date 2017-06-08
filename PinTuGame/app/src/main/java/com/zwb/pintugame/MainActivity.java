package com.zwb.pintugame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gl = (GridLayout) findViewById(R.id.gl);
        initImageViews();
        initGestureDetector();
    }

    private void initGestureDetector() {
        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.e("info", "onFling");
                int type = 0;
                float srcX = e1.getX();
                float srcY = e1.getY();
                float dstX = e2.getX();
                float dstY = e2.getY();
                float velocity = Math.abs(srcX - dstX) - Math.abs(srcY - dstY);
                //左右移动
                if (velocity > 0) {
                    float dx = e1.getX() - e2.getX();
                    if (dx > 0) {
                        Log.e("info", "向左移动");
                        type = 1;
                    } else {
                        Log.e("info", "向右移动");
                        type = 2;
                    }
                } else {
                    float dy = e1.getY() - e2.getY();
                    if (dy > 0) {
                        Log.e("info", "向上移动");
                        type = 4;
                    } else {
                        type = 3;
                        Log.e("info", "向下移动");
                    }
                }
                transitionType(type);
                return false;
            }
        });
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
                        if (nearBlank) {
                            changePosition(srcImageView);
                        }
                    }
                });
                imageView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        return gestureDetector.onTouchEvent(event);
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
        if (isGameOver()) {
            Toast.makeText(MainActivity.this, "恭喜你完成拼图", Toast.LENGTH_SHORT).show();
        }
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

    /**
     * 移动的类型
     *
     * @param type 类型 1：向左，2 ：向右，3：向下，4：向上
     */
    private void transitionType(int type) {
        GameData data = (GameData) blankImageview.getTag();
        int x = data.getX();
        int y = data.getY();
        if (type == 1) {//如果是向左滑动，空白快右边的块移动，其他以此类推
            y += 1;
        } else if (type == 2) {
            y -= 1;
        } else if (type == 3) {
            x -= 1;
        } else if (type == 4) {
            x += 1;
        }
        //这几种情况都是不符合移动要求的
        if (x < 0 || x >= columnCount || y < 0 || y >= rowCount) {
            return;
        }
        ImageView imageView = imageViews[x][y];
        changePosition(imageView);
    }


}
