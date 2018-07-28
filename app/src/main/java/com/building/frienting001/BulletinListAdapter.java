package com.building.frienting001;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//모집공고를 화면에 붙여주는 클래스
public class BulletinListAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<com.building.frienting001.RecruitmentItem> recruit_list;

    public BulletinListAdapter(Context context, List recruit_list) {
        this.context = context;
        this.recruit_list = recruit_list;
    }

    @Override
    public BulletinListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bulletin_list_item, null, false);
        return new BulletinListHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        BulletinListHolder newHolder = (BulletinListHolder) holder;

        if(recruit_list.get(position).getImagePath().equals("")){}
        else {Glide.with(holder.itemView.getContext()).load(recruit_list.get(position).getImagePath()).into(newHolder.image);}

        newHolder.title.setText(recruit_list.get(position).getTitle());
        newHolder.place.setText(recruit_list.get(position).getCity3());
        newHolder.time.setText(recruit_list.get(position).getHelloTime() + "~" + recruit_list.get(position).getGoodbyeTime() );
        newHolder.date.setText("20" +recruit_list.get(position).getHelloDate());

        newHolder.container.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(itemClick != null){
                    itemClick.onClick(v, position);
                }
            }
        });

        List<String> searched_list = preprocessing(recruit_list.get(position).getHashTag());
        HashTagAdapter adapter = new HashTagAdapter(holder.itemView.getContext(), searched_list);
        newHolder.recyclerView.setHasFixedSize(true);

        int num;
        if (searched_list.size()%2 ==1){num = searched_list.size()/2 +1;}
        else{num = searched_list.size()/2;}
        if (num ==0){num =1;}

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(num, StaggeredGridLayoutManager.HORIZONTAL);
        newHolder.recyclerView.setLayoutManager(layoutManager);
        newHolder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return this.recruit_list.size();
    }

    public class BulletinListHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView place;
        TextView time;
        TextView date;
        RecyclerView recyclerView;
        View container;

        public BulletinListHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView)itemView.findViewById(R.id.title);
            place = (TextView)itemView.findViewById(R.id.place);
            time = (TextView)itemView.findViewById(R.id.time);
            date = (TextView)itemView.findViewById(R.id.date);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.tag);


            container = itemView.findViewById(R.id.container);
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
            String[] array = s.split(" ");
            list = Arrays.asList(array);
        }

        return list;
    }
}
