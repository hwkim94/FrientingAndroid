package com.building.frienting001;



//환경설정-공지 아이템
public class SettingNotiItem {
    private String title;
    private String text;

    public SettingNotiItem() {}

    public SettingNotiItem(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setTect(String text) {
        this.text = text;
    }
}
