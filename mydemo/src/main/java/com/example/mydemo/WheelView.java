package com.example.mydemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * Created by litao on 16/8/12.
 */

public class WheelView extends SurfaceView implements Callback, Runnable {
    private Context mContext;

    private boolean isFirstStop = false;

    private SurfaceHolder mHolder;


    private Canvas mCanvas;//与SurfaceHolder绑定的Canvas

    private Thread t;//用于绘制的线程

    private boolean isRunning;// 线程的控制开关

    public boolean isPlusTrans = true;//控制正转与反转 true 正转;false 反转

    private String[] mStrs = new String[]{"一等奖", "二等奖", "三等奖", "四等奖", "五等奖", "六等奖"};//抽奖的文字

    private int[] mColors = new int[]{0xFF0939D4, 0xFF9878E4, 0xE945E2B6,
            0xE9D44880, 0xFF77CF5C, 0xE9ECCF2E};//每个盘块的颜色

    private int mItemCount = 6;//盘块的个数

    private RectF mRange = new RectF();//绘制盘块的范围

    private int mRadius;//圆的直径

    private Paint mArcPaint;//绘制盘快的画笔

    private Paint mTextPaint;//绘制文字的画笔

    private double mSpeed;//滚动的速度

    private volatile float mStartAngle = 0;

    private boolean isShouldEnd;//是否开始减速停止

