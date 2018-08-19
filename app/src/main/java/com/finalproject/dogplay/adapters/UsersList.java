package com.finalproject.dogplay.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.finalproject.dogplay.MainActivity;
import com.finalproject.dogplay.R;
import com.finalproject.dogplay.models.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UsersList extends ArrayAdapter<UserProfile> {

    private final Activity CONTEXT;
    private final List<UserProfile> USERS;
    UserProfile current_user;
    //private ArrayList<UserProfile> friends;
    //private ArrayList<UserProfile> enemies;
    ViewHolder viewHolder;

    public UsersList(Activity context, List<UserProfile> users, UserProfile current_user) {
        super(context, R.layout.users_listview_layout, users);
        this.CONTEXT = context;
        this.USERS = users;
        this.current_user = current_user;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final DatabaseReference databaseUserProfiles = FirebaseDatabase.getInstance().getReference("UserProfiles");
        final UserProfile selected_user = USERS.get(position);

        ViewHolder mainViewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = CONTEXT.getLayoutInflater();
            convertView = inflater.inflate(R.layout.users_listview_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.userInfo = convertView.findViewById(R.id.userprofile_textview);
            viewHolder.photo = convertView.findViewById(R.id.user_imageview);
            viewHolder.friend = convertView.findViewById(R.id.friend_button);
            viewHolder.enemy = convertView.findViewById(R.id.enemy_button);
            //in case lists are null
            current_user.checkLists();
            if (selected_user != null) {
                if (current_user.getdFriends().containsKey(selected_user.getuID()))
                viewHolder.userInfo.setBackgroundColor(Color.parseColor("#80ff00"));
                if (current_user.getdEnemies().containsKey(selected_user.getuID()))
                viewHolder.userInfo.setBackgroundColor(Color.parseColor("#ff0000"));
            viewHolder.userInfo.setText(selected_user.toString());


                if (current_user.getuID().equals(selected_user.getuID())) {
                    viewHolder.friend.setVisibility(View.GONE);
                    viewHolder.enemy.setVisibility(View.GONE);
                    viewHolder.userInfo.setText("YOU: " + selected_user.toString());
                }

            viewHolder.friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleFriendPressed(v, selected_user);
                    // updating users in firebase
                    databaseUserProfiles.child(current_user.getuID()).setValue(current_user);
                    databaseUserProfiles.child(selected_user.getuID()).setValue(selected_user);
                }
            });

            viewHolder.enemy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleEnemyPressed(v, selected_user);
                    // updating users in firebase

                    databaseUserProfiles.child(current_user.getuID()).setValue(current_user);
                    databaseUserProfiles.child(selected_user.getuID()).setValue(selected_user);
                }
            });
            } else {
                viewHolder.userInfo.setText("PLAYGROUND IS EMPTY");
                viewHolder.friend.setVisibility(View.GONE);
                viewHolder.enemy.setVisibility(View.GONE);
                viewHolder.photo.setVisibility(View.GONE);
            }
            convertView.setTag(viewHolder);
        } else {
            mainViewHolder = (ViewHolder) convertView.getTag();
            //add code here to set image
            mainViewHolder.userInfo.setText(selected_user.toString());
        }
        return convertView;
    }

    public void handleFriendPressed(View v, UserProfile selected_user) {
        Button f = (Button) v;

        //add as friend
        if (!current_user.getdFriends().containsKey(selected_user.getuID())) {
            current_user.setFriend(selected_user);
            ///////////////
            handleFollower(selected_user);
            ///////////////
            Toast.makeText(getContext(), selected_user.getdName() + " added to friends", Toast.LENGTH_LONG).show();
            f.setText("FRIEND");
            f.setBackgroundColor(Color.parseColor("#80ff00"));
            f.setPressed(true);

            // unfriend
        } else {
            current_user.getdFriends().remove(selected_user.getuID());
            ///////////////
            handleFollower(selected_user);
            ///////////////
            Toast.makeText(getContext(), selected_user.getdName() + " removed from friends", Toast.LENGTH_LONG).show();
            f.setText("TAG AS FRIEND");
            f.setBackgroundColor(viewHolder.friend.getContext().getResources().getColor(R.color.colorAccent));
        }
    }


    public void handleEnemyPressed(View v, UserProfile selected_user) {
        Button e = (Button) v;
        if (!current_user.getdEnemies().containsKey(selected_user.getuID())) {
            current_user.setEnemy(selected_user);
            ///////////////
            handleFollower(selected_user);
            ///////////////
            Toast.makeText(getContext(), selected_user.getdName() + " added to enemies", Toast.LENGTH_LONG).show();
            e.setText("ENEMY");
            e.setBackgroundColor(Color.parseColor("#ff0000"));
            e.setPressed(true);
        } else {
            current_user.getdEnemies().remove(selected_user.getuID());
            Toast.makeText(getContext(), selected_user.getdName() + " removed from enemies", Toast.LENGTH_LONG).show();
            e.setText("TAG AS ENEMY");
            e.setBackgroundColor(viewHolder.enemy.getContext().getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public void handleFollower(UserProfile selected_user) {
        //in case lists are null objects
        selected_user.checkLists();
        String follower_email = current_user.getuEMail();
        //adding current user as follower of selected_user
        if (!selected_user.getFollowers().contains(follower_email))
            selected_user.getFollowers().add(follower_email);
            //removing current user from followers list of selected_user
        else if (selected_user.getFollowers().contains(follower_email))
            selected_user.getFollowers().remove(follower_email);


    }
}


