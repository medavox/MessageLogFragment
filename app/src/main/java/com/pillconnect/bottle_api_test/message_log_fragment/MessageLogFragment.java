package com.pillconnect.bottle_api_test.message_log_fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pillconnect.bottle_api_test.R;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MessageLogFragment extends Fragment {
    private static final String TAG = "MessageLogFragment";
    // Create the initial data list.
    private final List<LogMessage> msgList = new CopyOnWriteArrayList<LogMessage>();

    // Create the data adapter with above data list.
    private final MessageAdapter messageAdapter = new MessageAdapter(msgList);

    private RecyclerView msgRecyclerView;

    private static Queue<LogMessage> pendingWrites = new ConcurrentLinkedQueue<LogMessage>();
    private static boolean pendingWriteQueueConsumerIsRunning = false;
    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    private static MessageLogFragment instance;

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
        instance = this;
        return view;
    }

    //either a)
    /*using a static reference to the instance, along with a static reference to our recycler view

    + will probably work
    + quick
    - may break again in the future
    - limits us to one instance of this MessageLogFragment per app
    - may cause memory leaks


    or b)

    create a static method which can be called from any thread, any class.
    this method merely creates a new LogMessage object and adds it to the queue.
    Then, at some later date (when we can do runOnUiThread() properly),
    write the backloggged Queue<LogFragment> to the actual UI message log RecyclerView
    * */

    //private boolean actualWrite(@NonNull final LogMessage logMessage) {
    public void postMessage(@NonNull String message) {
        if(TextUtils.isEmpty(message)) {
            new Exception("null or empty value passed to MessageLogFragment!").printStackTrace();
            return;
        }
        final LogMessage logMessage = new LogMessage(message);//*/

        Log.v(TAG, "this postMessage:"+this);
        //Activity ourActivity = instance.getActivity();
        Activity ourActivity = getActivity();

        if (ourActivity == null) {
            //new Exception("activity was null when attempting to write a message").printStackTrace();
            Log.w(TAG, "activity was null when attempting to write a message;" +
                    "using an AsynTask instead...");

            //return false;
            new Doer(msgList, messageAdapter, msgRecyclerView).execute(logMessage);
            return;
        }//else:
        ourActivity.runOnUiThread(new Runnable() {//*/
            @Override public void run() {
                // Add a new sent message to the list.
                msgList.add(logMessage);

                final int newMsgPosition = msgList.size() - 1;
                //final int newMsgPosition = 0;
                // Notify recycler view insert one new data.
                messageAdapter.notifyItemInserted(newMsgPosition);

                // Scroll RecyclerView to the last message.
                msgRecyclerView.scrollToPosition(newMsgPosition);

            }
        });
    }

    /*public static void postMessage(@NonNull String message) {
        if(TextUtils.isEmpty(message)) {
            new Exception("null or empty value passed to MessageLogFragment!").printStackTrace();
            return;
        }
        LogMessage logMessage = new LogMessage(message);
        pendingWrites.add(logMessage);

        //fire up a task in another thread to go through all the pending writes,
        //as long as there isn't one running already
        if(!pendingWriteQueueConsumerIsRunning) {
            pendingWriteQueueConsumerIsRunning = true;
            executor.submit(new Runnable() {
                @Override public void run() {
                    if (pendingWrites.size() > 0) {
                        //only perform a write if there is any data in the buffer NOW
                        //Log.i(TAG, "Writing queue to message log. Queue size: " + pendingWrites.size());
                        while(pendingWrites.size() > 0) {
                            LogMessage pw = pendingWrites.poll();
                            try {
                                //retryingWrite(pw.getKey(), pw.getValue());
                                actualWrite(pw);
                                //pendingWrites.remove(pw);
                            }
                            catch(Exception e) {
                                Log.e(TAG, "failed to write message \""+pw+
                                        "\" to onscreen message log! error: "+e.toString());
                                //e.printStackTrace();
                            }
                        }
                    }
                    pendingWriteQueueConsumerIsRunning = false;
                }
            });
        }
    }//*/

}
