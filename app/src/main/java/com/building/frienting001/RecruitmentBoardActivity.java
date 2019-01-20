package com.building.frienting001;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//모집공고 화면
public class RecruitmentBoardActivity extends AppCompatActivity {
    private RecruitmentItem recruitmentItem;
    private UserInfo userInfo;
    private ReviewDialogItem reviewDialogItem;
    private DatabaseReference recruitmentDBReference;
    private DatabaseReference userDBReference;

    private TextView title;
    private TextView nickname;
    private LinearLayout profile;
    private LinearLayout review;
    private ImageView photo;
    private RecyclerView hashtag;
    private TextView meetingDate;
    private TextView meetingTime;
    private TextView activity;
    private TextView place;
    private TextView text;
    private Button btn;
    private TextView detail;
    //private AdView mAdView;
    private int user_age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruitment_board);

        //상태창
        Frequent frequent = new Frequent();
        frequent.hideStatusBar(this);

        //액션바 설정
        LayoutInflater actionbarInflater = LayoutInflater.from(this);
        View customView = actionbarInflater.inflate(R.layout.actionbar, null);
        TextView actionbar_name = (TextView)customView.findViewById(R.id.n_title);
        final TextView actionbar_now = (TextView)customView.findViewById(R.id.n_ting);
        ImageView actionbar_back = (ImageView) customView.findViewById(R.id.actionbar_back);
        LinearLayout actionbar_container = (LinearLayout)customView.findViewById(R.id.actionbar_ting_container);
        actionbar_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 팅 충전 페이지 이동
                Intent intent = new Intent(RecruitmentBoardActivity.this, PaymentActivity.class);
                //intent.putExtra("userInfo", userInfo);
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
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        Toolbar parent = (Toolbar)customView.getParent();
        parent.setContentInsetsAbsolute(0,0);

        //액션바
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(3);

        //게시글 정보 가져오기
        Intent receive = getIntent();
        recruitmentItem = (RecruitmentItem)receive.getSerializableExtra("recruitment");

        //선언부
        title = (TextView)findViewById(R.id.recruitment_board_title);
        nickname = (TextView)findViewById(R.id.recruitment_board_nickname);
        profile = (LinearLayout)findViewById(R.id.recruitment_board_profile);
        review = (LinearLayout) findViewById(R.id.recruitment_board_review);
        photo = (ImageView)findViewById(R.id.recruitment_board_photo);
        hashtag = (RecyclerView)findViewById(R.id.recruitment_board_hashtag);
        meetingDate = (TextView)findViewById(R.id.recruitment_board_meetingDate);
        meetingTime = (TextView)findViewById(R.id.recruitment_board_meetingTime);
        activity = (TextView)findViewById(R.id.recruitment_board_activity);
        text = (TextView)findViewById(R.id.recruitment_board_text);
        btn = (Button)findViewById(R.id.recruitment_board_btn);
        detail = (TextView)findViewById(R.id.recruitment_detail);
        place = (TextView)findViewById(R.id.recruitment_board_place);

        //게시판 DB 연결
        FirebaseApp recruitmentApp = FirebaseApp.getInstance("user");
        final FirebaseAuth user_auth = FirebaseAuth.getInstance(recruitmentApp);
        recruitmentDBReference = FirebaseDatabase.getInstance(recruitmentApp).getReference("recruitment");
        userDBReference = FirebaseDatabase.getInstance(recruitmentApp).getReference("user");

        userDBReference.child(user_auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInfo = dataSnapshot.getValue(UserInfo.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        actionbar_name.setText("개별 공고");
        userDBReference.child(user_auth.getCurrentUser().getUid()).child("ting").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer ting_temp = dataSnapshot.getValue(Integer.class);
                String ting_text = Integer.toString(ting_temp);
                actionbar_now.setText(ting_text);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        }); // 팅은 실시간으로 계속 확인해야 하기에 value event 리스너를 달아주고 지속적으로 체크

        //모집공고 내용들을 붙여넣어줘야 함
        title.setText(recruitmentItem.getTitle());
        nickname.setText(recruitmentItem.getNickname());
        /*review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });*/

        if(!recruitmentItem.getImagePath().equals("")){
            Glide.with(this).load(recruitmentItem.getImagePath()).into(photo);
        }

        List<String> searched_list = splitHashTag(recruitmentItem.getHashTag());
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
                //intent.putExtra("userInfo", userInfo);
                intent.putExtra("uid", uid);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        meetingDate.setText(recruitmentItem.getDateSearched());
        meetingTime.setText(recruitmentItem.getTimeSearched());
        activity.setText(recruitmentItem.getActivity());
        text.setText(recruitmentItem.getText());
        place.setText(recruitmentItem.getPlaceName());

        detail.setText("인원제한 : " + recruitmentItem.getDetail1());

        //user_age =  Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt((String)userInfo.getBirth().subSequence(0,4)) +1;
        user_age = 10;

        //매칭신청
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recruitmentItem.getWriter_uid().equals(user_auth.getCurrentUser().getUid())){
                    Toast.makeText(getApplicationContext(), "내가 작성한 글입니다.", Toast.LENGTH_SHORT).show();
                } else if(recruitmentItem.getApplicant_uid().contains(user_auth.getCurrentUser().getUid())){
                    Toast.makeText(getApplicationContext(), "이미 신청한 공고입니다.", Toast.LENGTH_SHORT).show();
                }

                /*else if (current > detail1 | recruitmentItem.getIs_finished()== true) {
                    Toast.makeText(getApplicationContext(), "모집이 마감되었습니다.", Toast.LENGTH_SHORT).show();
                }*/
                else {
                    for(int i = 0; i < userInfo.recruit_progress.size(); i++) {
                        if (strComp(recruitmentItem.getMeetingTime(), userInfo.recruit_progress.get(i).get(2)) == 1 ||
                                strComp(recruitmentItem.getFarewellTime(), userInfo.recruit_progress.get(i).get(1)) == -1) { // 겹치는 약속 없음
                        } else {
                            //겹치는 약속을 보여주기 -> i번째 recruit_process 그냥 보여주면 됨.
                            //정보들을 DB와 Storage에 저장
                            AlertDialog.Builder builder = new AlertDialog.Builder(RecruitmentBoardActivity.this);
                            builder.setTitle("해당 시간에 약속이 존재합니다.");
                            builder.setCancelable(true);
                            builder.create().show();
                            return; // 마무리
                        }
                        //다이얼로그로 확인하기
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecruitmentBoardActivity.this);
                        builder.setTitle("매칭 신청을 완료하시겠습니까?");
                        builder.setMessage("7팅과 만남보증금 3팅이 소비됩니다.");
                        builder.setCancelable(false);

                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (userInfo.ting < 10) {
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
                                            //intent.putExtra("userInfo", userInfo);
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
                                }
                                else {
                                    dialogInterface.cancel();

                                    /*if (current > detail1 | recruitmentItem.getIs_finished()== true) { // 이 팅 소비 도중 공고가 마감되었을 경우.
                                        Toast.makeText(getApplicationContext(), "모집이 마감되었습니다.", Toast.LENGTH_SHORT).show();
                                    }*/

                                    recruitmentItem.getApplicant_uid().add(user_auth.getCurrentUser().getUid());
                                    recruitmentDBReference.child(recruitmentItem.getRecruitment_key()).child("applicant_uid").setValue(recruitmentItem.getApplicant_uid());

                                    ArrayList<String> temp = new ArrayList<>();
                                    temp.add(recruitmentItem.getRecruitment_key());
                                    temp.add(recruitmentItem.getMeetingTime());
                                    temp.add(recruitmentItem.getFarewellTime());
                                    userInfo.recruit_progress.add(temp);
                                    userInfo.ting = userInfo.ting - 10;
                                    userDBReference.child(user_auth.getCurrentUser().getUid()).setValue(userInfo);

                                    /*if ((current - 1) == detail1) {
                                        recruitmentItem.setIs_finished(true);
                                    }*/ // 인원차면 모집공고 닫기

                                    Intent intent = new Intent(RecruitmentBoardActivity.this, NavigationActivity.class);
                                    intent.putExtra("user_id", user_auth.getCurrentUser().getUid());
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
                    }
                }
            }
        });
    }

    private List<String> splitHashTag(String s){
        List<String> list = new ArrayList<>();
        if(!s.equals("")){
            String[] array = s.split("#");
            for(int i = 1; i < array.length; i++){
                array[i] = "#" + array[i];
                list.add(array[i]);
            }
        }
        return list;
    }

    private int strComp(String first, String second){ // 날짜 대소 비교
        for(int i = 0; i < 5; i++){ // 5 -> y, M, d, h, m
            if(first.charAt(i) > second.charAt(i)){
                return 1;
            } else if(first.charAt(i) < second.charAt(i)){
                return -1;
            }
        }
        return 0;
    }
    /*private void getReview(ReviewDialogItem item){

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
    }*/
}
//구글 addmob
        /*MobileAds.initialize(this, "ca-app-pub-7101905843238574~8811716481");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {// Code to be executed when an ad finishes loading.}
            @Override
            public void onAdFailedToLoad(int errorCode) {// Code to be executed when an ad request fails.}
            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
            @Override
            public void onAdLeftApplication() {// Code to be executed when the user has left the app.}
            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });*/
