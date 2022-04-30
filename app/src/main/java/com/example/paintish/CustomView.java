package com.example.paintish;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CustomView extends View {

    public final static int BRUSH_SIZE = 14;
    public final static int BRUSH_DEFAULT_COLOR = Color.BLUE;
    public final static int BG_DEFAULT_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private boolean undoClearScreen=false;





    private int currentColor ;
    private float Mx,My;
    private int currentBGColor ;
    private int currentStrokeWidth ;
    //a clipboard to put your canvas on like attaching your drawing to an blank image
    private Bitmap bitmap;
    // to use for drawing
    private Paint paint;
    private Paint paintLetter;
    //to store each line you draw as a collection of points
    private Path path;
    //to use as a paper sheet to draw on
    private Canvas canvas;
    private Paint bitmapPaint = new Paint(Paint.DITHER_FLAG);
    //store your drawing lines as paths here
    List<Stroke> mPaths = new ArrayList<>();
    List<Stroke> redo_Paths = new ArrayList<>();



    public CustomView(Context context) {
        super(context,null);
    }


    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        // this is used to smoothen your drawings and give it attribute like color and size
        paintLetter = new Paint();
        paintLetter.setColor(Color.BLACK);
        paintLetter.setStrokeCap(Paint.Cap.ROUND);
        paintLetter.setStyle(Paint.Style.STROKE);
        paintLetter.setStrokeJoin(Paint.Join.ROUND);
        paintLetter.setStrokeWidth(15);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        paint.setStrokeJoin(Paint.Join.ROUND);

        paint.setAlpha(0xff);
    }

    public void initialize(int height,int width)
    {

        //initialise your bitmap with screen width , height and color system
        bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        //put the canvas on the bitmap
        canvas = new Canvas(bitmap);
        // give it default attribute
        currentBGColor = BG_DEFAULT_COLOR;
        currentColor = BRUSH_DEFAULT_COLOR;
        currentStrokeWidth = BRUSH_SIZE;

    }

    // brush color
    public void setColor(int color)
    {
        currentColor = color;
    }
    // brush size
    public void setStrokeSize(int size)
    {
        currentStrokeWidth = size;

    }

    //undo your last drawing action
    public void undoChanges()
    {
        if(mPaths.size()!=0)
        {
            mPaths.remove(mPaths.size()-1);

            //to repaint the screen after deleting the last line you draw
            invalidate();
            Log.i("TAG", "undoChanges: mpaths  " + mPaths.size()+"redo paths "+ redo_Paths.size());
        }

    }


    public void redoChanges()
    {

        if(mPaths.size()<redo_Paths.size()) {

            if (undoClearScreen)
            {
                mPaths.addAll(redo_Paths);
                invalidate();
            }
            else {
//                mPaths.size();
//                redo_Paths.size();
                mPaths.add(redo_Paths.get(mPaths.size()));
                invalidate();
                Log.i("TAG", "undoChanges: mpaths  " + mPaths.size()+"redo paths "+ redo_Paths.size());
            }
        }


    }

    public void clearScreen()
    {
        undoClearScreen = true;
        mPaths.clear();
        invalidate();
    }

    //return the bitmap u draw to save it later
    public Bitmap saveDrawing()
    {
        return bitmap;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //save the canvas state
        canvas.save();

        //background color of the canvas.
        canvas.drawColor(BG_DEFAULT_COLOR);

        for(Stroke mstroke:mPaths)
        {
            paint.setColor(mstroke.color);
            paint.setStrokeWidth(mstroke.width);
            canvas.drawPath(mstroke.path,paint);

        }
        canvas.drawBitmap(bitmap,0,0,bitmapPaint);

        canvas.restore();

        drawLetters(Matrix.ch1);

    }

    private void onTouchStart(float x , float y )
    {

        path = new Path();

        mPaths.add(new Stroke(currentColor,currentStrokeWidth,path));
        redo_Paths.add(new Stroke(currentColor,currentStrokeWidth,path));

        path.reset();

        path.moveTo(x,y);

        Mx = x;
        My = y;

    }

    private void onTouchMove(float x , float y)
    {
        float dx = Math.abs(x-Mx);
        float dy = Math.abs(y-My);
        if(dx>=TOUCH_TOLERANCE||dy>=TOUCH_TOLERANCE)
        {
            path.quadTo(Mx, My, (x +Mx) / 2, (y + My) / 2);

            Mx = x;
            My = y;

        }

    }

    public void drawLetters( int x[][]) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY);
        int y = getHeight()/3;
        for (int i = 0; i < x.length; i++) {
            int z = getWidth()/4;
            for (int j = 0; j < x[i].length; j++) {
                if (x[i][j] == 0) {
                    z += 60;
                } else if (x[i][j] == 1) {
                    canvas.drawCircle(z, y, 10, paintLetter);
                    z += 60;
                }
            }
            y += 60;
        }
    }


    private void onTouchUp()
    {
        path.lineTo(Mx,My);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                onTouchStart(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(x,y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                onTouchUp();
                invalidate();
                break;
        }

        return true;
    }

    public void RestoreOnResume()
    {
        invalidate();
    }
}
