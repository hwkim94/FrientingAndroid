package com.building.frienting001;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

//모집공고를 화면에 붙여주는 클래스
public class HashTagAdapter2 extends RecyclerView.Adapter {
    private Context context;
    private List<String> recruit_list;

    public HashTagAdapter2(Context context, List recruit_list) {
        this.context = context;
        this.recruit_list = recruit_list;
    }

    @Override
    public BulletinListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hashtag_item2, null, false);
        return new BulletinListHolder(layoutView);
    }

    public void addItem(String string){
        recruit_list.add(string);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        BulletinListHolder newHolder = (BulletinListHolder) holder;

        newHolder.hashtag.setText(recruit_list.get(position).toString());



    }

    @Override
    public int getItemCount() {
        return this.recruit_list.size();
    }

    public class BulletinListHolder extends RecyclerView.ViewHolder {
        TextView hashtag;

        public BulletinListHolder(View itemView) {
            super(itemView);
            hashtag = (TextView) itemView.findViewById(R.id.hashTag);
        }
    }

    private ItemClick itemClick;
    public interface ItemClick{public void onClick(View view, int posotion);}
    public void setItemClick(ItemClick itemClick){
        this.itemClick = itemClick;
    }

}
