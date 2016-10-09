package cxy.com.spiderplotview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CXY on 2016/9/27.
 */

public class SpiderPlotView extends View {


    public List<String> dataTxt;//数据数组
    public List<Integer> dataNum;//数据数组
    private int mWidth;//控件的宽度
    private int mHeight;
    private float centerX;
    private float centerY;

    public float range;//默认的最大值
    private int tierNum;//层叠个数
    private int TXT_AND_POINT_DISTANCE;//汉字和点的距离
    private int spiderLineLength;//蛛网线长
    private int spiderLineWidth;//蛛网线宽度
    private int spiderLineColor;//蛛网线颜色
    private int areasColor;//面积区域颜色

    private float txtSize;
    private int txtColor;

    private Paint linePaint;
    private Paint areasPaint;
    private Paint txtPaint;

    public SpiderPlotView(Context context) {
        this(context, null);
    }

    public SpiderPlotView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpiderPlotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //初始化默认值
        TXT_AND_POINT_DISTANCE = 5;
        spiderLineLength = dip2px(getContext(), 80);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpiderPlotView);
        setRange(typedArray.getInt(R.styleable.SpiderPlotView_spiderplot_range, dip2px(context, 100)));
        setTierNum(typedArray.getInt(R.styleable.SpiderPlotView_spiderplot_tierNum, 4));

        setTxtSize(typedArray.getDimension(R.styleable.SpiderPlotView_spiderplot_txt_size, 24f));
        setTxtColor(typedArray.getColor(R.styleable.SpiderPlotView_spiderplot_txt_color, Color.parseColor("#399BFF")));

