package com.building.frienting001;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;
import java.util.regex.Pattern;

public class Login_SignUp_PhoneActivity extends AppCompatActivity {
    private EditText phoneNumber, authNumber;
    private LinearLayout signup_after_send;
    private Button requestAuth;
    private int result;
    private String phone;

    private DatabaseReference userDBRef;
    private ProgressDialog dialog;

    private FirebaseAnalytics firebaseAnalytics;
    Frequent frequent = new Frequent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        frequent.hideStatusBar(this); // 상태창 제거

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__sign_up__phone);

        //선언부
        phoneNumber = (EditText)findViewById(R.id.signup_phone_number);
        authNumber= (EditText)findViewById(R.id.signup_phone_authnum);
        signup_after_send = (LinearLayout)findViewById(R.id.signup_after_send);

        Button cancel = (Button)findViewById(R.id.signup_phone_back_btn);
        requestAuth= (Button)findViewById(R.id.signup_phone_send);
        Button inputAuth = (Button)findViewById(R.id.signup_phone_next_btn);

        FirebaseApp recruitmentApp = FirebaseApp.getInstance("user");
        userDBRef = FirebaseDatabase.getInstance(recruitmentApp).getReference("user");

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        /*Bundle params1 = new Bundle();
        params1.putLong("OpenTime", System.currentTimeMillis());
        firebaseAnalytics.logEvent("Login_SignUp_PhoneActivity", params1);*/

        //키보드
        final InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        LinearLayout layout = (LinearLayout)findViewById(R.id.phone_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(authNumber.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(phoneNumber.getWindowToken(), 0);
            }
        });

        // userInfo 전역변수 초기화하는 부분 추가하기

        //회원가입 취소
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_SignUp_PhoneActivity.this, LoginActivity.class));
                finish();
                /*Bundle params1 = new Bundle();
                params1.putLong("QuitTime", System.currentTimeMillis());
                firebaseAnalytics.logEvent("Login_SignUp_PhoneActivity", params1);*/
            }
        });

        //인증번호 전송
        requestAuth.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 phone = phoneNumber.getText().toString();
                 String regex = "01(0|1|2|6|7|9)[0-9]{7,8}"; //정규표현식
                 if(Pattern.matches(regex, phone)){
                     searchDB(phone);
                     //SmsManager sms = SmsManager.getDefault();
                     //sms.sendTextMessage(phone, null, "고객님의 인증번호는 " +result+" 입니다. 입력해주세요.", null, null);
                 }else{
                     Toast.makeText(getApplicationContext(), "잘못된 번호형식 입니다.", Toast.LENGTH_SHORT).show();
                 }
             }
         });

        //유저 정보 섹션으로 이동
        inputAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String auth = ""+result;
                if (auth.equals(authNumber.getText().toString())){
                    Toast.makeText(getApplicationContext(), "정상적으로 인증되었습니다.", Toast.LENGTH_SHORT).show();

                    UserInfo userInfo = (UserInfo)getApplication();
                    userInfo.setPhone(phone);

                    Intent intent = new Intent(Login_SignUp_PhoneActivity.this, Login_SignUp_EmailActivity.class);
                    //intent.putExtra("phone", phone);

                    /*Bundle params1 = new Bundle();
                    params1.putString("UserInfo", phone);
                    params1.putLong("ConfirmTime", System.currentTimeMillis());
                    firebaseAnalytics.logEvent("Login_SignUp_PhoneActivity", params1);*/
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "인증번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*인증 잔여시간 보여주는 것
    private class  authAsync extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String strings) {
            signup_phone_authnum.setHint(strings);
        }
        @Override
        protected String doInBackground(String... params) {
            int time=300, minute, second;
            String result;

            while(time>=0){
                try {
                    Thread.sleep(1000);
                }catch (Exception e){}

                time --;
                minute = time/60;
                second = time%60;

                result = "인증번호(남은시간 : "+ minute +"분 " + second +"초" +")";
            }
            return result;
        }
    }
    */

    // 해당 핸드폰 번호가 있는지 확인
    private void searchDB(String searchedText){
        dialog = new ProgressDialog(Login_SignUp_PhoneActivity.this);
        dialog.setMessage("번호인증 중...");
        dialog.show();
        try {
            final Query query = userDBRef.orderByChild("phone").equalTo(searchedText);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        frequent.showDialog("번호인증 실패", "이미 존재하는 번호입니다.\n같은 번호로 계정의 중복생성은 불가합니다."
                        ,Login_SignUp_PhoneActivity.this);
                    } else {
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(getApplicationContext(), "중복되지 않은 번호입니다.", Toast.LENGTH_SHORT).show();
                        signup_after_send.setVisibility(View.VISIBLE);
                        Random random = new Random();
                        result = (random.nextInt(1000000) + 100000) % 100000;
                        Toast.makeText(getApplicationContext(), "sms 인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "" + result, Toast.LENGTH_SHORT).show();

                        requestAuth.setText("다시 보내기");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }catch (Exception e){
            if(dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(getApplicationContext(), "알 수 없는 에러 발생", Toast.LENGTH_SHORT).show();
        }
    }
}
