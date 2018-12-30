package com.building.frienting001;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
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
        my_list.clear(); // 중요 - 매번 클리어 해줘야 사이즈에서 오류가 안남

        //게시판 DB 연결
        FirebaseApp recruitmentApp = FirebaseApp.getInstance("user");
        FirebaseAuth user_auth = FirebaseAuth.getInstance(recruitmentApp);
        recruitmentDBReference = FirebaseDatabase.getInstance(recruitmentApp).getReference().child("recruitment");
        userDBReference = FirebaseDatabase.getInstance(recruitmentApp).getReference("user").child(user_auth.getCurrentUser().getUid());

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
        //final ReviewDialogItem item = new ReviewDialogItem("","","");
        RecruitmentItem header = new RecruitmentItem();
        header.setTitle("진행현황");
        my_list.add(header);

        userDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInfo user_data = dataSnapshot.getValue(UserInfo.class);
                ArrayList<ArrayList<String>> included_recruit = user_data.recruit_progress;
                final ArrayList<String> recruits_progress = new ArrayList<>();
                final ArrayList<String> recruits_finished = new ArrayList<>();
                for(int i = 0; i < included_recruit.size(); i++){
                    if(time_finished(included_recruit.get(i).get(2))){ // 완전히 종료된 공고
                        recruits_finished.add(included_recruit.get(i).get(0));
                        //Log.d("myLog", "finished:" + included_recruit.get(i).get(0));
                    } else {
                        recruits_progress.add(included_recruit.get(i).get(0));
                        //Log.d("myLog", "progress:" + included_recruit.get(i).get(0));
                    }
                }
                for(int i = 0; i < recruits_progress.size(); i++){
                    recruitmentDBReference.child(recruits_progress.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            RecruitmentItem item = dataSnapshot.getValue(RecruitmentItem.class);
                            my_list.add(item);
                            //Log.d("myLog", "progress:" + item.getTitle());
                            if(my_list.size() == recruits_progress.size() + 1){
                                RecruitmentItem header2 = new RecruitmentItem();
                                header2.setTitle("완료현황");
                                my_list.add(header2);

                                for(int j = 0; j < recruits_finished.size(); j++){
                                    recruitmentDBReference.child(recruits_finished.get(j)).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            RecruitmentItem item = dataSnapshot.getValue(RecruitmentItem.class);
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
}