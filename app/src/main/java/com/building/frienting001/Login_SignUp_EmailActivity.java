package com.building.frienting001;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
//import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class Login_SignUp_EmailActivity extends AppCompatActivity {
    private EditText email_input, password_input, password_check_input;
    private CheckBox cb1, cb2;
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    private FirebaseAuth user_auth;
    private ProgressDialog dialog;
    private String phone;
    private String sex;

    Frequent frequent = new Frequent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        frequent.hideStatusBar(this); // 상태창 제거

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__sign_up_email);

        //회원정보DB 연결
        FirebaseApp userApp = FirebaseApp.getInstance("user");
        user_auth = FirebaseAuth.getInstance(userApp);

        //선언부
        email_input = (EditText)findViewById(R.id.login_email);
        password_input = (EditText)findViewById(R.id.login_password);
        password_check_input = (EditText)findViewById(R.id.login_password2);
        cb1 = (CheckBox)findViewById(R.id.checkBox1);
        cb2 = (CheckBox)findViewById(R.id.checkBox2);

        onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch(buttonView.getId()){
                    case R.id.checkBox1:
                        if(isChecked){
                            cb1.setChecked(true);
                            cb1.setTypeface(Typeface.DEFAULT_BOLD);
                            cb2.setChecked(false);
                            cb2.setTypeface(Typeface.DEFAULT);
                            sex = "male";
                        }else{
                            cb1.setChecked(false);
                            cb1.setTypeface(Typeface.DEFAULT);
                            cb2.setChecked(true);
                            cb2.setTypeface(Typeface.DEFAULT_BOLD);
                            sex = "female";
                        }
                        break;

                    case R.id.checkBox2:
                        if(isChecked){
                            cb1.setChecked(false);
                            cb1.setTypeface(Typeface.DEFAULT);
                            cb2.setChecked(true);
                            cb2.setTypeface(Typeface.DEFAULT_BOLD);
                            sex = "female";
                        }else{
                            cb1.setChecked(true);
                            cb1.setTypeface(Typeface.DEFAULT_BOLD);
                            cb2.setChecked(false);
                            cb2.setTypeface(Typeface.DEFAULT);
                            sex = "male";
                        }
                        break;
                    }
            }
        };

        cb1.setOnCheckedChangeListener(onCheckedChangeListener);
        cb2.setOnCheckedChangeListener(onCheckedChangeListener);

        Button complete_button = (Button)findViewById(R.id.sign_up_finish_button);
        Button cancel_button = (Button)findViewById(R.id.sue_cancel_button);
        phone = this.getIntent().getStringExtra("phone");

        //이메일 생성시도
        complete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= email_input.getText().toString();
                String password = password_input.getText().toString();
                String password_check = password_check_input.getText().toString();

                if (!password.equals(password_check)){
                    Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    String email_regex = "^([0-9a-zA-Z_-]+)@([0-9a-zA-Z_-]+)(\\.[0-9a-zA-Z_-]+){1,2}$";
                    String password_regex = "^(?=.*[a-zA-Z])((?=.*\\d)|(?=.*\\W)).{6,20}$";
                    if (Pattern.matches(email_regex, email)) { //정규표현식과 input 비교
                        if(Pattern.matches(password_regex, password)){
                            if(!sex.equals("")) {
                                dialog = new ProgressDialog(Login_SignUp_EmailActivity.this);
                                dialog.setMessage("회원정보 불러오는 중...");
                                dialog.show();
                                createUser(email, password, sex);
                            }else{
                                Toast.makeText(getApplicationContext(), "성별을 체크해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "잘못된 비밀번호 형식입니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "잘못된 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //회원가입 취소
        cancel_button.setOnClickListener(new View.OnClickListener() {
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
    private void createUser(final String email, final String password, final String sex){
        user_auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    frequent.showDialog("이메일 생성실패", "이미 존재하는 계정입니다.",
                            Login_SignUp_EmailActivity.this);
                } else {
                    Toast.makeText(getApplicationContext(), "가입 완료되었습니다. 계정과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Intent intent = new Intent(Login_SignUp_EmailActivity.this, LoginActivity.class);
                    UserInfo user_info = new UserInfo();
                    user_info.phone = phone;
                    user_info.sex = sex;
                    user_info.ting = 20;
                    intent.putExtra("UserInfo", user_info);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
