package com.github.aiden.focusview;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FocusViewZh focusViewZh = findViewById(R.id.focusview);
        focusViewZh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(focus){
                    focus = false;
                    focusViewZh.setProgressive();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            focusViewZh.setFocus(false);
                        }
                    },3000);
                }else{
                    focus = true;
                    focusViewZh.setFocus(true);
                }
                Log.e(TAG, "onClick: "+focus );
            }
        });
    }
    private boolean focus = false;
    private static final String TAG = "focusview:  ";
}
