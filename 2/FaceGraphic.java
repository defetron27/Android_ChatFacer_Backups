package com.deffe.max.chatfacer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

public class FaceGraphic extends GraphicOverlay.Graphic
{
    private static final String TAG = "FaceGraphic";

    private boolean mIsFrontFacing;

    // This variable may be written to by one of many threads. By declaring it as volatile,
    // we guarantee that when we read its contents, we're reading the most recent "write"
    // by any thread.
    private volatile FaceData mFaceData;

    private Drawable mMonkeyFace;

    // We want each iris to move independently,
    // so each one gets its own physics engine.
    private EyePhysics mLeftPhysics = new EyePhysics();
    private EyePhysics mRightPhysics = new EyePhysics();


    FaceGraphic(GraphicOverlay overlay, Context context, boolean isFrontFacing)
    {
        super(overlay);
        mIsFrontFacing = isFrontFacing;
        Resources resources = context.getResources();
        initializeGraphics(resources);
    }

    private void initializeGraphics(Resources resources)
    {
        mMonkeyFace = resources.getDrawable(R.drawable.monkey);
    }

    /**
     *  Update the face instance based on detection from the most recent frame.
     */
    void update(FaceData faceData)
    {
        mFaceData = faceData;
        postInvalidate(); // Trigger a redraw of the graphic (i.e. cause draw() to be called).
    }

    @Override
    public void draw(Canvas canvas)
    {
        // Confirm that the face data is still available
        // before using it.
        FaceData faceData = mFaceData;
        if (faceData == null) {
            return;
        }
        PointF detectPosition = faceData.getPosition();
        PointF detectNoseBasePosition = faceData.getNoseBasePosition();

        if ((detectPosition == null) || (detectNoseBasePosition == null) )
        {
            return;
        }

        // If we've made it this far, it means that the face data *is* available.
        // It's time to translate camera coordinates to view coordinates.

        PointF position = new PointF(translateX(detectPosition.x), translateY(detectPosition.y));

        float width = scaleX(faceData.getWidth());
        float height = scaleY(faceData.getHeight());

        // Face position, dimensions, and angle

        PointF noseBasePosition = new PointF(translateX(detectNoseBasePosition.x), translateY(detectNoseBasePosition.y));

        drawFace(canvas, noseBasePosition,width,height);
    }

    private void drawFace(Canvas canvas, PointF noseBasePosition, float faceWidth, float faceHeight)
    {
        int left = (int)(noseBasePosition.x - (faceWidth / 2));
        int right = (int)(noseBasePosition.x + (faceWidth / 2));
        int top = (int)(noseBasePosition.y - (faceHeight / 2));
        int bottom = (int)(noseBasePosition.y + (faceHeight / 2));

        mMonkeyFace.setBounds(left, top, right, bottom);
        mMonkeyFace.draw(canvas);
    }
}

