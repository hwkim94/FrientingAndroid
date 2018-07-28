package com.building.frienting001;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by LG_ on 2017-07-09.
 */

//커스텀 다이얼로그
public class DialogListAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<String> list;

    public DialogListAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public DialogListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_activity_dialog_item, null, false);
        return new DialogListHolder(layoutView);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        DialogListHolder newHolder = (DialogListHolder) holder;
        newHolder.dialog_content.setText(list.get(position).toString());
        newHolder.layout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(itemClick != null){
                    itemClick.onClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public class DialogListHolder extends RecyclerView.ViewHolder {
        TextView dialog_content;
        View layout;

        public DialogListHolder(View itemView) {
            super(itemView);
            dialog_content = (TextView) itemView.findViewById(R.id.dialog_content);
            layout = itemView.findViewById(R.id.layout);
        }
    }

    private ItemClick itemClick;
    public interface ItemClick{
        public void onClick(View view, int posotion);
    }
    public void setItemClick(ItemClick itemClick){
        this.itemClick = itemClick;
    }

}
