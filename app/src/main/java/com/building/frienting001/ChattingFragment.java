package com.building.frienting001;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//마이 화면
public class ChattingFragment extends Fragment {

    //private ListView chatting_list;
    //private ChattingMemberAdapter chattinglistAdpater;

    private StaggeredGridLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private View view;
    private ChattingListAdapter adapter;
    private UserInfo userInfo;

    private DatabaseReference recruitmentDBReference;
    private ProgressDialog dialog;

    private List<String> ing_list;

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_chatting, container, false);

        //게시판 DB 연결
        FirebaseApp recruitmentApp = FirebaseApp.getInstance("recruitment");
        recruitmentDBReference = FirebaseDatabase.getInstance(recruitmentApp).getReference().child("recruitments");

        firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        //유저정보 가져오기
        Bundle bundle = getArguments();
        userInfo = (UserInfo)bundle.getSerializable("userInfo");

        //마이 화면 열었을때 로그
        Bundle params1 = new Bundle();
        params1.putString("UserUid", userInfo.getFirebaseUserUid());
        params1.putLong("OpenTime", System.currentTimeMillis());
        firebaseAnalytics.logEvent("ChattingFragment", params1);

        searching();

        return view;
    }

    //공고 불러오기
    private void searching(){

        //채팅방리스트 만들기
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("불러오는 중...");
        dialog.show();

        //선언부
        List<ChattingListItem> temp = new ArrayList<>();
        final ReviewDialogItem item = new ReviewDialogItem("","","");
        RecruitmentItem header1 = new RecruitmentItem("", "매칭 대기 공고","","","","","","","","","","",item,"","","",0,0,false);
        RecruitmentItem header2 = new RecruitmentItem("", "매칭 완료 공고","","","","","","","","","","",item,"","","",0,0,false);

        //리스너 선언
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    RecruitmentItem recruitmentItem = dataSnapshot.getValue(RecruitmentItem.class);
                    adapter.addItem(new ChattingListItem(userInfo, recruitmentItem));

                    if (adapter.getItemCount() == (ing_list.size() +2)){
                        if(dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        //어댑터 선언
        adapter = new ChattingListAdapter(view.getContext(), temp);
        adapter.setItemClick(new ChattingListAdapter.ItemClick() {
            @Override
            public void onClick(View view, int posotion) {}
        });

        //헤더 만들어주기
        adapter.addSectionHeaderItem(new ChattingListItem(userInfo,header1));
        adapter.addSectionHeaderItem(new ChattingListItem(userInfo,header2));

        //진행중인 공고 가져오기
        ing_list = preprocessing(userInfo.getRecruiting());
        for (String recruitment : ing_list){
            if(recruitment.length() >= 5) {
                recruitment = recruitment.trim();
                try{recruitmentDBReference.child(recruitment).addValueEventListener(listener);}
                catch (Exception e){Toast.makeText(getActivity().getApplicationContext(), "알 수 없는 에러 발생, 문의하세요.", Toast.LENGTH_SHORT).show();}
            }
        }

        //화면 만들기
        recyclerView = (RecyclerView)view.findViewById(R.id.chatting_list2);
        recyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    //공고 목록 전처리
    private List<String> preprocessing(String s){
        List<String> list = new ArrayList<>();

        if(!s.equals("")){
            String[] array = s.split("/");
            list = Arrays.asList(array);
        }
        return list;
    }


}