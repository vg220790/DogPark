package com.finalproject.dogplay.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.finalproject.dogplay.ActivityCallback;
import com.finalproject.dogplay.ViewPlaygroundActivity;
import com.finalproject.dogplay.R;
import com.finalproject.dogplay.models.UserProfile;
import com.finalproject.dogplay.adapters.ChatAdapter;
import com.finalproject.dogplay.models.ChatData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Objects;

/**
 * Class responsible to be the chat screen of the app
 */
public class ChatFragment extends Fragment {

    private static final String TAG = "ChatFragment";

    private DatabaseReference users;

    /** Activity callback **/
    private ActivityCallback mCallback;

    /** Database instance **/
    private DatabaseReference mReference;

    /** UI Components **/
    private EditText mChatInput;
    private ChatAdapter mAdapter;

    /** Class variables **/
    private String mUsername;
    private String mUserId;

    private final int CHAT_TIME_OFFSET = 30*60*1000;

    /**
     * Create a instance of this fragment
     *
     * @return fragment instance
     */

    private long currentTime;

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    /// Lifecycle methods

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentTime = new Date().getTime();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mUserId = user.getUid();
        users = FirebaseDatabase.getInstance().getReference().child("UserProfiles");

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    UserProfile user = userSnapshot.getValue(UserProfile.class);
                    if (Objects.requireNonNull(user).getuID().equals(mUserId)) {
                        mUsername = user.getuName();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setHasOptionsMenu(true);
        setupConnection();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);


        mChatInput =  root.findViewById(R.id.chat_input);
        mChatInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                ChatData data = new ChatData();
                data.setTime();
                data.setMessage(mChatInput.getText().toString());
                data.setName(mUsername);

                mReference.child(String.valueOf(new Date().getTime())).setValue(data);

                closeAndClean();
                return true;
            }
        });

        RecyclerView chat = root.findViewById(R.id.chat_message);
        chat.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new ChatAdapter();
        chat.setAdapter(mAdapter);

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (ActivityCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }



    /// Private methods

    private void closeAndClean() {
        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(manager).hideSoftInputFromWindow(mChatInput.getWindowToken(), 0);
        mChatInput.setText("");
    }

    private void setupConnection() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final ViewPlaygroundActivity activity = (ViewPlaygroundActivity)getActivity();
        mReference = database.getReference("Playgrounds").child(activity.getCurrentPlaygroundID()).child("chat");

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,"SUCCESS!");
                handleReturn(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG,"ERROR: " + databaseError.getMessage());
                Toast.makeText(getContext(), "chat_init_error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void handleReturn(DataSnapshot dataSnapshot) {
        mAdapter.clearData();

        for(DataSnapshot item : dataSnapshot.getChildren()) {
            ChatData data = item.getValue(ChatData.class);
            if(currentTime - CHAT_TIME_OFFSET <= data.getTime())
                mAdapter.addData(data);
            else{
                Log.d("time","time passed");
            }
            Log.d("dfdfdf","dfdfsdf");
        }

        mAdapter.notifyDataSetChanged();
    }
}