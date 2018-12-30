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
import android.util.Log;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import static android.content.Context.INPUT_METHOD_SERVICE;

public class WritingFragment extends Fragment {
    private Map<String, String[]> dialog_table;
    private ImageView photoImageView;
    private String imagePath = "";
    private Button photoButton;
    private EditText titleEditText;
    private TextView activityTextView;
    private TextView activitySelectTextView;
    private TextView activityTextView2;
    private TextView activitySelectTextView2;
    private TextView meetingDateTextView;
    private TextView meetingTimeTextView;
    private TextView meetingTimeTextView2;
    private TextView meetingDateSelectTextView;
    private TextView meetingTimeSelectTextView;
    private TextView meetingTimeSelectTextView2;
    private TextView meetingCityTextView;
    private TextView meetingCityTextView2;
    private EditText meetingCityEditView;
    private TextView meetingCitySelectTextview;
    private TextView meetingCitySelectTextView2;

    private TextView detailTextView;
    private TextView detailTectVIew2;

    private EditText recruitmentTextEditText;
    private EditText hashtagEditText;
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
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_writing, container, false);

        dialog_table = new HashMap<>(); // 파일로 만들어 읽어들여도 될듯
        dialog_table.put("도시", new String[]{"서울", "인천", "부산", "대구", "광주", "대전", "울산","제주", "경기", "강원", "충북", "충남", "전북", "전남", "경북", "경남"});
        dialog_table.put("서울", new String[]{"종로구", "중구", "용산구", "성동구", "광진구", "동대문구", "중랑구", "성북구", "강북구", "도봉구", "노원구", "은평구", "서대문구", "마포구", "양천구", "강서구", "구로구", "금천구", "영등포구", "동작구", "관악구", "서초구", "강남구", "송파구", "강동구"});
        dialog_table.put("인천", new String[]{"중구", "동구", "남구", "연수구", "남동구", "부평구", "서구", "강화군", "웅진군"});
        dialog_table.put("부산", new String[]{"중구", "서구", "동구", "영도구", "부산진구", "동래구", "남구", "북구", "해운대구", "사하구", "금정구", "강서구", "연제구", "수영구", "사상구", "기장구"});
        dialog_table.put("대구", new String[]{"중구", "동구", "서구", "남구", "북구", "수성구", "달서구", "달성군"});
        dialog_table.put("광주", new String[]{"동구", "남구", "서구", "북구", "광산구"});
        dialog_table.put("대전", new String[]{"동구", "중구", "서구", "유성구", "대덕구"});
        dialog_table.put("울산", new String[]{"동구", "중구", "북구", "남구", "울주군"});
        dialog_table.put("제주", new String[]{"제주시", "서귀포시"});
        dialog_table.put("경기", new String[]{"수원시", "성남시", "의정부시", "안양시", "부천시", "광명시", "평택시", "동두천시", "안산시", "고양시", "과천시", "구리시", " 남양주시", "오산시", "시흥시", "군포시", "의왕시", "하남시", "용인시", "파주시", "이천시", "김포시","화성시", "광주시", "양주시", "포천시","여주시","연천시", "가평군", "양평군"});
        dialog_table.put("강원", new String[]{"춘천시", "원주시", "강릉시", "동해시", "태백시", "속초시", "삼척시", "홍천군", "횡성군", "영월군", "평창군", "정선군", "철원군", "화천군", "양구군", "인제군", "고성군", "양양군"});
        dialog_table.put("충북", new String[]{"청주시", "충주시", "제천시", "청원군", "보은군", "영동군", "옥천군", "진천군" ,"증평군", "괴산군", "단양군", "음성군"});
        dialog_table.put("충남", new String[]{"천안시", "공주시", "아산시", "보령시", "서산시", "논산시", "계룡시", "금산군", "연기군", "부여군", "서천군", "청양군", "홍성군", "예산군", "태안군", "당진군"});
        dialog_table.put("전북", new String[]{"전주시", "군산시", "익산시", "정읍시", "남원시", "김제시", "완주군", "진안군", "무주군", "장수군", "임실군", "순창군", "고창군", "부안군"});
        dialog_table.put("전남", new String[]{"목포시", "여수시", "순천시", "나주시", "광양시", "담양군", "곡성군", "구례군", "고흥군", "보성군", "화순군", "장흥군", "강진군", "헤남군", "영암군", "무안군", "함평군", "영광군", "장성군", "완도군", "진도군", "신안군"});
        dialog_table.put("경북", new String[]{"포항시", "경주시", "김천시", "안동시", "구미시", "영주시", "영천시", "상주시", "문경시", "경산시", "군위군", "의성군", "청송군", "영양군", "청도군", "고량군", "성주군", "칠곡군", "예천군", "봉화군", "울진군", "울릉군"});
        dialog_table.put("경남", new String[]{"마산시", "진주시", "창원시", "진해시", "통영시", "사천시", "김해시", "밀양시", "거제시", "양산시", "의령군", "함안군", "창녕군", "고성군", "남해군", "하동군", "산청군", "함양군", "거창군", "합천군"});

        dialog_table.put("인원", new String[]{"2", "3", "4", "5", "6", "7", "8", "9", "10"});
        dialog_table.put("나이", new String[]{"전체", "20", "25", "30", "35", "40", "45", "50"});

        dialog_table.put("활동", new String[]{"스포츠", "학업", "엔터테인먼트(취미)", "식사"});
        dialog_table.put("스포츠", new String[]{"축구", "풋살","농구", "당구", "탁구", "족구", "볼링", "골프", "야구", "배드민턴", "테니스", "수영", "요트", "카누", "서핑", "스쿠버다이빙", "빠지", "스케이트보드", "따릉이", "자전거", "헬스", "요가", "필라테스", "달리기","주짓수", "권투", "클라이밍", "스카이다이빙", "패러글라이딩"});
        dialog_table.put("학업", new String[]{"코딩", "주식", "고시", "면접", "자격증", "취업"});
        dialog_table.put("엔터테인먼트(취미)", new String[]{"영화", "뮤지컬", "놀이동산", "콘서트", "전시회", "쇼핑", "미술관", "꽃&자수&공예", "게임", "오락실", "여행", "댄스", "클럽", "독서", "요리&베이킹", "만화&애니"});
        dialog_table.put("식사", new String[]{"주류", "한식", "중식", "일식", "양식", "베트남", "타이", "멕시코", "퓨전", "이색음식", "건강식", "채식", "기타"});

        //선언부
        layout = (LinearLayout)view.findViewById(R.id.total_layout);
        photoImageView = (ImageView)view.findViewById(R.id.photo);
        photoButton = (Button)view.findViewById(R.id.choosePhotoBtn);
        titleEditText = (EditText)view.findViewById(R.id.title);
        activityTextView = (TextView)view.findViewById(R.id.activity);
        activityTextView2 = (TextView)view.findViewById(R.id.activity2);
        activitySelectTextView = (TextView)view.findViewById(R.id.select_activity);
        activitySelectTextView2 = (TextView)view.findViewById(R.id.select_activity2);
        meetingDateTextView = (TextView)view.findViewById(R.id.helloDate);
        meetingTimeTextView = (TextView)view.findViewById(R.id.helloTime);
        meetingTimeTextView2 = (TextView)view.findViewById(R.id.goodbyeTime);
        meetingDateSelectTextView = (TextView)view.findViewById(R.id.select_date);
        meetingTimeSelectTextView = (TextView)view.findViewById(R.id.select_time1);
        meetingTimeSelectTextView2 = (TextView)view.findViewById(R.id.select_time2);
        meetingCityTextView = (TextView)view.findViewById(R.id.writing_city1);
        meetingCityTextView2 = (TextView)view.findViewById(R.id.writing_city2);
        meetingCityEditView = (EditText) view.findViewById(R.id.writing_city3);
        meetingCitySelectTextview = (TextView)view.findViewById(R.id.select_city1);
        meetingCitySelectTextView2 = (TextView)view.findViewById(R.id.select_city2);
        recruitmentTextEditText = (EditText)view.findViewById(R.id.text);
        hashtagEditText = (EditText)view.findViewById(R.id.hashTag);
        finishButton = (Button)view.findViewById(R.id.finishWriting);

        detailTextView = (TextView)view.findViewById(R.id.detail_1);
        detailTectVIew2 = (TextView)view.findViewById(R.id.detail_2);

        imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);

        //유저DB & 공고DB, 공고 스토리지 연결
        userApp = FirebaseApp.getInstance("user");
        firebaseAuth = FirebaseAuth.getInstance(userApp);
        String user_id = firebaseAuth.getCurrentUser().getUid();
        userDBReference = FirebaseDatabase.getInstance(userApp).getReference().child("user").child(user_id);
        recruitmentDBReference = FirebaseDatabase.getInstance(userApp).getReference().child("recruitment");
        storageReference = FirebaseStorage.getInstance(userApp).getReferenceFromUrl("gs://frientinguser.appspot.com");

        layout.setOnClickListener(new View.OnClickListener() { // 키보드 설정
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(meetingCityEditView.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(recruitmentTextEditText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(hashtagEditText.getWindowToken(), 0);
            }
        });

        meetingCitySelectTextview.setOnClickListener(new View.OnClickListener() { //만나는 장소 설정
            @Override
            public void onClick(View v) {
                makingListDialog(1, dialog_table.get("도시"),2);
            }
        });
        meetingCitySelectTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (meetingCityTextView.getText().toString().equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "시/도 단위를 먼저 골라주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    String temp = meetingCityTextView.getText().toString();
                    makingListDialog(2, dialog_table.get(temp), 3);
                }
            }
        });

        detailTextView.setOnClickListener(new View.OnClickListener() { //세부설정
            @Override
            public void onClick(View v) {
                makingDialog(dialog_table.get("인원"), 1);
            }
        });
        detailTectVIew2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makingDialog(dialog_table.get("나이"), 2);
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 추후에 분기 나누기 - 무엇이 잘못되었는지 알 수 있또록
                /*if (titleEditText.getText().length() < 5 | activityTextView.getText().length() < 2 | meetingCityTextView.getText().length() < 2
                        | meetingCityTextView2.getText().length() < 2 | meetingCityEditView.getText().length() < 2 | meetingDateTextView.getText().length() < 2
                        | meetingTimeTextView.getText().length() < 2 | meetingTimeTextView2.getText().length() < 2 | recruitmentTextEditText.getText().length() < 10) {
                    Toast.makeText(getActivity().getApplicationContext(), "잘못된 형식입니다.", Toast.LENGTH_SHORT).show();
                }
                else {*/
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("매칭 신청을 완료하시겠습니까?");
                        builder.setMessage("확인 버튼을 누르실 경우 20팅이 소모됩니다.");
                        builder.setCancelable(false);

                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                userDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        UserInfo user_data = dataSnapshot.getValue(UserInfo.class);
                                        for(int i = 0; i < user_data.recruit_progress.size(); i++) {
                                            if (strComp(meeting_time, user_data.recruit_progress.get(i).get(2)) == 1 ||
                                                    strComp(farewell_time, user_data.recruit_progress.get(i).get(1)) == -1) { // 겹치는 약속 없음
                                            } else {
                                                //겹치는 약속을 보여주기 -> i번째 recruit_process 그냥 보여주면 됨.
                                                Toast.makeText(getActivity().getApplicationContext(), "겹치는 약속이 있습니다.", Toast.LENGTH_SHORT).show();
                                                return; // 마무리
                                            }
                                        }
                                        int ting = user_data.Ting;
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
            //}
        });

        activitySelectTextView.setOnClickListener(new View.OnClickListener() { //활동선택을 하는 다이얼로그
            @Override
            public void onClick(View v) {
                makingListDialog(4, dialog_table.get("활동"), 2);
            }
        });
        activitySelectTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityTextView.getText().toString().equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "활동 카테고리을 먼저 골라주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    String temp = activityTextView.getText().toString();
                    makingListDialog(3, dialog_table.get(temp), 3);
                }
            }
        });

        meetingDateSelectTextView.setOnClickListener(new View.OnClickListener() { // 날짜 선택
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

        photoButton.setOnClickListener(new View.OnClickListener() { //사진 가져오는 코드
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
        recruitment_item.setActivity(activityTextView.getText().toString(), activityTextView2.getText().toString());
        recruitment_item.setPlace(meetingCityTextView.getText().toString(), meetingCityTextView2.getText().toString(), meetingCityEditView.getText().toString());
        recruitment_item.setHelloTime(meeting_time[0], meeting_time[1], meeting_time[2], meeting_time[3], meeting_time[4]);
        recruitment_item.setGoodbyeTime(farewell_time[0], farewell_time[1], farewell_time[2], farewell_time[3], farewell_time[4]);
        recruitment_item.setText(recruitmentTextEditText.getText().toString());
        recruitment_item.setHashTag(hashtagEditText.getText().toString());
        recruitment_item.setRecruitment_key(key);
        recruitment_item.setWriter_uid(current_user_id);
        //recruitment_item.setNickname(firebaseAuth.getCurrentUser().getDisplayName());
        recruitment_item.getApplicant_uid().add(current_user_id);

        String s_detail2 = detailTectVIew2.getText().toString();
        int i_detail2 = 30; // 추후 수정 지금은 무조건 30
        if(s_detail2.equals("전체")){
            i_detail2 = 50;
        }
        recruitment_item.setDetail1(Integer.parseInt(detailTextView.getText().toString()));
        recruitment_item.setDetail2(i_detail2);
        //ReviewDialogItem reviewDialogItem = userInfo.getReview();

        if(uri.equals("")){ // 사진 없으면
            userDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserInfo data = dataSnapshot.getValue(UserInfo.class);
                    recruitmentDBReference.child(key).setValue(recruitment_item);
                    final ArrayList<String> temp = new ArrayList<>();
                    temp.add(key);
                    temp.add(recruitment_item.getHelloTime());
                    temp.add(recruitment_item.getGoodbyeTime());
                    data.recruit_progress.add(temp);
                    userDBReference.setValue(data);
                    userDBReference.child("Ting").setValue(data.Ting - 20);
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
                            temp.add(recruitment_item.getHelloTime());
                            temp.add(recruitment_item.getGoodbyeTime());
                            data.recruit_progress.add(temp);
                            userDBReference.setValue(data);
                            userDBReference.child("Ting").setValue(data.Ting - 20);
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

    private void makingDialog(final String[] items, final int flag){
        // 나중에 나눠야 함. 연령제한에도 인원 수 선택하라 나옴
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("인원 수를 선택하세요.").setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                if(flag == 1){
                    detailTextView.setText(items[position]); // 버튼 추가할 것(세모모양 원래 있었는데 없앴음. 인트 파싱 오류때문에)
                }
                else{
                    detailTectVIew2.setText(items[position]);
                }
            }
        }).create().show();
    }

    private void makingListDialog(final int id, final String[] lst, int num){
        StaggeredGridLayoutManager layoutManager;
        RecyclerView recyclerView;

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View convertView = (View) inflater.inflate(R.layout.select_activity_dialog_layout, null);
        TextView dialog_name = (TextView)convertView.findViewById(R.id.dialog_name);
        final TextView dialog_selected = (TextView)convertView.findViewById(R.id.dialog_selected);

        if(id != 3) {
            dialog_name.setText("지역 선택");
        }else{
            dialog_name.setText("카테고리 선택");
        }

        final ArrayList<String> activity_lst = new ArrayList<>(Arrays.asList(lst));
        DialogListAdapter adapter;
        adapter = new DialogListAdapter(getActivity(), activity_lst);
        adapter.setItemClick(new DialogListAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                dialog_selected.setText(activity_lst.get(position));
            }
        });

        recyclerView = (RecyclerView)convertView.findViewById(R.id.dialog_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(num, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(convertView);
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String string_temp = dialog_selected.getText().toString();
                switch (id){
                    case 1 :
                        meetingCityTextView.setText(string_temp);
                        makingListDialog(2, dialog_table.get(string_temp), 3);
                        break;
                    case 2 :
                        meetingCityTextView2.setText(string_temp);
                        break;
                    case 3 :
                        activityTextView2.setText(string_temp);
                        break;
                    case 4 :
                        activityTextView.setText(string_temp);
                        makingListDialog(3, dialog_table.get(string_temp), 3);
                        break;
                }
            }
        });
        alertDialog.show();
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