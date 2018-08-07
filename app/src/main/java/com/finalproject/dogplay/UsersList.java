package com.finalproject.dogplay;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.finalproject.dogplay.models.UserProfile;

import java.util.List;

public class UsersList extends ArrayAdapter<UserProfile> {

    private Activity context;
    private List<UserProfile> users;

    public UsersList(Activity context, List<UserProfile> users) {
        super(context, R.layout.users_listview_layout, users);
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem       = inflater.inflate(R.layout.users_listview_layout, null, true);

        TextView playgroundUser = listViewItem.findViewById(R.id.userprofile_textview);

        UserProfile user        = users.get(position);

        playgroundUser.setText(user.toString());

        return listViewItem;
    }
}
