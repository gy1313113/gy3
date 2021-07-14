package com.example.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Created by wanggaowan on 2021/6/16 9:53
 */
public class MyView1 extends View {
    
    private final Paint mPaintPolygon;
    private final Paint mPaintCircleBlue;
    private final Paint mPaintCircleGreen;
    private final Paint mPaintCircleMagenta;
    private final Paint mPaintCircleCyan;
    
    public MyView1(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        mPaintPolygon = new Paint();
        mPaintPolygon.setAntiAlias(true);//添加抗锯齿
        mPaintPolygon.setStyle(Paint.Style.STROKE);//描边
        mPaintPolygon.setColor(Color.RED);
        mPaintPolygon.setStrokeWidth(10.0f);
        
        mPaintCircleBlue = new Paint();
        mPaintCircleBlue.setAntiAlias(true);
        mPaintCircleBlue.setStyle(Paint.Style.FILL);//填充
        mPaintCircleBlue.setColor(Color.BLUE);
    
        mPaintCircleGreen = new Paint();
        mPaintCircleGreen.setAntiAlias(true);
        mPaintCircleGreen.setStyle(Paint.Style.FILL);//填充
        mPaintCircleGreen.setColor(Color.GREEN);
    
        mPaintCircleMagenta = new Paint();
        mPaintCircleMagenta.setAntiAlias(true);
        mPaintCircleMagenta.setStyle(Paint.Style.FILL);//填充
        mPaintCircleMagenta.setColor(Color.MAGENTA);
    
        mPaintCircleCyan = new Paint();
        mPaintCircleCyan.setAntiAlias(true);
        mPaintCircleCyan.setStyle(Paint.Style.FILL);//填充
        mPaintCircleCyan.setColor(Color.CYAN);
    }
    //attr是一组属性值
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        setMeasuredDimension(measureWidth(widthMeasureSpec),measureHeight(heightMeasureSpec));
    }
    
    private int measureWidth(int measureSpec){
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);//得到模式
        int specSize = MeasureSpec.getSize(measureSpec);//得到尺寸
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
                result = 500;
                result = Math.min(result, specSize);
                break;
        }
        return result;
    }
    
    private int measureHeight(int measureSpec){
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
                result = 500;
                result = Math.min(result, specSize);
                break;
        }
        return result;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        //将画布中心移动到中心点
        canvas.translate(500.0f / 2, 500.0f / 2);
        //画六边形
        drawPolygon(canvas);
        //画小圆
        drawSmallCircle(canvas);
        super.onDraw(canvas);
    }
    
    /*
    画六边形
     */
    private void drawPolygon(Canvas canvas) {
        //多边形边角顶点的x坐标
        float pointX;
        //多边形边角顶点的y坐标
        float pointY;
        //总的圆的半径，就是全部多边形的半径之和
        Path path = new Path();
        //计算最小的多边形的半径
        float mRadius = (500.0f / 2f) * 0.95f;
        //画前先重置路径
        path.reset();
        for (int i = 1; i <= 6; i++) {
            //cos三角函数，中心角的邻边 / 斜边，斜边的值刚好就是半径，cos值乘以斜边，就能求出邻边，而这个邻边的长度，就是点的x坐标
            pointX = (float) (Math.cos(i * (2*(Math.PI)/6)) * mRadius);
            //sin三角函数，中心角的对边 / 斜边，斜边的值刚好就是半径，sin值乘以斜边，就能求出对边，而这个对边的长度，就是点的y坐标
            pointY = (float) (Math.sin(i * (2*(Math.PI)/6)) * mRadius);
            //如果是一个点，则移动到这个点，作为起点
            if (i == 1) {
                path.moveTo(pointX, pointY);
            } else {
                //其他的点，就可以连线了
                path.lineTo(pointX, pointY);
            }
        }
        path.close();
        canvas.drawPath(path, mPaintPolygon);
    }
    
    /*
    画小圆
     */
    private void drawSmallCircle(Canvas canvas) {
        //计算中心小圆的半径
        float mSmallCircleRadius = (500.0f / 2f) * 0.25f;
        canvas.drawCircle(100, 0, mSmallCircleRadius, mPaintCircleBlue);
        canvas.drawCircle(-100, 0, mSmallCircleRadius, mPaintCircleGreen);
        canvas.drawCircle(0, 100, mSmallCircleRadius, mPaintCircleMagenta);
        canvas.drawCircle(0, -100, mSmallCircleRadius, mPaintCircleCyan);
    }
    
}
