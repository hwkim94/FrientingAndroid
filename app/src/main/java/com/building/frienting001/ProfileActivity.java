package com.building.frienting001;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference userDBReference;
    private UserInfo userInfo;
    private UserInfo counter_userInfo;

    private TextView review;
    private ImageView photo;
    private TextView nickname;
    private TextView sex;
    private TextView birth;
    private TextView personality;
    private TextView job;
    private TextView place;
    private TextView introduction;

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        //상대 유저정보 받기
        final Intent receive = getIntent();
        userInfo = (UserInfo) receive.getSerializableExtra("userInfo");
        String uid = (String) receive.getSerializableExtra("uid");

        //액션바 설정
        LayoutInflater actionbarInflater =LayoutInflater.from(this);
        View customView = actionbarInflater.inflate(R.layout.actionbar, null);
        TextView actionbar_name = (TextView)customView.findViewById(R.id.actionbar_name);
        TextView actionbar_now = (TextView)customView.findViewById(R.id.actionbar_now_ting);
        ImageView actionbar_back = (ImageView) customView.findViewById(R.id.actionbar_back);
        LinearLayout actionbar_container = (LinearLayout)customView.findViewById(R.id.actionbar_ting_container);
        actionbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionbar_now.setText(""+userInfo.getTing());
        actionbar_name.setText("프로필");

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        Toolbar parent = (Toolbar)customView.getParent();
        parent.setContentInsetsAbsolute(0,0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //액션바 설정
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(3);

        //UserDB연결
        FirebaseApp userApp = FirebaseApp.getInstance("user");
        userDBReference = FirebaseDatabase.getInstance(userApp).getReference().child("user");

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);


        Bundle params1 = new Bundle();
        params1.putString("UserUid", userInfo.getFirebaseUserUid());
        params1.putLong("OpenTime",System.currentTimeMillis());
        firebaseAnalytics.logEvent("ProfileActivity", params1);

        //선언부
        review = (TextView)findViewById(R.id.profile_review);
        photo = (ImageView)findViewById(R.id.profile_photo);
        nickname = (TextView)findViewById(R.id.profile_nickname);
        sex = (TextView)findViewById(R.id.profile_sex);
        birth = (TextView)findViewById(R.id.profile_birth);
        personality = (TextView)findViewById(R.id.profile_personaliy);
        job = (TextView)findViewById(R.id.profile_job);
        place = (TextView)findViewById(R.id.profile_place);
        introduction = (TextView)findViewById(R.id.profile_introduction);

        //상대방 정보 가져오기
        userDBReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                counter_userInfo = (UserInfo)dataSnapshot.getValue(UserInfo.class);

                //프로필 붙여넣기
                if(counter_userInfo.getImagePath().equals("")){}
                else {
                    Glide.with(ProfileActivity.this).load(counter_userInfo.getImagePath()).into(photo);}
                nickname.setText(counter_userInfo.getNickname());
                sex.setText(counter_userInfo.getSex());
                birth.setText(counter_userInfo.getBirth());
                personality.setText(counter_userInfo.getPersonality());
                job.setText(counter_userInfo.getJob());
                place.setText(counter_userInfo.getPlace());
                introduction.setText(counter_userInfo.getIntroduction());

                //리뷰보기
                review.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getReview(counter_userInfo.getReview());
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
*/
    }

    private void getReview(ReviewDialogItem item){

        String nicks = item.getNickname();
        String stars = item.getStar();
        String contents = item.getContents();

        List<String> nick_list = preprocessing(nicks);
        List<String> star_list = preprocessing(stars);
        List<String> content_list = preprocessing(contents);

        LayoutInflater inflater = getLayoutInflater();
        final View convertView = (View) inflater.inflate(R.layout.review_dialog_layout, null);

        ReviewDialogListAdapter adapter = new ReviewDialogListAdapter();
        for(int i= 0;i<nick_list.size(); i++){
            adapter.add(new ReviewDialogItem(nick_list.get(i), star_list.get(i), content_list.get(i)));
        }

        ListView listView = (ListView)convertView.findViewById(R.id.review_dialog_list);
        listView.setAdapter(adapter);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
        alertDialog.setView(convertView);
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.show();
    }

    private List<String> preprocessing(String s){
        List<String> list = new ArrayList<>();

        if(s != ""){
            String[] array = s.split("/");
            list = Arrays.asList(array);
        }
        return list;
    }

}
