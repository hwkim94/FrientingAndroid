package com.building.frienting001;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
//import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class WritingFragment extends Fragment {
    private EditText titleEditText;
    private ImageView photoImageView;
    private String imagePath = "";
    private Button photoButton;
    private TextView activityEditText;
    private TextView meetingDateTextView;
    private TextView meetingTimeTextView;
    private TextView meetingTimeTextView2;
    private TextView meetingDateSelectTextView;
    private TextView meetingTimeSelectTextView;
    private TextView meetingTimeSelectTextView2;
    private EditText placeEditText;
    private EditText recruitmentTextEditText;
    private EditText hashTagEditText;
    private Button hashTagButton;
    private RecyclerView written_tag;
    private HashTagWritingAdapter hashTagWritingAdapter;
    private TextView detailEditText;
    private Button finishButton;
    private LinearLayout layout;

    private DatabaseReference userDBReference;
    private DatabaseReference recruitmentDBReference;
    private String key;

    private StorageReference storageReference;
    private InputMethodManager imm;

    private final int CAMERA_INTENT_CODE = 8000;
    private final int GALLARY_INTENT_CODE = 9000;
    FirebaseApp userApp;
    FirebaseAuth firebaseAuth;

    final char[] meeting_time = new char[5];
    final char[] farewell_time = new char[5];
    final int[] fail = {0};

    private UserInfo userInfo;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_writing, container, false);

        //선언부
        layout = (LinearLayout)view.findViewById(R.id.total_layout);
        titleEditText = (EditText)view.findViewById(R.id.title);
        photoImageView = (ImageView)view.findViewById(R.id.photo);
        photoButton = (Button)view.findViewById(R.id.choosePhotoBtn);
        activityEditText = (EditText)view.findViewById(R.id.activity);
        meetingDateTextView = (TextView)view.findViewById(R.id.meetingDate);
        meetingTimeTextView = (TextView)view.findViewById(R.id.meetingTime);
        meetingTimeTextView2 = (TextView)view.findViewById(R.id.farewellTime);
        meetingDateSelectTextView = (TextView)view.findViewById(R.id.select_date);
        meetingTimeSelectTextView = (TextView)view.findViewById(R.id.select_time1);
        meetingTimeSelectTextView2 = (TextView)view.findViewById(R.id.select_time2);
        placeEditText = (EditText) view.findViewById(R.id.place);
        recruitmentTextEditText = (EditText)view.findViewById(R.id.text);
        hashTagEditText = (EditText)view.findViewById(R.id.hashTag);
        hashTagButton = (Button)view.findViewById(R.id.add_tag);
        written_tag = (RecyclerView)view.findViewById(R.id.hashTag_written);
        detailEditText = (EditText)view.findViewById(R.id.detail_1);
        finishButton = (Button)view.findViewById(R.id.finishWriting);

        imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);

        //유저DB & 공고DB, 공고 스토리지 연결
        userApp = FirebaseApp.getInstance("user");
        firebaseAuth = FirebaseAuth.getInstance(userApp);
        String user_id = firebaseAuth.getCurrentUser().getUid();
        userDBReference = FirebaseDatabase.getInstance(userApp).getReference().child("user").child(user_id);
        recruitmentDBReference = FirebaseDatabase.getInstance(userApp).getReference().child("recruitment");
        storageReference = FirebaseStorage.getInstance(userApp).getReferenceFromUrl("gs://frientinguser.appspot.com");

        userDBReference.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInfo = dataSnapshot.getValue(UserInfo.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        layout.setOnClickListener(new View.OnClickListener() { // 키보드 설정
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(placeEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(recruitmentTextEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(hashTagEditText.getWindowToken(), 0);
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(0 < titleEditText.getText().length() || titleEditText.getText().length() < 20 ||
                        0 < activityEditText.getText().length() || activityEditText.getText().length() < 2 ||
                        0 < placeEditText.getText().length() || placeEditText.getText().length() < 2 ||
                        meetingDateTextView.getText().length() < 2 || meetingTimeTextView.getText().length() < 2 || meetingTimeTextView2.getText().length() < 2 ||
                        recruitmentTextEditText.getText().length() < 200)) {
                    Toast.makeText(getActivity().getApplicationContext(), "잘못된 형식입니다.", Toast.LENGTH_SHORT).show();

                } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("공고 작성을 완료하시겠습니까?");
                        builder.setMessage("확인 버튼을 누르실 경우 20팅이 소모됩니다.");
                        builder.setCancelable(false);

                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                userDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        userInfo = dataSnapshot.getValue(UserInfo.class);
                                        for(int i = 0; i < userInfo.recruit_progress.size(); i++) {
                                            if (strComp(meeting_time, userInfo.recruit_progress.get(i).get(2)) == 1 ||
                                                    strComp(farewell_time, userInfo.recruit_progress.get(i).get(1)) == -1) { // 겹치는 약속 없음
                                            } else {
                                                //겹치는 약속을 보여주기 -> i번째 recruit_process 그냥 보여주면 됨.
                                                Toast.makeText(getActivity().getApplicationContext(), "겹치는 약속이 있습니다.", Toast.LENGTH_SHORT).show();
                                                return; // 마무리
                                            }
                                        }
                                        int ting = userInfo.ting;

                                        if (ting < 20) {
                                            dialogInterface.cancel();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            builder.setTitle("팅이 부족합니다.");
                                            builder.setMessage("팅을 충전하러 가시겠습니까?");
                                            builder.setCancelable(false);
                                            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();
                                                    Intent intent = new Intent(getActivity(), PaymentActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                    startActivity(intent);
                                                    // finish는 안함 다시 여기로 돌아와야 하기 때문
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
                                        else {
                                            dialogInterface.cancel();
                                            UploadAsyncTask uploadAsyncTask = new UploadAsyncTask();
                                            uploadAsyncTask.execute();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {}
                                });
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
            }
        });

        // 날짜 선택
        meetingDateSelectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR) ;
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        meeting_time[0] = (char)(year - 2000 + 56); // 56은 출력가능한 캐릭터 값으로 만들기 위해서
                        meeting_time[1] = (char)((month + 1) + 56);
                        meeting_time[2] = (char)(dayOfMonth + 56);
                        farewell_time[0] = (char)(year - 2000 + 56);
                        farewell_time[1] = (char)((month + 1) + 56);
                        farewell_time[2] = (char)(dayOfMonth + 56);

                        String date = (year - 2000) + "/" + (month + 1) + "/" + dayOfMonth;
                        meetingDateTextView.setText(date);
                        meetingTimeSelectTextView.performClick();
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), mDateSetListener, year, month, day);
                dialog.show();
            }
        });

        View.OnClickListener time_listener = new View.OnClickListener() { //시간선택하는 다이얼로그
            @Override
            public void onClick(final View v) {
                TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
                        String AM_PM = "AM";
                        if(hourOfDay >= 12) {
                            AM_PM = "PM";
                        }
                        String hour = String.valueOf(hourOfDay);
                        String minute = String.valueOf(minuteOfDay);
                        if(hour.length() == 1) {hour = "0" + hour;}
                        if(minute.length() ==1) {minute = "0" + minute;}

                        if (v.getId() == R.id.select_time1){
                            meeting_time[3] = (char)(hourOfDay + 56);
                            meeting_time[4] = (char)(minuteOfDay + 56);
                            meetingTimeTextView.setText(AM_PM + " " + hour + " : " + minute);
                            meetingTimeSelectTextView2.performClick();
                        }
                        else {
                            farewell_time[3] = (char)(hourOfDay + 56);
                            farewell_time[4] = (char)(minuteOfDay + 56);
                            meetingTimeTextView2.setText(AM_PM + " " + hour + " : " + minute);
                        }
                    }
                };
                TimePickerDialog alert = new TimePickerDialog(getActivity(), mTimeSetListener, 0, 0, false);
                alert.show();
            }
        };

        meetingTimeSelectTextView.setOnClickListener(time_listener);
        meetingTimeSelectTextView2.setOnClickListener(time_listener);

        //해시태그 추가
        List<String> list = new ArrayList<>();
        hashTagWritingAdapter= new HashTagWritingAdapter(getActivity(), list);

        written_tag.setHasFixedSize(false);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);
        written_tag.setLayoutManager(layoutManager);
        written_tag.setAdapter(hashTagWritingAdapter);

        hashTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hashTagWritingAdapter.getItemCount() <10) {
                    String[] tags = hashTagEditText.getText().toString().split(" ,");
                    for(String tag : tags) {
                        if (tag.length() <= 5) {
                            if (tag.startsWith("#")) {
                                Pattern pattern = Pattern.compile("(#[가-힣ㄱ-ㅎa-zA-Z0-9]{1,6})");
                                Matcher matcher = pattern.matcher(tag);
                                while (matcher.find()) {
                                    hashTagWritingAdapter.addItem(matcher.group(1));
                                }

                            } else {
                                Pattern pattern = Pattern.compile("([가-힣ㄱ-ㅎa-zA-Z0-9]{1,6})");
                                Matcher matcher = pattern.matcher(tag);
                                while (matcher.find()) {
                                    hashTagWritingAdapter.addItem("#" + matcher.group(1));
                                }
                            }
                        }
                    }
                    hashTagEditText.setText("");
                }else{Toast.makeText(getActivity().getApplicationContext(), "최대 10개까지 추가가능합니다.", Toast.LENGTH_SHORT).show();}
            }
        });

        //사진 가져오는 코드
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), R.layout.support_simple_spinner_dropdown_item);
                adapter.add("사진 찍기");
                adapter.add("사진첩에서 가져오기");
                adapter.add("사진 지우기");

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        if(position == 0){
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, CAMERA_INTENT_CODE);
                        }
                        else if(position == 1){
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                            startActivityForResult(intent, GALLARY_INTENT_CODE);
                        }
                        else {
                            imagePath = "";
                            photoImageView.setImageDrawable(null);
                            dialogInterface.cancel();
                        }
                    }
                });
                builder.create().show();
            }
        });
        return view;
    }

    private void uploadWithPhoto(String uri, final String current_user_id){ // 공고 업로드
        StorageReference storageRef = storageReference;
        key = recruitmentDBReference.push().getKey();

        final RecruitmentItem recruitment_item = new RecruitmentItem();

        recruitment_item.setImagePath(uri);
        recruitment_item.setTitle(titleEditText.getText().toString());
        recruitment_item.setActivity(activityEditText.getText().toString());
        recruitment_item.setPlace(placeEditText.getText().toString());
        recruitment_item.setMeetingTime(meeting_time[0], meeting_time[1], meeting_time[2], meeting_time[3], meeting_time[4]);
        recruitment_item.setFarewellTime(farewell_time[0], farewell_time[1], farewell_time[2], farewell_time[3], farewell_time[4]);
        recruitment_item.setText(recruitmentTextEditText.getText().toString());

        String s_hashTag = "";
        List<String> list = hashTagWritingAdapter.getList();
        if(!list.isEmpty()){
            for (String str : list){
                if (s_hashTag.equals("")){s_hashTag = str;}
                else{s_hashTag = s_hashTag + " " + str;}
            }
        }
        recruitment_item.setHashTag(s_hashTag);
        recruitment_item.setDetail1(Integer.valueOf(detailEditText.getText().toString()));
        recruitment_item.setRecruitment_key(key);
        recruitment_item.setWriter_uid(current_user_id);
        recruitment_item.getApplicant_uid().add(current_user_id);
        recruitment_item.setNickname(userInfo.name);

        if(uri.equals("")){ // 사진 없으면
            userDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserInfo data = dataSnapshot.getValue(UserInfo.class);
                    recruitmentDBReference.child(key).setValue(recruitment_item);
                    final ArrayList<String> temp = new ArrayList<>();
                    temp.add(key);
                    temp.add(recruitment_item.getMeetingTime());
                    temp.add(recruitment_item.getFarewellTime());
                    data.recruit_progress.add(temp);
                    userDBReference.setValue(data);
                    userDBReference.child("ting").setValue(data.ting - 20);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
        }
        else { // 사진 있을 경우
            Uri file = Uri.fromFile(new File(uri));
            StorageReference reference = storageRef.child("images/recruit/" + key);
            UploadTask uploadTask = reference.putFile(file);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    fail[0] = 1;
                    // Toast에서 계속 오류. context를 불러오는데 실패함
                    //Toast.makeText(getActivity().getApplicationContext(), "사진 업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    // @SuppressWarnings("VisibleForTests")
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    recruitment_item.setImagePath(downloadUrl.toString());
                    userDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserInfo data = dataSnapshot.getValue(UserInfo.class);
                            recruitmentDBReference.child(key).setValue(recruitment_item);
                            final ArrayList<String> temp = new ArrayList<>();
                            temp.add(key);
                            temp.add(recruitment_item.getMeetingTime());
                            temp.add(recruitment_item.getFarewellTime());
                            data.recruit_progress.add(temp);
                            userDBReference.setValue(data);
                            userDBReference.child("ting").setValue(data.ting - 20);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                }
            });
        }
    }

    private class UploadAsyncTask extends AsyncTask<Void, Void, Void> { // 공고 업로드 하기. 이후 바꿀지 고민 - pre가 어차피 안나옴
        ProgressDialog dialog = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            //set message of the dialog
            dialog.setMessage("모집공고 업로딩...");
            //dialog.show();
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... args) {
            uploadWithPhoto(imagePath, firebaseAuth.getCurrentUser().getUid());
            return null;
        }
        @Override
        protected void onPostExecute(Void result) { //do UI work here
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("등록 완료");
            builder.setMessage("확인 버튼을 눌러주세요");
            if(fail[0] == 1){
                builder.setTitle("등록 실패");
                builder.setMessage("사진 등록에 실패했습니다.");
                fail[0] = 0;
            }
            builder.setCancelable(false);

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    Intent intent = new Intent(getActivity(), NavigationActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
            builder.create().show();
        }
    }

    public Uri getImageUri(Context context, Bitmap inImage) { //Uri 가져오는 함수
        //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) { //Uri 가지고 경로를 반환해주는 함수
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    public String getPath(Uri uri){ //사진의 경로를 가져오는 것
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, proj, null, null,null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(index);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {  //사진첩/카메라 접근
        if (requestCode == CAMERA_INTENT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                Bitmap image = (Bitmap) bundle.get("data");

                Uri uri = getImageUri(getActivity().getApplicationContext(), image);
                imagePath = getPath(uri);
                File file = new File(getRealPathFromURI(uri));
                photoImageView.setImageURI(Uri.fromFile(file));
            }
        }else {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    imagePath = getPath(data.getData());
                    File file = new File(imagePath);
                    photoImageView.setImageURI(Uri.fromFile(file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int strComp(char[] first, String second){ // 날짜 대소 비교
        for(int i = 0; i < 5; i++){ // 5 -> y, M, d, h, m
            if(first[i] > second.charAt(i)){
                return 1;
            } else if(first[i] < second.charAt(i)){
                return -1;
            }
        }
        return 0;
    }
}