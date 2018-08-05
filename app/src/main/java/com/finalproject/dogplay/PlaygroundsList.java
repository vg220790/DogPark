package com.finalproject.dogplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.finalproject.dogplay.models.Playground;
import com.finalproject.dogplay.models.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class PlaygroundsList extends ArrayAdapter<Playground> {

    private Activity context;
    private List<Playground> playgrounds;

    public PlaygroundsList(Activity context, List<Playground> playgroundsList) {
        super(context, R.layout.playgrounds_listview_layout, playgroundsList);
        this.context = context;
        this.playgrounds = playgroundsList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.playgrounds_listview_layout, null, true);

        TextView playgroundName = (TextView) listViewItem.findViewById(R.id.playgroundName_textView);
        TextView playgroundUsers = (TextView) listViewItem.findViewById(R.id.playgroundUsers_textView);

        Playground playground = playgrounds.get(position);

        playgroundName.setText(playground.getAddress());

        String users = "";
        if (!playground.getUsers().isEmpty()) {
            for (UserProfile userProfile : playground.getUsers()) {
                users += userProfile.getuName() + " & " + userProfile.getdName() + "; ";
            }
        } else {
            users = " There are'nt any users currently at this playground";
        }

        playgroundUsers.setText(users);


        return listViewItem;
    }

}
