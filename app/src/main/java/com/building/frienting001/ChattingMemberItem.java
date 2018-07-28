package com.building.frienting001;

/**
 * Created by LG_ on 2017-09-01.
 */

//
public class ChattingMemberItem {
    private String icon;
    private String name;
    private String contents;
    private UserInfo user;


    public ChattingMemberItem() {
    }

    public ChattingMemberItem(String icon, String name, String contents, UserInfo user) {
        this.icon = icon;
        this.name = name;
        this.contents = contents;
        this.user = user;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUserInfo(UserInfo user) {
        this.user = user;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

}