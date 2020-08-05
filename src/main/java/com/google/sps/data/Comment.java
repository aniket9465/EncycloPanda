package com.google.sps.data;

/** An item in the comments section */
public class Comment {

    private long id;
    private long userId;
    private long likes;
    private long createdAt;
    private String text;
    private String websiteUrl;
   
    public Comment(long id, long userId, long likes, long createdAt, String text, String websiteUrl) {
        this.id = id;
        this.userId = userId;
        this.likes = likes;
        this.createdAt = createdAt;
        this.text = text;
        this.websiteUrl = websiteUrl;
    }
}