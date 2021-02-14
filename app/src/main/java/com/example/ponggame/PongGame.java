package com.example.ponggame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PongGame extends SurfaceView implements Runnable {

    //Attribute

    // Holds the resolution of the screen
    private int mScreenX;
    private int mScreenY;
    // How big will the text be?
    private int mFontSize;
    private int mFontMargin;
    // The current score and lives remaining
    private int mScore;
    private int mLives;

    // The game objects
    private Bat mBat;
    private Ball mBall;

    // These objects are needed to do the drawing
    private SurfaceHolder mOurHolder;
    private Canvas mCanvas;
    private Paint mPaint;

    // Are we debugging?
    private final boolean DEBUGGING = true;
    // How many frames per second did we get?
    private long mFPS;
    // The number of milliseconds in a second
    private final int MILLIS_IN_SECOND = 1000;

    // Here is the Thread and two control variables
    private Thread mGameThread = null;

    // This volatile variable can be accessed from inside and outside the thread
    private volatile boolean mPlaying;
    private boolean mPaused = true;

    //the PongGame Constructor
    public PongGame (Context context, int x,int y){
        super(context);
        // Initialize these two members/fields
    // With the values passed in as parameters
        mScreenX = x;
        mScreenY = y;
    // Font is 5% (1/20th) of screen width
        mFontSize = mScreenX / 20;
    // Margin is 1.5% (1/75th) of screen width
        mFontMargin = mScreenX / 50;
    // Initialize the objects
    // ready for drawing with
    // getHolder is a method of SurfaceView
        mOurHolder = getHolder();
        mPaint = new Paint();
        mBall = new Ball (mScreenX);
    // Initialize the bat and ball
    // Everything is ready so start the game
        mBall = new Ball(mScreenX);
        mBat = new Bat(mScreenX, mScreenY);

        startNewGame();
        mBall.reset(mScreenX, mScreenY);
        // Initialize the bat and ball
    }
    // The player has just lost or is starting their first game
    private void startNewGame() {
        // Put the ball back to the starting position

    // Reset the score and the player's chances
        mScore = 0;
        mLives = 3;
    }
    private void draw(){
           if (mOurHolder.getSurface().isValid()) {
                mCanvas = mOurHolder.lockCanvas(); // Lock the canvas (graphics memory)
                mCanvas.drawColor(Color.argb(255, 26, 128, 182));
                mPaint.setColor(Color.argb(255, 255, 255, 255));
                mPaint.setTextSize(mFontSize);

                mCanvas.drawText("Score: " + mScore + " Lives: " + mLives,



                mFontMargin, mFontSize, mPaint);
               // Draw the bat and ball
                    mCanvas.drawRect(mBall.getRect(), mPaint);
                    mCanvas.drawRect(mBat.getRect(), mPaint);
                   if (DEBUGGING) {
                      printDebuggingText();
                    }
                   mOurHolder.unlockCanvasAndPost(mCanvas);

               // Draw the bat and ball

        }
    }

    private void printDebuggingText() {
        int debugSize = mFontSize / 2;
        int debugStart = 150;
        mPaint.setTextSize(debugSize);
        mCanvas.drawText("FPS: " + mFPS ,

                10, debugStart + debugSize, mPaint);
    }
    // This method is called by PongActivity when the player quits the game
    public void pause() {

    // Set mPlaying to false. Stopping the thread isn’t always instant
        mPlaying = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }
    // This method is called by PongActivity when the player starts the game
    public void resume() {
        mPlaying = true;
    // Initialize the instance of Thread
        mGameThread = new Thread(this);
    // Start the thread
        mGameThread.start();
    }
    @Override
    public void run() {
    // mPlaying must be true AND the thread running for the main loop to execute
        while (mPlaying) {
    // What time is it now at the start of the loop?
            long frameStartTime = System.currentTimeMillis();
    // Provided the game isn't paused call the update method
            if(!mPaused){
                update(); // update new positions
                detectCollisions(); // detect collisions
            }
    //draw the scene
            draw();
    // How long did this frame/loop take?
            long timeThisFrame = System.currentTimeMillis() - frameStartTime;
    // check timeThisFrame > 0 ms because dividing by 0 will crashes game
            if (timeThisFrame > 0) {
    // Store frame rate to pass to the update methods of mBat and mBall
                mFPS = MILLIS_IN_SECOND / timeThisFrame;
            }
        }
    }

    private void update() {
        // Update the bat and the ball
        mBall.update(mFPS);
        mBat.update(mFPS);
    }
    private void detectCollisions() {
        // Bottom
        if(mBall.getRect().bottom > mScreenY){
            mBall.reverseYVelocity();
            mLives--;

            if(mLives == 0){
                mPaused = true;
                startNewGame();
            }
            if(mBall.getRect().)
        }
// Top
        if(mBall.getRect().top < 0){
            mBall.reverseYVelocity();
        }

// Left
        if(mBall.getRect().left < 0){
            mBall.reverseXVelocity();
        }
// Right
        if(mBall.getRect().right > mScreenX){
            mBall.reverseXVelocity();
            mScore-=5;
        }
        // Has the bat hit the ball?
        // Has the ball hit the edge of the screen
        // Bottom
        // Top
        // Left
        // Right
        // Has the bat hit the ball?
        if(RectF.intersects(mBat.getRect(),mBall.getRect())) {
// Realisticish bounce
            mBall.batBounce(mBat.getRect());
            mBall.increaseVelocity();
            mScore+=10;
        }
    }
    // Handle all the screen touches
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
// This switch block replaces the if statement from the Sub Hunter game
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
// The player has put their finger on the screen
            case MotionEvent.ACTION_DOWN:
// If the game was paused unpause
                mPaused = false;
// Where did the touch happen
                if(motionEvent.getX() > mScreenX / 2){
// On the right hand side
                    mBat.setMovementState(mBat.RIGHT);
                }
                else{
// On the left hand side
                    mBat.setMovementState(mBat.LEFT);
                }
                break;
// The player lifted their finger from anywhere on screen.
// It is possible to create bugs by using multiple fingers.
            case MotionEvent.ACTION_UP:
// Stop the bat moving
                mBat.setMovementState(mBat.STOPPED);
                break;

        }
        return true;
    }
}

