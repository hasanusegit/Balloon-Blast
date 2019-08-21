package com.sehab.pranta.balloonblast;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sehab.pranta.balloonblast.utils.Balloon;
import com.sehab.pranta.balloonblast.utils.HighScoreHelper;
import com.sehab.pranta.balloonblast.utils.SoundHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements Balloon.BalloonListener {
    private static final int BALLOONS_PER_LEVEL = 10;
    private ViewGroup mContentView;
  private int[] mBalloonColors = new int[5];
  private int mNextColor,mScreenWidth,mScreenHeight;
    public static final int MIN_ANIMATION_DELAY=500;
    public static final int MAX_ANIMATION_DELAY=1500;
    public static final int MIN_ANIMATION_DURATION=1000;
    public static final int MAX_ANIMATION_DURATION=8000;
    private int HELP=2;
    private int mLevel,mScore,mPinsUsed;
    TextView mScoreDisplay,mLevelDisplay,helpPause;
    public static final int NUMBER_OF_PINS=5;
    private List<ImageView> mPinImages=new ArrayList<>();
    private List<Balloon> mBalloons = new ArrayList<>();
    private Button mGoButton , pauseButton;
    private boolean mPlaying;
    private boolean mGameStopped =true;
    private int mBalloonsPopped;
    private SoundHelper mSoundHelper;
    private boolean pause_flg = false; //pause button status
   //initialize class for pause button
    private Timer timer = new Timer();
    private Handler handler = new Handler();

    int black = 0xff000000;
    int  magenta  = 0xffff00ff;
    int yellow = 0xffffff00;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBalloonColors[0]= Color.argb(255,255,0,0);
    //    int  magenta  = 0xffff00ff;
        mBalloonColors[1]=magenta;
        mBalloonColors[2]= Color.argb(255,0,0,255);
    //    int yellow = 0xffffff00;
        mBalloonColors[3]=yellow;
     //   int black = 0xff000000;
        mBalloonColors[4]=black;
        getWindow().setBackgroundDrawableResource(R.drawable.back);
        mContentView = findViewById(R.id.activity_main);
         FullScreen();
        ViewTreeObserver viewTreeObserver =  mContentView.getViewTreeObserver(); //event listener
        if (viewTreeObserver.isAlive()){
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                  mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                  mScreenWidth = mContentView.getWidth();
                  mScreenHeight = mContentView.getHeight();
                }
            });
        }

        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 FullScreen();
            }
        });


        mScoreDisplay = findViewById(R.id.score_display);
      mLevelDisplay = findViewById(R.id.level_display);
        mPinImages.add((ImageView) findViewById(R.id.pushpin1));
        mPinImages.add((ImageView) findViewById(R.id.pushpin2));
        mPinImages.add((ImageView) findViewById(R.id.pushpin3));
        mPinImages.add((ImageView) findViewById(R.id.pushpin4));
        mPinImages.add((ImageView) findViewById(R.id.pushpin5));
        mGoButton = findViewById(R.id.go_button);
        pauseButton = findViewById(R.id.pauseBtn);
        helpPause = findViewById(R.id.chance);

      updateDisplay();

      mSoundHelper =new SoundHelper(this);
      mSoundHelper.prepareMusicPlayer(this);



    } //oncreate ends

    private void  FullScreen(){
        ViewGroup rootLayout = (ViewGroup) findViewById(R.id.activity_main);
        rootLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

    }

    @Override
    protected void onResume() {
        super.onResume();
        FullScreen();
    }
    //this file updated today

    private void startGame(){
        getWindow().setBackgroundDrawableResource(R.drawable.back);
        HELP=2;
        String str = Integer.toString(HELP);
        helpPause.setText("("+str+")");
        FullScreen();
        mScore =0;
        mLevel =0;
        mPinsUsed = 0;
        for (ImageView pin : mPinImages){
              pin.setImageResource(R.drawable.pin);
        }
        mGameStopped = false;
        startLevel();
        mSoundHelper.playMusic();
    }
    private void startLevel(){
        mLevel++;
        if (mLevel>4 && mLevel<10){
            getWindow().setBackgroundDrawableResource(R.drawable.modern_background);
        }
        if (mLevel>=10){
            getWindow().setBackgroundDrawableResource(R.drawable.back_final);
        }
        updateDisplay();
        BalloonLauncher launcher = new BalloonLauncher();
        launcher.execute(mLevel);
        mPlaying = true;
        mBalloonsPopped = 0;
        mGoButton.setText("Stop Game");
    }


    private void finishLevel(){
        Toast.makeText(this,String.format("Finished Level %d",mLevel),Toast.LENGTH_SHORT).show();
        mPlaying=false;
        mGoButton.setText(String.format("Start Level %d",mLevel+1));
    }


    public void goButtonClickHandler(View view) {
        if (mPlaying){
            gameOver(false);
        }else if (mGameStopped){
            startGame();
        }else {
           startLevel();
        }

    }

    @Override
    public void popBalloon(Balloon balloon, boolean userTouch) {
        mBalloonsPopped++;
        mSoundHelper.playSound();
        mContentView.removeView(balloon);
        mBalloons.remove(balloon);
     //   int keepTrack=mBalloonsPopped;
        if(userTouch){
           mScore++;
        }
        else {

            mPinsUsed++;
            if(mPinsUsed <= mPinImages.size()){
                mPinImages.get(mPinsUsed - 1).setImageResource(R.drawable.pin_off);
            }
            if (mPinsUsed==NUMBER_OF_PINS){
                gameOver(true);
                return;
            }else {
                Toast.makeText(this,"missed one!",Toast.LENGTH_SHORT).show();
            }
        }
   //    if (userTouch && mBalloons.get(4).getId()==4){
   //       mScore=mScore-2;

   //    }

      //  mBalloonsPopped%5==0 &&
        yy();

        updateDisplay();

        if(mBalloonsPopped == BALLOONS_PER_LEVEL){
            finishLevel();
        }
    }


    private void gameOver(boolean AllPinsUsed) {
        mSoundHelper.pauseMusic();
        for (Balloon balloon : mBalloons){
            mContentView.removeView(balloon);
            balloon.setPopped(true);
        }
        mBalloons.clear();
        mPlaying = false;
        mGameStopped = true;
        mGoButton.setText("Start Game");
       String st="",st2="";

        if(AllPinsUsed){
            int x = HighScoreHelper.getTopScore(this);
          st = Integer.toString(x);

            if (HighScoreHelper.isTopScore(this,mScore)){
              HighScoreHelper.setTopScore(this,mScore);
              st = Integer.toString(mScore);

            }
            st2 = Integer.toString(mScore);
            Intent intent =new Intent(MainActivity.this,EndActivity.class);
           intent.putExtra("MSG",st);
            intent.putExtra("MSG2",st2);
             startActivity(intent);
        }

    }

    private void updateDisplay() {
       mScoreDisplay.setText(String.valueOf(mScore));
       mLevelDisplay.setText(String.valueOf(mLevel));
    }




    public void pausePlay(View view) {
       if (HELP!=-1) {
           HELP--;
       }
       mScore =mScore+10;

       String str = Integer.toString(HELP);
       helpPause.setText("("+str+")");
       if (HELP==-1){
           int keep = HELP;
           String str2 = Integer.toString(keep+1);
           helpPause.setText("("+str2+")");
       }
        if (HELP!=-1) {
            for (Balloon balloon : mBalloons) {
                mContentView.removeView(balloon);
                balloon.setPopped(true);
            }
            mBalloons.clear();
            finishLevel();
        }

    }


    private class BalloonLauncher extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected Void doInBackground(Integer... params) {

            if (params.length != 1) {
                throw new AssertionError(
                        "Expected 1 param for current level");
            }

            int level = params[0];
            int maxDelay = Math.max(MIN_ANIMATION_DELAY,
                    (MAX_ANIMATION_DELAY - ((level - 1) * 500)));
            int minDelay = maxDelay / 2;

            int balloonsLaunched = 0;
            while (mPlaying && balloonsLaunched < BALLOONS_PER_LEVEL) {

//              Get a random horizontal position for the next balloon
                Random random = new Random(new Date().getTime());
                int xPosition = random.nextInt(mScreenWidth - 200);
                publishProgress(xPosition);
                balloonsLaunched++;

//              Wait a random number of milliseconds before looping
                int delay = random.nextInt(minDelay) + minDelay;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int xPosition = values[0];
            launchBalloon(xPosition);
        }

    }

    private void launchBalloon(int x) {

        Balloon balloon = new Balloon(this, mBalloonColors[mNextColor], 150);
        mBalloons.add(balloon);

        if (mNextColor + 1 == mBalloonColors.length) {
            mNextColor = 0;
        } else {
            mNextColor++;
        }

//      Set balloon vertical position and dimensions, add to container
        balloon.setX(x);
        balloon.setY(mScreenHeight + balloon.getHeight());
        mContentView.addView(balloon);

//      Let 'er fly
        int duration = Math.max(MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION - (mLevel * 1000));
        balloon.releaseBalloon(mScreenHeight, duration);

    }

    public void yy(){
        Balloon b1 = new Balloon(this,mBalloonColors[4],150);
        for(int i=0;i<mBalloons.size();i++) {
            if (mBalloons.get(i) == b1) {
                mScore=mScore-2;
            }
        }
    }



}

//main activity updated