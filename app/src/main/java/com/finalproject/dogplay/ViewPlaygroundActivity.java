package com.finalproject.dogplay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewPlaygroundActivity extends AppCompatActivity {

    TextView playgroundName_TextView;
    String playgroundName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_playground);

        playgroundName = getIntent().getStringExtra("EXTRA_SELECTED_PLAYGROUND");

        playgroundName_TextView = (TextView) findViewById(R.id.playgroundName_textView);
        playgroundName_TextView.setText(playgroundName);
    }
}
