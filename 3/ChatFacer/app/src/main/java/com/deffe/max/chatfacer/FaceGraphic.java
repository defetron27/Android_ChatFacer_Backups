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

    private static final float DOT_RADIUS = 3.0f;
    private static final float TEXT_OFFSET_Y = -30.0f;

    private boolean mIsFrontFacing;

    // This variable may be written to by one of many threads. By declaring it as volatile,
    // we guarantee that when we read its contents, we're reading the most recent "write"
    // by any thread.
    private volatile FaceData mFaceData;

    private Drawable mDogLeftEar;
    private Drawable mDogRightEar;
    private Drawable mDogNose;

    // We want each iris to move independently,
    // so each one gets its own physics engine.
    private EyePhysics mLeftPhysics = new EyePhysics();
    private EyePhysics mRightPhysics = new EyePhysics();


    FaceGraphic(GraphicOverlay overlay, Context context, boolean isFrontFacing) {
        super(overlay);
        mIsFrontFacing = isFrontFacing;
        Resources resources = context.getResources();
        initializeGraphics(resources);
    }

    private void initializeGraphics(Resources resources) {
        mDogLeftEar = resources.getDrawable(R.drawable.dog_left_ear);
        mDogRightEar = resources.getDrawable(R.drawable.dog_right_ear);
        mDogNose = resources.getDrawable(R.drawable.dog_nose);
    }

    /**
     *  Update the face instance based on detection from the most recent frame.
     */
    void update(FaceData faceData) {
        mFaceData = faceData;
        postInvalidate(); // Trigger a redraw of the graphic (i.e. cause draw() to be called).
    }

    @Override
    public void draw(Canvas canvas) {
        // Confirm that the face data is still available
        // before using it.
        FaceData faceData = mFaceData;
        if (faceData == null) {
            return;
        }
        PointF detectPosition = faceData.getPosition();
        PointF detectLeftEyePosition = faceData.getLeftEyePosition();
        PointF detectRightEyePosition = faceData.getRightEyePosition();
        PointF detectNoseBasePosition = faceData.getNoseBasePosition();
        {
            if ((detectPosition == null) ||
                    (detectLeftEyePosition == null) ||
                    (detectRightEyePosition == null) ||
                    (detectNoseBasePosition == null)) {
                return;
            }
        }

        // If we've made it this far, it means that the face data *is* available.
        // It's time to translate camera coordinates to view coordinates.

        // Face position, dimensions, and angle

        PointF position = new PointF(translateX(detectPosition.x), translateY(detectPosition.y));

        float width = scaleX(faceData.getWidth());
        float height = scaleY(faceData.getHeight());

        // Eye coordinates
        PointF leftEyePosition = new PointF(translateX(detectLeftEyePosition.x), translateY(detectLeftEyePosition.y));
        PointF rightEyePosition = new PointF(translateX(detectRightEyePosition.x), translateY(detectRightEyePosition.y));

        // Nose coordinates
        PointF noseBasePosition = new PointF(translateX(detectNoseBasePosition.x), translateY(detectNoseBasePosition.y));

        drawLeftEar(canvas, leftEyePosition, width, height);

        drawRightEar(canvas, rightEyePosition, width, height);

        // Draw the nose.
        drawNose(canvas, noseBasePosition, width, height);
    }

    // Cartoon feature draw routines
    // =============================

    private void drawLeftEar(Canvas canvas, PointF leftEarPosition,float faceWidth,float faceHeight)
    {
        final float EYE_FACE_WIDTH_RATIO = (float) (1 / 5.0);
        final float EYE_FACE_HEIGHT_RATIO = (float) (1 / 6.0);

        float eyeWidth = faceWidth * EYE_FACE_WIDTH_RATIO;
        float eyeHeight = faceHeight * EYE_FACE_HEIGHT_RATIO;

        int left = (int)(leftEarPosition.x - (eyeWidth /2));
        int right = (int)(leftEarPosition.x + (eyeWidth /2));
        int top = (int)(leftEarPosition.y + eyeHeight) / 2;
        int bottom = (int)(leftEarPosition.y - (eyeHeight /2));

        mDogLeftEar.setBounds(left, top, right, bottom);
        mDogLeftEar.draw(canvas);
    }

    private void drawRightEar(Canvas canvas, PointF rightEarPosition,float faceWidth,float faceHeight)
    {
        final float EYE_FACE_WIDTH_RATIO = (float) (1 / 5.0);
        final float EYE_FACE_HEIGHT_RATIO = (float) (1 / 6.0);

        float eyeWidth = faceWidth * EYE_FACE_WIDTH_RATIO;
        float eyeHeight = faceHeight * EYE_FACE_HEIGHT_RATIO;

        int left = (int)(rightEarPosition.x - (eyeWidth /2));
        int right = (int)(rightEarPosition.x + (eyeWidth /2));
        int top = (int)(rightEarPosition.y + eyeHeight) / 2;
        int bottom = (int)(rightEarPosition.y - (eyeHeight /2));

        mDogRightEar.setBounds(left, top, right, bottom);
        mDogRightEar.draw(canvas);
    }

    private void drawNose(Canvas canvas, PointF noseBasePosition, float faceWidth, float faceHeight)
    {
        final float NOSE_FACE_WIDTH_RATIO = (float)(2 / 5.0);
        final float NOSE_FACE_HEIGHT_RATIO = (float)(2 / 6.0);

        float noseWidth = faceWidth * NOSE_FACE_WIDTH_RATIO;
        float noseHeight = faceHeight * NOSE_FACE_HEIGHT_RATIO;

        int left = (int)(noseBasePosition.x - (noseWidth / 2));
        int right = (int)(noseBasePosition.x + (noseWidth / 2));
        int top = (int)(noseBasePosition.y - (noseHeight / 2));
        int bottom = (int)(noseBasePosition.y + (noseHeight / 2));

        mDogNose.setBounds(left, top, right, bottom);
        mDogNose.draw(canvas);
    }

}
