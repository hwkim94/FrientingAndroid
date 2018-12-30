package com.building.frienting001;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

//환경설정 세부 화면
public class SettingContainerActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private ScrollView setting_scroll;
    private LinearLayout setting_conatainer0;

    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_container);
/*
        //상태창
        if (Build.VERSION.SDK_INT >=21) {
            Window window = getWindow();
            Drawable background = ResourcesCompat.getDrawable(getResources(),R.drawable.gradient,null);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setBackgroundDrawable(background);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.color_transparent));
        }

        //ID에 따라 어떤 Preference Fragment를 붙일지 결정
        Intent recieveData = SettingContainerActivity.this.getIntent();
        String setting = recieveData.getStringExtra("ID");
        userInfo = (UserInfo)recieveData.getSerializableExtra("userInfo");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_container);

        actionBar = getSupportActionBar();
        actionBar.setElevation(3);

        //선언부
        setting_conatainer0 = (LinearLayout)findViewById(R.id.setting_container0);
        setting_scroll = (ScrollView)findViewById(R.id.setting_scroll);

        switch (setting){
            case "setting_email" :
                makingActionbar("계정 설정");
                setting_scroll.setVisibility(View.VISIBLE);
                setting_conatainer0.setVisibility(View.GONE);

                SettingPreferenceFragment settingPreferenceFragment_email1 = new SettingPreferenceFragment();
                makingFragment(settingPreferenceFragment_email1,R.xml.pref_email1 ,R.id.setting_container1);

                SettingPreferenceFragment settingPreferenceFragment_email2 = new SettingPreferenceFragment();
                makingFragment(settingPreferenceFragment_email2, R.xml.pref_email2 , R.id.setting_container2);

                return;

            case "setting_push" :
                makingActionbar("푸쉬알림");
                setting_scroll.setVisibility(View.VISIBLE);
                setting_conatainer0.setVisibility(View.GONE);

                SettingPreferenceFragment settingPreferenceFragment_push1 = new SettingPreferenceFragment();
                makingFragment(settingPreferenceFragment_push1,R.xml.pref_push1 ,R.id.setting_container1);

                SettingPreferenceFragment settingPreferenceFragment_push2 = new SettingPreferenceFragment();
                makingFragment(settingPreferenceFragment_push2, R.xml.pref_push2 , R.id.setting_container2);

                SettingPreferenceFragment settingPreferenceFragment_push3 = new SettingPreferenceFragment();
                makingFragment(settingPreferenceFragment_push3, R.xml.pref_push3 , R.id.setting_container3);

                return;

            case "setting_friend" :
                makingActionbar("지인 차단");
                setting_scroll.setVisibility(View.VISIBLE);
                setting_conatainer0.setVisibility(View.GONE);

                SettingPreferenceFragment settingPreferenceFragment_friend1 = new SettingPreferenceFragment();
                makingFragment(settingPreferenceFragment_friend1, R.xml.pref_friend1 , R.id.setting_container1);
                return;

            case "setting_notice" :
                makingActionbar("공지사항");
                setting_scroll.setVisibility(View.GONE);
                setting_conatainer0.setVisibility(View.VISIBLE);

                SettingNotiFragment setting_notificationFragment = new SettingNotiFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.setting_container0, setting_notificationFragment);
                transaction.commit();
                return;

            case "setting_guide" :;return;
            case "setting_evaluate" :;return;
            case "setting_client" :
                makingActionbar("고객센터");
                setting_scroll.setVisibility(View.VISIBLE);
                setting_conatainer0.setVisibility(View.GONE);

                SettingPreferenceFragment settingPreferenceFragment_center1 = new SettingPreferenceFragment();
                makingFragment(settingPreferenceFragment_center1,R.xml.pref_center1 ,R.id.setting_container1);

                SettingPreferenceFragment settingPreferenceFragment_center2 = new SettingPreferenceFragment();
                makingFragment(settingPreferenceFragment_center2, R.xml.pref_center2 , R.id.setting_container2);

                SettingPreferenceFragment settingPreferenceFragment_center3 = new SettingPreferenceFragment();
                makingFragment(settingPreferenceFragment_center3, R.xml.pref_center3 , R.id.setting_container3);
                return;
        }
    }

    //Fragment를 붙여줌
    public void makingFragment(Fragment fragment, int xml_ID, int container_ID){

        Bundle args = new Bundle();
        args.putInt("xml", xml_ID);
        args.putInt("container", container_ID);
        fragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(container_ID, fragment);
        transaction.commit();
    }

    //액션바 설정
    public void makingActionbar(String name){
        LayoutInflater actionbarInflater =LayoutInflater.from(this);
        View customView = actionbarInflater.inflate(R.layout.actionbar, null);
        TextView actionbar_name = (TextView)customView.findViewById(R.id.actionbar_name);
        TextView actionbar_now = (TextView)customView.findViewById(R.id.actionbar_now_ting);
        ImageView actionbar_back = (ImageView) customView.findViewById(R.id.actionbar_back);
        LinearLayout actionbar_container = (LinearLayout)customView.findViewById(R.id.actionbar_ting_container);
        actionbar_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingContainerActivity.this, PaymentActivity.class);
                intent.putExtra("userInfo", userInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        actionbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionbar_now.setText(""+userInfo.getTing());
        actionbar_name.setText(name);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        Toolbar parent = (Toolbar)customView.getParent();
        parent.setContentInsetsAbsolute(0,0);
*/
    }
}
