package com.building.frienting001;

/**
 * Created by LG_ on 2018-02-18.
 */

public class InterestItem {
    private String category;
    private String activity;

    public InterestItem() {
    }

    public InterestItem(String category, String activity) {
        this.category = category;
        this.activity = activity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
