package com.building.frienting001;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
public class NavigationActivity extends AppCompatActivity {
    private UserInfo userInfo;
    private FirebaseAnalytics firebaseAnalytics;

    private ActionBar actionBar;
    private FrameLayout navigation_container;
    private boolean start = true;
    private final int TOTAL_PERMISSION_CODE = 9000;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_main:
                    makingActionbar("메인");
                    MainFragment Mainfragment = new MainFragment();
                    makingFragment(Mainfragment);
                    return true;
                case R.id.navigation_bulletinboard:
                    makingActionbar("모집공고");
                    BulletinBoardFragment bulletinboardFragment = new BulletinBoardFragment();
                    makingFragment(bulletinboardFragment);
                    return true;
                case R.id.navigation_writing:
                    makingActionbar("모집공고 작성");
                    WritingFragment writingFragment = new WritingFragment();
                    makingFragment(writingFragment);
                    return true;
                case R.id.navigation_chatting:
                    makingActionbar("My");
                    ChattingFragment chattingFragment =new ChattingFragment();
                    makingFragment(chattingFragment);
                    return true;
                case R.id.navigation_setting:
                    makingActionbar("환경설정");
                    SettingFragment settingFragment = new SettingFragment();
                    makingFragment(settingFragment);
                    return true;
            }
            return false;
        }
    };
    //Fragment를 화면에 붙여줌
    public void makingFragment(Fragment fragment){
        Bundle bundle = new Bundle();
        if(fragment instanceof BulletinBoardFragment){
            String searchTextString;
            try {
                EditText editText = (EditText)findViewById(R.id.searchText_main);
                searchTextString = editText.getText().toString();
            }catch (Exception e){
                searchTextString = "";
            }
            bundle.putString("searchTextString", searchTextString);
            bundle.putBoolean("fromMain", true);
        }
        bundle.putSerializable("userInfo", userInfo);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.navigation_container, fragment);
        transaction.commit();

        Bundle params1 = new Bundle();
        params1.putString("UserUid", userInfo.getFirebaseUserUid());
        params1.putString("Fragment", fragment.toString());
        params1.putLong("ClickTime", System.currentTimeMillis());
        firebaseAnalytics.logEvent("NavigationActivity", params1);
    }

    //커스텀 액션바를 붙여줌
    public void makingActionbar(String name){
        LayoutInflater actionbarInflater =LayoutInflater.from(this);
        View customView = actionbarInflater.inflate(R.layout.actionbar, null);
        TextView actionbar_name = (TextView)customView.findViewById(R.id.actionbar_name);
        actionbar_name.setText(name);
        TextView actionbar_now = (TextView)customView.findViewById(R.id.actionbar_now_ting);
        actionbar_now.setText(""+userInfo.getTing());

        ImageView actionbar_back = (ImageView) customView.findViewById(R.id.actionbar_back);
        LinearLayout actionbar_container = (LinearLayout)customView.findViewById(R.id.actionbar_ting_container);
        actionbar_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, PaymentActivity.class);
                intent.putExtra("userInfo", userInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        Toolbar parent = (Toolbar)customView.getParent();
        parent.setContentInsetsAbsolute(0,0);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //유저정보 받기
        Intent receive = getIntent();
        userInfo = (UserInfo)receive.getSerializableExtra("userInfo");

        //액션바 설정
        makingActionbar("메인");

        //상태창
        if (Build.VERSION.SDK_INT >=21) {
            Window window = getWindow();
            Drawable background = ResourcesCompat.getDrawable(getResources(),R.drawable.gradient,null);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setBackgroundDrawable(background);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.color_transparent));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        actionBar = getSupportActionBar();
        actionBar.setElevation(3);

        //초기 메인화면 붙여줌
        navigation_container = (FrameLayout) findViewById(R.id.navigation_container);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        MainFragment mainFragment = new MainFragment();
        makingFragment(mainFragment);

        //권한체크
        if((ContextCompat.checkSelfPermission(this ,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                | (ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                | (ContextCompat.checkSelfPermission(this , Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
                | (ContextCompat.checkSelfPermission(this , Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                | (ContextCompat.checkSelfPermission(this , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                | (ContextCompat.checkSelfPermission(this , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            //앱 처음 실행시, 접근이 허가안됐을 때, 접근요청
            String[] need_permission = new String[]{Manifest.permission.CAMERA
                    , Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.READ_CONTACTS
                    , Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.INTERNET};

            ActivityCompat.requestPermissions(this, need_permission, TOTAL_PERMISSION_CODE);

        }else{
            //접근이 허가 됐을 때
            return;
        }
    }

    //어떤 권한이 거절되어있는지 확인
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == TOTAL_PERMISSION_CODE){

            if (grantResults.length>0){
                StringBuffer buffer = new StringBuffer();
                int count =0;
                for (int i =0; i<grantResults.length;i++){
                    if (grantResults[i] !=PackageManager.PERMISSION_GRANTED){
                        switch(permissions[i]){
                            case "android.permission.CAMERA" : permissions[i] = "CAMERA"; break;
                            case "android.permission.ACCESS_FINE_LOCATION" : permissions[i] = "ACCESS_FINE_LOCATION"; break;
                            case "android.permission.READ_CONTACTS" : permissions[i] = "READ_CONTACTS"; break;
                            case "android.permission.READ_EXTERNAL_STORAGE" : permissions[i] = "READ_EXTERNAL_STORAGE"; break;
                            case "android.permission.WRITE_EXTERNAL_STORAGE" : permissions[i] = "WRITE_EXTERNAL_STORAGE"; break;

                        }
                        buffer.append((i+1)+")"+permissions[i]+ "\n");
                        count ++;
                    }
                }

                if(count==0) return;//거절된 권한이 한개도 없다면 여기까지만 실행하라는 의미

                buffer.append("\n");
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

