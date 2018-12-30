package com.building.frienting001;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class Login_SignUp_UserActivity extends AppCompatActivity {
    private FirebaseAuth userAuth_user;
    private FirebaseUser user_user;
    private FirebaseDatabase userDB;
    private DatabaseReference userDBReference;
    private FirebaseAuth userAuth_chatting;
    private FirebaseUser user_chatting;
    private FirebaseAuth userAuth_log;
    private FirebaseUser user_log;
    private FirebaseAuth userAuth_recruitment;
    private FirebaseUser user_recruitment;

    private String user_uid;
    private String chatting_uid;
    private String log_uid;
    private String recruitment_uid;

    private ImageView signup_photo;
    private TextView signup_take_photo;
    private EditText signup_name;
    private RadioButton signup_sex_male;
    private RadioButton signup_sex_female;
    private TextView signup_birth;
    private Button signup_take_birth;
    private EditText signup_job;
    private TextView signup_place;
    private Button signup_take_place;
    private EditText signup_nickname;
    private TextView signup_personality;
    private Button signup_take_personality;
    private TextView signup_category1;
    private TextView signup_category2;
    private TextView signup_take_category1;
    private TextView signup_take_category2;
    private RecyclerView signup_interst_lst;
    private Button signup_add_interest;
    private EditText signup_introduction;
    private EditText signup_recommendation;

    private HashTagWritingAdapter hashTagWritingAdapter;

    private StorageReference storageReference;
    private final int CAMERA_INTENT_CODE = 8000;
    private final int GALLARY_INTENT_CODE = 9000;
    private String imagePath ="";
    private String key;

    private String email, pw, phone;
    private String  photo_uri, name, sex, birth, job, place, nickname, personality,introduction, recommendation, s_hashTag;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__sign_up__user);
        //선언부
        FirebaseApp AuthApp_user = FirebaseApp.getInstance("user");
        userAuth_user = FirebaseAuth.getInstance(AuthApp_user);
        userDB= FirebaseDatabase.getInstance(AuthApp_user);
        userDBReference = userDB.getReference().child("user");
        storageReference = FirebaseStorage.getInstance(AuthApp_user).getReferenceFromUrl("gs://frientinguser.appspot.com");
        FirebaseApp AuthApp_chat = FirebaseApp.getInstance("chatting");
        userAuth_chatting = FirebaseAuth.getInstance(AuthApp_chat);
        FirebaseApp AuthApp_log = FirebaseApp.getInstance("log");
        userAuth_log = FirebaseAuth.getInstance(AuthApp_log);
        FirebaseApp AuthApp_recruitment = FirebaseApp.getInstance("recruitment");
        userAuth_recruitment = FirebaseAuth.getInstance(AuthApp_recruitment);
        user_user = userAuth_user.getCurrentUser();
        user_chatting = userAuth_chatting.getCurrentUser();
        user_log = userAuth_log.getCurrentUser();
        user_recruitment = userAuth_recruitment.getCurrentUser();

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        user_uid = user_user.getUid();
        chatting_uid = user_chatting.getUid();
        log_uid = user_log.getUid();
        recruitment_uid = user_recruitment.getUid();

        Button signup_exit_button = (Button)findViewById(R.id.signup_exit_btn);
        Button signup_finish_button = (Button)findViewById(R.id.signup_finish_btn);

        Bundle params1 = new Bundle();
        params1.putString("UserUid", phone);
        params1.putLong("OpenTime", System.currentTimeMillis());
        firebaseAnalytics.logEvent("Login_SignUp_UserActivity", params1);

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        Intent receive = Login_SignUp_UserActivity.this.getIntent();
        String[] address = receive.getStringArrayExtra("address");
        phone = address[0];
        email = address[1];
        pw = address[2];
        sex ="";

        signup_photo = (ImageView)findViewById(R.id.signup_photo);
        signup_take_photo = (TextView)findViewById(R.id.signup_take_photo);
        signup_name = (EditText)findViewById(R.id.signup_name);
        signup_sex_male = (RadioButton)findViewById(R.id.signup_sex_male);
        signup_sex_female = (RadioButton)findViewById(R.id.signup_sex_female);
        signup_birth = (TextView)findViewById(R.id.signup_birth);
        signup_take_birth = (Button)findViewById(R.id.signup_take_birth);
        signup_job = (EditText) findViewById(R.id.signup_job);
        signup_place = (TextView)findViewById(R.id.signup_place);
        signup_take_place = (Button)findViewById(R.id.signup_take_place);
        signup_nickname = (EditText)findViewById(R.id.signup_nickname);
        signup_category1 = (TextView)findViewById(R.id.signup_category1);
        signup_category2 = (TextView)findViewById(R.id.signup_category2);
        signup_take_category1 = (TextView)findViewById(R.id.signup_take_category1);
        signup_take_category2 = (TextView)findViewById(R.id.signup_take_category2);
        signup_add_interest = (Button)findViewById(R.id.signup_add_interest);
        signup_interst_lst = (RecyclerView)findViewById(R.id.signup_interest_list);
        signup_personality = (TextView)findViewById(R.id.signup_personaliy);
        signup_take_personality = (Button)findViewById(R.id.signup_take_personality);
        signup_introduction = (EditText)findViewById(R.id.signup_introduction);
        signup_recommendation = (EditText)findViewById(R.id.signup_recommendation);

        //회원가입 취소
        signup_exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login_SignUp_UserActivity.this);
                builder.setMessage("계정을 취소하시겠습니까?\n해당 내용은 저장되지 않습니다.");
                builder.setCancelable(false);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        user_user.delete();
                        user_chatting.delete();
                        user_log.delete();
                        user_recruitment.delete();
                        userAuth_user.signOut();
                        userAuth_chatting.signOut();
                        userAuth_log.signOut();
                        userAuth_recruitment.signOut();

                        Bundle params1 = new Bundle();
                        params1.putString("UserUid", phone);
                        params1.putLong("Quit" + "Time", System.currentTimeMillis());
                        firebaseAnalytics.logEvent("Login_SignUp_UserActivity", params1);

                        dialogInterface.cancel();
                        startActivity(new Intent(Login_SignUp_UserActivity.this, LoginActivity.class));
                        finish();
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
        });

        //회원가입 완료버튼
        signup_finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //photo_uri;
                name = signup_name.getText().toString();
                birth = signup_birth.getText().toString();
                job = signup_job.getText().toString();
                place = signup_place.getText().toString();
                nickname = signup_nickname.getText().toString();
                personality = signup_personality.getText().toString();
                introduction = signup_introduction.getText().toString();
                recommendation = signup_recommendation.getText().toString();

                s_hashTag = "";
                List<String> list = hashTagWritingAdapter.getList();
                if(!list.isEmpty()){
                    for (String str : list){
                        if (s_hashTag.equals("")){s_hashTag = str;}
                        else{s_hashTag = s_hashTag + " " + str;}
                    }
                }
                String regex1 = "[a-zA-Z가-힣]{2,5}"; //정규표현식
                String regex2 = ".{2,10}";
                String regex3 = "[0-9a-zA-Z가-힣]{4,10}";
                String regex4 = ".{10,25}";

                if(Pattern.matches(regex1, name) & Pattern.matches(regex2, job) & Pattern.matches(regex3, nickname) & Pattern.matches(regex4, introduction) & list.size()>=3){
                    if(!sex.equals("") & !birth.equals("") & !job.equals("") & !place.equals("") & !nickname.equals("") & !personality.equals("") & !introduction.equals("")) {
                        sendVerificationEmail();
                    }else{
                        Toast.makeText(getApplicationContext(), "필수사항(*)을 모두 입력해주세요!", Toast.LENGTH_SHORT).show();
                    }
                }else{Toast.makeText(getApplicationContext(), "잘못된 형식 입니다.", Toast.LENGTH_SHORT).show();}

            }
        });

        //사진 가져오기
        signup_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Login_SignUp_UserActivity.this, R.layout.support_simple_spinner_dropdown_item);
                adapter.add("사진 찍기");
                adapter.add("사진첩에서 가져오기");
                adapter.add("사진 지우기");

                AlertDialog.Builder builder = new AlertDialog.Builder(Login_SignUp_UserActivity.this);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, CAMERA_INTENT_CODE);
                        }else if(i==1){
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                            startActivityForResult(intent, GALLARY_INTENT_CODE);

                        }else{
                            imagePath ="";
                            signup_photo.setImageDrawable(null);
                            dialogInterface.cancel();
                        }
                    }
                });
                builder.create().show();
            }
        });

        //성별 선택
        signup_sex_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = "male";
            }
        });
        signup_sex_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = "female";
            }
        });

        //생일 선택
        signup_take_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = year +"년 " + (month+1) + "월 " + dayOfMonth + "일";
                        signup_birth.setText(date);
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog(Login_SignUp_UserActivity.this, mDateSetListener, year, month, day);
                dialog.show();
            }
        });

        signup_take_place.setOnClickListener(new View.OnClickListener() {

            StaggeredGridLayoutManager layoutManager;
            RecyclerView recyclerView;

            @Override
            public void onClick(View v) {

                LayoutInflater inflater = getLayoutInflater();
                final View convertView = (View) inflater.inflate(R.layout.select_activity_dialog_layout, null);
                TextView dialog_name = (TextView)convertView.findViewById(R.id.dialog_name);
                final TextView dialog_selected = (TextView)convertView.findViewById(R.id.dialog_selected);
                dialog_name.setText("활동장소");

                final List<String> selected_activity =new ArrayList<String>();
                final ArrayList<String> activity_listitem = new ArrayList<>();
                final String[] activity_lst = new String[]{"서울", "인천", "부산", "대구", "광주", "대전", "울산","제주", "경기", "강원", "충북", "충남", "전북", "전남", "경북", "경남"};
                for (String act: activity_lst){
                    activity_listitem.add(act);
                }
                DialogListAdapter adapter;
                adapter = new DialogListAdapter(Login_SignUp_UserActivity.this ,activity_listitem);
                adapter.setItemClick(new DialogListAdapter.ItemClick() {
                    @Override
                    public void onClick(View view, int position) {
                        String selected = activity_lst[position];

                        if (selected_activity.isEmpty()){
                            selected_activity.add(selected);
                            dialog_selected.setText(selected);
                        }else{
                            selected_activity.remove(0);
                            selected_activity.add(selected);
                            dialog_selected.setText(selected);
                        }
                    }
                });


                recyclerView = (RecyclerView)convertView.findViewById(R.id.dialog_list);
                recyclerView.setHasFixedSize(true);
                layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);


                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login_SignUp_UserActivity.this);
                alertDialog.setView(convertView);
                alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String string_temp = "";

                        for(String idx : selected_activity){
                            string_temp =idx;
                        }
                        signup_place.setText(string_temp);
                    }
                });
                alertDialog.show();

            }
        });


        List<String> list = new ArrayList<>();
        hashTagWritingAdapter= new HashTagWritingAdapter(this, list);

        signup_interst_lst.setHasFixedSize(false);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        signup_interst_lst.setLayoutManager(layoutManager);
        signup_interst_lst.setAdapter(hashTagWritingAdapter);

        //카테고리 해시태그
        signup_add_interest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hashTagWritingAdapter.getItemCount() <10) {
                    String tag = signup_category1.getText().toString() + " " + signup_category2.getText().toString();
                    hashTagWritingAdapter.addItem(tag);

                    signup_category1.setText("");
                    signup_category2.setText("");

                }else{Toast.makeText(getApplicationContext(), "최대 10개까지 추가가능합니다.", Toast.LENGTH_SHORT).show();}

            }
        });

        //카테고리 추가
        signup_take_category1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makingListDialog(1, new String[]{"스포츠", "학업", "엔터테인먼트(취미)", "식사"}, 2);
            }
        });
        signup_take_category2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signup_category1.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "카테고리1을 먼저 골라주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    switch (signup_category1.getText().toString()){
                        case "스포츠" :
                            String[] interest1 = new String[]{"축구", "풋살","농구", "당구", "탁구", "족구", "볼링", "골프", "야구", "배드민턴", "테니스", "수영", "요트", "카누", "서핑", "스쿠버다이빙", "빠지", "스케이트보드", "따릉이", "자전거", "헬스", "요가", "필라테스", "달리기","주짓수", "권투", "클라이밍", "스카이다이빙", "패러글라이딩"};
                            makingListDialog(2, interest1, 3);
                            break;
                        case "학업" :
                            String[] interest2 = new String[]{"코딩", "주식", "고시", "면접", "자격증", "취업"};
                            makingListDialog(2,interest2, 3);
                            break;
                        case "엔터테인먼트(취미)" :
                            String[] interest3 = new String[]{"영화", "뮤지컬", "놀이동산", "콘서트", "전시회", "쇼핑", "미술관", "꽃&자수&공예", "게임", "오락실", "여행", "댄스", "클럽", "독서", "요리&베이킹", "만화&애니"};
                            makingListDialog(2, interest3, 3);
                            break;
                        case "식사" :
                            String[] interest4 = new String[]{"주류", "한식", "중식", "일식", "양식", "베트남", "타이", "멕시코", "퓨전", "이색음식", "건강식", "채식", "기타"};
                            makingListDialog(2, interest4, 3);
                            break;
                    }
                }
            }
        });

        signup_take_personality.setOnClickListener(new View.OnClickListener() {

            StaggeredGridLayoutManager layoutManager;
            RecyclerView recyclerView;

            @Override
            public void onClick(View v) {

                LayoutInflater inflater = getLayoutInflater();
                final View convertView = (View) inflater.inflate(R.layout.select_activity_dialog_layout, null);
                TextView dialog_name = (TextView)convertView.findViewById(R.id.dialog_name);
                final TextView dialog_selected = (TextView)convertView.findViewById(R.id.dialog_selected);
                dialog_name.setText("성격 선택");

                final List<String> selected_activity =new ArrayList<String>();
                final ArrayList<String> activity_listitem = new ArrayList<>();
                final String[] activity_lst = new String[]{"수줍", "낙천", "활발" , "꼼꼼", "정직", "감성", "친절", "츤데레", "털털", "애교", "도도", "도발", "웃긴", "열정", "단순", "엉뚱" };
                for (String act: activity_lst){
                    activity_listitem.add(act);
                }
                DialogListAdapter adapter;
                adapter = new DialogListAdapter(Login_SignUp_UserActivity.this ,activity_listitem);
                adapter.setItemClick(new DialogListAdapter.ItemClick() {
                    @Override
                    public void onClick(View view, int position) {
                        String selected = activity_lst[position];

                        if (selected_activity.isEmpty()){
                            selected_activity.add(selected);
                        }else{
                            if (selected_activity.contains(selected)) {
                                selected_activity.remove(selected);
                            } else {
                                if (selected_activity.size() < 5){
                                    selected_activity.add(selected);
                                }else{
                                    Toast.makeText(getApplicationContext(), "최대 5개까지 선택가능 합니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        String selected_string ="";
                        for(String str : selected_activity){
                            selected_string = selected_string + str + " ";
                        }
                        dialog_selected.setText(selected_string);
                    }
                });


                recyclerView = (RecyclerView)convertView.findViewById(R.id.dialog_list);
                recyclerView.setHasFixedSize(true);
                layoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);


                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login_SignUp_UserActivity.this);
                alertDialog.setView(convertView);
                alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String string_temp = "";

                        for(String idx : selected_activity){
                            string_temp =string_temp + idx +" ";
                        }
                        signup_personality.setText(string_temp);
                    }
                });
                alertDialog.show();

            }
        });

    }

    //회원가입 인증메일 전송
    private void sendVerificationEmail() {
        //user_user = userAuth_user.getCurrentUser();
        user_user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent
                            // after email is sent just logout the user_user and finish this activity
                            AlertDialog.Builder builder = new AlertDialog.Builder(Login_SignUp_UserActivity.this);
                            builder.setTitle("이메일 계정 인증");
                            builder.setMessage("해당 이메일에서 인증을 완료하시면,\n계정생성이 완료됩니다!");
                            builder.setCancelable(false);

                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.cancel();

                                    //정보들을 DB와 Storage에 저장
                                    savingUser();
                                }
                            });

                            builder.create().show();
                        } else {
                            // email not sent, so display message and restart the activity or do whatever you wish to do
                            Toast.makeText(getApplicationContext(), "다시 시도해주세요.", Toast.LENGTH_SHORT).show();

                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                        }
                    }
                });
    }

    //사진첩/카메라 접근
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_INTENT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                Bitmap image = (Bitmap) bundle.get("data");
                //photo.setImageBitmap(image);

                Uri uri = getImageUri(getApplicationContext(), image);
                imagePath  = getPath(uri);
                File file = new File(getRealPathFromURI(uri));
                signup_photo.setImageURI(Uri.fromFile(file));

            }
        }else {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    imagePath = getPath(data.getData());
                    File file = new File(imagePath);
                    signup_photo.setImageURI(Uri.fromFile(file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //uri를 가지고, 사진의 경로를 가져오는 것
    public String getPath(Uri uri){
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(getApplicationContext(), uri, proj, null, null,null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(index);
    }

    //Uri 가져오는 함수
    public Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //Uri 가지고 경로를 반환해주는 함수
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    //회원정보 저장하기
    private void savingUser(){
        if(imagePath.equals("")){
            uploadWithoutPhoto();
            //startActivity(new Intent(getActivity(), NavigationActivity.class));
        }else {
            uploadWithPhoto(imagePath);
        }

        userAuth_recruitment.signOut();
        userAuth_chatting.signOut();
        userAuth_log.signOut();
        userAuth_user.signOut();

        // do UI work here
        startActivity(new Intent(Login_SignUp_UserActivity.this, LoginActivity.class));
        finish();
    }

    //사진이 있을 때 저장
    private void uploadWithPhoto(String uri){

        StorageReference storageRef = storageReference;
        //key = user_user.getUid();

        Uri file = Uri.fromFile(new File(uri));
        StorageReference reference = storageRef.child("images/"+key);
        UploadTask uploadTask = reference.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getApplicationContext(), "사진 업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                @SuppressWarnings("VisibleForTests")
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                //key = databaseReference.push().getKey();


                String s_imagePath = downloadUrl.toString();

                Calendar cal = Calendar.getInstance();
                String date = cal.YEAR +"/"+(cal.MONTH+1)+"/"+cal.DATE + " " + cal.HOUR_OF_DAY +":"+cal.MINUTE;

                //UserInfo userInfo = new UserInfo(phone, email, pw, s_imagePath, name, sex, birth, job, place, nickname, personality, s_hashTag,introduction, recommendation,20, "",user_uid, recruitment_uid, chatting_uid, log_uid, date, "", 0, new ReviewDialogItem("", "", ""),0, "","","");
                //userDBReference.child(user_uid).setValue(userInfo);

                Bundle params1 = new Bundle();
                params1.putString("UserUid", user_uid);
                params1.putLong("SignUpTime",System.currentTimeMillis());
                firebaseAnalytics.logEvent("Login_SignUp_UserActivity", params1);

            }
        });
    }

    //사진이 없을 때 저장
    private void uploadWithoutPhoto(){

        Calendar cal = Calendar.getInstance();
        String date = cal.YEAR +"/"+(cal.MONTH+1)+"/"+cal.DATE + " " + cal.HOUR_OF_DAY +":"+cal.MINUTE;

        //UserInfo userInfo = new UserInfo(phone, email, pw, "", name, sex, birth, job, place, nickname, personality, s_hashTag, introduction, recommendation,20, "",user_uid, recruitment_uid, chatting_uid, log_uid, date, "", 0, new ReviewDialogItem("", "", ""),0, "","","");


        //userDBReference.child(user_uid).setValue(userInfo);

        Bundle params1 = new Bundle();
        params1.putString("UserUid", user_uid);
        params1.putLong("SignUpTime", System.currentTimeMillis());
        firebaseAnalytics.logEvent("Login_SignUp_UserActivity", params1);
    }

    private void makingListDialog(final int id, String[] lst, int num){

        StaggeredGridLayoutManager layoutManager;
        RecyclerView recyclerView;

        LayoutInflater inflater = getLayoutInflater();
        final View convertView = (View) inflater.inflate(R.layout.select_activity_dialog_layout, null);
        TextView dialog_name = (TextView)convertView.findViewById(R.id.dialog_name);
        final TextView dialog_selected = (TextView)convertView.findViewById(R.id.dialog_selected);
        dialog_name.setText("관심사 선택");

        final List<String> selected_activity =new ArrayList<String>();
        final ArrayList<String> activity_listitem = new ArrayList<>();
        final String[] activity_lst = lst;
        for (String act: activity_lst){
            activity_listitem.add(act);
        }
        DialogListAdapter adapter;
        adapter = new DialogListAdapter(this ,activity_listitem);
        adapter.setItemClick(new DialogListAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                String selected = activity_lst[position];

                if (selected_activity.isEmpty()){
                    selected_activity.add(selected);
                    dialog_selected.setText(selected);
                }else{
                    selected_activity.remove(0);
                    selected_activity.add(selected);
                    dialog_selected.setText(selected);
                }
            }
        });

        recyclerView = (RecyclerView)convertView.findViewById(R.id.dialog_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(num, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setView(convertView);
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String string_temp = "";

                for(String idx : selected_activity){
                    string_temp =idx;
                }

                switch (id){
                    case 1 : signup_category1.setText(string_temp);break;
                    case 2 : signup_category2.setText(string_temp);break;
                }
            }
        });
        alertDialog.show();
    }

    public void hide_keyboard(View v){
        imm.hideSoftInputFromWindow(signup_introduction.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(signup_job.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(signup_name.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(signup_nickname.getWindowToken(), 0);
    }

}
