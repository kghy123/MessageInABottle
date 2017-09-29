package com.kghy1234gmail.messagesinabottle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class IntroView extends SurfaceView implements SurfaceHolder.Callback{

    Context context;
    SurfaceHolder holder;

    int width, height;

    IntroThread thread;

    boolean introIsRun = true;

    int introTime = 5000;

    int wave1Pos = 0;
    int wave2Pos = 0;
    int bottlePos = 0;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            introIsRun = false;
        }
    };

    public IntroView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public IntroView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    void init(){
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {



    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        if(thread == null){
            width = getWidth();
            height = getHeight();

            thread = new IntroThread();
            thread.start();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    class IntroThread extends Thread{

        int fps = 45;                // 초당 fps장
        int frameTime = 1000/fps;   //1장당 걸리는 시간
        long loopTime;               //while문 한바퀴를 도는 시간
        long sleepTime;              //잠잘 시간
        long lastTime;              //이전 시간
        long currentTime;           //현재 시간

        int skippedFrame;           //스킵된 프레임 숫자

        Bitmap wave1;
        Bitmap wave2;
        Bitmap bottle;
        Bitmap sky;

        void createBitmap(){
            Bitmap img = null;
            img = BitmapFactory.decodeResource(getResources(), R.drawable.wave1);
            wave1 = Bitmap.createScaledBitmap(img, width, height/4, true);
            img.recycle(); img = null;


            img = BitmapFactory.decodeResource(getResources(), R.drawable.wave2);
            wave2 = Bitmap.createScaledBitmap(img, width, height/4, true);
            img.recycle(); img = null;


            img = BitmapFactory.decodeResource(getResources(), R.drawable.message_in_a_bottle);
            bottle = Bitmap.createScaledBitmap(img, width/24, height/8, true);
            img.recycle(); img = null;

            img = BitmapFactory.decodeResource(getResources(), R.drawable.sky);
            sky = Bitmap.createScaledBitmap(img, width, height, true);
            img.recycle(); img = null;

        }

        void removeResources(){
            wave1.recycle(); wave1 = null;
            wave2.recycle(); wave2 = null;
            bottle.recycle(); bottle = null;

            Log.d("Intro", "인트로 종료");
        }

        void moveAll(){
            wave1Pos -= 2;
            wave2Pos -= 4;

            bottlePos -= 3;

            if(wave1Pos <= -width) wave1Pos += width;
            if(wave2Pos <= -width) wave2Pos += width;


        }

        void adjustFPS(){
            currentTime = System.currentTimeMillis();
            loopTime = currentTime - lastTime;
            lastTime = currentTime;

            sleepTime = frameTime - loopTime;

            //fast
            if(sleepTime>0){

                try {Thread.sleep(sleepTime);} catch (InterruptedException e) {e.printStackTrace();}

            }

            //slow
            skippedFrame = 0;
            while(sleepTime<0 && skippedFrame<5){
                moveAll();

                sleepTime += frameTime;
                skippedFrame++;
            }
        }

        void drawAll(Canvas canvas){

            canvas.drawBitmap(sky, 0, 0, null);

            canvas.drawBitmap(wave2, wave2Pos, height - height/4 - height/64, null);
            canvas.drawBitmap(wave2, width + wave2Pos, height - height/4 - height/64, null);

            canvas.drawBitmap(bottle, bottlePos + width, height - height/4 + height/64, null);

            canvas.drawBitmap(wave1, wave1Pos, height - height/4, null);
            canvas.drawBitmap(wave1, width + wave1Pos, height - height/4, null);

        }

        @Override
        public void run() {

            createBitmap();

            Canvas canvas = null;

            lastTime = System.currentTimeMillis();

            handler.sendEmptyMessageDelayed(1, introTime);
            Log.d("Intro", "인트로 시작");

            while(introIsRun){

                canvas = holder.lockCanvas();

                try {
                    synchronized (holder){
                        moveAll();
                        adjustFPS();
                        drawAll(canvas);
                    }
                }finally {
                    if(canvas != null) holder.unlockCanvasAndPost(canvas);
                }

            }

            removeResources();

        }
    }

}