    private int mPadding;//控件padding

    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(),
            R.drawable.bg2);//背景图的bitmap

    private float mTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 25, getResources().getDisplayMetrics());//文字的大小

    public WheelView(Context context) {
        this(context, null);
        mContext = context;
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHolder = getHolder();
        mHolder.addCallback(this);

        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
        mContext = context;

    }

    /**
     * 设置控件为正方形
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        // 获取圆形的直径
        mRadius = width - getPaddingLeft() - getPaddingRight();
        // padding值
        mPadding = getPaddingLeft();

        setMeasuredDimension(width, width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 初始化绘制圆弧的画笔
        mArcPaint = new Paint();
        //抗锯齿
        mArcPaint.setAntiAlias(true);
        //抗抖动
        mArcPaint.setDither(true);
        // 初始化绘制文字的画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(0xFFffffff);
        mTextPaint.setTextSize(mTextSize);
        // 圆弧的绘制范围
        mRange = new RectF(getPaddingLeft(), getPaddingLeft(), mRadius
                + getPaddingLeft(), mRadius + getPaddingLeft());

        // 开启线程
        isRunning = true;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 通知关闭线程
        isRunning = false;
    }

    @Override
    public void run() {
        // 不断的进行draw
        while (isRunning) {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            try {
                if (end - start < 50) {
                    Thread.sleep(50 - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    private void draw() {
        try {
            // 获得canvas
            mCanvas = mHolder.lockCanvas();//锁定画布
            if (mCanvas != null) {
                // 绘制背景图
                drawBg();

                /**
                 * 绘制每个块，每个块上的文本
                 */
                float tmpAngle = mStartAngle;
                float sweepAngle = (float) (360 / mItemCount);
                for (int i = 0; i < mItemCount; i++) {
                    // 绘制快
                    mArcPaint.setColor(mColors[i]);
                    mArcPaint.setStyle(Paint.Style.FILL);
                    mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true,
                            mArcPaint);
                    // 绘制文本
                    drawText(tmpAngle, sweepAngle, mStrs[i]);
                    tmpAngle += sweepAngle;
                }

                // 如果mSpeed不等于0，则相当于在滚动
                if (isPlusTrans) {
                    mStartAngle += mSpeed;//正转
                } else {
                    mStartAngle -= mSpeed;//反转
                }

                // 停止时，设置mSpeed为递减，为0值转盘停止
                if (isShouldEnd) {
                    mSpeed -= 1;
                }
                if (mSpeed <= 0) {
                    mSpeed = 0;
                    isShouldEnd = false;

                    if (isFirstStop) {
                        // 根据当前旋转的mStartAngle计算当前滚动到的区域

                        plusCalInExactArea(Math.abs(mStartAngle));
                        isFirstStop = false;

                      /*  if (isPlusTrans) {//正转
                            PlusCalInExactArea(Math.abs(mStartAngle));
                            isFirstStop = false;
                        } else {          //反转
                            minusCalInExactArea(Math.abs(mStartAngle));
                            isFirstStop = false;
                        }*/
                    }

                } else {

                    isFirstStop = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null)
                mHolder.unlockCanvasAndPost(mCanvas);//解锁画布
        }

    }

    /**
     * 绘制文本
     */
    private void drawText(float startAngle, float sweepAngle, String string) {
        Path path = new Path();
        path.addArc(mRange, startAngle, sweepAngle);
        float textWidth = mTextPaint.measureText(string);
        // 利用水平偏移让文字居中
        float hOffset = (float) (mRadius * Math.PI / mItemCount / 2 - textWidth / 2);// 水平偏移
        float vOffset = mRadius / 2 / 4;// 垂直偏移
        mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPaint);
    }

    /**
     * 绘制背景
     */
    private void drawBg() {
        mCanvas.drawColor(0xFFFFFFFF);
        mCanvas.drawBitmap(mBgBitmap, null, new Rect(mPadding / 2,
                mPadding / 2, getMeasuredWidth() - mPadding / 2,
                getMeasuredWidth() - mPadding / 2), null);
    }

    /**
     * 根据当前旋转的Angle计算当前滚动到的区域
     */
    public void plusCalInExactArea(float transAngle) {
        // 指针从水平向右开始计算
        float rotate;
        if (isPlusTrans) {
            rotate = transAngle + 90;
        } else {
            rotate = transAngle - 90;
        }
        rotate %= 360.0;
        for (int i = 0; i < mItemCount; i++) {
            // 每个的中奖区域的角度范围 start~end;
            float start;
            if (isPlusTrans) {
                //正转开始角度
                start = 360 - (i + 1) * (360 / mItemCount);
            } else {
                //反转开始角度
                start = i * (360 / mItemCount);
            }
            //结束角度
            float end = start + 60;

            if ((rotate > start) && (rotate < end)) {
                Log.d("Logzhongjiangquyu:", mStrs[i]);
                Log.d("Logstart:", start + "");
                Log.d("Logend:", end + "");
                Log.d("LogtransAngle", transAngle + "");
                final int finalI = i;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, mStrs[finalI], Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }
        }
    }


    /**
     * 反转根据当前旋转的mStartAngle计算当前滚动到的区域
     */
   /* public void minusCalInExactArea(float transAngle) {
        // 指针从水平向右开始计算
        float rotate = transAngle - 90;
        rotate %= 360.0;
        for (int i = 0; i < mItemCount; i++) {
            // 每个的中奖范围
            float start = i * (360 / mItemCount);
            float end = start + 60;

            if ((rotate > start) && (rotate < end)) {
                Log.d("zhongjiangquyu:", mStrs[i]);
                Log.d("start:", start + "");
                Log.d("end:", end + "");
                Log.d("transAngle", transAngle + "");
                final int finalI = i;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, mStrs[finalI], Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }
        }
    }*/


    /**
     * 开始旋转
     */
    public void luckyStart(float startSpeed, boolean transDirection) {//startSpeed  开始旋转速度
        if (transDirection) {
            isPlusTrans = true;
        } else {
            isPlusTrans = false;
        }
        mSpeed = startSpeed;
        isShouldEnd = false;
    }
    /**
     * 停止旋转
     */
    public void luckyEnd() {
        mStartAngle = 0;
        isShouldEnd = true;
    }
    /**
     * 判断是否在转动
     */
    public boolean isStart() {
        return mSpeed != 0;
    }

}