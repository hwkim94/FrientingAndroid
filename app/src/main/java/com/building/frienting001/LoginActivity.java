package com.building.frienting001;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    Frequent frequent = new Frequent();

    private EditText eMailInput, passWordInput;

    private FirebaseAuth userAuth;
    private FirebaseUser userInfo;
    private DatabaseReference userDBRef;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAnalytics firebaseAnalytics;

    private String email,pw;
    private ProgressDialog dialog;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        frequent.hideStatusBar(this); // 상태창 제거

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp AuthApp_user = FirebaseApp.getInstance("user");
        userAuth = FirebaseAuth.getInstance(AuthApp_user);
        userDBRef = FirebaseDatabase.getInstance(AuthApp_user).getReference().child("user");
        // user DB 연동
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //선언부
        eMailInput = (EditText)findViewById(R.id.eMailInput);
        passWordInput = (EditText)findViewById(R.id.passWordInput);
        Button login_btn = (Button)findViewById(R.id.login_btn);
        TextView signIn = (TextView)findViewById(R.id.making_email);

        RelativeLayout layout = (RelativeLayout)findViewById(R.id.login_layout);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    email = eMailInput.getText().toString().trim();
                    pw = passWordInput.getText().toString().trim();
                    loginUser(email, pw);
                }catch (Exception e){
                    frequent.showDialog("로그인 실패", "올바른 이메일과 비밀번호를 입력해주세요.",
                            LoginActivity.this);
                }
            }
        });

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                userInfo = userAuth.getCurrentUser();
                searchDB(userInfo.getUid());
                /*if (userInfo != null) { // User is signed in
                    if (userInfo.isEmailVerified()) {
                        // user_user is verified, so you can finish this activity or send user_user to activity which you want.
                        try { //이메일 인증이 되어있을 경우
                            searchDB(userInfo.getUid());
                        }catch (Exception e){
                            if(dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        // email is not verified, so just prompt the message to the user_user and restart this activity.
                        // NOTE: don't forget to log out the user_user.
                        frequent.showDialog("계정 인증 미완료", "로그인에 실패하였습니다.\n해당 이메일에서 인증을 완료하세요.",
                                LoginActivity.this);
                        userAuthentication.signOut();
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }
                else {}*/
            }
        };

        signIn.setOnClickListener(new View.OnClickListener() { //회원가입
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Login_SignUp_PhoneActivity.class));
                finish();
            }
        });

        //키보드 옵션
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide_keyboard();
            }
        });
    }

    private void loginUser(final String email, final String password){
        userAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            frequent.showDialog("로그인 실패", "로그인에 실패하였습니다.\n계정 및 비밀번호를 확인하세요."
                            ,LoginActivity.this);
                        }
                        else{ //로그인 성공
                            userInfo = userAuth.getCurrentUser();
                            userAuth.addAuthStateListener(authListener);
                        }
                    }
                });
    }

    //자동로그인
    @Override
    public void onStart() {
        super.onStart();
        if (authListener != null) {
            userAuth.addAuthStateListener(authListener);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            userAuth.removeAuthStateListener(authListener);
        }
    }

    private void searchDB(String searchedText){ //해당 uid의 userinfo 가져오기
        userDBRef.child(searchedText).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                intent.putExtra("userInfo", userInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

                Bundle params1 = new Bundle();
                params1.putString("UserUid", userInfo.getFirebaseUserUid());
                params1.putLong("LogInTime", System.currentTimeMillis());
                firebaseAnalytics.logEvent("LoginActivity", params1);

                /*if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }*/
                finish();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void hide_keyboard(){
        imm.hideSoftInputFromWindow(eMailInput.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(passWordInput.getWindowToken(), 0);
    }
}
