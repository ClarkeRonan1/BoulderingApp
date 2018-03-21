package com.example.ronanclarke.boulderingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

/**
 * Created by ronanclarke on 13/03/2018.
 * This activity is used with an adapter to manage the post parameters
 * from the Post class. The PostViewHolder is used to hold a view
 * representing a single list parameter.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>
{
    public List<Post> postList;
    public Context context;

    private FirebaseFirestore mFirestore;

    public PostAdapter(List<Post> postList)
    {

        this.postList = postList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row, parent, false);
        context = parent.getContext();
        mFirestore = FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i)
    {

        String desc_data = postList.get(i).getDesc();
        holder.setDescText(desc_data);

        String image_url = postList.get(i).getImage_url();
        holder.setBlogImage(image_url);

        String user_id = postList.get(i).getUser_id();
        //User Data will be retrieved here...
        mFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");

                    holder.setUserData(userName, userImage);


                }
                else
                {

                    //Firebase Exception

                }

            }
        });

    }

    @Override
    public int getItemCount()
    {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private View holdView;

        private TextView postStatusTV;
        private ImageView postPicIV;
        private TextView postTimeStampTV;

        private TextView postUserNameTV;
        private ImageView userProfilerIV;

        public ViewHolder(View itemView)
        {
            super(itemView);
            holdView = itemView;
        }

        public void setDescText(String descText){

            postStatusTV = holdView.findViewById(R.id.post_status);
            postStatusTV.setText(descText);

        }

        public void setBlogImage(String downloadUri)
        {

            postPicIV = holdView.findViewById(R.id.post_route_image);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.ic_launcher_background);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).into(postPicIV);

        }

        public void setTime(String date)
        {

            postTimeStampTV = holdView.findViewById(R.id.post_timestamp);
            postTimeStampTV.setText(date);

        }

        public void setUserData(String name, String image)
        {

            userProfilerIV= holdView.findViewById(R.id.post_profiler_image);
            postUserNameTV= holdView.findViewById(R.id.post_username);

            postUserNameTV.setText(name);


            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.ic_launcher_background);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(userProfilerIV);

        }

    }

}