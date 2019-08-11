package com.sehab.pranta.balloonblast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {
    TextView textView,textView2;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        textView = findViewById(R.id.Bscore);
        textView2 = findViewById(R.id.Mscore);
        button = findViewById(R.id.btnE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(EndActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });



        String output = getIntent().getStringExtra("MSG");
        textView.setText("Best Score:"+" "+output);
        String output2 = getIntent().getStringExtra("MSG2");
        textView2.setText("Your Score:"+" "+output2);

    /*   full.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               FullScreen();
           }
       });
  */

    } //oncreate ends

    private void  FullScreen(){
        ViewGroup rootLayout = (ViewGroup) findViewById(R.id.activity_end);
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
}
