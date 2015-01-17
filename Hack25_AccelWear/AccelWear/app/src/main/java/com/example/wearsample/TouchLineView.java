package com.example.wearsample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;

/**
 * Created by mitak_000 on 2014/08/28.
 */
public class TouchLineView extends View {

    public TouchLineView(Context context){
        super(context);
        setBackgroundColor(Color.WHITE);
    }

    private HashMap<String, PointF> points = new HashMap<String, PointF>();
    Paint paint = new Paint();
    Path path = new Path();
    Canvas canvas = new Canvas();

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = event.getAction();
        int count = event.getPointerCount();

        paint.setAntiAlias(true);

        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);

        switch(action&MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                points.put(""+pointerId, new PointF(event.getX(), event.getY()));
                paint.setStyle(Paint.Style.STROKE);
                path.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                for(int i=0 ; i<count ; i++){
                    PointF pos = points.get(""+event.getPointerId(i));
                    pos.x = event.getX(i);
                    pos.y = event.getY(i);

                    path.lineTo(pos.x, pos.y);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                points.remove(""+pointerId);
                path.lineTo(event.getX(), event.getY());
                invalidate();
                break;
            default:
                break;
        }

        return true;

    }

    @Override
    protected void onDraw(Canvas canvas){

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(16);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawText("TouchEx>", 0, 20*1, paint);
        Object[] keys = points.keySet().toArray();
        for(int i=0 ; i<keys.length ; i++){
            paint.setColor(Color.BLACK);
            PointF pos = (PointF)points.get(keys[i]);
            canvas.drawText((int)pos.x+", "+(int)pos.y, 80, 20*1, paint);
        }

        paint.setColor(Color.RED);
        paint.setStrokeWidth(3);
        if(path != null){
            canvas.drawPath(path, paint);
        }
    }
}
