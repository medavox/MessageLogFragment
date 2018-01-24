package com.pillconnect.bottle_api_test.message_log_fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pillconnect.bottle_api_test.R;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class MessageLogFragment extends Fragment {
    public static final String INTENT_ACTION = "message log queue has been updated";
    private static final String TAG = "MessageLogFragment";
    // Create the initial data list.
    private final List<LogMessage> msgList = new CopyOnWriteArrayList<LogMessage>();

    // Create the data adapter with above data list.
    private final MessageAdapter messageAdapter = new MessageAdapter(msgList);

    private RecyclerView msgRecyclerView;

    private BroadcastReceiver updateLogListener = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            //Activity ourActivity = instance.getActivity();
            Activity ourActivity = getActivity();

            if (ourActivity == null) {
                new Exception("activity was null when attempting to write a message")
                        .printStackTrace();
                return;
            }//else:
            ourActivity.runOnUiThread(new Runnable() {//*/
                @Override public void run() {
                    int itemsInserted = 0;
                    while(!UiLog.pendingWrites.isEmpty()) {
                        LogMessage lm = UiLog.pendingWrites.poll();
                        msgList.add(lm);
                        itemsInserted++;
                    }

                    final int newMsgPosition = msgList.size() - 1;
                    //final int newMsgPosition = 0;
                    // Notify recycler view insert one new data.
                    messageAdapter.notifyItemRangeInserted(newMsgPosition, itemsInserted);

                    // Scroll RecyclerView to the last message.
                    msgRecyclerView.scrollToPosition(newMsgPosition);
                }
            });
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(updateLogListener, new IntentFilter(INTENT_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext())
                .unregisterReceiver(updateLogListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_log, container, true);
        Log.v(TAG, "this onCreateView:"+this);

        // Get RecyclerView object.
        msgRecyclerView = (RecyclerView)view.findViewById(R.id.log_recycler_view);

        // Set RecyclerView layout manager.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        msgRecyclerView.setLayoutManager(linearLayoutManager);

        // Set data adapter to RecyclerView.
        msgRecyclerView.setAdapter(messageAdapter);
        return view;
    }

    @Override
    public String toString() {
        return super.toString()
                +"; recyclerview: "+msgRecyclerView
                +"; msgList: "+msgList.toString()
                ;
    }
}
