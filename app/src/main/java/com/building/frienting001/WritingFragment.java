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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;

//글쓰기 화면 - 지역 설정 가져오기 : 다이얼로그 형식으로 노가다를 해야할 듯
public class WritingFragment extends Fragment {
    private ImageView photo;
    private String imagePath = "";
    private Button choosePhotoBtn;
    private EditText title;
    private TextView activity;
    private TextView select_activity;
    private List<String> selected_activity;
    private TextView activity2;
    private TextView select_activity2;
    private TextView helloDate;
    private TextView helloTime;
    private TextView goodbyeTime;
    private TextView select_date;
    private TextView select_time1;
    private TextView select_time2;
    private TextView detail_text;
    private TextView writing_city1;
    private TextView writing_city2;
    private EditText writing_city3;
    private TextView select_city1;
    private TextView select_city2;

    private LinearLayout detail;
    private TextView detail_1;
    private TextView detail_2;


    private EditText text;
    private EditText hashTag;
    private Button add_tag;
    private RecyclerView written_tag;
    private HashTagWritingAdapter hashTagWritingAdapter;
    private Button finishWriting;

    private DatabaseReference userDBReference;
    private UserInfo userInfo;

    private DatabaseReference recruitmentDBReference;
    private RecruitmentItem recruitmentItem;
    private String key;

    private StorageReference storageReference;

    private String pre_promise;
    private InputMethodManager imm;

    private FirebaseAnalytics firebaseAnalytics;

