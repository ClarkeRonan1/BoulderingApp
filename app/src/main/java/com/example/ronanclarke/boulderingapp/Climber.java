package com.example.ronanclarke.boulderingapp;

/**
 * Created by francisclarke on 04/04/2018.
 */

public class Climber
{
    private String email, uid;
    private String username;

    public Climber(String email,String uid, String username)
    {
        this.email = email;
        this.uid = uid;
        this.username = username;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }
}
