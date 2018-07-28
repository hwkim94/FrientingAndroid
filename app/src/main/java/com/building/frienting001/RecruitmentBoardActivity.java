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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

//모집공고 화면
public class RecruitmentBoardActivity extends AppCompatActivity {

    private RecruitmentItem recruitmentItem;
    private ReviewDialogItem reviewDialogItem;

    private DatabaseReference recruitmentDBReference;
    private DatabaseReference userDBReference;

    private TextView title;
    private TextView nickname;
    private LinearLayout profile;
    private LinearLayout review;
    private ImageView photo;
    private RecyclerView hashtag;
    private TextView helloDate;
    private TextView helloTime;
    private TextView activity;
    private TextView place;
    private TextView text;
    private Button btn;
    private TextView detail;

    private String applicant;
    private String ing_recruitment;
    private UserInfo userInfo;
    private String key;

    private AdView mAdView;
    private static final String TAG = "RecruitmentActivity";

    private int user_age;

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //상태창
        if (Build.VERSION.SDK_INT >=21) {
            Window window = getWindow();
            Drawable background = ResourcesCompat.getDrawable(getResources(),R.drawable.gradient,null);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setBackgroundDrawable(background);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.color_transparent));
        }

        //게시글 정보 가져오기
        Intent receive = getIntent();
        recruitmentItem = (RecruitmentItem) receive.getSerializableExtra("recruitment");
        userInfo = (UserInfo)receive.getSerializableExtra("userInfo");

        //상태창, 액션바 설정
        LayoutInflater actionbarInflater =LayoutInflater.from(this);
        View customView = actionbarInflater.inflate(R.layout.actionbar, null);
        TextView actionbar_name = (TextView)customView.findViewById(R.id.actionbar_name);
        TextView actionbar_now = (TextView)customView.findViewById(R.id.actionbar_now_ting);
        ImageView actionbar_back = (ImageView) customView.findViewById(R.id.actionbar_back);
        LinearLayout actionbar_container = (LinearLayout)customView.findViewById(R.id.actionbar_ting_container);
        actionbar_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecruitmentBoardActivity.this, PaymentActivity.class);
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
        actionbar_name.setText("모집공고");

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        Toolbar parent = (Toolbar)customView.getParent();
        parent.setContentInsetsAbsolute(0,0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruitment_board);

        //구글 addmob
        MobileAds.initialize(this, "ca-app-pub-7101905843238574~8811716481");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        //액션바
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(3);

        //게시판 DB 연결
        FirebaseApp recruitmentApp = FirebaseApp.getInstance("recruitment");
        recruitmentDBReference = FirebaseDatabase.getInstance(recruitmentApp).getReference().child("recruitments");

        final FirebaseApp userApp = FirebaseApp.getInstance("user");
        userDBReference = FirebaseDatabase.getInstance(userApp).getReference().child("user");

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle params1 = new Bundle();
        params1.putString("UserUid", userInfo.getFirebaseUserUid());
        params1.putString("RecruitmentUid", recruitmentItem.getRecruitment_key());
        params1.putLong("OpenTime", System.currentTimeMillis());
        firebaseAnalytics.logEvent("RecruitmentBoardActivity", params1);

        //선언부
        title = (TextView)findViewById(R.id.recruitment_board_title);
        nickname = (TextView)findViewById(R.id.recruitment_board_nickname);
        profile = (LinearLayout)findViewById(R.id.recruitment_board_profile);
        review = (LinearLayout) findViewById(R.id.recruitment_board_review);
        photo = (ImageView)findViewById(R.id.recruitment_board_photo);
        hashtag = (RecyclerView)findViewById(R.id.recruitment_board_hashtag);
        helloDate = (TextView)findViewById(R.id.recruitment_board_helloDate);
        helloTime = (TextView)findViewById(R.id.recruitment_board_helloTime);
        activity = (TextView)findViewById(R.id.recruitment_board_activity);
        text = (TextView)findViewById(R.id.recruitment_board_text);
        btn = (Button)findViewById(R.id.recruitment_board_btn);
        detail = (TextView)findViewById(R.id.recruitment_detail);
        place = (TextView)findViewById(R.id.recruitment_board_place);

        //모집공고 내용들을 붙여넣어줘야 함
        key = recruitmentItem.getRecruitment_key();
        applicant = recruitmentItem.getApplicant_uid();
        ing_recruitment = userInfo.getRecruiting();

        final int detail1 = recruitmentItem.getDetail1();
        final int detail2 = recruitmentItem.getDetail2();

        title.setText(recruitmentItem.getTitle());
        nickname.setText(recruitmentItem.getNickname());
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle params1 = new Bundle();
                params1.putString("UserUid", userInfo.getFirebaseUserUid());
                params1.putString("RecruitmentUid", recruitmentItem.getRecruitment_key());
                params1.putLong("CheckReviewTime", System.currentTimeMillis());
                firebaseAnalytics.logEvent("RecruitmentBoardActivity", params1);

                userDBReference.child(recruitmentItem.getWriter_uid()).child("review").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            reviewDialogItem = dataSnapshot.getValue(ReviewDialogItem.class);
                            getReview(reviewDialogItem);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
                //Toast.makeText(getApplicationContext(), recruitmentItem.getReview().getNickname().toString() , Toast.LENGTH_SHORT).show();
            }
        });

        if(recruitmentItem.getImagePath().equals("")){
        }else {Glide.with(this).load(recruitmentItem.getImagePath()).into(photo);}



        List<String> searched_list = preprocessing2(recruitmentItem.getHashTag());
        HashTagAdapter adapter = new HashTagAdapter(RecruitmentBoardActivity.this, searched_list);
        hashtag.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        hashtag.setLayoutManager(layoutManager);
        hashtag.setAdapter(adapter);


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uid = recruitmentItem.getWriter_uid();

                Intent intent = new Intent(RecruitmentBoardActivity.this, ProfileActivity.class);
                intent.putExtra("userInfo", userInfo);
                intent.putExtra("uid", uid);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        helloDate.setText(recruitmentItem.getHelloDate());
        helloTime.setText(recruitmentItem.getHelloTime() + " ~ " + recruitmentItem.getGoodbyeTime());
        activity.setText(recruitmentItem.getActivity());
        text.setText(recruitmentItem.getText());
        place.setText(recruitmentItem.getCity1() +" " + recruitmentItem.getCity2() + " " + recruitmentItem.getCity3());

        detail.setText("인원제한 : " + detail1 + "    " + "나이상한 : " + detail2);

        user_age =  Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt((String)userInfo.getBirth().subSequence(0,4)) +1;


        final List<String > applicant_uid= preprocessing(recruitmentItem.getApplicant_uid());
        final int current = applicant_uid.size() ; //맨 앞에 ""가 포함되어 있으므로

        //매칭신청
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recruitmentItem.getWriter_uid().equals(userInfo.getFirebaseUserUid())){
                    Toast.makeText(getApplicationContext(), "내가 작성한 글입니다.", Toast.LENGTH_SHORT).show();
                    return;
                } else if(applicant_uid.contains(userInfo.getFirebaseUserUid())){
                    Toast.makeText(getApplicationContext(), "이미 신청한 공고입니다.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (user_age > detail2) {
                    Toast.makeText(getApplicationContext(), "나이 상한을 넘으셨습니다.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (current > detail1 | recruitmentItem.getIs_finished()== true) {
                    Toast.makeText(getApplicationContext(), "모집이 마감되었습니다.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (checkingTime()) {
                        //다이얼로그로 확인하기
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecruitmentBoardActivity.this);
                        builder.setTitle("매칭 신청을 완료하시겠습니까?");
                        builder.setMessage("7팅과 만남보증금 3팅이 소비됩니다.");
                        builder.setCancelable(false);

                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (userInfo.getTing() < 10) {
                                    dialogInterface.cancel();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RecruitmentBoardActivity.this);
                                    builder.setTitle("팅이 부족합니다.");
                                    builder.setMessage("팅을 충전하러 가시겠습니까?");
                                    builder.setCancelable(false);

                                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                            Intent intent = new Intent(RecruitmentBoardActivity.this, PaymentActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            intent.putExtra("userInfo", userInfo);
                                            startActivity(intent);


                                        }
                                    });
                                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    builder.create().show();
                                } else {

                                    dialogInterface.cancel();
                                    userInfo.setTing(userInfo.getTing() - 10);

                                    if (applicant.equals("")) {
                                        applicant = userInfo.getFirebaseUserUid();
                                    } else {
                                        applicant = applicant + "/" + userInfo.getFirebaseUserUid();
                                    }

                                    if (ing_recruitment.equals("")) {
                                        ing_recruitment = recruitmentItem.getRecruitment_key();
                                    } else {
                                        ing_recruitment = ing_recruitment + "/" + recruitmentItem.getRecruitment_key();
                                    }

                                    if ((current - 1) == detail1) {
                                        recruitmentItem.setIs_finished(true);
                                    }
                                    recruitmentItem.setApplicant_uid(applicant);
                                    recruitmentDBReference.child(key).setValue(recruitmentItem);
                                    userInfo.setRecruiting(ing_recruitment);

                                    String u_fromto = makingFromTo(recruitmentItem.getHelloDate(), recruitmentItem.getHelloTime(), recruitmentItem.getGoodbyeTime());
                                    String fromto = userInfo.getPromiseTime();
                                    if(fromto.equals("")){fromto = u_fromto;}
                                    else{fromto = fromto + "/" + u_fromto;}
                                    userInfo.setPromiseTime(fromto);

                                    userDBReference.child(userInfo.getFirebaseUserUid()).setValue(userInfo);

                                    Intent intent = new Intent(RecruitmentBoardActivity.this, NavigationActivity.class);
                                    intent.putExtra("userInfo", userInfo);

                                    Bundle params1 = new Bundle();
                                    params1.putString("UserUid", userInfo.getFirebaseUserUid());
                                    params1.putString("RecruitmentUid", recruitmentItem.getRecruitment_key());
                                    params1.putLong("MatchingTime", System.currentTimeMillis());
                                    firebaseAnalytics.logEvent("RecruitmentBoardActivity", params1);

                                    startActivity(intent);
                                    finish();

                                }
                            }
                        });

                        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.create().show();
                    }else{
                        //겹치는 약속을 보여주기
                        //정보들을 DB와 Storage에 저장
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecruitmentBoardActivity.this);
                        builder.setTitle("해당 시간에 약속이 존재합니다.");
                        builder.setCancelable(false);

                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        builder.create().show();

                    }
                }
            }
        });
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

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecruitmentBoardActivity.this);
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

    private List<String> preprocessing2(String s){
        List<String> list = new ArrayList<>();

        if(s != ""){
            String[] array = s.split(" ");
            list = Arrays.asList(array);
        }
        return list;
    }


    private boolean checkingTime(){
        String time = userInfo.getPromiseTime();

        if(time.equals("")){return true;}

        String w_from = "";
        String w_to = "";

        String s_helloDate = recruitmentItem.getHelloDate();
        String[] date = s_helloDate.split("/");
        w_from = date[0] + date[1] + date[2];
        w_to = date[0] + date[1] + date[2];

        String s_helloTime = recruitmentItem.getHelloTime();
        String[] hello = s_helloTime.split(" ");

        int part1 = 0;
        if(!hello[0].equals("AM")){part1 = 12;}
        if(Integer.parseInt(hello[1]) + part1 <10){
            w_from = w_from + "0"+ String.valueOf(Integer.parseInt(hello[1]) + part1) + hello[3];
        }else {
            w_from = w_from + String.valueOf(Integer.parseInt(hello[1]) + part1) + hello[3];
        }

        String s_goodbyeTime = recruitmentItem.getGoodbyeTime();
        String[] bye = s_goodbyeTime.split(" ");
        int part2 = 0;
        if(!bye[0].equals("AM")){part2 = 12;}
        if(Integer.parseInt(bye[1]) + part2 <10){
            w_to = w_to + "0"+ String.valueOf(Integer.parseInt(bye[1]) + part2) + bye[3];
        }else {
            w_to = w_to + String.valueOf(Integer.parseInt(bye[1]) + part2) + bye[3];
        }

        String[] time_lst = time.split("/");
        List<String> time_array = Arrays.asList(time_lst);

        for (String str : time_array){
            String[] from_to = str.split("~");
            int from = Integer.parseInt(from_to[0]);
            int to = Integer.parseInt(from_to[1]);

            Log.d("11111111111111111111111", w_to);
            Log.d("11111111111111111111111", w_from);
            Log.d("11111111111111111111111",""+ to);
            Log.d("11111111111111111111111", ""+from);

            if((to > Integer.parseInt(w_to) & from <  Integer.parseInt(w_to)) |  ((to > Integer.parseInt(w_from) & from <  Integer.parseInt(w_from)))){
                return false;
            }
        }
        return true;
    }

    private String makingFromTo(String Date, String h_time, String g_time){
        String w_from = "";
        String w_to = "";

        String[] date = Date.split("/");
        w_from = date[0] + date[1] + date[2];
        w_to = date[0] + date[1] + date[2];

        String[] hello = h_time.split(" ");
        int part1 = 0;
        if(!hello[0].equals("AM")){part1 = 12;}

        if(Integer.parseInt(hello[1]) + part1 <10){
            w_from = w_from + "0"+ String.valueOf(Integer.parseInt(hello[1]) + part1) + hello[3];
        }else {
            w_from = w_from + String.valueOf(Integer.parseInt(hello[1]) + part1) + hello[3];
        }

        String[] bye = g_time.split(" ");
        int part2 = 0;
        if(!bye[0].equals("AM")){part2 = 12;}

        if(Integer.parseInt(bye[1]) + part2 <10){
            w_to = w_to + "0"+ String.valueOf(Integer.parseInt(bye[1]) + part2) + bye[3];
        }else {
            w_to = w_to + String.valueOf(Integer.parseInt(bye[1]) + part2) + bye[3];
        }

        return w_from + "~" + w_to;
    }



}
