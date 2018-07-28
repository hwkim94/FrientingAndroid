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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ChattingMemberActivity extends AppCompatActivity {

    private DatabaseReference userDBReference;
    private DatabaseReference recruitmentDBReference;

    private UserInfo userInfo;
    private RecruitmentItem recruitmentItem;

    private List<ChattingMemberItem> chattingMember_lst;
    private List<String> chattingMember_uid_lst;

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

        //정보 받아오기
        Intent receive = getIntent();
        recruitmentItem = (RecruitmentItem)receive.getSerializableExtra("recruitment");
        userInfo = (UserInfo)receive.getSerializableExtra("userInfo");

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
                Intent intent = new Intent(ChattingMemberActivity.this, PaymentActivity.class);
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
        actionbar_name.setText(recruitmentItem.getTitle());

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        Toolbar parent = (Toolbar)customView.getParent();
        parent.setContentInsetsAbsolute(0,0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_member);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(3);

        //선언부
        Button btn1 = (Button)findViewById(R.id.chatting_setting_item_btn1);
        Button btn2 = (Button)findViewById(R.id.chatting_setting_item_btn2);
        Button btn3 = (Button)findViewById(R.id.chatting_setting_item_btn3);

        //DB연결
        FirebaseApp AuthApp_user = FirebaseApp.getInstance("user"); // Retrieve secondary app.
        userDBReference = FirebaseDatabase.getInstance(AuthApp_user).getReference().child("user");

        FirebaseApp recruitmentApp = FirebaseApp.getInstance("recruitment");
        recruitmentDBReference = FirebaseDatabase.getInstance(recruitmentApp).getReference().child("recruitments");

        //마이 화면 열었을때 로그
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle params1 = new Bundle();
        params1.putString("UserUid", userInfo.getFirebaseUserUid());
        params1.putString("RecruitmentUid", recruitmentItem.getRecruitment_key());
        params1.putLong("OpenTime", System.currentTimeMillis());
        firebaseAnalytics.logEvent("ChattingMemberActivity", params1);

        //만남인증 기능 - 나중에
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        //모집종료
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recruitmentItem.getWriter_uid().equals(userInfo.getFirebaseUserUid())) {
                    //자신이 방장일 경우
                    makingDialog("모집 종료", "정말로 모집을 종료하시겠습니까?", 0);
                }else{
                    //자신이 방장이 아닌 경우
                    makingDialog("모집 종료", "모집 종료는 방장의 권한입니다.", 1);
                }

                Bundle params1 = new Bundle();
                params1.putSerializable("BtnName", "EndBtn");
                params1.putString("UserUid", userInfo.getFirebaseUserUid());
                params1.putString("RecruitmentUid", recruitmentItem.getRecruitment_key());
                params1.putLong("EndBtnTime", System.currentTimeMillis());
                firebaseAnalytics.logEvent("ChattingMemberActivity", params1);
            }
        });

        //나가기
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recruitmentItem.getWriter_uid().equals(userInfo.getFirebaseUserUid())){
                    //자신이 방장일 경우
                    makingDialog("나가기", "방장은 나가실 수 없습니다.\n모집을 종료해주시기 바랍니다.", 2);
                }else {
                    //자신이 방장이 아닌 경우
                    makingDialog("나가기", "정말로 나가시겠습니까?", 3);
                }

                Bundle params1 = new Bundle();
                params1.putSerializable("BtnName", "QuitBtn");
                params1.putString("UserUid", userInfo.getFirebaseUserUid());
                params1.putString("RecruitmentUid", recruitmentItem.getRecruitment_key());
                params1.putLong("QuitBtnTime", System.currentTimeMillis());
                firebaseAnalytics.logEvent("ChattingMemberActivity", params1);
            }
        });


        //채팅방 멤버
        chattingMember_lst= new ArrayList<>();
        chattingMember_lst.add(new ChattingMemberItem(userInfo.getImagePath(), userInfo.getNickname(), userInfo.getIntroduction(), userInfo));
        chattingMember_uid_lst = new ArrayList<>();
        String applicants = recruitmentItem.getApplicant_uid();
        List<String> applicant_lst= preprocessing(applicants);
        chattingMember_uid_lst.addAll(applicant_lst);
        chattingMember_uid_lst.add(userInfo.getFirebaseUserUid());

        ListView listView = (ListView)findViewById(R.id.chatting_setting_dialog_list);
        final ChattingMemberAdapter adapter = new ChattingMemberAdapter();
        adapter.add(new ChattingMemberItem(userInfo.getImagePath(), userInfo.getNickname(), userInfo.getIntroduction(), userInfo));

        for(String str : applicant_lst){
            userDBReference.child(str).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserInfo applicant = (UserInfo)dataSnapshot.getValue(UserInfo.class);
                    adapter.additem(new ChattingMemberItem(applicant.getImagePath(), applicant.getNickname(), applicant.getIntroduction(), applicant));

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        adapter.setUserInfo(userInfo);
        adapter.setRecruitmentItem(recruitmentItem);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String uid = chattingMember_uid_lst.get(position);

                Intent intent = new Intent(ChattingMemberActivity.this, ProfileActivity.class);
                intent.putExtra("userInfo", userInfo);
                intent.putExtra("uid", uid);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    //다이얼로그
    public void makingDialog(String title, String message, final int flag){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChattingMemberActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        //builder.setIcon(R.mipmap.ic_launcher);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (flag){
                    case 0 :
                        if (recruitmentItem.getIs_finished() ==false) {
                            recruitmentItem.setIs_finished(true);
                            recruitmentDBReference.child(recruitmentItem.getRecruitment_key()).setValue(recruitmentItem);
                            Toast.makeText(getApplicationContext(), "모집이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        }else{ Toast.makeText(getApplicationContext(), "이미 모집이 완료된 공고입니다.", Toast.LENGTH_SHORT).show();}
                        break;

                    case 3 :
                        List<String> recruitment_lst = preprocessing(userInfo.getRecruiting());
                        String temp1 = "";
                        List<String> applicant_lst= preprocessing(recruitmentItem.getApplicant_uid());
                        String temp2 = "";

                        for (String str : recruitment_lst) {
                            if (!str.equals(recruitmentItem.getRecruitment_key())) {
                                if (temp1.equals("")){temp1 = str;}
                                else{temp1 = temp1 +"/"+str;}
                            }
                        }

                        for (String str : applicant_lst) {
                            if (!str.equals(userInfo.getFirebaseUserUid())) {
                                if (temp2.equals("")){temp2 = str;}
                                else{temp2 = temp2 +"/"+str;}
                            }
                        }

                        userInfo.setRecruiting(temp1);
                        recruitmentItem.setApplicant_uid(temp2);
                        userDBReference.child(userInfo.getFirebaseUserUid()).setValue(userInfo);
                        recruitmentDBReference.child(recruitmentItem.getRecruitment_key()).setValue(recruitmentItem);

                        //이제 유저 정보를 다시 뒤로 보내줘야함
                        //!!!!!!!!!!!!!!!!!!!!!!!!!
                        //근데 이상하게 됨;;
                        break;
                }
                dialogInterface.cancel();
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

    //전처리
    private List<String> preprocessing(String s){
        List<String> list = new ArrayList<>();

        if(!s.equals("")){
            String[] array = s.split("/");
            list = Arrays.asList(array);
        }
        return list;
    }

}
