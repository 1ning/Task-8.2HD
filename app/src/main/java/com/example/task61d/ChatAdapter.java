package com.example.task61d;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ChatAdapter extends BaseAdapter {
    //Chat Data List
    private List<ChatBean> chatBeanList;
    private LayoutInflater layoutInflater;
    public ChatAdapter(List<ChatBean> chatBeanList, Context context) {
        this.chatBeanList = chatBeanList;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return chatBeanList.size();
    }
    @Override
    public Object getItem(int position) {
        return chatBeanList.get(position);
    }
    //The id of the item object
    @Override
    public long getItemId(int position) {
        return position;
    }

    //Return the corresponding view
    @Override
    public View getView(int position, View contentView, ViewGroup viewGroup) {

        //Used to get the controls on the Item screen
        Holder holder = new Holder();
        // Determine whether the current message is a sent message or a received
        // message, and load different views for different messages
        if (chatBeanList.get(position).getState() == ChatBean.RECEIVE){
        // Load the left layout, i.e. the layout information corresponding to the robot
            contentView = layoutInflater.inflate(R.layout.chatting_left_item,null);
        }
        // Load the layout of the user message on the right
        else {
            contentView = layoutInflater.inflate(R.layout.chatting_right_item,null);
            holder.iv_head=contentView.findViewById(R.id.iv_head);
            holder.iv_head.setImageBitmap(Global.photo1);
        }
        holder.tv_chat_content = (TextView) contentView.findViewById(R.id.tv_chat_content);
        // Display the chat data between the bot and the user on the interface
        holder.tv_chat_content.setText(chatBeanList.get(position).getMessage());
        return contentView;
    }
    //Used to get the controls on the Item screen
    class Holder{
        public TextView tv_chat_content;
        public ImageView iv_head;
    }
}


