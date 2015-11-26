package com.happyhome.kkommanapall;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happyhome.kkommanapall.model.MessageObject;

import java.util.List;

/**
 * Created by kkommanapall on 10/25/2015.
 */
public class ChatAdapter extends BaseAdapter {
    private final List<MessageObject> chatMessages;
    private Context context;
    private final int paddingDp;
    private final int paddingBottom;

    public ChatAdapter(Context context, List<MessageObject> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
        this.paddingDp = (int)(100 * context.getResources().getDisplayMetrics().density);
        this.paddingBottom = (int)(10 * context.getResources().getDisplayMetrics().density);
    }

    @Override
    public int getCount() {
            return chatMessages.size();
    }

    @Override
    public MessageObject getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(MessageObject message) {
        chatMessages.add(message);
    }

    public void add(List<MessageObject> messages) {
        chatMessages.addAll(messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        MessageObject chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.item_message, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        boolean myMsg = chatMessage.isMe() ;
        setAlignment(holder, myMsg);
        holder.txtMessage.setText(chatMessage.getMessage());

        return convertView;
    }

    private void setAlignment(ViewHolder holder, boolean isMe) {
        if (!isMe) {
            holder.txtMessage.setBackgroundResource(R.drawable.light_violet);
            holder.content.setPadding(0, 0, paddingDp, paddingBottom);
            holder.content.setGravity(Gravity.LEFT);
        } else {
            holder.txtMessage.setBackgroundResource(R.drawable.gray_rectangle);
            holder.content.setPadding(paddingDp, 0, 0, paddingBottom);
            holder.content.setGravity(Gravity.RIGHT);
        }
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.message);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.txtChatExecutive = (TextView) v.findViewById(R.id.chatExecutive);
        holder.txtCallExecutive = (TextView) v.findViewById(R.id.callExecutive);
        return holder;
    }


    private static class ViewHolder {
        public TextView txtMessage;
        public LinearLayout content;
        public TextView txtChatExecutive;
        public TextView txtCallExecutive;
    }
}
