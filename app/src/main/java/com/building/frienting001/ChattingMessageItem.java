package com.building.frienting001;

//채팅창에 보이는 메세지의 내용을 담아주는 클래스
public class ChattingMessageItem {
    private String chatting_photo;
    //private String chatting_profile;
    private String chatting_name;
    private String chatting_text;
    private String writer_uid;

    public ChattingMessageItem() { }

    /*public ChattingMessageItem(String chatting_photo, String chatting_profile, String chatting_name, String chatting_text, String writer_uid) {
        this.chatting_photo = chatting_photo;
        this.chatting_profile = chatting_profile;
        this.chatting_name = chatting_name;
        this.chatting_text = chatting_text;
        this.writer_uid = writer_uid;
    }*/

    public String getWriter_uid() {
        return writer_uid;
    }

    public void setWriter_uid(String writer_uid) {
        this.writer_uid = writer_uid;
    }

    public String getChatting_photo() {
        return chatting_photo;
    }

    public void setChatting_photo(String chatting_photo) {
        this.chatting_photo = chatting_photo;
    }

    /*public String getChatting_profile() {
        return chatting_profile;
    }

    public void setChatting_profile(String chatting_profile) {
        this.chatting_profile = chatting_profile;
    }*/

    public String getChatting_name() {
        return chatting_name;
    }

    public void setChatting_name(String chatting_name) {
        this.chatting_name = chatting_name;
    }

    public String getChatting_text() {
        return chatting_text;
    }

    public void setChatting_text(String chatting_text) {
        this.chatting_text = chatting_text;
    }
}