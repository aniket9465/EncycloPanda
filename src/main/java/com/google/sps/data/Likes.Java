package com.google.sps.data;

/** An item in the Likes section */
public class Likes {

    private long id;
    private long postId;
    private String userId;
   
    public Likes(long id, long postId, String userId) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
    }
}
