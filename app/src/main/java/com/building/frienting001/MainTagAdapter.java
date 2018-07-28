package com.building.frienting001;

/**
 * Created by JM on 2017-10-08.
 */

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

//모집공고를 화면에 붙여주는 클래스
public class MainTagAdapter extends PagerAdapter {
    Context mContext;
    int[] mResources;
    LayoutInflater mLayoutInflater;

    public MainTagAdapter(Context context, int[] resources) {
        mContext = context;
        this.mResources = resources;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.main_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.image);
        imageView.setImageResource(mResources[position]);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
