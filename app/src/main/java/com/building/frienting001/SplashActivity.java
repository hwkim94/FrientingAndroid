package com.building.frienting001;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

//로딩화면
public class SplashActivity extends Activity {
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //상태창 없애는 코드
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //채팅DB 생성 및 연결
        FirebaseOptions option1 = new FirebaseOptions.Builder()
                .setApplicationId("1:334273366672:android:3203f1eed40d8bdd") // Required for Analytics.
                .setApiKey("AIzaSyDcPnd4KaBacemcTfmKutLFlQ9Qt7D2IeI") // Required for Auth.
                .setDatabaseUrl("https://frientingchatting.firebaseio.com/")// Required for RTDB.
                .build();
        FirebaseApp.initializeApp(this, option1, "chatting");

        //유저DB 생성 및 연결
        FirebaseOptions option2 = new FirebaseOptions.Builder()
                .setApplicationId("1:917305792599:android:3203f1eed40d8bdd") // Required for Analytics.
                .setApiKey("AIzaSyBqYt42Rvpt13lmk9DFoEz1novOM3Hgh3s") // Required for Auth.
                .setDatabaseUrl("https://frientinguser.firebaseio.com/")// Required for RTDB.
                .build();
        FirebaseApp.initializeApp(this, option2, "user");
        //FirebaseApp.initializeApp(this);

        //게시판DB 생성 및 연결
        FirebaseOptions option3 = new FirebaseOptions.Builder()
                .setApplicationId("1:49528722600:android:3203f1eed40d8bdd") // Required for Analytics.
                .setApiKey("AIzaSyC8yU4KzITsvLkrruwuOB_c1HVLZUs8Di0") // Required for Auth.
                .setDatabaseUrl("https://frienting001.firebaseio.com/")// Required for RTDB.
                .build();
        FirebaseApp.initializeApp(this, option3, "recruitment");

        //로그DB 생성 및 연결
        FirebaseOptions option4 = new FirebaseOptions.Builder()
                .setApplicationId("1:495557640632:android:3203f1eed40d8bdd") // Required for Analytics.
                .setApiKey("AIzaSyDKQ3GoMVaBWc_t6Pze9ampWFcRgCd4JvM") // Required for Auth.
                .setDatabaseUrl("https://frientinglog.firebaseio.com/")// Required for RTDB.
                .build();
        FirebaseApp.initializeApp(this, option4, "log");

        // 이 화면은 앱 실행전에 잠깐 나오는 화면, 메세지를 받으면 꺼짐 따라서 5초뒤에 메세지를 보내야함
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Intent intent =  new Intent(SplashActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        };
        handler.sendEmptyMessageDelayed(0, 3000);
    }
}