//        setSpiderLineLength(typedArray.getInt(R.styleable.SpiderPlotView_spiderplot_txt_size, 24));
        setSpiderLineColor(typedArray.getColor(R.styleable.SpiderPlotView_spiderplot_line_color, Color.parseColor("#FF0000")));
        setSpiderLineWidth(typedArray.getInt(R.styleable.SpiderPlotView_spiderplot_line_width, 1));

        setAreasColor(typedArray.getColor(R.styleable.SpiderPlotView_spiderplot_areas_color, Color.parseColor("#55399BFF")));

        setRange(typedArray.getInt(R.styleable.SpiderPlotView_spiderplot_range, 100));
        setTierNum(typedArray.getInt(R.styleable.SpiderPlotView_spiderplot_tierNum, 3));

        typedArray.recycle();


        linePaint = new Paint();
        linePaint.setColor(spiderLineColor);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(spiderLineWidth);

        areasPaint = new Paint();
        areasPaint.setColor(areasColor);

        txtPaint = new Paint();
        txtPaint.setColor(txtColor);
        txtPaint.setAntiAlias(true);
        txtPaint.setTextSize(txtSize);
        txtPaint.setStyle(Paint.Style.STROKE);
        txtPaint.setTextSize(txtSize);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画最外圈
        drawOuter(canvas);

        //画中点距各点的线段
        for (int i = 0; i < dataTxtXY.size(); i++) {
            canvas.drawLine(centerX, centerY, dataTxtXY.get(i)[0], dataTxtXY.get(i)[1], linePaint);
        }

        //画内圈
        drawInner(canvas);

        //根据数据来计算覆盖面积
        drawAreas(canvas);

        //画文字
        drawTxt(canvas);
    }


    private void drawTxt(Canvas canvas) {
        int size = dataNum.size();
        for (int i = 0; i < size; i++) {

            float width = txtPaint.measureText(dataTxt.get(i));
            float height = txtPaint.ascent() + txtPaint.descent();
            float x = 0f;
            float y = 0f;

            if (i == 0) {//顶点
                x = dataTxtXY.get(i)[0] - width / 2;
                y = dataTxtXY.get(i)[1] - TXT_AND_POINT_DISTANCE;
            } else if (i == size / 2 && size % 2 == 0) {//偶数最低点
                x = dataTxtXY.get(i)[0] - width / 2;
                y = dataTxtXY.get(i)[1] - height + TXT_AND_POINT_DISTANCE;
            } else if (i <= size / 2) {//右面
                x = dataTxtXY.get(i)[0] + TXT_AND_POINT_DISTANCE;
                y = dataTxtXY.get(i)[1] - height / 2;
            } else {//左面
                x = dataTxtXY.get(i)[0] - (width + TXT_AND_POINT_DISTANCE);
                y = dataTxtXY.get(i)[1] - height / 2;
            }

            canvas.drawText(dataTxt.get(i), x, y, txtPaint);
        }
    }

    private void drawAreas(Canvas canvas) {
        int size = dataNum.size();
        List<Float[]> dataXY = new ArrayList<>();
        Path path = new Path();
        for (int i = 0; i < size; i++) {
            double angle = Math.toRadians(i * (float) (360.0 / size));
            Float[] xy = new Float[2];

            float length = dataNum.get(i) / range * spiderLineLength;
            if (0 <= angle && angle < 90) {
                xy[0] = (float) (centerX + Math.sin(angle) * length);
                xy[1] = (float) (centerY - Math.cos(angle) * length);
            } else if (90 <= angle && angle < 180) {
                xy[0] = (float) (centerX + Math.cos(angle - 90) * length);
                xy[1] = (float) (centerY + Math.sin(angle - 90) * length);
            } else if (180 <= angle && angle < 270) {
                xy[0] = (float) (centerX - Math.sin(angle - 180) * length);
                xy[1] = (float) (centerY + Math.cos(angle - 180) * length);
            } else if (270 <= angle && angle < 360) {
                xy[0] = (float) (centerX - Math.cos(angle - 270) * length);
                xy[1] = (float) (centerY - Math.sin(angle - 270) * length);
            }
            dataXY.add(xy);
        }

        for (int i = 0; i < dataXY.size(); i++) {
            if (i == 0) {
                path.moveTo(dataXY.get(i)[0], dataXY.get(i)[1]);
            } else {
                path.lineTo(dataXY.get(i)[0], dataXY.get(i)[1]);
            }
        }
        path.close();
        canvas.drawPath(path, areasPaint);
    }

    /**
     * 画内圈
     *
     * @param canvas
     */
    private void drawInner(Canvas canvas) {

        //  每一层的小线段
        for (int i = 0; i < tierNum; i++) {
            List<Float[]> xylist = computeDataTxtXYArray((spiderLineLength / tierNum) * i);
            Path path = new Path();
            for (int j = 0; j < xylist.size(); j++) {
                if (j == 0) {
                    path.moveTo(xylist.get(j)[0], xylist.get(j)[1]);
                } else {
                    path.lineTo(xylist.get(j)[0], xylist.get(j)[1]);
                }
            }
            path.close();
            canvas.drawPath(path, linePaint);
        }
    }

    /**
     * 画最外圈
     *
     * @param canvas
     */
    private void drawOuter(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < dataTxtXY.size(); i++) {
            if (i == 0) {
                path.moveTo(dataTxtXY.get(i)[0], dataTxtXY.get(i)[1]);
            } else {
                path.lineTo(dataTxtXY.get(i)[0], dataTxtXY.get(i)[1]);
            }
        }
        path.close();
        canvas.drawPath(path, linePaint);
    }


    public void update() {
        if (dataTxt.size() < 3) {
            throw new RuntimeException("描述文字过少，无法使用蛛网图");
        }
        if (dataNum.size() < 3) {
            throw new RuntimeException("描述文字值的个数过少，无法使用蛛网图");
        }
        if (dataNum.size() != dataTxt.size()) {
            throw new RuntimeException("描述文字与默认值个数不相同");
        }

        //根据数组获取坐标
        dataTxtXY = computeDataTxtXYArray(spiderLineLength);

        invalidate();
    }

    List<Float[]> dataTxtXY = new ArrayList<>();

    /**
     * 根据固定线段长度， 算出点的坐标
     *
     * @param spiderLineLength 线段长度
     * @return
     */
    private List<Float[]> computeDataTxtXYArray(int spiderLineLength) {
        List<Float[]> dataXY = new ArrayList<>();
        double angle = 0;
        for (int i = 0; i < dataTxt.size(); i++) {
            //转换
            angle = Math.toRadians(i * (float) (360.0 / dataTxt.size()));
            Float[] xy = new Float[2];
            if (0 <= angle && angle < 90) {
                xy[0] = (float) (centerX + Math.sin(angle) * spiderLineLength);
                xy[1] = (float) (centerY - Math.cos(angle) * spiderLineLength);
            } else if (90 <= angle && angle < 180) {
                xy[0] = (float) (centerX + Math.cos(angle - 90) * spiderLineLength);
                xy[1] = (float) (centerY + Math.sin(angle - 90) * spiderLineLength);
            } else if (180 <= angle && angle < 270) {
                xy[0] = (float) (centerX - Math.sin(angle - 180) * spiderLineLength);
                xy[1] = (float) (centerY + Math.cos(angle - 180) * spiderLineLength);
            } else if (270 <= angle && angle < 360) {
                xy[0] = (float) (centerX - Math.cos(angle - 270) * spiderLineLength);
                xy[1] = (float) (centerY - Math.sin(angle - 270) * spiderLineLength);
            }
            dataXY.add(xy);
        }
        return dataXY;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * 设置宽度
         * 单位 px
         */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
        {
            mWidth = specSize;
        } else {
            mWidth = dip2px(getContext(), 300);
        }

        /***
         * 设置高度
         */
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
        {
            mHeight = specSize;
        } else {
            mHeight = dip2px(getContext(), 300);
        }
        centerX = mWidth / 2;
        centerY = mHeight / 2;
        setMeasuredDimension(mWidth, mHeight);
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setDataTxt(List<String> dataTxt) {
        this.dataTxt = dataTxt;
    }

    public void setDataNum(List<Integer> dataNum) {
        this.dataNum = dataNum;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public float getTxtSize() {
        return txtSize;
    }

    public void setTxtSize(float txtSize) {
        this.txtSize = txtSize;
    }

    public int getTxtColor() {
        return txtColor;
    }

    public void setTxtColor(int txtColor) {
        this.txtColor = txtColor;
    }

    public int getTierNum() {
        return tierNum;
    }

    public void setTierNum(int tierNum) {
        this.tierNum = tierNum;
    }

    public int getSpiderLineLength() {
        return spiderLineLength;
    }

    public void setSpiderLineLength(int spiderLineLength) {
        this.spiderLineLength = spiderLineLength;
    }

    public int getSpiderLineColor() {
        return spiderLineColor;
    }

    public void setSpiderLineColor(int spiderLineColor) {
        this.spiderLineColor = spiderLineColor;
    }

    public int getSpiderLineWidth() {
        return spiderLineWidth;
    }

    public void setSpiderLineWidth(int spiderLineWidth) {
        this.spiderLineWidth = spiderLineWidth;
    }

    public int getAreasColor() {
        return areasColor;
    }

    public void setAreasColor(int areasColor) {
        this.areasColor = areasColor;
    }
}
