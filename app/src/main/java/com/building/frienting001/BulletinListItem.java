package com.building.frienting001;

//모집공고리스트를 담는 클래스
public class BulletinListItem {
    private String image;
    private String title;
    private String activity;
    private String key;
    private String ImageUri;
    private RecruitmentItem recruitmentItem;

    public BulletinListItem() {
    }

    public BulletinListItem(String image, String title, String activity, String key, String imageUri, RecruitmentItem recruitmentItem) {
        this.image = image;
        this.title = title;
        this.activity = activity;
        this.key = key;
        ImageUri = imageUri;
        this.recruitmentItem = recruitmentItem;
    }

    public RecruitmentItem getRecruitmentItem() {
        return recruitmentItem;
    }

    public void setRecruitmentItem(RecruitmentItem recruitmentItem) {
        this.recruitmentItem = recruitmentItem;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
