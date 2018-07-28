package com.building.frienting001;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

//채팅목록에 존재하는 채팅방을 보내주는 클래스
public class ChattingListAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ChattingListItem> chatting_list;

    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private int count=0;

    private View layoutView;

    public ChattingListAdapter(Context context, List chatting_list) {
        this.context = context;
        this.chatting_list = chatting_list;
    }

    public void addItemList(List<ChattingListItem> item){
        if (item.size() >= 1) {
            for (ChattingListItem chattingListItem : item) {
                chatting_list.add(chattingListItem);
                notifyDataSetChanged();
            }
        }
    }

    public void addItem(ChattingListItem item) {

        if(item.getRecruitmentItem().getIs_finished() == false){
            chatting_list.add(1,item);
            count++;
            renewItemViewType(count);
        } else{chatting_list.add(chatting_list.size(),item);}
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(ChattingListItem item) {
        chatting_list.add(item);
        sectionHeader.add(chatting_list.size()-1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    public void renewItemViewType(int count){
        sectionHeader.clear();
        sectionHeader.add(0);
        sectionHeader.add(count+1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_SEPARATOR) {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_item, null, false);
            return new ChattingSectionHolder(layoutView);
        } else {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_list_item, null, false);
            return new ChattingListHolder(layoutView);

        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        int rowType = getItemViewType(position);
        final RecruitmentItem recruitmentItem = chatting_list.get(position).getRecruitmentItem();
        final UserInfo userInfo = chatting_list.get(position).getUserInfo();


        switch (rowType){
            case TYPE_ITEM:
                final ChattingListHolder newHolder = (ChattingListHolder) holder;
                newHolder.title.setText(recruitmentItem.getTitle());

                if(recruitmentItem.getImagePath().equals("")){}
                else {Glide.with(holder.itemView.getContext()).load(recruitmentItem.getImagePath()).into(newHolder.photo);}

                if(recruitmentItem.getImagePath().equals("")){}
                else {
                    //Glide.with(holder.itemView.getContext()).load(R.drawable.filter).into(newHolder.filter);
                    newHolder.filter.setImageResource(R.drawable.filter);
                }

                if (!userInfo.getFirebaseUserUid().equals(recruitmentItem.getWriter_uid())){
                    newHolder.crown.setVisibility(View.INVISIBLE);
                }

                List<String> searched_list = preprocessing2(recruitmentItem.getHashTag());
                HashTagAdapter2 adapter = new HashTagAdapter2(holder.itemView.getContext(), searched_list);
                newHolder.hashTag.setHasFixedSize(true);

                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
                newHolder.hashTag.setLayoutManager(layoutManager);
                newHolder.hashTag.setAdapter(adapter);

                List<String> list1 = preprocessing(recruitmentItem.getApplicant_uid());
                newHolder.count.setText((list1.size()+1) +"/"+recruitmentItem.getDetail1());

                newHolder.recruitment.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(itemClick != null){
                            itemClick.onClick(v, position);
                            if (newHolder.button.getVisibility() ==View.GONE){
                                newHolder.button.setVisibility(View.VISIBLE);
                            }else{
                                newHolder.button.setVisibility(View.GONE);
                            }
                        }
                    }
                });

                newHolder.btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, RecruitmentBoardActivity.class);
                        intent.putExtra("recruitment", recruitmentItem);
                        intent.putExtra("userInfo", chatting_list.get(position).getUserInfo());
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        ActivityCompat.startActivity(context,intent, null);
                    }
                });

                newHolder.btn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent intent = new Intent(context, ChattingActivity.class);
                        intent.putExtra("recruitment", recruitmentItem);
                        intent.putExtra("userInfo", chatting_list.get(position).getUserInfo());
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        ActivityCompat.startActivity(context, intent, null);
                    }
                });
                break;

            case TYPE_SEPARATOR :
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
        ImageView photo;
        ImageView filter;
        RecyclerView hashTag;
        TextView count;
        ImageView inout;
        ImageView crown;

        RelativeLayout recruitment;

        LinearLayout button;
        Button btn1, btn3;


        public ChattingListHolder(View itemView) {
            super(itemView);

            title = (TextView)itemView.findViewById(R.id.chatting_item_title);
            photo = (ImageView)itemView.findViewById(R.id.chatting_item_photo);
            filter = (ImageView)itemView.findViewById(R.id.chatting_item_filter);
            hashTag = (RecyclerView)itemView.findViewById(R.id.chatting_item_hashTag);
            count = (TextView)itemView.findViewById(R.id.chatting_item_count);
            inout = (ImageView)itemView.findViewById(R.id.chatting_item_inout);
            crown = (ImageView)itemView.findViewById(R.id.chatting_item_crown);

            recruitment = (RelativeLayout)itemView.findViewById(R.id.chatting_item_recruitment);

            button = (LinearLayout)itemView.findViewById(R.id.chatting_item_button);

            btn1 = (Button)itemView.findViewById(R.id.chatting_item_btn1);
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

    private List<String> preprocessing(String s){
        List<String> list = new ArrayList<>();

        if(!s.equals("")){
            String[] array = s.split("/");
            list = Arrays.asList(array);
        }
        return list;
    }

    private List<String> preprocessing2(String s){
        List<String> list = new ArrayList<>();

        if(!s.equals("")){
            String[] array = s.split(" ");
            list = Arrays.asList(array);
        }

        return list;
    }

}
