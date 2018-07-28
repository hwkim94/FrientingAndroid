package com.building.frienting001;

import java.io.Serializable;


//리뷰화면 아이템
public class ReviewDialogItem2 implements Serializable {
    private String nickname;
    private int star;
    private String contents;

    public ReviewDialogItem2() {
    }

    public ReviewDialogItem2(String nickname, int star, String contents) {
        this.nickname = nickname;
        this.star = star;
        this.contents = contents;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