    private final int CAMERA_INTENT_CODE = 8000;
    private final int GALLARY_INTENT_CODE = 9000;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_writing, container, false);

        //선언부
        photo = (ImageView)view.findViewById(R.id.photo);
        choosePhotoBtn = (Button)view.findViewById(R.id.choosePhotoBtn);
        title = (EditText)view.findViewById(R.id.title);
        activity = (TextView)view.findViewById(R.id.activity);
        activity2 = (TextView)view.findViewById(R.id.activity2);
        select_activity = (TextView)view.findViewById(R.id.select_activity);
        select_activity2 = (TextView)view.findViewById(R.id.select_activity2);
        helloDate = (TextView)view.findViewById(R.id.helloDate);
        helloTime = (TextView)view.findViewById(R.id.helloTime);
        goodbyeTime = (TextView)view.findViewById(R.id.goodbyeTime);
        select_date = (TextView)view.findViewById(R.id.select_date);
        select_time1 = (TextView)view.findViewById(R.id.select_time1);
        select_time2 = (TextView)view.findViewById(R.id.select_time2);
        writing_city1 = (TextView)view.findViewById(R.id.writing_city1);
        writing_city2 = (TextView)view.findViewById(R.id.writing_city2);
        writing_city3 = (EditText) view.findViewById(R.id.writing_city3);
        select_city1 = (TextView)view.findViewById(R.id.select_city1);
        select_city2 = (TextView)view.findViewById(R.id.select_city2);
        text = (EditText)view.findViewById(R.id.text);
        hashTag = (EditText)view.findViewById(R.id.hashTag);
        add_tag = (Button)view.findViewById(R.id.add_tag);
        written_tag = (RecyclerView)view.findViewById(R.id.hashTag_written);
        finishWriting= (Button)view.findViewById(R.id.finishWriting);

        detail_text = (TextView)view.findViewById(R.id.writing_detail_text) ;
        detail = (LinearLayout)view.findViewById(R.id.writing_detail);
        detail_1 = (TextView)view.findViewById(R.id.detail_1);
        detail_2 = (TextView)view.findViewById(R.id.detail_2);

        LinearLayout layout = (LinearLayout)view.findViewById(R.id.total_layout);

        selected_activity =new ArrayList<String>();
        imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);

        //게시판DB 연결
        FirebaseApp recruitmentApp = FirebaseApp.getInstance("recruitment");
        recruitmentDBReference = FirebaseDatabase.getInstance(recruitmentApp).getReference().child("recruitments");
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://frienting001.appspot.com");

        //유저DB 연결
        FirebaseApp userApp = FirebaseApp.getInstance("user");
        userDBReference = FirebaseDatabase.getInstance(userApp).getReference().child("user");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(userApp);

        firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        //유저DB 연결
        Bundle bundle = getArguments();
        userInfo = (UserInfo)bundle.getSerializable("userInfo");

        Bundle params1 = new Bundle();
        params1.putString("UserUid", userInfo.getFirebaseUserUid());
        params1.putLong("OpenTime",System.currentTimeMillis());
        firebaseAnalytics.logEvent("WritingFragment", params1);

        //키보드 설정
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(title.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(writing_city3.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(hashTag.getWindowToken(), 0);
            }
        });

        //사진 가져오는 코드
        choosePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), R.layout.support_simple_spinner_dropdown_item);
                adapter.add("사진 찍기");
                adapter.add("사진첩에서 가져오기");
                adapter.add("사진 지우기");

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
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
                        }
                    }
                });
                builder.create().show();
            }
        });

        //만나는 장소 설정
        select_city1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makingListDialog(1, new String[]{"서울", "인천", "부산", "대구", "광주", "대전", "울산","제주", "경기", "강원", "충북", "충남", "전북", "전남", "경북", "경남"},2);
            }
        });
        select_city2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (writing_city1.getText().toString().equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "시/도 단위를 먼저 골라주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    switch (writing_city1.getText().toString()){
                        case "서울" :
                            String[] city1 = new String[]{"종로구", "중구", "용산구", "성동구", "광진구", "동대문구", "중랑구", "성북구", "강북구", "도봉구", "노원구", "은평구", "서대문구", "마포구", "양천구", "강서구", "구로구", "금천구", "영등포구", "동작구", "관악구", "서초구", "강남구", "송파구", "강동구"};
                            makingListDialog(2, city1,3);
                            break;
                        case "인천" :
                            String[] city2 = new String[]{"중구", "동구", "남구", "연수구", "남동구", "부평구", "서구", "강화군", "웅진군"};
                            makingListDialog(2, city2,2);
                            break;
                        case "부산" :
                            String[] city3 = new String[]{"중구", "서구", "동구", "영도구", "부산진구", "동래구", "남구", "북구", "해운대구", "사하구", "금정구", "강서구", "연제구", "수영구", "사상구", "기장구"};
                            makingListDialog(2, city3,2);
                            break;
                        case "대구" :
                            String[] city4 = new String[]{"중구", "동구", "서구", "남구", "북구", "수성구", "달서구", "달성군"};
                            makingListDialog(2, city4,2);
                            break;
                        case "광주" :
                            String[] city5 = new String[]{"동구", "남구", "서구", "북구", "광산구"};
                            makingListDialog(2, city5,2);
                            break;
                        case "대전" :
                            String[] city6 = new String[]{"동구", "중구", "서구", "유성구", "대덕구"};
                            makingListDialog(2, city6,2);
                            break;
                        case "울산" :
                            String[] city7 = new String[]{"동구", "중구", "북구", "남구", "울주군"};
                            makingListDialog(2, city7,2);
                            break;
                        case "제주" :
                            String[] city8 = new String[]{"제주시", "서귀포시"};
                            makingListDialog(2, city8,2);
                            break;
                        case "경기" :
                            String[] city9 = new String[]{"수원시", "성남시", "의정부시", "안양시", "부천시", "광명시", "평택시", "동두천시", "안산시", "고양시", "과천시", "구리시", " 남양주시", "오산시", "시흥시", "군포시", "의왕시", "하남시", "용인시", "파주시", "이천시", "김포시","화성시", "광주시", "양주시", "포천시","여주시","연천시", "가평군", "양평군"};
                            makingListDialog(2, city9,3);
                            break;
                        case "강원" :
                            String[] city10 = new String[]{"춘천시", "원주시", "강릉시", "동해시", "태백시", "속초시", "삼척시", "홍천군", "횡성군", "영월군", "평창군", "정선군", "철원군", "화천군", "양구군", "인제군", "고성군", "양양군"};
                            makingListDialog(2, city10,3);
                            break;
                        case "충북" :
                            String[] city11 = new String[]{"청주시", "충주시", "제천시", "청원군", "보은군", "영동군", "옥천군", "진천군" ,"증평군", "괴산군", "단양군", "음성군"};
                            makingListDialog(2, city11,3);
                            break;
                        case "충남" :
                            String[] city12 = new String[]{"천안시", "공주시", "아산시", "보령시", "서산시", "논산시", "계룡시", "금산군", "연기군", "부여군", "서천군", "청양군", "홍성군", "예산군", "태안군", "당진군"};
                            makingListDialog(2, city12,3);
                            break;
                        case "전북" :
                            String[] city13 = new String[]{"전주시", "군산시", "익산시", "정읍시", "남원시", "김제시", "완주군", "진안군", "무주군", "장수군", "임실군", "순창군", "고창군", "부안군"};
                            makingListDialog(2, city13,3);
                            break;
                        case "전남" :
                            String[] city14 = new String[]{"목포시", "여수시", "순천시", "나주시", "광양시", "담양군", "곡성군", "구례군", "고흥군", "보성군", "화순군", "장흥군", "강진군", "헤남군", "영암군", "무안군", "함평군", "영광군", "장성군", "완도군", "진도군", "신안군"};
                            makingListDialog(2, city14,3);
                            break;
                        case "경북" :
                            String[] city15 = new String[]{"포항시", "경주시", "김천시", "안동시", "구미시", "영주시", "영천시", "상주시", "문경시", "경산시", "군위군", "의성군", "청송군", "영양군", "청도군", "고량군", "성주군", "칠곡군", "예천군", "봉화군", "울진군", "울릉군"};
                            makingListDialog(2, city15,3);
                            break;
                        case "경남" :
                            String[] city16 = new String[]{"마산시", "진주시", "창원시", "진해시", "통영시", "사천시", "김해시", "밀양시", "거제시", "양산시", "의령군", "함안군", "창녕군", "고성군", "남해군", "하동군", "산청군", "함양군", "거창군", "합천군"};
                            makingListDialog(2, city16,3);
                            break;

                    }

                }
            }
        });

        List<String> list = new ArrayList<>();
        hashTagWritingAdapter= new HashTagWritingAdapter(getActivity(), list);

        written_tag.setHasFixedSize(false);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);
        written_tag.setLayoutManager(layoutManager);
        written_tag.setAdapter(hashTagWritingAdapter);

        //해시태그
        add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hashTagWritingAdapter.getItemCount() <10) {
                    String tag = hashTag.getText().toString();
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
                    hashTag.setText("");
                }else{Toast.makeText(getActivity().getApplicationContext(), "최대 10개까지 추가가능합니다.", Toast.LENGTH_SHORT).show();}
            }
        });



        //세부설정
        detail_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[]{"전체", "2", "3", "4", "5", "6", "7", "8","9","10"};
                makingDialog(items, 1);
            }
        });

        detail_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[]{"전체", "20", "25", "30", "35", "40", "45", "50"};
                makingDialog(items, 2);
            }
        });

        //글쓰기 버튼

        finishWriting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (title.getText().length() < 5 | activity.getText().length() < 2 | writing_city1.getText().length() < 2
                        | writing_city2.getText().length() < 2 | writing_city3.getText().length() < 2 | helloDate.getText().length() < 2
                        | helloTime.getText().length() < 2 | goodbyeTime.getText().length() < 2 | text.getText().length() < 10) {
                    Toast.makeText(getActivity().getApplicationContext(), "잘못된 형식입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    if(checkingTime()) {
                        //정보들을 DB와 Storage에 저장
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("매칭 신청을 완료하시겠습니까?");
                        builder.setMessage("확인 버튼을 누르실 경우 20팅이 소모됩니다.");
                        builder.setCancelable(false);

                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (userInfo.getTing() < 20) {
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
                                            intent.putExtra("userInfo", userInfo);
                                            startActivity(intent);


                                        }
                                    });
                                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    builder.create().show();

                                } else {
                                    dialogInterface.cancel();
                                    userInfo.setTing(userInfo.getTing() - 20);
                                    UploadAsyncTask uploadAsyncTask = new UploadAsyncTask();
                                    uploadAsyncTask.execute();
                                }
                            }
                        });
                        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.create().show();
                    }else{
                        //겹치는 약속을 보여주기
                        //정보들을 DB와 Storage에 저장
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("해당 시간에 약속이 존재합니다.");
                        builder.setCancelable(false);

                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        builder.create().show();
                    }
                }

            }
        });

        //활동선택을 하는 다이얼로그
        select_activity.setOnClickListener(new View.OnClickListener() {

            StaggeredGridLayoutManager layoutManager;
            RecyclerView recyclerView;

            @Override
            public void onClick(View v) {
                makingListDialog(4, new String[]{"스포츠", "학업", "엔터테인먼트(취미)", "식사"}, 2);
            }
        });

        select_activity2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.getText().toString().equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(), "활동 카테고리을 먼저 골라주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    switch (activity.getText().toString()){
                        case "스포츠" :
                            String[] interest1 = new String[]{"축구", "풋살","농구", "당구", "탁구", "족구", "볼링", "골프", "야구", "배드민턴", "테니스", "수영", "요트", "카누", "서핑", "스쿠버다이빙", "빠지", "스케이트보드", "따릉이", "자전거", "헬스", "요가", "필라테스", "달리기","주짓수", "권투", "클라이밍", "스카이다이빙", "패러글라이딩"};
                            makingListDialog(3, interest1, 3);
                            break;
                        case "학업" :
                            String[] interest2 = new String[]{"코딩", "주식", "고시", "면접", "자격증", "취업"};
                            makingListDialog(3,interest2, 2);
                            break;
                        case "엔터테인먼트(취미)" :
                            String[] interest3 = new String[]{"영화", "뮤지컬", "놀이동산", "콘서트", "전시회", "쇼핑", "미술관", "꽃&자수&공예", "게임", "오락실", "여행", "댄스", "클럽", "독서", "요리&베이킹", "만화&애니"};
                            makingListDialog(3, interest3, 3);
                            break;
                        case "식사" :
                            String[] interest4 = new String[]{"주류", "한식", "중식", "일식", "양식", "베트남", "타이", "멕시코", "퓨전", "이색음식", "건강식", "채식", "기타"};
                            makingListDialog(3, interest4, 3);
                            break;
                    }
                }
            }
        });

        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR) ;
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = (year-2000) +"/" + (month+1) + "/" + dayOfMonth ;
                        helloDate.setText(date);
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), mDateSetListener, year, month, day);
                dialog.show();
            }
        });



        //시간선택하는 다이얼로그
        View.OnClickListener time_listener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final View now_view = v;
                TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
                        String AM_PM ;
                        if(hourOfDay < 12) {
                            AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                            hourOfDay -= 12;
                        }

                        String hour = String.valueOf(hourOfDay);
                        String minute = String.valueOf(minuteOfDay);

                        if(hour.length() == 1) {hour = "0" + hour;}

                        if(minute.length() ==1) {minute = "0" + minute;}

                        if (v.getId() == R.id.select_time1){helloTime.setText(AM_PM + " " + hour + " : " + minute);}
                        else {goodbyeTime.setText(AM_PM + " " + hour + " : " + minute);}
                    }
                };

                TimePickerDialog alert = new TimePickerDialog(getActivity(), mTimeSetListener, 0, 0, false);
                alert.show();

            }
        };

        select_time1.setOnClickListener(time_listener);
        select_time2.setOnClickListener(time_listener);

        return view;
    }

    //사진첩/카메라 접근
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_INTENT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                Bitmap image = (Bitmap) bundle.get("data");
                //photo.setImageBitmap(image);

                Uri uri = getImageUri(getActivity().getApplicationContext(), image);
                imagePath  =getPath(uri);
                File file = new File(getRealPathFromURI(uri));
                photo.setImageURI(Uri.fromFile(file));

            }
        }else {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    imagePath = getPath(data.getData());
                    File file = new File(imagePath);
                    photo.setImageURI(Uri.fromFile(file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //사진의 경로를 가져오는 것
    public String getPath(Uri uri){
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(getActivity(), uri, proj, null, null,null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(index);
    }

    //사진이 있을 때 저장
    private void uploadWithPhoto(String uri){

        StorageReference storageRef = storageReference;
        key = recruitmentDBReference.push().getKey();

        String writing_recruitment = userInfo.getRecruiting();
        if(writing_recruitment.equals("")){writing_recruitment = key;
        }else{writing_recruitment = writing_recruitment + "/" + key;}

        userInfo.setRecruiting(writing_recruitment);

        Uri file = Uri.fromFile(new File(uri));
        StorageReference reference = storageRef.child("images/"+key);
        UploadTask uploadTask = reference.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getActivity().getApplicationContext(), "사진 업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                @SuppressWarnings("VisibleForTests")
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                String s_imagePath = downloadUrl.toString();
                String s_title = title.getText().toString();
                String s_activity = activity.getText().toString() + " " + activity2.getText().toString();
                String s_city1 = writing_city1.getText().toString();
                String s_city2 = writing_city2.getText().toString();
                String s_city3 = writing_city3.getText().toString();
                String s_helloDate = helloDate.getText().toString();
                String s_helloTime = helloTime.getText().toString();
                String s_goodbyeTime = goodbyeTime.getText().toString();
                String s_text = text.getText().toString();

                String s_hashTag = "";
                List<String> list = hashTagWritingAdapter.getList();
                if(!list.isEmpty()){
                    for (String str : list){
                        if (s_hashTag.equals("")){s_hashTag = str;}
                        else{s_hashTag = s_hashTag + " " + str;}
                    }
                }

                String time ="";

                String s_key= key;
                String s_writer_uid = userInfo.getFirebaseUserUid();
                String s_writer_nickname = userInfo.getNickname();
                String s_applicant = "";

                String i_detail1 = detail_1.getText().toString().split(" ")[0];
                String i_detail2 = detail_2.getText().toString().split(" ")[0];

                if(i_detail1.equals("전체")){i_detail1 = "10";}
                if(i_detail2.equals("전체")){i_detail2 = "50";}

                int ii_detail1 = Integer.valueOf(i_detail1);
                int ii_detail2 = Integer.valueOf(i_detail2);

                ReviewDialogItem reviewDialogItem = userInfo.getReview();

                String u_fromto = makingFromTo(s_helloDate, s_helloTime, s_goodbyeTime);
                String fromto = userInfo.getPromiseTime();
                if(fromto.equals("")){fromto = u_fromto;}
                else{fromto = fromto + "/" + u_fromto;}
                userInfo.setPromiseTime(fromto);

                recruitmentItem = new RecruitmentItem(s_imagePath, s_title, s_activity, s_city1, s_city2, s_city3, s_helloDate, s_helloTime,s_goodbyeTime, s_text, s_hashTag,  s_writer_nickname, reviewDialogItem, s_key, s_writer_uid, s_applicant, ii_detail1, ii_detail2, false);
                recruitmentDBReference.child(key).setValue(recruitmentItem);
                userDBReference.child(userInfo.getFirebaseUserUid()).setValue(userInfo);

                Bundle params1 = new Bundle();
                params1.putString("UserUid", userInfo.getFirebaseUserUid());
                params1.putString("RecruitmentUid", recruitmentItem.getRecruitment_key());
                params1.putLong("WritingTime",System.currentTimeMillis());
                firebaseAnalytics.logEvent("WritingFragment", params1);

            }
        });
    }

    //사진이 없을 때 저장
    private void uploadWithoutPhoto(){
        key = recruitmentDBReference.push().getKey();

        String s_imagePath = "";
        String s_title = title.getText().toString();
        String s_activity = activity.getText().toString() + " " + activity2.getText().toString();
        String s_city1 = writing_city1.getText().toString();
        String s_city2 = writing_city2.getText().toString();
        String s_city3 = writing_city3.getText().toString();
        String s_helloDate = helloDate.getText().toString();
        String s_helloTime = helloTime.getText().toString();
        String s_goodbyeTime = goodbyeTime.getText().toString();
        String s_text = text.getText().toString();

        String s_hashTag = "";
        List<String> list = hashTagWritingAdapter.getList();
        if(!list.isEmpty()){
            for (String str : list){
                if (s_hashTag.equals("")){s_hashTag = str;}
                else{s_hashTag = s_hashTag + " " + str;}
            }
        }

        String s_key= key;
        String s_writer_uid = userInfo.getFirebaseUserUid();
        String s_writer_nickname = userInfo.getNickname();
        String s_applicant = "";
        ReviewDialogItem reviewDialogItem = userInfo.getReview();

        String i_detail1 = detail_1.getText().toString().split(" ")[0];
        String i_detail2 = detail_2.getText().toString().split(" ")[0];

        if(i_detail1.equals("전체")){i_detail1 = "10";}
        if(i_detail2.equals("전체")){i_detail2 = "50";}

        int ii_detail1 = Integer.valueOf(i_detail1);
        int ii_detail2 = Integer.valueOf(i_detail2);

        String writing_recruitment = userInfo.getRecruiting();
        if(writing_recruitment.equals("")){writing_recruitment = key;}
        else{writing_recruitment = writing_recruitment + "/" + key;}
        userInfo.setRecruiting(writing_recruitment);


        String u_fromto = makingFromTo(s_helloDate, s_helloTime, s_goodbyeTime);
        String fromto = userInfo.getPromiseTime();
        if(fromto.equals("")){fromto = u_fromto;}
        else{fromto = fromto + "/" + u_fromto;}
        userInfo.setPromiseTime(fromto);


        recruitmentItem = new RecruitmentItem(s_imagePath, s_title, s_activity, s_city1, s_city2, s_city3, s_helloDate, s_helloTime,s_goodbyeTime, s_text, s_hashTag,  s_writer_nickname, reviewDialogItem, s_key, s_writer_uid, s_applicant, ii_detail1, ii_detail2, false);
        recruitmentDBReference.child(key).setValue(recruitmentItem);
        userDBReference.child(userInfo.getFirebaseUserUid()).setValue(userInfo);

        Bundle params1 = new Bundle();
        params1.putString("UserUid", userInfo.getFirebaseUserUid());
        params1.putString("RecruitmentUid", recruitmentItem.getRecruitment_key());
        params1.putLong("WritingTime",System.currentTimeMillis());
        firebaseAnalytics.logEvent("WritingFragment", params1);

    }

    //Uri 가져오는 함수
    public Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //Uri 가지고 경로를 반환해주는 함수
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    //글쓰기
    private class UploadAsyncTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            //set message of the dialog
            dialog.setMessage("모집공고 업로딩...");
            //dialog.show();
            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {
            if(imagePath.equals("")){
                uploadWithoutPhoto();
            }else {
                uploadWithPhoto(imagePath);
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            //do UI work here
            //BottomNavigationView bottomNavigationView = (BottomNavigationView)getActivity().findViewById(R.id.navigation);
            //bottomNavigationView.setSelectedItemId(R.id.navigation_main);

            Intent intent = new Intent(getActivity(), NavigationActivity.class);
            intent.putExtra("userInfo", userInfo);
            startActivity(intent);

            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }

    private void makingDialog(final String[] items, final int flag){

        final int[] selectedIndex = {0};

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("인원수를 선택하세요.")
                .setSingleChoiceItems(items,
                        0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedIndex[0] = which;

                            }
                        })
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(flag == 1){detail_1.setText(items[selectedIndex[0]] + " ▼");}
                        else{detail_2.setText(items[selectedIndex[0]] + " ▼");}
                    }
                }).create().show();
    }

    private void makingListDialog(final int id, String[] lst, int num){

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

        final List<String> selected_activity =new ArrayList<String>();
        final ArrayList<String> activity_listitem = new ArrayList<>();
        final String[] activity_lst = lst;
        for (String act: activity_lst){
            activity_listitem.add(act);
        }
        DialogListAdapter adapter;
        adapter = new DialogListAdapter(getActivity() ,activity_listitem);
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


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(convertView);
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String string_temp = "";

                for(String idx : selected_activity){
                    string_temp =idx;
                }

                switch (id){
                    case 1 : writing_city1.setText(string_temp);break;
                    case 2 : writing_city2.setText(string_temp);break;
                    case 3 : activity2.setText(string_temp);break;
                    case 4 :
                        activity.setText(string_temp);
                        activity2.setText("");
                        break;
                }
            }
        });
        alertDialog.show();
    }

    private boolean checkingTime(){
        String time = userInfo.getPromiseTime();

        if(time.equals("")){return true;}

        String w_from = "";
        String w_to = "";

        String s_helloDate = helloDate.getText().toString();
        String[] date = s_helloDate.split("/");
        w_from = date[0] + date[1] + date[2];
        w_to = date[0] + date[1] + date[2];

        String s_helloTime = helloTime.getText().toString();
        String[] hello = s_helloTime.split(" ");

        int part1 = 0;
        if(!hello[0].equals("AM")){part1 = 12;}
        if(Integer.parseInt(hello[1]) + part1 <10){
            w_from = w_from + "0"+ String.valueOf(Integer.parseInt(hello[1]) + part1) + hello[3];
        }else {
            w_from = w_from + String.valueOf(Integer.parseInt(hello[1]) + part1) + hello[3];
        }

        String s_goodbyeTime = goodbyeTime.getText().toString();
        String[] bye = s_goodbyeTime.split(" ");
        int part2 = 0;
        if(!bye[0].equals("AM")){part2 = 12;}
        if(Integer.parseInt(bye[1]) + part2 <10){
            w_to = w_to + "0"+ String.valueOf(Integer.parseInt(bye[1]) + part2) + bye[3];
        }else {
            w_to = w_to + String.valueOf(Integer.parseInt(bye[1]) + part2) + bye[3];
        }

        String[] time_lst = time.split("/");
        List<String> time_array = Arrays.asList(time_lst);

        for (String str : time_array){
            String[] from_to = str.split("~");
            int from = Integer.parseInt(from_to[0]);
            int to = Integer.parseInt(from_to[1]);

            Log.d("11111111111111111111111", w_to);
            Log.d("11111111111111111111111", w_from);
            Log.d("11111111111111111111111",""+ to);
            Log.d("11111111111111111111111", ""+from);

            if((to >= Integer.parseInt(w_to) & from <  Integer.parseInt(w_to)) |  ((to > Integer.parseInt(w_from) & from <=  Integer.parseInt(w_from)))){
                return false;
            }
        }
        return true;
    }

    private String makingFromTo(String Date, String h_time, String g_time){
        String w_from = "";
        String w_to = "";

        String[] date = Date.split("/");
        w_from = date[0] + date[1] + date[2];
        w_to = date[0] + date[1] + date[2];

        String[] hello = h_time.split(" ");
        int part1 = 0;
        if(!hello[0].equals("AM")){part1 = 12;}

        if(Integer.parseInt(hello[1]) + part1 <10){
            w_from = w_from + "0"+ String.valueOf(Integer.parseInt(hello[1]) + part1) + hello[3];
        }else {
            w_from = w_from + String.valueOf(Integer.parseInt(hello[1]) + part1) + hello[3];
        }

        String[] bye = g_time.split(" ");
        int part2 = 0;
        if(!bye[0].equals("AM")){part2 = 12;}

        if(Integer.parseInt(bye[1]) + part2 <10){
            w_to = w_to + "0"+ String.valueOf(Integer.parseInt(bye[1]) + part2) + bye[3];
        }else {
            w_to = w_to + String.valueOf(Integer.parseInt(bye[1]) + part2) + bye[3];
        }

        return w_from + "~" + w_to;
    }

}
