package com.finalproject.dogplay.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.finalproject.dogplay.R;
import com.finalproject.dogplay.models.Playground;

import java.util.List;

public class PlaygroundsList extends ArrayAdapter<Playground> {

    private final Activity CONTEXT;
    private final List<Playground> PLAYGROUNDS;

    public PlaygroundsList(Activity context, List<Playground> playgroundsList) {
        super(context, R.layout.playgrounds_listview_layout, playgroundsList);
        this.CONTEXT = context;
        this.PLAYGROUNDS = playgroundsList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = CONTEXT.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.playgrounds_listview_layout, parent, false);

        TextView playgroundName     = listViewItem.findViewById(R.id.playgroundName_textView);
        TextView playgroundUsers    = listViewItem.findViewById(R.id.playgroundUsers_textView);

        Playground playground = this.PLAYGROUNDS.get(position);

        playgroundName.setText(playground.getAddress());

        String users;
        if (playground.hasUsers()) {
            users = playground.usersToString(false);
        } else {
            users = " There aren't any users currently at this playground";
        }

        playgroundUsers.setText(users);


        return listViewItem;
    }

}
