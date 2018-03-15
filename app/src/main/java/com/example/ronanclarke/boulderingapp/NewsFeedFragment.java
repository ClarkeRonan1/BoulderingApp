package com.example.ronanclarke.boulderingapp;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by francisclarke on 15/03/2018.
 */

public class NewsFeedFragment
{
    //Firebase variables
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private PostAdapter postAdapter;
    //Post variables
    private DocumentSnapshot displayedPrev;
    public RecyclerView postRecycler;
    public List<Post> postList;

    public NewsFeedFragment()
    {
        //empty constructor
    }

    public View onCreateView(LayoutInflater filler, final ViewGroup box,
                             Bundle savedInstanceState)
    {
        View v = filler.inflate(R.layout.activity_news_feed_fragment,box,false);
        postList = new ArrayList<>();
        postList = v.findViewById(R.id.post_view);
        mAuth = FirebaseAuth.getInstance();

        //Make new post adapter with postList list
        postAdapter = new PostAdapter(postList);
        postRecycler.setLayoutManager(new LinearLayoutManager(box.getContext()));
        postRecycler.setAdapter(postAdapter);

        if(mAuth.getCurrentUser() == null)
        {
            //Display error message
        }
        else
        {
            mFirestore = FirebaseFirestore.getInstance();
            postRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                {
                    super.onScrolled(recyclerView, dx, dy);

                }

                @Override
                public int hashCode() {
                    return super.hashCode();
                }

                @Override
                public boolean equals(Object obj) {
                    return super.equals(obj);
                }

                @Override
                protected Object clone() throws CloneNotSupportedException {
                    return super.clone();
                }

                @Override
                public String toString() {
                    return super.toString();
                }

                @Override
                protected void finalize() throws Throwable {
                    super.finalize();
                }
            });
        }
        //Return the view layout
        return v;
    }
}
