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
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
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

    private RecruitmentItem recruitmentItem;

    private ChattingMessageAdapter chattingMessageAdapter;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //상태창
        Frequent frequent = new Frequent();
        frequent.hideStatusBar(this);

        //정보 받아오기
        Intent receive = getIntent();
        recruitmentItem = (RecruitmentItem)receive.getSerializableExtra("recruitment");

        //액션바 설정
        LayoutInflater actionbarInflater =LayoutInflater.from(this);
        View customView = actionbarInflater.inflate(R.layout.actionbar, null);
        TextView actionbar_name = (TextView)customView.findViewById(R.id.n_title);
        final TextView actionbar_now = (TextView)customView.findViewById(R.id.n_ting);
        ImageView actionbar_back = (ImageView) customView.findViewById(R.id.actionbar_back);
        LinearLayout actionbar_container = (LinearLayout)customView.findViewById(R.id.actionbar_ting_container);
        actionbar_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 팅 충전 페이지 이동
                Intent intent = new Intent(ChattingActivity.this, PaymentActivity.class);
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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        //DB 연결
        FirebaseApp chattingApp = FirebaseApp.getInstance("user");
        userDBReference = FirebaseDatabase.getInstance(chattingApp).getReference().child("user");
        recruitmentDBReference = FirebaseDatabase.getInstance(chattingApp).getReference().child("recruitment");
        chattingDatabaseReference = FirebaseDatabase.getInstance(chattingApp).getReference().child("chatting").child(recruitmentItem.getRecruitment_key());
        final FirebaseAuth user_auth = FirebaseAuth.getInstance(chattingApp);

        actionbar_name.setText("채팅");
        userDBReference.child(user_auth.getCurrentUser().getUid()).child("Ting").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer ting_temp = dataSnapshot.getValue(Integer.class);
                String ting_text = Integer.toString(ting_temp);
                actionbar_now.setText(ting_text);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        }); // 팅은 실시간으로 계속 확인해야 하기에 value event 리스너를 달아주고 지속적으로 체크

        //선언부
        chatting_message = (ListView)findViewById(R.id.chatting_message);
        chatting_setting =(Button)findViewById(R.id.chatting_setting);
        chatting_send = (Button)findViewById(R.id.chatting_send);
        chatting_edit = (EditText)findViewById(R.id.chatting_edit);
        chatting_warning = (LinearLayout)findViewById(R.id.chatting_warning);
        chatting_warning_ok = (Button)findViewById(R.id.chatting_warning_ok);

        List<ChattingMessageItem> chattingMessageItems = new ArrayList<>();
        chattingMessageAdapter = new ChattingMessageAdapter(this, R.layout.chatting_message_item, chattingMessageItems, user_auth.getCurrentUser().getUid());
        chatting_message.setAdapter(chattingMessageAdapter);
        chatting_edit.setText("");
        chatting_send.setEnabled(false);

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        //채팅 경고 화면
        chatting_warning_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatting_warning.setVisibility(View.GONE);
                chatting_message.setVisibility(View.VISIBLE);
                chatting_send.setEnabled(true);
            }
        });

        chatting_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChattingActivity.this, ChattingMemberActivity.class);
                intent.putExtra("recruitment", recruitmentItem.getRecruitment_key()); // 키값만 전달
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        chatting_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    chatting_send.setEnabled(true);
                } else {
                    chatting_send.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        //메세지 보내기
        chatting_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("myLog", "here1");
                ChattingMessageItem chattingMessageItem = new ChattingMessageItem();
                chattingMessageItem.setChatting_photo("");
                chattingMessageItem.setChatting_name("테스트용");
                chattingMessageItem.setChatting_text(chatting_edit.getText().toString());
                chattingMessageItem.setWriter_uid(user_auth.getCurrentUser().getUid());
                Log.d("myLog", "here");
                chattingDatabaseReference.push().setValue(chattingMessageItem);
                Log.d("myLog", "here2");
                chatting_edit.setText("");
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
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };
        chattingDatabaseReference.addChildEventListener(mChildEventListener);
    }

    public void hide_keyboard3(View v){
        imm.hideSoftInputFromWindow(chatting_edit.getWindowToken(), 0);
    }
}
