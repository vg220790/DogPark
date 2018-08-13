package com.finalproject.dogplay.adapters;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.finalproject.dogplay.R;
import com.finalproject.dogplay.models.ChatData;
import com.finalproject.dogplay.models.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible to show all the messages
 * in the chat
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    /**
     * ViewHolder to be the item of the list
     */
    static final class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView message;

        ChatViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.item_username);
            message = view.findViewById(R.id.item_message);
        }
    }

    private List<ChatData> mContent = new ArrayList<>();

    public void clearData() {
        mContent.clear();
    }

    public void addData(ChatData data) {
        mContent.add(data);
    }

    @Override
    public int getItemCount() {
        return mContent.size();
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat, parent, false);
        return new ChatViewHolder(root);
    }

    @Override
    public void onBindViewHolder(final ChatViewHolder holder, int position) {
        final ChatData data = mContent.get(position);
        loadUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(myUserProfile.getuName().equals(data.getName())){
                    holder.message.setText(data.getMessage());
                    holder.message.setGravity(Gravity.RIGHT);
                    holder.name.setText(data.getName());
                    holder.name.setBackgroundColor(Color.BLUE);
                }
                else    {
                    holder.message.setText(data.getMessage());
                    holder.name.setText(data.getName());
                    holder.message.setGravity(Gravity.LEFT);
                    holder.name.setTextColor(Color.BLACK);
                    holder.name.setBackgroundColor(Color.WHITE);
                }
            }
        }, 500);


    }
    private DatabaseReference userProfileRef;
    private UserProfile myUserProfile;

    private void loadUser(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String mUserId = user.getUid();
        userProfileRef= FirebaseDatabase.getInstance().getReference().child("UserProfiles");
        userProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren())
                {
                    if(mUserId.equals(userSnapshot.child("uID").getValue())) {
                        myUserProfile = userSnapshot.getValue(UserProfile.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}