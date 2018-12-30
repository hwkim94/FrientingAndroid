package com.building.frienting001;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
//import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class Login_SignUp_PhoneActivity extends AppCompatActivity {
    private EditText phone_input, code_input;
    private LinearLayout code_input_layout;
    private Button request_code_button, request_auth_button, cancel_button;
    private int code;
    private String phone;

    private DatabaseReference user_db_ref;
    private ProgressDialog dialog;

    //private FirebaseAnalytics firebaseAnalytics;
    Frequent frequent = new Frequent();

    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        frequent.hideStatusBar(this); // 상태창 제거

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__sign_up__phone);

        //선언부
        phone_input = (EditText)findViewById(R.id.sup_phone_input);
        code_input= (EditText)findViewById(R.id.sup_code_input);
        code_input_layout = (LinearLayout)findViewById(R.id.sup_code_input_layout);

        cancel_button = (Button)findViewById(R.id.sup_cancel_button);
        request_code_button = (Button)findViewById(R.id.sup_request_code_button);
        request_auth_button = (Button)findViewById(R.id.sup_request_auth_button);

        FirebaseApp userApp = FirebaseApp.getInstance("user");
        user_db_ref = FirebaseDatabase.getInstance(userApp).getReference("user");

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        //회원가입 취소
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "회원가입을 취소하고 이전 화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Login_SignUp_PhoneActivity.this, LoginActivity.class));
                finish();
           }
        });

        //인증번호 전송
        request_code_button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 phone = phone_input.getText().toString();
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
        request_auth_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String auth = Integer.toString(code);
                if (auth.equals(code_input.getText().toString())){
                    Toast.makeText(getApplicationContext(), "정상적으로 인증되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login_SignUp_PhoneActivity.this, Login_SignUp_EmailActivity.class);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "인증번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        LinearLayout layout = (LinearLayout)findViewById(R.id.phone_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(code_input.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(phone_input.getWindowToken(), 0);
            }
        });
    }

    private void searchDB(String searchedText){
        dialog = new ProgressDialog(Login_SignUp_PhoneActivity.this);
        dialog.setMessage("번호인증 중...");
        dialog.show();
        Query query = user_db_ref.orderByChild("phone").equalTo(searchedText);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    frequent.showDialog("번호인증 실패", "이미 존재하는 번호입니다.\n같은 번호로 계정의 중복생성은 불가합니다."
                            ,Login_SignUp_PhoneActivity.this);
                }
                else{
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    Random number = new Random();
                    code = (number.nextInt(1000000) + 100000) % 100000;
                    Toast.makeText(getApplicationContext(), "SMS 인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "" + code, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "중복되지 않은 번호입니다.", Toast.LENGTH_SHORT).show();
                    code_input_layout.setVisibility(View.VISIBLE);

                    request_code_button.setText("다시 보내기");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
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
    }*/
}
