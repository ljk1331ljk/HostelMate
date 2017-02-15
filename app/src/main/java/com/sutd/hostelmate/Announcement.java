package com.sutd.hostelmate;

import java.util.Date;

/**
 * Created by JK on 22/1/2017.
 */

public class Announcement {
    String author, message, msgId, subject, timestamp;

    public Announcement(){

    }

    public Announcement(String mAuthor, String mMessage, String mMsgId, String mSubject, String timestamp) {
        this.author = mAuthor;
        this.message = mMessage;
        this.msgId = mMsgId;
        this.subject = mSubject;
        this.timestamp = timestamp;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String mAuthor) {
        this.author = mAuthor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String mMessage) {
        this.message = mMessage;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String mMsgId) {
        this.msgId = mMsgId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String mSubject) {
        this.subject = mSubject;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}

