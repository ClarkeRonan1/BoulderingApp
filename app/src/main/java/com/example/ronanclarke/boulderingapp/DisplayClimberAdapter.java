package com.example.ronanclarke.boulderingapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by francisclarke on 04/04/2018.
 */

public class DisplayClimberAdapter extends RecyclerView.ViewHolder
{
    public TextView username;
    public Button follow;

    public DisplayClimberAdapter(View itemView)
    {
        super(itemView);
        username = itemView.findViewById(R.id.user_name);
        follow = itemView.findViewById(R.id.follow_btn);
    }
}
