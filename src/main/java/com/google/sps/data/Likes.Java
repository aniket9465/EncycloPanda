package com.google.sps.data;

/** An item in the Likes section */
public class Likes {

    private long userId;
    private long postId;
   
    public Likes(long userId, long postId) {
        this.userId = userId;
        this.postId = postId;
    }
}