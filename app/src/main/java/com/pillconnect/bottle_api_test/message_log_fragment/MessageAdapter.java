package com.pillconnect.bottle_api_test.message_log_fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pillconnect.bottle_api_test.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Howard
 * @date 23/01/18
 */
final class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<LogMessage> msgList = null;

    public MessageAdapter(List<LogMessage> msgList) {
        this.msgList = msgList;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        String msg = msgList.get(position).message;
        if (msg == null) {
            msg = "<null>";
        }
        // Show message in linearlayout.
        holder.leftMsgTextView.setText(msg);
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.message_item_view, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (msgList == null) {
            // if this is null, why wasn't it initialised in the constructor?
            msgList = new ArrayList<LogMessage>();//fixme: questionable.
        }
        return msgList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        //LinearLayout leftMsgLayout;
        TextView leftMsgTextView;

        public MessageViewHolder(View itemView) {
            super(itemView);

            if (itemView != null) {
                //leftMsgLayout = (LinearLayout) itemView.findViewById(R.id.message_layout);
                leftMsgTextView = (TextView) itemView.findViewById(R.id.message_text_view);
            }
        }
    }
}
