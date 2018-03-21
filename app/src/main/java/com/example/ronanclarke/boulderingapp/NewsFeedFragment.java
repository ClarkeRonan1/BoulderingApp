package com.example.ronanclarke.boulderingapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by francisclarke on 15/03/2018.
 */

public class NewsFeedFragment extends Fragment
{
    private RecyclerView recyclePost;
    private List<Post> postList;

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private PostAdapter postAdapt;

    private DocumentSnapshot lastVisible;

    public NewsFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater layoutInflater, final ViewGroup box,
                             Bundle savedInstanceState)
    {

        View view = layoutInflater.inflate(R.layout.activity_news_feed_fragment, box, false);

        postList = new ArrayList<>();
        recyclePost = view.findViewById(R.id.recycler_view);

        mAuth = FirebaseAuth.getInstance();

        postAdapt = new PostAdapter(postList);
        recyclePost.setLayoutManager(new LinearLayoutManager(box.getContext()));
        recyclePost.setAdapter(postAdapt);

        if(mAuth.getCurrentUser() != null)
        {

            mFirestore = FirebaseFirestore.getInstance();

            recyclePost.addOnScrollListener(new RecyclerView.OnScrollListener()
            {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if(reachedBottom){

                        String desc = lastVisible.getString("desc");
                        Toast.makeText(box.getContext(), "Reached : " + desc, Toast.LENGTH_SHORT).show();

                        repopulate();

                    }

                }
            });

            Query orderByTime = mFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);
            orderByTime.addSnapshotListener(new EventListener<QuerySnapshot>()
            {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() -1);

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges())
                    {
                        if (doc.getType() == DocumentChange.Type.ADDED)
                        {

                            Post blogPost = doc.getDocument().toObject(Post.class);
                            postList.add(blogPost);

                            postAdapt.notifyDataSetChanged();

                        }
                    }

                }
            });

            //Implement Query to only display posts to users following
            //User 1 should only see posts from users they follow, user 2 in this
            //case. So user 1 signed in, populate newsfeed where users.following
            //is equal to posts.user_id specified.
            Query displayToFollowers = mFirestore.collection("Posts");
            displayToFollowers.orderBy("following").equals("following");


        }

        // Inflate the layout for this fragment
        return view;
    }


    public void repopulate()
    {

        Query repopulating = mFirestore.collection("Posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);

        repopulating.addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e)
            {

                if(!documentSnapshots.isEmpty())
                {

                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges())
                    {
                        if (doc.getType() == DocumentChange.Type.ADDED)
                        {

                            Post blogPost = doc.getDocument().toObject(Post.class);
                            postList.add(blogPost);

                            postAdapt.notifyDataSetChanged();

                        }
                    }

                }

            }
        });

    }

}