package com.example.ronanclarke.boulderingapp;

/**
 * Created by ronanclarke on 13/03/2018.
 */

public class Post
{
    private String userName;
    private String postID;
    private String postStatus;
    private String userID;
    private String imageURL;
    private float routeTime;

    public Post(String userName,String postID,
                String postStatus,
                String userID,String imageURL,
                float routeTime)
    {
        this.userName = userName;
        this.postID = postID;
        this.postStatus = postStatus;
        this.userID = userID;
        this.imageURL = imageURL;
        this.routeTime = routeTime;
    }

    public Post()
    {
        //This will be another constructor
        //to allow for less inputs
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPostID()
    {
        return postID;
    }

    public void setPostID(String postID)
    {
        this.postID = postID;
    }

    public String getPostStatus()
    {
        return postStatus;
    }

    public void setPostStatus(String postStatus)
    {
        this.postStatus = postStatus;
    }

    public String getUserID()
    {
        return userID;
    }

    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    public String getImageURL()
    {
        return imageURL;
    }

    public void setImageURL(String imageURL)
    {
        this.imageURL = imageURL;
    }

    public float getRouteTime() {
        return routeTime;
    }

    public void setRouteTime(float routeTime)
    {
        this.routeTime = routeTime;
    }
}
