package netsurf.woohoo.test_project.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class DrawingView extends View {
    public Bitmap  mBitmap;
    public Canvas  mCanvas;
    private Path    mPath;
    private Paint   mBitmapPaint;
    private Paint   mPaint;
    private  boolean eraserMode = false;
    boolean mPause;


    Matrix mMatrix;
    RectF mSrcRectF;
    RectF mDestRectF;



    public DrawingView(Context c, AttributeSet attrs) {
        super(c, attrs);

        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(15);
        mMatrix = new Matrix();
        mSrcRectF = new RectF();
        mDestRectF = new RectF();
        mPause = false;

    }




    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {


       if(mPause){
            if(mBitmap!=null){

                // Setting size of Source Rect
                mSrcRectF.set(0, 0,mBitmap.getWidth(),mBitmap.getHeight());

                // Setting size of Destination Rect
                mDestRectF.set(0, 0, getWidth(), getHeight());

                // Scaling the bitmap to fit the PaintView
                mMatrix.setRectToRect( mSrcRectF , mDestRectF, Matrix.ScaleToFit.CENTER);

                // Drawing the bitmap in the canvas
                canvas.drawBitmap(mBitmap, mMatrix, mPaint);
            }

            // Redraw the canvas
            invalidate();
        }else {

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

            canvas.drawPath(mPath, mPaint);
        }

    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    public void touch_up(boolean bool_val) {


        // commit the path to our offscreen

        if (bool_val == false) {
            mPath.lineTo(mX, mY);
            mCanvas.drawPath(mPath, mPaint);
        }else
        {
            mPaint.setXfermode(null);
           // mPaint.setAlpha(0xFF);
           // mPaint.setColor(Color.BLUE);

            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }



        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up(false);
                invalidate();
                break;
        }
        return true;
    }

    public Bitmap getBitmap()
    {

        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);


        return bmp;
    }


    // Pause or resume onDraw method
    public void pause(boolean pause){
        mPause = pause;
    }


    public void addBitmap(Bitmap bitmap){
        mBitmap = bitmap;
    }


    public void clear(){
        mBitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
        System.gc();

    }



}