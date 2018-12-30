package com.building.frienting001;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static android.R.attr.key;

//환경설정-내 정보
public class SettingContainerProfileActivity extends AppCompatActivity {

    private DatabaseReference userDBReference;
    private StorageReference storageReference;

    private ActionBar actionBar;
    private UserInfo userInfo;


    private final int CAMERA_INTENT_CODE = 8000;
    private final int GALLARY_INTENT_CODE = 9000;
    private String imagePath = "";
    private ImageView photo;

    private boolean is_changed = false;
    private boolean any_changed = false;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_profile_container);
/*
        //상태창
        if (Build.VERSION.SDK_INT >=21) {
            Window window = getWindow();
            Drawable background = ResourcesCompat.getDrawable(getResources(),R.drawable.gradient,null);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setBackgroundDrawable(background);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.color_transparent));
        }

        //유저정보 받기
        Intent receive = getIntent();
        userInfo = (UserInfo)receive.getSerializableExtra("userInfo");

        //상태창, 액션바 설정
        LayoutInflater actionbarInflater =LayoutInflater.from(this);
        View customView = actionbarInflater.inflate(R.layout.actionbar, null);
        TextView actionbar_name = (TextView)customView.findViewById(R.id.actionbar_name);
        TextView actionbar_now = (TextView)customView.findViewById(R.id.actionbar_now_ting);
        ImageView actionbar_back = (ImageView) customView.findViewById(R.id.actionbar_back);
        LinearLayout actionbar_container = (LinearLayout)customView.findViewById(R.id.actionbar_ting_container);
        actionbar_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingContainerProfileActivity.this, PaymentActivity.class);
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
        actionbar_name.setText("프로필 설정");

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(customView, params);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        Toolbar parent = (Toolbar)customView.getParent();
        parent.setContentInsetsAbsolute(0,0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_profile_container);

        //선언부
        FirebaseApp userApp = FirebaseApp.getInstance("user");
        userDBReference = FirebaseDatabase.getInstance(userApp).getReference().child("user");
        storageReference = FirebaseStorage.getInstance(userApp).getReferenceFromUrl("gs://frientinguser.appspot.com");

        photo = (ImageView)findViewById(R.id.setting_profile_photo);
        TextView new_photo = (TextView)findViewById(R.id.setting_profile_new_photo);
        TextView sex = (TextView)findViewById(R.id.setting_container_profile_sex);
        TextView birth = (TextView)findViewById(R.id.setting_container_profile_birth);
        final TextView nickname = (TextView)findViewById(R.id.setting_container_profile_nickname);
        final TextView personality = (TextView)findViewById(R.id.setting_container_profile_personality);
        final TextView job = (TextView)findViewById(R.id.setting_container_profile_job);
        final TextView place = (TextView)findViewById(R.id.setting_container_profile_place);
        final TextView introduction = (TextView)findViewById(R.id.setting_container_profile_introduction);
        Button cancel = (Button)findViewById(R.id.setting_profile_cancel);
        Button confirm = (Button)findViewById(R.id.setting_profile_confirm);


        //붙여주기
        if(userInfo.getImagePath().equals("")){}
        else {Glide.with(this).load(userInfo.getImagePath()).into(photo);}
        imagePath = userInfo.getImagePath();

        String str1 = userInfo.getSex();
        sex.setText(str1);

        String str2 = userInfo.getBirth();
        birth.setText(str2);

        View.OnClickListener listener1 = makingListener("닉네임 변경", "글자 및 숫자 4~10자로 작성하여 주세요", nickname, "[0-9a-zA-Z가-힣]{4,10}");
        String str3 = userInfo.getNickname();
        nickname.setText(str3);
        nickname.setOnClickListener(listener1);

        String str4 = userInfo.getPersonality();
        personality.setText(str4);
        personality.setOnClickListener(new View.OnClickListener() {

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
                adapter = new DialogListAdapter(SettingContainerProfileActivity.this ,activity_listitem);
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


                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingContainerProfileActivity.this);
                alertDialog.setView(convertView);
                alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String string_temp = "";

                        for(String idx : selected_activity){
                            string_temp =string_temp + idx +" ";
                        }
                        any_changed =true;
                        personality.setText(string_temp);
                    }
                });
                alertDialog.show();

            }
        });

        View.OnClickListener listener2 = makingListener("직업 변경", "2~10자로 작성하여 주세요", job, ".{2,10}");
        String str5 = userInfo.getJob();
        job.setText(str5);
        job.setOnClickListener(listener2);

        String str6 = userInfo.getPlace();
        place.setText(str6);
        place.setOnClickListener(new View.OnClickListener() {

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
                adapter = new DialogListAdapter(SettingContainerProfileActivity.this ,activity_listitem);
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


                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingContainerProfileActivity.this);
                alertDialog.setView(convertView);
                alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String string_temp = "";

                        for(String idx : selected_activity){
                            string_temp =idx;
                        }
                        any_changed = true;
                        place.setText(string_temp);
                    }
                });
                alertDialog.show();

            }
        });

        View.OnClickListener listener3 = makingListener("자기소개 변경", "10~25자로 작성하여 주세요", introduction,  ".{10,20}");
        String str7 = userInfo.getIntroduction();
        introduction.setText(str7);
        introduction.setOnClickListener(listener3);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nick = nickname.getText().toString();
                String pers = personality.getText().toString();
                String jo = job.getText().toString();
                String pla = place.getText().toString();
                String intro = introduction.getText().toString();

                if (nick.equals("") | pers.equals("") | jo.equals("") | pla.equals("") | intro.equals("")){
                    Toast.makeText(getApplicationContext(), "빈칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
                }else {

                    dialog = new ProgressDialog(SettingContainerProfileActivity.this);
                    dialog.setMessage("회원정보 저장 중...");
                    dialog.show();

                    userInfo.setNickname(nick);
                    userInfo.setPersonality(pers);
                    userInfo.setJob(jo);
                    userInfo.setPlace(pla);
                    userInfo.setIntroduction(intro);

                    savingUser();
                }
            }
        });

        //사진 수정
        new_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingContainerProfileActivity.this, R.layout.support_simple_spinner_dropdown_item);
                adapter.add("사진 찍기");
                adapter.add("사진첩에서 가져오기");
                adapter.add("사진 지우기");

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingContainerProfileActivity.this);
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
                            photo.setImageDrawable(null);
                            dialogInterface.cancel();
                            is_changed = true;
                        }
                    }
                });
                builder.create().show();
            }
        });

    }

    private View.OnClickListener makingListener(final String title, final String contents, final TextView view, final String regex){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingContainerProfileActivity.this);
                builder.setTitle(title);
                builder.setMessage(contents);

                final EditText et = new EditText(SettingContainerProfileActivity.this);
                et.setMaxLines(1);
                builder.setView(et);

                builder.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = et.getText().toString();
                        if(Pattern.matches(regex, value)){
                            view.setText(value);
                            any_changed =true;
                            dialog.cancel();
                        }else{
                            Toast.makeText(getApplicationContext(), "조건에 맞지 않습니다", Toast.LENGTH_SHORT).show();}
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
        };

        return listener;
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
                photo.setImageURI(Uri.fromFile(file));
                is_changed =true;
            }
        }else {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    imagePath = getPath(data.getData());
                    File file = new File(imagePath);
                    photo.setImageURI(Uri.fromFile(file));
                    is_changed =true;
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

    private void savingUser(){

        if (is_changed){
            if(imagePath.equals("")){
                userInfo.setImagePath("");
                uploadWithoutPhoto();
            }else{
                uploadWithPhoto(imagePath);
            }

        }else{
            if(any_changed) {
                if (imagePath.equals("")) {
                    userInfo.setImagePath("");
                    uploadWithoutPhoto();
                } else {
                    uploadWithoutPhoto();
                }
            }else{
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Toast.makeText(getApplicationContext(), "변경사항이 없습니다.", Toast.LENGTH_SHORT).show();

            }

        }
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
                userInfo.setImagePath(s_imagePath);
                userDBReference.child(userInfo.getFirebaseUserUid()).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    //사진이 없을 때 저장
    private void uploadWithoutPhoto(){

        userDBReference.child(userInfo.getFirebaseUserUid()).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });;*/

    }
}


