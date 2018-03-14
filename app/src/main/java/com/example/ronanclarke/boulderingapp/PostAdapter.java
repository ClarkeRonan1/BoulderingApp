package com.example.ronanclarke.boulderingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Created by ronanclarke on 13/03/2018.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>
{
    private List<Post> items;
    public Context context;

    private FirebaseFirestore firebaseFirestore;

    public PostAdapter(List<Post> items)
    {
        this.items =items;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //Put layout inflator here
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row,parent,false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();

        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final PostViewHolder viewHolder, int i)
    {
        //use getters here
        String postID = items.get(i).getPostID();
        String postStatus = items.get(i).getPostStatus();
        final String userName = items.get(i).getUserName();
        final String imageURL = items.get(i).getImageURL();
        String userID = items.get(i).getUserID();
        float routeTime = items.get(i).getRouteTime();

        //use setters here
        viewHolder.setPostID(postID);
        viewHolder.setPostStatus(postStatus);
        viewHolder.setUserName(userName);
        viewHolder.setImageURL(imageURL);
        viewHolder.setUserID(userID);
        viewHolder.setRouteTime(routeTime);

        //Connect to Firebase to collect information
        firebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            //
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                //If connection made successfully
                if(task.isSuccessful())
                {
                    //String postID = task.getResult().getString("postID");
                    //String postStatus = task.getResult().getString("postStatus");
                    //String userName = task.getResult().getString(username);
                    //String imageURL = task.getResult().getString(imageURL);
                    //String userID = task.getResult().getString(userID);
                    //float routeTime = task.getResult().getInt("routeTime);

                    viewHolder.setupUserInfo(userName,imageURL);
                }
                //Else
                else
                {
                    //display error message
                }
            }
        });


    }

    //Workout the size of the list
    @Override
    public int getItemCount()
    {
        return items.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder
    {
        public View postView;
        public TextView postID;
        public TextView postStatus;
        public TextView userName;
        public ImageView imageURL;
        public TextView routeTime;
        public TextView userID;

        public PostViewHolder(View rowView)
        {
            super(rowView);
            postView = rowView;
        }

        public void setPostID(String postID)
        {
            //set layout file accordingly
            //postID = postView.findViewById(R.id.post_id);
        }
        public void setPostStatus(String postStatus)
        {
            //postStatus = postView.findViewById(R.id.status_text);
        }
        public void setUserName(String userName)
        {
            //userName = postView.findViewById(R.id.user_name);
        }
        public void setImageURL(String imageURL)
        {
            //imageURL = postView.findViewById(R.id.image_url);
        }
        public void setUserID(String userID)
        {
            //userID = postView.findViewById(R.id.user_id)
        }
        public void setRouteTime(float routeTime)
        {
            //routeTime = postView.findViewById(R.id.route_time);
        }

        public void setupUserInfo(String username, String imageURL)
        {

        }

    }









}

