package com.zwb.pintugame;

import android.graphics.Bitmap;

/**
 * Created by zwb
 * Description 每个方块的数据 3*3的素组
 * Date 2017/6/8.
 */

public class GameData {

    private int x;//x下标--原始位置
    private int y;//y 下标--原始位置
    private Bitmap bitmap;//保存的图快

    private int p_x;//现在的位置
    private int p_y;//现在的位置

    public GameData(int x, int y, Bitmap bitmap) {
        this.x = x;
        this.y = y;
        this.p_x = x;
        this.p_y = y;
        this.bitmap = bitmap;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getP_x() {
        return p_x;
    }

    public void setP_x(int p_x) {
        this.p_x = p_x;
    }

    public int getP_y() {
        return p_y;
    }

    public void setP_y(int p_y) {
        this.p_y = p_y;
    }

    @Override
    public String toString() {
        return "GameData{" +
                "x=" + x +
                ", y=" + y +
                ", bitmap=" + bitmap +
                ", p_x=" + p_x +
                ", p_y=" + p_y +
                '}';
    }

    /**
     * 是否与空白快相邻
     *
     * @param blank 空白快数据
     * @return false
     */
    public boolean nearBlank(GameData blank) {
        int fx = blank.getX() - this.x;
        int fy = blank.getY() - this.getY();
        //在同一行
        if (fx == 0 && Math.abs(fy) == 1) {
            return true;
            //在同一列
        } else if (fy == 0 && Math.abs(fx) == 1) {
            return true;
        }
        return false;
    }

    /**
     * 图片是否在正确的位置上
     * @return true
     */
    public boolean isRightPosition(){
        return x == p_x && y == p_y;
    }
}
