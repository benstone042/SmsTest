package com.sujityadav.smstest.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sujityadav.smstest.Activity.SmsDetail;
import com.sujityadav.smstest.Model.Conversations;
import com.sujityadav.smstest.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sujit yadav on 2/12/2017.
 */

public class SmsDetailAdapter extends RecyclerView.Adapter<SmsDetailAdapter.MyViewHolder>  {

    private ArrayList<Conversations> conversationses;
    Context context;



    public class MyViewHolder extends RecyclerView.ViewHolder  {
        public TextView address,body,time;


        public MyViewHolder(View view) {
            super(view);
            address = (TextView) itemView.findViewById(R.id.address);
            body = (TextView) itemView.findViewById(R.id.body);
            time = (TextView) itemView.findViewById(R.id.time);
        }


    }


    public SmsDetailAdapter(Context context, ArrayList<Conversations> conversationses) {
        this.context=context;
        this.conversationses = conversationses;
    }

    @Override
    public SmsDetailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_msg_item, parent, false);


        return new SmsDetailAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SmsDetailAdapter.MyViewHolder holder, int position) {
        Conversations conversations = conversationses.get(position);

        holder.body.setText(conversations.getSnippet());
        holder.time.setText(Epoch2DateString(conversations.getDate(),"yyyy-MM-dd h:mm a"));

    }

    @Override
    public int getItemCount() {
        return conversationses.size();
    }

    public void swapdata(ArrayList<Conversations> messageBoxObjects){
        conversationses=messageBoxObjects;
        notifyDataSetChanged();

    }
    public static String Epoch2DateString(Long epochSeconds, String formatString) {
        Date updatedate = new Date(epochSeconds.longValue());
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(updatedate);
    }
}