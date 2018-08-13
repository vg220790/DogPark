package com.finalproject.dogplay.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.finalproject.dogplay.R;
import com.finalproject.dogplay.models.UserProfile;

import java.util.List;

public class UsersList extends ArrayAdapter<UserProfile> {

    private final Activity CONTEXT;
    private final List<UserProfile> USERS;

    public UsersList(Activity context, List<UserProfile> users) {
        super(context, R.layout.users_listview_layout, users);
        this.CONTEXT = context;
        this.USERS = users;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = CONTEXT.getLayoutInflater();

        View listViewItem       = inflater.inflate(R.layout.users_listview_layout, parent, false);

        TextView playgroundUser = listViewItem.findViewById(R.id.userprofile_textview);

        UserProfile user        = USERS.get(position);

        playgroundUser.setText(user.toString());

        return listViewItem;
    }
}
