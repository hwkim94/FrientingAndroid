package com.building.frienting001;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//채팅목록에 존재하는 채팅방을 보내주는 클래스
public class ChattingListAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<RecruitmentItem> chatting_list;

    final FirebaseApp user_app = FirebaseApp.getInstance("user");
    FirebaseAuth user_auth = FirebaseAuth.getInstance(user_app);

    FirebaseApp recruitmentApp = FirebaseApp.getInstance("user");
    private DatabaseReference recruitmentDBReference = FirebaseDatabase.getInstance(recruitmentApp).getReference().child("recruitment");
    private View layoutView;
    Frequent frequent = new Frequent();

    public ChattingListAdapter(Context context, List<RecruitmentItem> chatting_list) {
        this.context = context;
        this.chatting_list = chatting_list;
    }

    @Override
    public int getItemViewType(int position) {
        if(chatting_list.get(position).getTitle().equals("진행현황")){
            return 0;
        }
        else if(chatting_list.get(position).getTitle().equals("완료현황")){
            return 0;
        }
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_item, null, false);
            return new ChattingSectionHolder(layoutView);
        } else {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_list_item, null, false);
            return new ChattingListHolder(layoutView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final RecruitmentItem recruitmentItem = chatting_list.get(position);
        switch (getItemViewType(position)){
            case 1:
                //Log.d("myLog", "case1");
                final ChattingListHolder newHolder = (ChattingListHolder) holder;
                newHolder.title.setText(recruitmentItem.getTitle());

                if(recruitmentItem.getImagePath().equals("")){}
                else {Glide.with(holder.itemView.getContext()).load(recruitmentItem.getImagePath()).into(newHolder.photo);}

                if(recruitmentItem.getImagePath().equals("")){}
                else {
                    //Glide.with(holder.itemView.getContext()).load(R.drawable.filter).into(newHolder.filter);
                    newHolder.filter.setImageResource(R.drawable.filter);
                }

                if(recruitmentItem.finished.equals("True")){ // 방장 & 참여자 모두
                    newHolder.status.setText("모집 완료");
                }
                else if(recruitmentItem.finished.equals("False")){ // 참여자일경우
                    newHolder.status.setText("모집중");
                }

                if (!user_auth.getCurrentUser().getUid().equals(recruitmentItem.getWriter_uid())){ // 방장 아닐 경우 --> 추후 확인
                    newHolder.crown.setVisibility(View.INVISIBLE);

                    if(recruitmentItem.finished.equals("False")){
                        newHolder.status.setText("대기중");
                    }
                }

                List<String> searched_list = splitHashTag(recruitmentItem.getHashTag());
                final HashTagAdapter adapter = new HashTagAdapter(holder.itemView.getContext(), searched_list);
                newHolder.hashTag.setHasFixedSize(true);

                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
                newHolder.hashTag.setLayoutManager(layoutManager);
                newHolder.hashTag.setAdapter(adapter);

                newHolder.count.setText(recruitmentItem.getApplicant_uid().size() + "/" + recruitmentItem.getDetail1());

                newHolder.recruitment.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(itemClick != null){
                            itemClick.onClick(v, position);
                            if (newHolder.button.getVisibility() == View.GONE){
                                if (user_auth.getCurrentUser().getUid().equals(recruitmentItem.getWriter_uid())){
                                    newHolder.btn2.setVisibility(View.VISIBLE);
                                    if(recruitmentItem.finished.equals("True")){
                                        newHolder.btn2.setVisibility(View.GONE);
                                    }
                                }
                                newHolder.button.setVisibility(View.VISIBLE);
                            }
                            else{
                                newHolder.button.setVisibility(View.GONE);
                            }
                        }
                    }
                });

                newHolder.btn1.setOnClickListener(new View.OnClickListener() { // 공고보기
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, RecruitmentBoardActivity.class);
                        intent.putExtra("recruitment", recruitmentItem); // Extra 넘겨주는 것 맞는지?
                        //intent.putExtra("userInfo", chatting_list.get(position).getUserInfo());
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        ActivityCompat.startActivity(context,intent, null);
                    }
                });

                newHolder.btn2.setOnClickListener(new View.OnClickListener() { // 마감하기
                    @Override
                    public void onClick(View v) {
                        // 바로 바뀌게 하기
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("공고 마감");
                        builder.setMessage("공고를 마감하시겠습니까?\n지정하신 마감시간 전입니다.");
                        builder.setCancelable(false);

                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                recruitmentDBReference.child(recruitmentItem.getRecruitment_key()).child("finished").setValue("True");
                                // 새로고침 추가!!
                                dialogInterface.cancel();
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

                newHolder.btn3.setOnClickListener(new View.OnClickListener() { // 채팅하기
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ChattingActivity.class);
                        intent.putExtra("recruitment", recruitmentItem);
                        //intent.putExtra("userInfo", chatting_list.get(position).getUserInfo());
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        ActivityCompat.startActivity(context, intent, null);
                    }
                });
                break;

            case 0 :
                //Log.d("myLog", "case2");
                final ChattingSectionHolder sectionHolder = (ChattingSectionHolder) holder;
                sectionHolder.section.setText(recruitmentItem.getTitle());
                break;

        }
    }

    @Override
    public int getItemCount() {
        return this.chatting_list.size();
    }

    public class ChattingSectionHolder extends RecyclerView.ViewHolder {
        TextView section;
        public ChattingSectionHolder(View itemView) {
            super(itemView);
            section = (TextView)itemView.findViewById(R.id.section_header);
        }
    }
    public class ChattingListHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView status;
        ImageView photo;
        ImageView filter;
        RecyclerView hashTag;
        TextView count;
        ImageView inout;
        ImageView crown;

        RelativeLayout recruitment;

        LinearLayout button;
        Button btn1, btn2, btn3;


        public ChattingListHolder(View itemView) {
            super(itemView);

            title = (TextView)itemView.findViewById(R.id.chatting_item_title);
            status = (TextView)itemView.findViewById(R.id.status_test);

            photo = (ImageView)itemView.findViewById(R.id.chatting_item_photo);
            filter = (ImageView)itemView.findViewById(R.id.chatting_item_filter);
            hashTag = (RecyclerView)itemView.findViewById(R.id.chatting_item_hashTag);
            count = (TextView)itemView.findViewById(R.id.chatting_item_count);
            inout = (ImageView)itemView.findViewById(R.id.chatting_item_inout);
            crown = (ImageView)itemView.findViewById(R.id.chatting_item_crown);

            recruitment = (RelativeLayout)itemView.findViewById(R.id.chatting_item_recruitment);

            button = (LinearLayout)itemView.findViewById(R.id.chatting_item_button);

            btn1 = (Button)itemView.findViewById(R.id.chatting_item_btn1);
            btn2 = (Button)itemView.findViewById(R.id.chatting_item_btn2);
            btn3 = (Button)itemView.findViewById(R.id.chatting_item_btn3);
        }
    }

    private ItemClick itemClick;
    public interface ItemClick{
        public void onClick(View view, int posotion);
    }
    public void setItemClick(ItemClick itemClick){
        this.itemClick = itemClick;
    }

    private List<String> splitHashTag(String s){
        List<String> list = new ArrayList<>();
        if(!s.equals("")){
            String[] array = s.split("#");
            for(int i = 1; i < array.length; i++){
                array[i] = "#" + array[i];
                list.add(array[i]);
            }
        }
        return list;
    }
}
