package com.building.frienting001;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class Login_SignUp_EmailActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput, repasswordInput;

    private FirebaseAuth userAuth_user;
    private DatabaseReference userDBReference;
    private ProgressDialog dialog;
    private String phone;

    private FirebaseAnalytics firebaseAnalytics;
    Frequent frequent = new Frequent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        frequent.hideStatusBar(this); // 상태창 제거

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__sign_up_email);

        //회원정보DB 연결
        FirebaseApp userApp = FirebaseApp.getInstance("user"); // Retrieve secondary app.
        userAuth_user = FirebaseAuth.getInstance(userApp);
        userDBReference = FirebaseDatabase.getInstance(userApp).getReference().child("user");
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        /*Bundle params1 = new Bundle();
        params1.putString("UserUid", phone);
        params1.putLong("OpenTime", System.currentTimeMillis());
        firebaseAnalytics.logEvent("Login_SignUp_Activity", params1);*/

        //선언부
        emailInput = (EditText)findViewById(R.id.signup_email);
        passwordInput = (EditText)findViewById(R.id.signup_pw);
        repasswordInput = (EditText)findViewById(R.id.signup_pw2);

        Button completeSignUp = (Button)findViewById(R.id.signup_email_next_btn);
        Button cancelSignUp = (Button)findViewById(R.id.signup_email_back_btn);

        //이메일 생성시도
        completeSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= emailInput.getText().toString();
                String pw1 = passwordInput.getText().toString();
                String pw2 = repasswordInput.getText().toString();

                if (!pw1.equals(pw2)){
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    String regex1 = "^([0-9a-zA-Z_-]+)@([0-9a-zA-Z_-]+)(\\.[0-9a-zA-Z_-]+){1,2}$";
                    String regex2 = "^(?=.*[a-zA-Z])((?=.*\\d)|(?=.*\\W)).{6,20}$";
                    if (Pattern.matches(regex1, email)) { //정규표현식과 input 비교
                        if(Pattern.matches(regex2, pw1)){
                            dialog = new ProgressDialog(Login_SignUp_EmailActivity.this);
                            dialog.setMessage("회원정보 불러오는 중...");
                            dialog.show();
                            createUser(email, pw1);
                        }else {
                            Toast.makeText(getApplicationContext(), "잘못된 비밀번호 형식입니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "잘못된 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //회원가입 취소
        cancelSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frequent.showDialog("회원가입 취소", "회원 가입을 취소합니다.",
                        Login_SignUp_EmailActivity.this);
                startActivity(new Intent(Login_SignUp_EmailActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    //회원정보 생성
    private void createUser(final String email, final String password){
        OnCompleteListener listener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (!task.isSuccessful()) {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    frequent.showDialog("이메일 생성실패", "이미 존재하는 계정입니다.",
                            Login_SignUp_EmailActivity.this);
                }else{
                    Toast.makeText(getApplicationContext(), "가입 완료되었습니다. 계정과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    UserInfo userInfo = (UserInfo)getApplication();
                    userInfo.setEmail(email);
                    userDBReference.child(userAuth_user.getUid()).setValue(userInfo);
                    Intent intent = new Intent(Login_SignUp_EmailActivity.this, LoginActivity.class);
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    /*Bundle params1 = new Bundle();
                    params1.putString("UserUid", phone);
                    params1.putLong("makingTime", System.currentTimeMillis());
                    firebaseAnalytics.logEvent("Login_SignUp_EmailActivity", params1);*/

                    startActivity(intent);
                    finish();
                }
            }
        };
        //이 부분을 위로 올림. 생성에 실패하더라도 실행되기 때문에 취소해도 계정이 그대로 등록되는 문제발생
        userAuth_user.createUserWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }
}
