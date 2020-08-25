package com.google.sps.data;

/** An item in the comments section */
public class Comment {

    private long id;
    private long likes;
    private long createdAt;
    private String userId;
    private String comment;
    private String websiteURL;
   
    public Comment(long id, long likes, long createdAt, String userId, String comment, String websiteURL) {
        this.id = id;
        this.likes = likes;
        this.createdAt = createdAt;
        this.userId = userId;
        this.comment = comment;
        this.websiteURL = websiteURL;
    }
}
