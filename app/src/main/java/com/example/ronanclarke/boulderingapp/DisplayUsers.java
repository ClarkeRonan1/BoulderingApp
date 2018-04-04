package com.example.ronanclarke.boulderingapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DisplayUsers extends AppCompatActivity
{
    private RecyclerView searchRecycler;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String TAG = "Displaying Following:";
    EditText searchTerm;
    Button searchBtn;
    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_users);

        searchTerm = findViewById(R.id.search_et);
        //Button to search for username
        searchBtn = findViewById(R.id.search_btn);
        searchRecycler = findViewById(R.id.search_result_recyclerview);
        searchRecycler.setNestedScrollingEnabled(false);
        searchRecycler.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getApplication());
        searchRecycler.setLayoutManager(mLayoutManager);
        recyclerAdapter = new RecyclerViewAdapter(retrieveClimbers(),getApplication());
        searchRecycler.setAdapter(recyclerAdapter);

        //set onclickListener for button
        searchBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                clearDatabase();
                queryDatabase();
            }
        });

    }

    private void clearDatabase()
    {
        int size = this.climberArrayList.size();
        this.climberArrayList.clear();
        recyclerAdapter.notifyItemRangeRemoved(0,size);
    }

    private void queryDatabase()
    {
        //using the search term, use this to compare to database
        mFirestore = FirebaseFirestore.getInstance();
        //mFirestore.collection("Users");
        mFirestore.collection("Users")
                .orderBy("name").startAt(searchTerm.getText().toString()).endAt(searchTerm.getText().toString() + "\uf8ff")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        String email = "";
                        String uid = "Whammy";
                        String username = "";
                        if(task.isSuccessful())
                        {
                            for(DocumentSnapshot document : task.getResult())
                            {
                                Log.d(TAG,document.getId() + " => " + document.getData());
                                username = document.getData().toString();
                            }
                            if(!username.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            {
                                Toast.makeText(DisplayUsers.this, "username" + username, Toast.LENGTH_SHORT).show();
                                Climber person = new Climber(email,uid,username);
                                climberArrayList.add(person);
                                recyclerAdapter.notifyDataSetChanged();
                            }
                        }
                        else
                            {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        //.orderBy("name").startAt(searchTerm.getText().toString()).endAt(searchTerm.getText().toString() + "\uf8ff");
        /*allUsers.addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e)
            {
                String username = "";
                //String uid = documentSnapshots.getDocuments().toString);
                if(!documentSnapshots.isEmpty())
                {
                    usernam
                }
            }
        });
        */

    }

    //Array list to store the climbers from the database
    private ArrayList<Climber>climberArrayList = new ArrayList<>();
    private ArrayList<Climber> retrieveClimbers()
    {
        return climberArrayList;
    }
}
