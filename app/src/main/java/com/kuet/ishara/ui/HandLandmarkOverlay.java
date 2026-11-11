package com.kuet.ishara.ui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import java.util.List;
/** Live 21-point hand skeleton. Coordinates are approximate over PreviewView FILL_CENTER. */
public final class HandLandmarkOverlay extends View {
    private static final int[][] EDGES = {
        {0,1},{1,2},{2,3},{3,4},{0,5},{5,6},{6,7},{7,8},
        {5,9},{9,10},{10,11},{11,12},{9,13},{13,14},{14,15},{15,16},
        {13,17},{0,17},{17,18},{18,19},{19,20}};
    private final Paint line=new Paint(Paint.ANTI_ALIAS_FLAG), dot=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float[][] points=new float[21][2];
    private boolean hasPoints=false;
    public HandLandmarkOverlay(Context c,AttributeSet a){ super(c,a);
        line.setColor(Color.rgb(10,228,187));line.setStrokeWidth(5f);line.setStyle(Paint.Style.STROKE);
        dot.setColor(Color.WHITE);dot.setStyle(Paint.Style.FILL);
        setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }
    public void updatePoints(List<NormalizedLandmark> hand,boolean mirror){
        if(hand==null || hand.size()<21){clear();return;}
        for(int i=0;i<21;i++){
            float x=hand.get(i).x(),y=hand.get(i).y();
            points[i][0]=Math.max(0,Math.min(1,mirror?1-x:x));
            points[i][1]=Math.max(0,Math.min(1,y));
        }
        hasPoints=true;invalidate();
    }
    public void clear(){hasPoints=false;invalidate();}
    @Override protected void onDraw(Canvas c){super.onDraw(c);if(!hasPoints)return;
        for(int[] e:EDGES)c.drawLine(points[e[0]][0]*getWidth(),points[e[0]][1]*getHeight(),points[e[1]][0]*getWidth(),points[e[1]][1]*getHeight(),line);
        for(float[] point:points)c.drawCircle(point[0]*getWidth(),point[1]*getHeight(),5.4f,dot);
    }
}
