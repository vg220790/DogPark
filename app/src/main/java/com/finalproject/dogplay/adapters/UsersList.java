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
    private final UserProfile current_user;
    private ArrayList<String> friends;
    private ArrayList<String> enemies;
    ViewHolder viewHolder;

    public UsersList(Activity context, List<UserProfile> users, UserProfile current_user) {
        super(context, R.layout.users_listview_layout, users);
        this.CONTEXT = context;
        this.USERS = users;
        this.current_user = current_user;
        setLists();
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
            current_user.checkLists();
            if (current_user.getdFriends().contains(selected_user.getuID()))
                viewHolder.userInfo.setBackgroundColor(Color.parseColor("#80ff00"));
            if (current_user.getdEnemies().contains(selected_user.getuID()))
                viewHolder.userInfo.setBackgroundColor(Color.parseColor("#ff0000"));
            viewHolder.userInfo.setText(selected_user.toString());
            viewHolder.friend = convertView.findViewById(R.id.friend_button);
            viewHolder.enemy = convertView.findViewById(R.id.enemy_button);
            viewHolder.friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleFriendPressed(v, selected_user);
                    databaseUserProfiles.child(current_user.getuID()).setValue(current_user);
                }
            });

            viewHolder.enemy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleEnemyPressed(v, selected_user);
                    databaseUserProfiles.child(current_user.getuID()).setValue(current_user);
                }
            });
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
        if (!this.friends.contains(selected_user.getuID())) {
            this.friends.add(selected_user.getuID());
            current_user.setFriend(selected_user.getuID());
            Toast.makeText(getContext(), selected_user.getdName() + " added to friends", Toast.LENGTH_LONG).show();
            f.setText("FRIEND");
            f.setBackgroundColor(Color.parseColor("#80ff00"));
            f.setPressed(true);
            //viewHolder.friend.setBackgroundColor(viewHolder.friend.getContext().getResources().getColor(Color.green(0x80ff00)));
        } else {
            current_user.setFriend(selected_user.getuID());
            this.friends.remove(selected_user.getuID());
            Toast.makeText(getContext(), selected_user.getdName() + " removed from friends", Toast.LENGTH_LONG).show();
            f.setText("TAG AS FRIEND");
            f.setBackgroundColor(viewHolder.friend.getContext().getResources().getColor(R.color.colorAccent));
        }
    }


    public void handleEnemyPressed(View v, UserProfile selected_user) {
        Button e = (Button) v;
        if (!this.enemies.contains(selected_user.getuID())) {
            this.enemies.add(selected_user.getuID());
            current_user.setEnemy(selected_user.getuID());
            Toast.makeText(getContext(), selected_user.getdName() + " added to enemies", Toast.LENGTH_LONG).show();
            e.setText("ENEMY");
            e.setBackgroundColor(Color.parseColor("#ff0000"));
            e.setPressed(true);
        } else {
            current_user.setEnemy(selected_user.getuID());
            this.enemies.remove(selected_user.getuID());
            Toast.makeText(getContext(), selected_user.getdName() + " removed from enemies", Toast.LENGTH_LONG).show();
            e.setText("TAG AS ENEMY");
            e.setBackgroundColor(viewHolder.enemy.getContext().getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public void setLists() {
        if (current_user.getdFriends() == null)
            this.friends = new ArrayList<String>();
        else
            this.friends = this.current_user.getdFriends();

        if (current_user.getdEnemies() == null)
            this.enemies = new ArrayList<String>();
        else
            this.enemies = this.current_user.getdEnemies();
    }
}


