package com.building.frienting001;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

//리뷰화면
public class ReviewActivity extends AppCompatActivity {

    private TextView reviewName1, reviewName2;
    private ImageView reviewStar1, reviewStar2, reviewStar3, reviewStar4, reviewStar5;
    private TextView reviewResult;
    private RecyclerView reviewList;
    private StaggeredGridLayoutManager layoutManager;
    private Button reviewWriting;

    private DatabaseReference userDBReference;
    private int count = 0;

    private ActionBar actionBar;

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
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

        //유저정보 받기
        Intent receive = getIntent();
        final UserInfo userInfo = (UserInfo) receive.getSerializableExtra("userInfo");
        final UserInfo user = (UserInfo)receive.getSerializableExtra("user");
        final ReviewDialogItem reviewDialogItem= user.getReview();

        //액션바 설정
        LayoutInflater actionbarInflater =LayoutInflater.from(this);
        View customView = actionbarInflater.inflate(R.layout.actionbar, null);
        TextView actionbar_name = (TextView)customView.findViewById(R.id.actionbar_name);
        TextView actionbar_now = (TextView)customView.findViewById(R.id.actionbar_now_ting);
        ImageView actionbar_back = (ImageView) customView.findViewById(R.id.actionbar_back);
        LinearLayout actionbar_container = (LinearLayout)customView.findViewById(R.id.actionbar_ting_container);
        actionbar_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewActivity.this, PaymentActivity.class);
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
        actionbar_name.setText("리뷰 작성");

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        Toolbar parent = (Toolbar)customView.getParent();
        parent.setContentInsetsAbsolute(0,0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //액션바 설정
        actionBar = getSupportActionBar();
        actionBar.setElevation(3);

        //유저DB 연결
        FirebaseApp userApp = FirebaseApp.getInstance("user");
        userDBReference = FirebaseDatabase.getInstance(userApp).getReference().child("user");

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle params1 = new Bundle();
        params1.putString("UserUid", userInfo.getFirebaseUserUid());
        params1.putString("PartnerUid", user.getFirebaseUserUid());
        params1.putLong("OpenTime", System.currentTimeMillis());
        firebaseAnalytics.logEvent("ReviewActivity", params1);

        //선언부
        reviewName1 = (TextView)findViewById(R.id.reviewName1);
        reviewName2 = (TextView)findViewById(R.id.reviewName2);
        reviewStar1 = (ImageView)findViewById(R.id.reviewStar1);
        reviewStar2 = (ImageView)findViewById(R.id.reviewStar2);
        reviewStar3 = (ImageView)findViewById(R.id.reviewStar3);
        reviewStar4 = (ImageView)findViewById(R.id.reviewStar4);
        reviewStar5 = (ImageView)findViewById(R.id.reviewStar5);
        reviewResult = (TextView)findViewById(R.id.reviewResult);
        reviewList = (RecyclerView)findViewById(R.id.reviewList);
        reviewWriting = (Button)findViewById(R.id.reviewWriting);

        //이름text
        final String nick = user.getNickname() +"님";
        reviewName1.setText(nick +"의 리뷰를 작성해주세요!");
        reviewName2.setText(nick +"은 어떤 분이신가요?");

        //별
        View.OnClickListener listener1 = new View.OnClickListener(){

            boolean flag = false;

            @Override
            public void onClick(View v) {


                if(!flag){
                    v.setBackgroundColor(ContextCompat.getColor(ReviewActivity.this, R.color.frienting_yellow));
                    count ++;
                }else{
                    v.setBackgroundColor(ContextCompat.getColor(ReviewActivity.this, R.color.color_transparent));
                    count --;
                }
                flag = !flag;

            }
        };
        View.OnClickListener listener2 = new View.OnClickListener(){

            boolean flag = false;

            @Override
            public void onClick(View v) {


                if(!flag){
                    v.setBackgroundColor(ContextCompat.getColor(ReviewActivity.this, R.color.frienting_yellow));
                    count ++;
                }else{
                    v.setBackgroundColor(ContextCompat.getColor(ReviewActivity.this, R.color.color_transparent));
                    count --;
                }
                flag = !flag;

            }
        };
        View.OnClickListener listener3 = new View.OnClickListener(){

            boolean flag = false;

            @Override
            public void onClick(View v) {


                if(!flag){
                    v.setBackgroundColor(ContextCompat.getColor(ReviewActivity.this, R.color.frienting_yellow));
                    count ++;
                }else{
                    v.setBackgroundColor(ContextCompat.getColor(ReviewActivity.this, R.color.color_transparent));
                    count --;
                }
                flag = !flag;

            }
        };
        View.OnClickListener listener4 = new View.OnClickListener(){

            boolean flag = false;

            @Override
            public void onClick(View v) {


                if(!flag){
                    v.setBackgroundColor(ContextCompat.getColor(ReviewActivity.this, R.color.frienting_yellow));
                    count ++;
                }else{
                    v.setBackgroundColor(ContextCompat.getColor(ReviewActivity.this, R.color.color_transparent));
                    count --;
                }
                flag = !flag;

            }
        };
        View.OnClickListener listener5 = new View.OnClickListener(){

            boolean flag = false;

            @Override
            public void onClick(View v) {


                if(!flag){
                    v.setBackgroundColor(ContextCompat.getColor(ReviewActivity.this, R.color.frienting_yellow));
                    count ++;
                }else{
                    v.setBackgroundColor(ContextCompat.getColor(ReviewActivity.this, R.color.color_transparent));
                    count --;
                }
                flag = !flag;

            }
        };

        reviewStar1.setOnClickListener(listener1);
        reviewStar2.setOnClickListener(listener2);
        reviewStar3.setOnClickListener(listener3);
        reviewStar4.setOnClickListener(listener4);
        reviewStar5.setOnClickListener(listener5);


        //성격리스트 만들기
        final List<String> selected_review =new ArrayList<String>();
        final ArrayList<String> review_listitem = new ArrayList<>();
        final String[] review_lst = new String[]{"유머러스", "친철한", "사교적인", "사차원", "수다스런", "밝은", "조용한", "똑똑한", "감각적인", "철학있는", "재치있는", "사려깊은", "섬세한", "깔끔한", "편안한", "똘끼있는", "특이한", "솔직한", "겸손한", "믿음직한", "소심한", "순수한", "예의바른", "그저그런", "심심한", "과묵한", "위험한", "무서운"};

        for (String act: review_lst){
            review_listitem.add(act);
        }
        DialogListAdapter adapter;
        adapter = new DialogListAdapter(ReviewActivity.this ,review_listitem);
        adapter.setItemClick(new DialogListAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                String selected = review_lst[position];

                if (selected_review.isEmpty()) {
                    selected_review.add(selected);
                } else {
                    if (selected_review.contains(selected)) {
                        selected_review.remove(selected);
                    } else {
                        if(selected_review.size() <10){
                            selected_review.add(selected);
                        }else{
                            Toast.makeText(getApplicationContext(), "최대 10개까지 선택하실 수 있습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                String selected_string = "";
                for (String str : selected_review) {
                    selected_string = selected_string + str + " ";
                }
                reviewResult.setText(selected_string);
            }
        });

        reviewList.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        reviewList.setLayoutManager(layoutManager);
        reviewList.setAdapter(adapter);

        //리뷰 완료
        reviewWriting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String string_temp = "";

                for(String idx : selected_review){
                    string_temp =string_temp + idx +" ";
                }

                String w_nick = "";
                if(reviewDialogItem.getNickname().equals("")){w_nick =  userInfo.getNickname();}
                else{w_nick = reviewDialogItem.getNickname() + "/" + userInfo.getNickname();}


                String scount = "";
                if(reviewDialogItem.getStar().equals("")){scount = String.valueOf(count);}
                else{scount = reviewDialogItem.getStar() + "/" + String.valueOf(count);}

                String contents = "";
                if(reviewDialogItem.getContents().equals("")){contents =string_temp;}
                else{contents = reviewDialogItem.getContents() + "/" + string_temp;}

                userInfo.setTing(userInfo.getTing()+2);
                userDBReference.child(userInfo.getFirebaseUserUid()).setValue(userInfo);
                userDBReference.child(user.getFirebaseUserUid()).child("review").setValue(new ReviewDialogItem(w_nick, scount, contents));

                Bundle params1 = new Bundle();
                params1.putString("UserUid", userInfo.getFirebaseUserUid());
                params1.putString("PartnerUid", user.getFirebaseUserUid());
                params1.putString("Star", scount);
                params1.putLong("WritingTime",System.currentTimeMillis());
                firebaseAnalytics.logEvent("ReviewActivity", params1);

                //Intent intent = new Intent(ReviewActivity.this, NavigationActivity.class);
                //intent.putExtra("userInfo", userInfo);
                //startActivity(intent);
                finish();
            }
        });*/
    }

}
