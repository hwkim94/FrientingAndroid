package com.building.frienting001;

//채팅목록을 담는 클래스
public class ChattingListItem {
    private UserInfo userInfo;
    private RecruitmentItem recruitmentItem;

    public ChattingListItem() {}

    public ChattingListItem(UserInfo userInfo, RecruitmentItem recruitmentItem) {
        this.userInfo = userInfo;
        this.recruitmentItem = recruitmentItem;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public RecruitmentItem getRecruitmentItem() {
        return recruitmentItem;
    }

    public void setRecruitmentItem(RecruitmentItem recruitmentItem) {
        this.recruitmentItem = recruitmentItem;
    }
}
