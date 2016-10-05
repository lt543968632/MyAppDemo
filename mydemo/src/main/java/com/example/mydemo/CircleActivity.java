package com.example.mydemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by litao on 16/8/12.
 */

public class CircleActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    public static final int LEFT_SLIP = 1;//左滑
    public static final int RIGHT_SLIP = 2;//右滑
    public static final int UP_SLIP = 3;//上滑
    public static final int DOWN_SLIP = 4;//下滑

    private GestureDetector detector;

    private WheelView wheelView;

    private float xCenterPoint = 0;//转盘中心点x轴绝度坐标
    private float yCenterPoint = 0;//转盘中心点y轴绝度坐标

    private float minMove = 100; //滑动最小距离阈值

    private float beginX;//手势x轴起点
    private float endX;//手势x轴终点
    private float beginY;//手势y轴起点
    private float endY;//手势y轴终点

    private float x;//x轴滑动速度
    private float y;//y轴滑动速度

    private float startSpeed;//开始旋转速度


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);

        detector = new GestureDetector(this, this);

        wheelView = (WheelView) findViewById(R.id.id_wheelView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    /**
     * 开始旋转
     * startSpeed 起始速度; isPlusTrans 旋转方向, true:正转,false 反转
     */
    public void startTrans(float startSpeed, boolean isPlusTrans) {

        if (!wheelView.isStart()) {
            wheelView.luckyStart(startSpeed, isPlusTrans);
            wheelView.luckyEnd();
        }
    }

    /**
     * 计算转盘中心点在屏幕中的绝度坐标
     */
    public void xyCenterPoint() {
        int[] location = new int[2];
        wheelView.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
        int width = wheelView.getWidth();//转盘控件的宽
        int height = wheelView.getHeight();//转盘控件的高
        xCenterPoint = location[0] + width / 2;
        yCenterPoint = location[1] + height / 2;
        Log.d("xyCenter", xCenterPoint + "," + yCenterPoint);

    }

    /**
     * 检测滑动的四个方向
     */
    private int gestureDirection(float vx, float vy) {
        int directionInt = 0;
        if (Math.abs(beginX - endX) > minMove || Math.abs(beginY - endY) > minMove) {

            if (x > y && vx < 0) {   //左滑
                // Toast.makeText(this, "左滑", Toast.LENGTH_SHORT).show();
                directionInt = LEFT_SLIP;
            } else if (x > y && vx > 0) {   //右滑
                //Toast.makeText(this, "右滑", Toast.LENGTH_SHORT).show();
                directionInt = RIGHT_SLIP;
            } else if (x < y && vy < 0) {   //上滑
                //Toast.makeText(this, "上滑", Toast.LENGTH_SHORT).show();
                directionInt = UP_SLIP;
            } else if (x < y && vy > 0) {   //下滑
                //Toast.makeText(this, "下滑", Toast.LENGTH_SHORT).show();
                directionInt = DOWN_SLIP;
            }
        }
        return directionInt;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
        Log.d("speed", vx + "   " + vy);
        //计算转盘中心点在屏幕中的绝度坐标
        xyCenterPoint();

        beginX = e1.getX();
        endX = e2.getX();
        beginY = e1.getY();
        endY = e2.getY();
        x = Math.abs(vx);
        y = Math.abs(vy);
        startSpeed = Math.max(Math.abs(vx / 150), Math.abs(vy / 150));
        int direction = gestureDirection(vx, vy);


        if (beginY > yCenterPoint) {//下部分
            if (beginX < xCenterPoint) {//下左侧
                //Toast.makeText(this, "下左侧", Toast.LENGTH_SHORT).show();

                switch (direction) {
                    case LEFT_SLIP:
                        startTrans(startSpeed, true);
                        break;
                    case RIGHT_SLIP:
                        startTrans(startSpeed, false);
                        break;
                    case UP_SLIP:
                        startTrans(startSpeed, true);
                        break;
                    case DOWN_SLIP:
                        startTrans(startSpeed, false);
                        break;
                    default:
                        break;
                }

            } else {//下右侧
                //Toast.makeText(this, "下右侧", Toast.LENGTH_SHORT).show();

                switch (direction) {
                    case LEFT_SLIP:
                        startTrans(startSpeed, true);
                        break;
                    case RIGHT_SLIP:
                        startTrans(startSpeed, false);
                        break;
                    case UP_SLIP:
                        startTrans(startSpeed, false);
                        break;
                    case DOWN_SLIP:
                        startTrans(startSpeed, true);
                        break;
                    default:
                        break;
                }
            }

        } else {//上部分
            if (beginX < xCenterPoint) {//上左侧
                //Toast.makeText(this, "上左侧", Toast.LENGTH_SHORT).show();

                switch (direction) {
                    case LEFT_SLIP:
                        startTrans(startSpeed, false);
                        break;
                    case RIGHT_SLIP:
                        startTrans(startSpeed, true);
                        break;
                    case UP_SLIP:
                        startTrans(startSpeed, true);
                        break;
                    case DOWN_SLIP:
                        startTrans(startSpeed, false);
                        break;
                    default:
                        break;
                }

            } else {//上右侧
                //Toast.makeText(this, "上右侧", Toast.LENGTH_SHORT).show();

                switch (direction) {
                    case LEFT_SLIP:
                        startTrans(startSpeed, false);
                        break;
                    case RIGHT_SLIP:
                        startTrans(startSpeed, true);
                        break;
                    case UP_SLIP:
                        startTrans(startSpeed, false);
                        break;
                    case DOWN_SLIP:
                        startTrans(startSpeed, true);
                        break;
                    default:
                        break;
                }
            }
        }

        /*if (Math.abs(beginX - endX) > minMove || Math.abs(beginY - endY) > minMove) {

            if (x > y && vx < 0) {   //左滑
                // Toast.makeText(this, "左滑", Toast.LENGTH_SHORT).show();
                if (!wheelView.isStart()) {
                    wheelView.luckyStart(startSpeed, true);
                    wheelView.luckyEnd();
                }
                startTrans(startSpeed, true);
            } else if (x > y && vx > 0) {   //右滑
                //Toast.makeText(this, "右滑", Toast.LENGTH_SHORT).show();
                if (!wheelView.isStart()) {
                    wheelView.luckyStart(startSpeed, false);
                    wheelView.luckyEnd();
                }
                startTrans(startSpeed, false);
            } else if (x < y && vy < 0) {   //上滑
                Toast.makeText(this, "上滑", Toast.LENGTH_SHORT).show();
            } else if (x < y && vy > 0) {   //下滑
                Toast.makeText(this, "下滑", Toast.LENGTH_SHORT).show();
            }
        }*/

        return false;
    }

}

