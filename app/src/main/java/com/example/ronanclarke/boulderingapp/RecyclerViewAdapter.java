package com.example.ronanclarke.boulderingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Created by francisclarke on 04/04/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<DisplayClimberAdapter>
{
    private List<Climber> climberList;
    private Context context;
    private FirebaseFirestore mFirestore;

    public RecyclerViewAdapter(List<Climber>climberList,Context context)
    {
        this.climberList = climberList;
        this.context = context;

    }

    @NonNull
    @Override
    public DisplayClimberAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.followers_recyclerview,null);
        DisplayClimberAdapter climberAdapter = new DisplayClimberAdapter(layoutView);
        return climberAdapter;
    }

    @Override
    public void onBindViewHolder(final DisplayClimberAdapter holder, int position)
    {
        //places email from Climber object into the viewholder
        holder.username.setText(climberList.get(position).getUsername());

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(holder.follow.getText().equals("Follow"))
                {
                    mFirestore = FirebaseFirestore.getInstance();
                    //mFirestore.collection("Users").document("")

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return climberList.size();
    }
}
