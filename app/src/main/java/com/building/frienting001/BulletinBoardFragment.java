package com.building.frienting001;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class BulletinBoardFragment extends Fragment {
    private StaggeredGridLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private View view;
    private BulletinListAdapter adapter;

    private boolean loading = true;
    private int pastVisibleItems,visibleItemCount, totalItemCount;
    private int visibleThreshold = 5;
    private int previousTotal = 0;

    private EditText searchText;
    private Button searchBtn;
    private ImageView searchMode;
    private String mode = "title";

    private FirebaseAnalytics firebaseAnalytics;
    private DatabaseReference databaseReference;
    private ProgressDialog dialog;

    private Bundle bundle;
    private UserInfo userInfo;

    private InputMethodManager imm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        //선언부
        view = inflater.inflate(R.layout.fragment_bulletin_board, container, false);
        FirebaseApp recruitmentApp = FirebaseApp.getInstance("recruitment");
        databaseReference = FirebaseDatabase.getInstance(recruitmentApp).getReference("recruitments");

        firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        searchText = (EditText)view.findViewById(R.id.searchText);
        searchBtn = (Button)view.findViewById(R.id.searchBtn);
        searchMode = (ImageView) view.findViewById(R.id.searchMode);
        LinearLayout layout = (LinearLayout)view.findViewById(R.id.total_layout2);

        bundle = getArguments();
        userInfo = (UserInfo) bundle.getSerializable("userInfo");

        imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
            }
        });

        //로그
        Bundle params1 = new Bundle();
        params1.putString("UserUid", userInfo.getFirebaseUserUid());
        params1.putLong("OpenTime", System.currentTimeMillis());
        firebaseAnalytics.logEvent("BulletinBoardFragment", params1);


        //전체보기 or 메인 화면에서 검색, 입력이 없을 경우 ""로 검색되기 때문에 모두 가져옴
        String searchTextString = getArguments().getString("searchTextString");
        searching(searchTextString);

        //검색버튼을 누르면 실행되는 코드
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTextString = searchText.getText().toString();
                searching(searchTextString);

                Bundle params = new Bundle();
                params.putString("UserUid", userInfo.getFirebaseUserUid());
                params.putString("SearchBtn", searchText.getText().toString());
                params.putString("SearchMode", mode);
                params.putLong("SearchTime", System.currentTimeMillis());
                firebaseAnalytics.logEvent("BulletinBoardFragment", params);
            }
        });

        searchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRadioButtonDialog();

            }
        });

        return view;
    }

    //검색모드 설정
    private void showRadioButtonDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View convertView = (View) inflater.inflate(R.layout.search_mode_dialog_layout, null);

        RadioGroup group= (RadioGroup)convertView.findViewById(R.id.radio_group);
        RadioButton btn_title = (RadioButton)convertView.findViewById(R.id.radio_btn_title);
        RadioButton btn_hashTag = (RadioButton)convertView.findViewById(R.id.radio_btn_hashTag);
        RadioButton btn_place = (RadioButton)convertView.findViewById(R.id.radio_btn_place);

        switch (mode){
            case "title" :
                btn_title.setChecked(true);
                break;
            case "city3" :
                btn_place.setChecked(true);
                break;
            case "hashTag" :
                btn_hashTag.setChecked(true);
                break;
        }

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case R.id.radio_btn_title :
                        mode = "title";
                        break;
                    case R.id.radio_btn_hashTag :
                        mode ="hashTag";
                        searchText.setHint("#한개만");
                        break;
                    case R.id.radio_btn_place :
                        mode = "city3";
                        break;
                }
            }
        });

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("검색 카테고리 설정");
        alertDialog.setMessage("어떤 주제로 검색하시겠습니까?");
        alertDialog.setView(convertView);
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    //검색
    private void searching(final String searchedText){
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("불러오는 중...");
        dialog.show();

        StringTokenizer token = new StringTokenizer(searchedText, ",. /;");
        final Vector<String> vector = new Vector<String>();
        while(token.hasMoreTokens()) {
            vector.add((token.nextToken()));
        }
        try {
            Query query = databaseReference.orderByChild(mode); //수정
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final List<RecruitmentItem> searched_list = new ArrayList<>();
                        int count = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            RecruitmentItem item = snapshot.getValue(RecruitmentItem.class);
                            if(item.getIs_finished() == false) {

                                String temp = "";
                                for(int i = 0; i < vector.size(); i++) {
                                    temp = vector.get(i);
                                    if (item.getTitle().contains((temp)) || item.getCity3().contains(temp) || item.getHashTag().contains(temp)){
                                        continue;
                                    }
                                    if(i == vector.size() - 1){
                                        searched_list.add(item);
                                    }
                                    break;
                                }

                                /*switch (mode){
                                    case "title" :
                                        if(item.getTitle().contains(searchedText)){
                                            searched_list.add(item);
                                        }
                                        break;
                                    case "city3" :
                                        if(item.getCity3().contains(searchedText)){
                                            searched_list.add(item);
                                        }
                                        break;
                                    case "hashTag" :
                                        if(item.getHashTag().contains(searchedText)){
                                            searched_list.add(item);
                                        }
                                        break;
                                }*/
                            }
                            count ++;
                            if(count ==50){break;}
                        }

                        Collections.sort(searched_list);
                        adapter = new BulletinListAdapter(view.getContext() ,searched_list);
                        adapter.setItemClick(new BulletinListAdapter.ItemClick() {
                            @Override
                            public void onClick(View view, int posotion) {
                                Intent intent = new Intent(getActivity(), RecruitmentBoardActivity.class);
                                intent.putExtra("recruitment",searched_list.get(posotion));
                                intent.putExtra("userInfo", userInfo);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                            }
                        });

                        recyclerView = (RecyclerView)view.findViewById(R.id.bulletinboard_list);
                        recyclerView.setHasFixedSize(false);
                        layoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
                        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

                        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                ((StaggeredGridLayoutManager)recyclerView.getLayoutManager()).invalidateSpanAssignments();
                            }
                        });
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);

                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    } else {
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(getActivity().getApplicationContext(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
        }catch (Exception e){
            if(dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            Toast.makeText(getActivity().getApplicationContext(), "알 수 없는 에러 발생, 문의하세요.", Toast.LENGTH_SHORT).show();
        }
    }
}


