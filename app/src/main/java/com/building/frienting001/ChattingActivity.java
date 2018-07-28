package com.building.frienting001;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//채팅화면
public class ChattingActivity extends AppCompatActivity {
    private ChildEventListener mChildEventListener;
    private DatabaseReference chattingDatabaseReference;
    private FirebaseDatabase chattingDatabase;
    private DatabaseReference userDBReference;
    private DatabaseReference recruitmentDBReference;

    private ListView chatting_message;
    private Button chatting_send;
    private Button chatting_setting;
    private EditText chatting_edit;
    private Button chatting_warning_ok;
    private LinearLayout chatting_warning;

    private UserInfo userInfo;
    private RecruitmentItem recruitmentItem;

    private List<ChattingMemberItem> chattingMember_lst;
    private List<String> chattingMember_uid_lst;

    private ChattingMessageAdapter chattingMessageAdapter;
    private InputMethodManager imm;

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
                Intent intent = new Intent(ChattingActivity.this, PaymentActivity.class);
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
        setContentView(R.layout.activity_chatting);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(3);

        //선언부
        chatting_message = (ListView)findViewById(R.id.chatting_message);
        chatting_setting =(Button)findViewById(R.id.chatting_setting);
        chatting_send = (Button)findViewById(R.id.chatting_send);
        chatting_edit = (EditText)findViewById(R.id.chatting_edit);
        chatting_warning = (LinearLayout)findViewById(R.id.chatting_warning);
        chatting_warning_ok = (Button)findViewById(R.id.chatting_warning_ok);
        final String chatting_name = userInfo.getName();

        List<ChattingMessageItem> chattingMessageItems = new ArrayList<>();
        chattingMessageAdapter = new ChattingMessageAdapter(this, R.layout.chatting_message_item, chattingMessageItems, userInfo.getFirebaseUserUid());
        chatting_message.setAdapter(chattingMessageAdapter);
        chatting_edit.setText("");
        chatting_send.setEnabled(false);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        //DB 연결
        FirebaseApp chattingApp = FirebaseApp.getInstance("chatting"); // Retrieve secondary app.
        chattingDatabase = FirebaseDatabase.getInstance(chattingApp); // Get the database for the other app.
        chattingDatabaseReference = chattingDatabase.getReference().child("message").child(recruitmentItem.getRecruitment_key());

        FirebaseApp AuthApp_user = FirebaseApp.getInstance("user"); // Retrieve secondary app.
        userDBReference = FirebaseDatabase.getInstance(AuthApp_user).getReference().child("user");

        FirebaseApp recruitmentApp = FirebaseApp.getInstance("recruitment");
        recruitmentDBReference = FirebaseDatabase.getInstance(recruitmentApp).getReference().child("recruitments");

        //채팅 경고 화면
        chatting_warning_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatting_warning.setVisibility(View.GONE);
                chatting_message.setVisibility(View.VISIBLE);
                chatting_send.setEnabled(true);
            }
        });

        //채팅방 열었을때 로그
        Bundle params1 = new Bundle();
        params1.putString("UserUid", userInfo.getFirebaseUserUid());
        params1.putString("RecruitmentUid", recruitmentItem.getRecruitment_key());
        params1.putLong("OpenTime", System.currentTimeMillis());
        firebaseAnalytics.logEvent("ChattingActivity", params1);

        //채팅방 멤버
        chattingMember_lst= new ArrayList<>();
        chattingMember_uid_lst = new ArrayList<>();
        String applicants = recruitmentItem.getApplicant_uid();
        List<String> applicant_lst= preprocessing(applicants);
        for(String str : applicant_lst){userDBReference.child(str).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserInfo applicant = (UserInfo)dataSnapshot.getValue(UserInfo.class);
                    chattingMember_lst.add(new ChattingMemberItem(applicant.getImagePath(), applicant.getNickname(), applicant.getIntroduction(), applicant));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });}
        chattingMember_lst.add(new ChattingMemberItem(userInfo.getImagePath(), userInfo.getNickname(), userInfo.getIntroduction(), userInfo));
        chattingMember_uid_lst.addAll(applicant_lst);
        chattingMember_uid_lst.add(userInfo.getFirebaseUserUid());

        //채팅설정
        chatting_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChattingActivity.this, ChattingMemberActivity.class);
                intent.putExtra("userInfo", userInfo);
                intent.putExtra("recruitment", recruitmentItem);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

            }
        });


        // Enable Send button when there's text to send
        chatting_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    chatting_send.setEnabled(true);
                } else {
                    chatting_send.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //메세지 보내기
        chatting_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click
                ChattingMessageItem chattingMessageItem = new ChattingMessageItem("", userInfo.getImagePath(), chatting_name, chatting_edit.getText().toString(), userInfo.getFirebaseUserUid());
                chattingDatabaseReference.push().setValue(chattingMessageItem);
                chatting_edit.setText("");

                Bundle params = new Bundle();
                params.putString("UserUid", userInfo.getFirebaseUserUid());
                params.putString("RecruitmentUid", recruitmentItem.getRecruitment_key());
                params.putLong("SendTime", System.currentTimeMillis());
                firebaseAnalytics.logEvent("ChattingActivity", params);

            }
        });

        //채팅내용이 추가됐을 경우
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChattingMessageItem chattingMessageItem = dataSnapshot.getValue(ChattingMessageItem.class);
                chattingMessageAdapter.add(chattingMessageItem);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        chattingDatabaseReference.addChildEventListener(mChildEventListener);
    }

    private List<String> preprocessing(String s){
        List<String> list = new ArrayList<>();

        if(s != ""){
            String[] array = s.split("/");
            list = Arrays.asList(array);
        }
        return list;
    }

    public void hide_keyboard3(View v){
        imm.hideSoftInputFromWindow(chatting_edit.getWindowToken(), 0);
    }


}
