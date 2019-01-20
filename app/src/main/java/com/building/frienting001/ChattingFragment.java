package com.building.frienting001;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

//마이 화면
public class ChattingFragment extends Fragment {
    private StaggeredGridLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private View view;
    private ChattingListAdapter adapter;

    private DatabaseReference recruitmentDBReference;
    private DatabaseReference userDBReference;
    private ProgressDialog dialog;

    final private ArrayList<RecruitmentItem> my_list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chatting, container, false);
        Log.d("myLog","[Activity_MY] onCreateView");
        my_list.clear(); // 중요 - 매번 클리어 해줘야 사이즈에서 오류가 안남

        //게시판 DB 연결
        FirebaseApp recruitmentApp = FirebaseApp.getInstance("user");
        FirebaseAuth user_auth = FirebaseAuth.getInstance(recruitmentApp);
        recruitmentDBReference = FirebaseDatabase.getInstance(recruitmentApp).getReference().child("recruitment");
        userDBReference = FirebaseDatabase.getInstance(recruitmentApp).getReference("user").child(user_auth.getCurrentUser().getUid());

        searching();
        return view;
    }

    // MY에도 검색할떄처럼 현재 시간이 지났는데 finished가 False이면 True로 바꿔주는 부분 넣기.!!!!

    //공고 불러오기
    private void searching(){
        Log.d("myLog","[Activity_MY] searching()");
        //채팅방리스트 만들기
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("불러오는 중...");
        dialog.show();

        //선언부
        RecruitmentItem header = new RecruitmentItem();
        header.setTitle("진행현황");
        my_list.add(header);

        userDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfo user_data = dataSnapshot.getValue(UserInfo.class);
                ArrayList<ArrayList<String>> included_recruit = user_data.recruit_progress;
                final ArrayList<ArrayList<String>> recruits_progress = new ArrayList<>();
                final ArrayList<ArrayList<String>> recruits_finished = new ArrayList<>();
                for(int i = 0; i < included_recruit.size(); i++){ // 공고의 finished 변수가 아니라 시간 기준으로 나눔. 모집완료/마감 차이때문
                    if(time_finished(included_recruit.get(i).get(2))){ // 완전히 종료된 공고
                        if(finished_completely(included_recruit.get(i).get(2))){
                            continue; // 2주가 지난 경우라면 그냥 넘어감.
                        }
                        recruits_finished.add(included_recruit.get(i));
                        //Log.d("myLog", "finished:" + included_recruit.get(i).get(0));
                    } else {
                        recruits_progress.add(included_recruit.get(i));
                        //Log.d("myLog", "progress:" + included_recruit.get(i).get(0));
                    }
                }

                if(included_recruit.size() == 0){
                    /*AlertDialog.Builder builder = new AlertDialog.Builder();
                    builder.setTitle("해당 시간에 약속이 존재합니다.");
                    builder.setCancelable(true);
                    builder.create().show();*/

                    // 개인 설정 마치도록 유도하기
                    // 활동 유도 Dialogue 띄워주고 Activity 다른 곳으로 이동.
                }

                Log.d("myLog","[Activity_MY] progress: " + String.valueOf(recruits_progress.size()) + " finished: " + String.valueOf(recruits_finished.size()));
                if(recruits_progress.size() == 0){ // recruits_finished의 크기는 무조건 0이상이다. 위의 if문을 통과했으므로
                    RecruitmentItem header2 = new RecruitmentItem();
                    header2.setTitle("완료현황");
                    my_list.add(header2);
                    for(int j = 0; j < recruits_finished.size(); j++){
                        recruitmentDBReference.child(recruits_finished.get(j).get(0)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                RecruitmentItem item = dataSnapshot.getValue(RecruitmentItem.class);
                                if(item.finished.equals("False")){ // 끝난는데 True면 바꿔줘야한다.
                                    recruitmentDBReference.child(item.getRecruitment_key()).child("finished").setValue("True");
                                }

                                my_list.add(item);
                                //Log.d("myLog", "finished:" + item.getTitle());
                                if(my_list.size() == recruits_finished.size() + recruits_progress.size() + 2){
                                    adapter = new ChattingListAdapter(view.getContext(), my_list);
                                    adapter.setItemClick(new ChattingListAdapter.ItemClick() {
                                        @Override
                                        public void onClick(View view, int posotion) {}
                                    });

                                    recyclerView = (RecyclerView)view.findViewById(R.id.chatting_list2);
                                    recyclerView.setHasFixedSize(true);
                                    layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setAdapter(adapter);

                                    if(dialog != null && dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) { }
                        });
                    }
                }
                // recruits_progress의 size가 0이 아닐 경우

                //Collections.sort(recruits_progress, recruits_progress.get(0).get(1));

                for(int i = 0; i < recruits_progress.size(); i++){
                    recruitmentDBReference.child(recruits_progress.get(i).get(0)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            RecruitmentItem item = dataSnapshot.getValue(RecruitmentItem.class);
                            my_list.add(item);
                            //Log.d("myLog", "[Activity_MY] progress:" + item.getTitle());
                            if(my_list.size() == recruits_progress.size() + 1){
                                RecruitmentItem header2 = new RecruitmentItem();
                                header2.setTitle("완료현황");
                                my_list.add(header2);

                                if(recruits_finished.size() == 0){
                                    adapter = new ChattingListAdapter(view.getContext(), my_list);
                                    adapter.setItemClick(new ChattingListAdapter.ItemClick() {
                                        @Override
                                        public void onClick(View view, int posotion) {}
                                    });

                                    recyclerView = (RecyclerView)view.findViewById(R.id.chatting_list2);
                                    recyclerView.setHasFixedSize(true);
                                    layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setAdapter(adapter);

                                    if(dialog != null && dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                }
                                // recruits_finished의 크기가 0이 아닐 경우
                                for(int j = 0; j < recruits_finished.size(); j++){
                                    recruitmentDBReference.child(recruits_finished.get(j).get(0)).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            RecruitmentItem item = dataSnapshot.getValue(RecruitmentItem.class);
                                            if(item.finished.equals("False")){ // 끝난는데 True면 바꿔줘야한다.
                                                recruitmentDBReference.child(item.getRecruitment_key()).child("finished").setValue("True");
                                            }

                                            my_list.add(item);
                                            //Log.d("myLog", "finished:" + item.getTitle());
                                            if(my_list.size() == recruits_finished.size() + recruits_progress.size() + 2){
                                                adapter = new ChattingListAdapter(view.getContext(), my_list);
                                                adapter.setItemClick(new ChattingListAdapter.ItemClick() {
                                                    @Override
                                                    public void onClick(View view, int posotion) {
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                });

                                                recyclerView = (RecyclerView)view.findViewById(R.id.chatting_list2);
                                                recyclerView.setHasFixedSize(true);
                                                layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                                                recyclerView.setLayoutManager(layoutManager);
                                                recyclerView.setAdapter(adapter);

                                                if(dialog != null && dialog.isShowing()) {
                                                    dialog.dismiss();
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) { }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public boolean time_finished(String helloTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.YEAR) - 2000 + 56;
        int month = calendar.get(Calendar.MONTH) + 1 + 56;
        int day = calendar.get(Calendar.DAY_OF_MONTH) + 56;
        int hour_now = calendar.get(Calendar.HOUR_OF_DAY) + 56;
        int minute = calendar.get(Calendar.MINUTE) + 56;

        String now = (char) hour + "" + (char) month + "" + (char) day + "" + (char) hour_now + "" + (char) minute;

        for (int i = 0; i < 5; i++) {
            if (now.charAt(i) > helloTime.charAt(i)) {
                return true;
            } else if (now.charAt(i) < helloTime.charAt(i)) {
                return false;
            }
        }
        return false;
    }

    public boolean finished_completely(String goodbyeTime) {
        Calendar calendar = Calendar.getInstance();
        //Log.d("myLog", Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
        calendar.add(Calendar.DAY_OF_MONTH, -14);
        //Log.d("myLog", Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)));
        int hour = calendar.get(Calendar.YEAR) - 2000 + 56;
        int month = calendar.get(Calendar.MONTH) + 1 + 56;
        int day = calendar.get(Calendar.DAY_OF_MONTH) + 56;
        int hour_now = calendar.get(Calendar.HOUR_OF_DAY) + 56;
        int minute = calendar.get(Calendar.MINUTE) + 56;

        String two_weeks = (char) hour + "" + (char) month + "" + (char) day + "" + (char) hour_now + "" + (char) minute;

        for (int i = 0; i < 5; i++) {
            if (two_weeks.charAt(i) > goodbyeTime.charAt(i)) {
                return true;
            } else if (two_weeks.charAt(i) < goodbyeTime.charAt(i)) {
                return false;
            }
        }
        return false;
    }
}