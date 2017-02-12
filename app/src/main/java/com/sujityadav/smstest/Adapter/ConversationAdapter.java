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
import java.util.List;

/**
 * Created by sujit yadav on 2/10/2017.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder>  {

private ArrayList<Conversations> conversationses;
    Context context;



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView address,body,count,time;


    public MyViewHolder(View view) {
        super(view);
        itemView.setOnClickListener(this);
        address = (TextView) itemView.findViewById(R.id.address);
        body = (TextView) itemView.findViewById(R.id.body);
        count = (TextView) itemView.findViewById(R.id.count);
        time = (TextView) itemView.findViewById(R.id.time);
    }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context,SmsDetail.class);
            intent.putExtra("_id",conversationses.get(getAdapterPosition()).get_id());
            intent.putExtra("address",conversationses.get(getAdapterPosition()).getAddress());
            context.startActivity(intent);
        }
    }


    public ConversationAdapter(Context context, ArrayList<Conversations> conversationses) {
        this.context=context;
        this.conversationses = conversationses;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_msg_item, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Conversations conversations = conversationses.get(position);
        holder.body.setMaxLines(3);
        holder.address.setText(conversations.getAddress());
        holder.body.setText(conversations.getSnippet());
        holder.count.setText(conversations.getMessage_count());
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
        Date updatedate = new Date(epochSeconds);
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(updatedate);
    }
}