package com.building.frienting001;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//리뷰화면 리스트 어댑터
public class ReviewDialogListAdapter extends BaseAdapter{
    private List<ReviewDialogItem> list = new ArrayList<>();

    public void add(ReviewDialogItem item){
        list.add(item);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        //view 생성 - LayoutInflater 이용
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.review_dialog_item, parent, false);
        }

        //생성된 view에 데이터를 설정해 주는 코드
        ReviewDialogItem data = list.get(position);//i번 위치의 데이터를 추출
        String name = data.getNickname();
        String star = data.getStar();
        String contents= data.getContents();

        TextView nickname = (TextView) convertView.findViewById(R.id.review_dialog_nickname);
        ImageView star_1 = (ImageView) convertView.findViewById(R.id.review_dialog_star1);
        ImageView star_2 = (ImageView) convertView.findViewById(R.id.review_dialog_star2);
        ImageView star_3 = (ImageView) convertView.findViewById(R.id.review_dialog_star3);
        ImageView star_4 = (ImageView) convertView.findViewById(R.id.review_dialog_star4);
        ImageView star_5 = (ImageView) convertView.findViewById(R.id.review_dialog_star5);
        RecyclerView review = (RecyclerView) convertView.findViewById(R.id.review_dialog_review);

        nickname.setText(name);

        switch (star){
            case "0" :
                star_1.setVisibility(View.INVISIBLE);
                star_2.setVisibility(View.INVISIBLE);
                star_3.setVisibility(View.INVISIBLE);
                star_4.setVisibility(View.INVISIBLE);
                star_5.setVisibility(View.INVISIBLE);
                break;
            case "1" :
                star_1.setVisibility(View.INVISIBLE);
                star_2.setVisibility(View.INVISIBLE);
                star_3.setVisibility(View.INVISIBLE);
                star_4.setVisibility(View.INVISIBLE);
                star_5.setVisibility(View.VISIBLE);
                break;
            case "2" :
                star_1.setVisibility(View.INVISIBLE);
                star_2.setVisibility(View.INVISIBLE);
                star_3.setVisibility(View.INVISIBLE);
                star_4.setVisibility(View.VISIBLE);
                star_5.setVisibility(View.VISIBLE);
                break;
            case "3" :
                star_1.setVisibility(View.INVISIBLE);
                star_2.setVisibility(View.INVISIBLE);
                star_3.setVisibility(View.VISIBLE);
                star_4.setVisibility(View.VISIBLE);
                star_5.setVisibility(View.VISIBLE);
                break;
            case "4" :
                star_1.setVisibility(View.INVISIBLE);
                star_2.setVisibility(View.VISIBLE);
                star_3.setVisibility(View.VISIBLE);
                star_4.setVisibility(View.VISIBLE);
                star_5.setVisibility(View.VISIBLE);
                break;
            case "5" :
                star_1.setVisibility(View.VISIBLE);
                star_2.setVisibility(View.VISIBLE);
                star_3.setVisibility(View.VISIBLE);
                star_4.setVisibility(View.VISIBLE);
                star_5.setVisibility(View.VISIBLE);
                break;
        }

        List<String> searched_list = preprocessing(contents);
        HashTagAdapter adapter = new HashTagAdapter(context, searched_list);
        review.setHasFixedSize(true);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
        review.setLayoutManager(layoutManager);
        review.setAdapter(adapter);



        /*

        LinearLayout linearLayout1 = (LinearLayout)convertView.findViewById(R.id.review_dialog_container);
        LinearLayout linearLayout2 = (LinearLayout)convertView.findViewById(R.id.review_dialog_review);
        for(int i=0; i<Integer.valueOf(star); i++) {
            ImageView imgView = new ImageView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(200, 200);
            imgView.setLayoutParams(lp);
            Glide.with(context)
                    .load(R.mipmap.ic_launcher)
                    .into(imgView);
            linearLayout1.addView(imgView);
        }

        List<String> list1 = new ArrayList<>();
        if(contents != ""){
            String[] array = contents.split(" ");
            list1 = Arrays.asList(array);
        }

        for(int i=0; i< list1.size(); i++) {
            TextView txtView = new TextView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(200, 200);
            txtView.setLayoutParams(lp);
            //txtView.setBackground();
            linearLayout1.addView(txtView);
        }
        */

        return convertView;
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
