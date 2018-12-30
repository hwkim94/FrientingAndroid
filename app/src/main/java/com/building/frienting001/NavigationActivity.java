package com.building.frienting001;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
//import com.google.firebase.analytics.FirebaseAnalytics;

public class NavigationActivity extends AppCompatActivity {
    //private FirebaseAnalytics firebaseAnalytics;
    private TextView title, ting;
    private ImageView payment_button;
    private DatabaseReference user_db_ref;
    private MainFragment main_fragment;
    private BulletinBoardFragment bulletin_fragment;
    private WritingFragment write_fragment;
    private ChattingFragment chat_fragment;
    private SettingFragment setting_fragment;
    public String text_from_main;

    private final int TOTAL_PERMISSION_CODE = 9000;
    private BottomNavigationView.OnNavigationItemSelectedListener navigation_listener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_main:
                    title.setText("메인");
                    setFragment(main_fragment);
                    return true;
                case R.id.navigation_bulletinboard:
                    title.setText("모집공고");
                    setFragment(bulletin_fragment);
                    return true;
                case R.id.navigation_writing:
                    title.setText("모집공고 작성");
                    setFragment(write_fragment);
                    return true;
                case R.id.navigation_chatting:
                    title.setText("My");
                    setFragment(chat_fragment);
                    return true;
                case R.id.navigation_setting:
                    title.setText("환경설정");
                    setFragment(setting_fragment);
                    return true;
            }
            return false;
        }
    };

    //Fragment를 화면에 붙여줌
    public void setFragment(Fragment fragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.navigation_container, fragment);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final FirebaseApp user_app = FirebaseApp.getInstance("user");
        FirebaseAuth user_auth = FirebaseAuth.getInstance(user_app);
        user_db_ref = FirebaseDatabase.getInstance(user_app).getReference("user");

        // 커스텀 액션바 설정 및 활성화
        LayoutInflater inflater = LayoutInflater.from(this);
        View custom_actionbar = inflater.inflate(R.layout.actionbar, null);
        title = (TextView)custom_actionbar.findViewById(R.id.n_title);
        ting = (TextView)custom_actionbar.findViewById(R.id.n_ting);
        user_db_ref.child(user_auth.getCurrentUser().getUid()).child("Ting").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer ting_temp = dataSnapshot.getValue(Integer.class);
                String ting_text = Integer.toString(ting_temp);
                ting.setText(ting_text);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        }); // 팅은 실시간으로 계속 확인해야 하기에 value event 리스너를 달아주고 지속적으로 체크


        /*UserInfo information = new UserInfo();
        information.personality.add("abc");
        information.personality.add("abd");
        user_db_ref.child(user_auth.getCurrentUser().getUid()).setValue(information);*/


        /*user_db_ref.child(user_auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("myLog", "onDatachange");
                UserInfo shot = dataSnapshot.getValue(UserInfo.class);
                Log.d("myLog", shot.personality.get(1));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


        //ImageView actionbar_back = (ImageView) customView.findViewById(R.id.actionbar_back); 추후 앱 종료 버튼으로 연결
        payment_button = (ImageView)custom_actionbar.findViewById(R.id.actionbar_ting);
        payment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, PaymentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(custom_actionbar, params);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setElevation(3);
        Toolbar parent = (Toolbar)custom_actionbar.getParent();
        parent.setContentInsetsAbsolute(0,0);

        //상태창
        Frequent frequent = new Frequent();
        frequent.hideStatusBar(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        main_fragment = new MainFragment();
        bulletin_fragment = new BulletinBoardFragment();
        write_fragment = new WritingFragment();
        chat_fragment = new ChattingFragment();
        setting_fragment = new SettingFragment();

        //초기 메인화면 붙여줌
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(navigation_listener);
        BottomNavigationViewHelper.disableShiftMode(navigation); //이 부분이 없으면 네비게이션 메뉴들이 한쪽으로 밀린다
        navigation.setSelectedItemId(R.id.navigation_main);

        //권한체크 - 처음 설치할때
        String[] permissions = new String[]{
                Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET
        };

        for(int i = 0; i < permissions.length; i++){
            if(ContextCompat.checkSelfPermission(this, permissions[i]) == 0){ // 0 -> PackageManager.PERMISSON_GRANTED
                continue;
            }
            ActivityCompat.requestPermissions(this, permissions, TOTAL_PERMISSION_CODE);
            break;
        }
    }

    long FINISH_INTERVAL_TIME = 2000;
    long backPressedTime = 0;

    @Override
    public void onBackPressed(){
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(this, "한번 더 눌러주세요", Toast.LENGTH_SHORT).show();
        }
    }
    //어떤 권한이 거절되어있는지 확인
    @Override
    public void onRequestPermissionsResult(int request_code, @NonNull String[] permissions, @NonNull int[] grant_results) {
        if(request_code == TOTAL_PERMISSION_CODE){
            if (grant_results.length > 0){
                StringBuffer buffer = new StringBuffer();
                int count = 0;
                for (int i = 0; i < grant_results.length; i++){
                    if (grant_results[i] != 0){
                        switch(permissions[i]){
                            case "android.permission.CAMERA":
                                permissions[i] = "CAMERA";
                                break;
                            case "android.permission.ACCESS_FINE_LOCATION":
                                permissions[i] = "ACCESS_FINE_LOCATION";
                                break;
                            case "android.permission.READ_CONTACTS":
                                permissions[i] = "READ_CONTACTS";
                                break;
                            case "android.permission.READ_EXTERNAL_STORAGE":
                                permissions[i] = "READ_EXTERNAL_STORAGE";
                                break;
                            case "android.permission.WRITE_EXTERNAL_STORAGE":
                                permissions[i] = "WRITE_EXTERNAL_STORAGE";
                                break;
                        }
                        buffer.append((i+1) + ")" + permissions[i] + "\n");
                        count ++;
                    }
                }
                if(count == 0)
                    return;//거절된 권한이 한개도 없다면 여기까지만 실행하라는 의미

                buffer.append("앱 실행시 위의 권한이 필수적으로 필요합니다.\n");
                buffer.append("설정으로 이동하셔서 권한을 허락해주십시오.");
                //권한 이동 방식은 AlertDialog로 사용
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("권한 허용안내");
                builder.setMessage(buffer.toString());
                builder.setCancelable(false);
                builder.setPositiveButton("이동", new DialogInterface.OnClickListener() { //이동을 누르면 권한페이지로 넘어감
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
                builder.create().show();
            }
        }
    }
}
