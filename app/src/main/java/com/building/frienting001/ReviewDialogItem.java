package com.building.frienting001;

import java.io.Serializable;


//리뷰화면 아이템
public class ReviewDialogItem implements Serializable {
    private String nickname;
    private String star;
    private String contents;

    public ReviewDialogItem() {
    }

    public ReviewDialogItem(String nickname, String star, String contents) {
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

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
