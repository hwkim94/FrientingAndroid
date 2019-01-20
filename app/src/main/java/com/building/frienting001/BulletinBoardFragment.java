package com.building.frienting001;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
//import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.support.annotation.IdRes;
//import android.support.v7.app.AlertDialog;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class BulletinBoardFragment extends Fragment {
    private StaggeredGridLayoutManager layout_manager;
    private RecyclerView recycler_view;
    private View bulletin_view;
    private BulletinListAdapter bulletin_adapter;

    private boolean loading = true;
    private int pastVisibleItems,visibleItemCount, totalItemCount;
    private int visibleThreshold = 5;
    private int previousTotal = 0;

    private EditText searchText;
    private Button searchBtn;
    private ImageView searchMode;
    private String mode = "title";

    //private FirebaseAnalytics firebaseAnalytics;
    private DatabaseReference databaseReference;
    private ProgressDialog dialog;

    private Bundle bundle;

    private InputMethodManager imm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        //선언부
        bulletin_view = inflater.inflate(R.layout.fragment_bulletin_board, container, false);
        FirebaseApp recruitmentApp = FirebaseApp.getInstance("user");
        databaseReference = FirebaseDatabase.getInstance(recruitmentApp).getReference("recruitment");

        //firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        searchText = (EditText)bulletin_view.findViewById(R.id.searchText);
        searchBtn = (Button)bulletin_view.findViewById(R.id.searchBtn);
        //searchMode = (ImageView) view.findViewById(R.id.searchMode);
        LinearLayout layout = (LinearLayout)bulletin_view.findViewById(R.id.total_layout2);

        imm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
            }
        });

        //전체보기 or 메인 화면에서 검색, 입력이 없을 경우 ""로 검색되기 때문에 모두 가져옴

        //String searchTextString = getArguments().getString("searchTextString");
        //searching(searchTextString);

        //검색버튼을 누르면 실행되는 코드
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTextString = searchText.getText().toString();
                searching(searchTextString);
            }
        });
        return bulletin_view;
    }

    //검색
    private void searching(final String searchedText){
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("불러오는 중...");
        dialog.show();

        StringTokenizer token = new StringTokenizer(searchedText, ",. /;");
        final Vector<String> vector = new Vector<>();
        while(token.hasMoreTokens()) {
            vector.add((token.nextToken()));
        }
        try {
            Query query = databaseReference.orderByChild("meetingTime"); // 오름차순
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final ArrayList<RecruitmentItem> searched_list = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            RecruitmentItem item = snapshot.getValue(RecruitmentItem.class);

                            if(item.finished.equals("True")){
                                continue; // 입장시간이 이미 지난 공고라면 스킵
                            }
                            if(time_finished(item.getMeetingTime())){ // 시간이 이미 지난 공고
                                databaseReference.child(item.getRecruitment_key()).child("finished").setValue("True");
                                continue;
                            }

                            int count = 0;
                            for(int i = 0; i < vector.size(); i++) {
                                String temp = vector.get(i);
                                if (item.getTitle().contains(temp) || item.getPlace().contains(temp) || item.getHashTag().contains(temp)){
                                    count++;
                                }
                            }
                            if(count > 0){
                                item.search_count = count;
                                searched_list.add(item);
                            }
                        }
                        Collections.sort(searched_list);
                        bulletin_adapter = new BulletinListAdapter(bulletin_view.getContext(), searched_list);
                        bulletin_adapter.setItemClick(new BulletinListAdapter.ItemClick() {
                            @Override
                            public void onClick(View view, int posotion) {
                                Intent intent = new Intent(getActivity(), RecruitmentBoardActivity.class);
                                intent.putExtra("recruitment", searched_list.get(posotion));
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                            }
                        });

                        recycler_view = (RecyclerView)bulletin_view.findViewById(R.id.bulletinboard_list);
                        recycler_view.setHasFixedSize(false);
                        layout_manager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
                        layout_manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

                        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                ((StaggeredGridLayoutManager)recyclerView.getLayoutManager()).invalidateSpanAssignments();
                            }
                        });
                        recycler_view.setLayoutManager(layout_manager);
                        recycler_view.setAdapter(bulletin_adapter);

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
    public boolean time_finished(String helloTime){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.YEAR) - 2000 + 56;
        int month = calendar.get(Calendar.MONTH) + 1 + 56;
        int day = calendar.get(Calendar.DAY_OF_MONTH) + 56;
        int hour_now = calendar.get(Calendar.HOUR_OF_DAY) + 56;
        int minute = calendar.get(Calendar.MINUTE) + 56;

        String now = (char)hour + "" + (char)month + "" + (char)day + "" + (char)hour_now + "" + (char)minute;

        for(int i = 0; i < 5; i++){
            if(now.charAt(i) > helloTime.charAt(i)){
                return true;
            }
            else if(now.charAt(i) < helloTime.charAt(i)){
                return false;
            }
        }
        return false;
    }
}