package com.building.frienting001;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    Frequent frequent;

    private EditText email_input, password_input;

    private FirebaseAuth user_auth;
    private FirebaseUser user_info;
    private FirebaseAuth.AuthStateListener auth_listener;
    private DatabaseReference user_db_ref;

    private String email,password;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        frequent = new Frequent();
        frequent.hideStatusBar(this); // 상태창 제거

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp AuthApp_user = FirebaseApp.getInstance("user");
        user_auth = FirebaseAuth.getInstance(AuthApp_user);
        user_db_ref = FirebaseDatabase.getInstance(AuthApp_user).getReference("user");
        auth_listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user_info = user_auth.getCurrentUser();

                if(user_info != null){
                    UserInfo information = (UserInfo)LoginActivity.this.getIntent().getSerializableExtra("UserInfo");
                    if(information != null) { // 처음 로그인 화면 들어가는 거면 null 이어서 여기 안거침
                        user_db_ref.child(user_info.getUid()).setValue(information);
                    }

                    Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish(); // 정말중요 이 부분 없으면 무한 로그인 - 뒤로가기 반복
                }
                if(user_info == null){
                    Log.d("myLog", "user has signed out");
                }
            }
        };

        email_input = (EditText)findViewById(R.id.email_input);
        password_input = (EditText)findViewById(R.id.password_input);
        Button login_button = (Button)findViewById(R.id.login_button);
        TextView sign_up = (TextView)findViewById(R.id.sing_up);
        RelativeLayout login_layout = (RelativeLayout)findViewById(R.id.login_layout);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    email = email_input.getText().toString().trim();
                    password = password_input.getText().toString().trim();
                    loginUser(email, password);
                }catch (Exception e) {
                    frequent.showDialog("로그인 실패", "올바른 이메일과 비밀번호를 입력해주세요.",
                            LoginActivity.this);
                }
            }
        });
        //UserProfileChangeRequest req = new UserProfileChangeRequest.Builder().setDisplayName("s").setPhotoUri(Uri.parse("s")).build();
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Login_SignUp_PhoneActivity.class));
                finish();
            }
        });

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        login_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(email_input.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(password_input.getWindowToken(), 0);
            }
        });
    }

    private void loginUser(final String email, final String password) {
        user_auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            frequent.showDialog("로그인 실패", "로그인에 실패하였습니다.\n계정 및 비밀번호를 확인하세요."
                            ,LoginActivity.this);
                        }
                        // 성공했을 경우 유저가 로그인하게 된다. 이는 auth에 붙은 리스너를 작동시키게 되고, 로그인 과정이 알아서 진행된다.
                        // 따라서 성공했을 경우를 따로 지정할 필요가 없음
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        user_auth.addAuthStateListener(auth_listener);
        // OnStart는 OnCreate로 초기화된 직후에 실행된다. 즉 여기서 auth에 리스너를 달아주기 때문에
        // 다른 버튼의 클릭 리스너를 통해 auth에 리스너를 또 달아줄 필요가 없다.
    }
    @Override
    public void onStop() {
        super.onStop();
        if (auth_listener != null) {
            user_auth.removeAuthStateListener(auth_listener);
        }
    }
}
