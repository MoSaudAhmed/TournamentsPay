package com.mgdapps.play360.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mgdapps.play360.R;
import com.mgdapps.play360.models.MessageModel;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MViewHolder> {

    Context context;
    List<MessageModel> messageModelList;

    public MessagesAdapter(Context context, List<MessageModel> messageModelList) {
        this.context = context;
        this.messageModelList = messageModelList;
    }

    @NonNull
    @Override
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row, parent, false);

        return new MViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MViewHolder holder, int position) {

        holder.tv_messageRow_message.setText(messageModelList.get(position).getMessage());
        holder.tv_messageRow_name.setText(messageModelList.get(position).getUserName());

        Date date = new Date(String.valueOf(messageModelList.get(position).getDate()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setDateAndTimeToText(calendar, holder.tv_messageRow_time);

        if (!TextUtils.isEmpty(messageModelList.get(position).getProfilePic())) {
            Picasso.get().load(messageModelList.get(position).getProfilePic()).error(R.drawable.ic_person).into(holder.img_messageRow_profilePic);
        }
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public class MViewHolder extends RecyclerView.ViewHolder {

        TextView tv_messageRow_name, tv_messageRow_time, tv_messageRow_message;
        ImageView img_messageRow_profilePic;

        public MViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_messageRow_name = itemView.findViewById(R.id.tv_messageRow_name);
            tv_messageRow_time = itemView.findViewById(R.id.tv_messageRow_time);
            tv_messageRow_message = itemView.findViewById(R.id.tv_messageRow_message);
            img_messageRow_profilePic = itemView.findViewById(R.id.img_messageRow_profilePic);
        }
    }

    private void setDateAndTimeToText(Calendar calendar, TextView textView) {
        try {
            String hours = "";
            if (calendar.get(Calendar.HOUR_OF_DAY) != 0 || calendar.get(Calendar.HOUR_OF_DAY) != 00) {
                if (calendar.get(Calendar.HOUR_OF_DAY) > 12) {
                    hours = String.format("%02d", (calendar.get(Calendar.HOUR_OF_DAY) - 12));
                } else {
                    hours = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
                }

            } else {
                hours = "12";
            }
            String Mins = String.format("%02d", calendar.get(Calendar.MINUTE));
            if (calendar.get(Calendar.HOUR_OF_DAY) >= 12) {
                textView.setText(hours + ":" + Mins + " PM-" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" +
                        calendar.get(Calendar.YEAR));
            } else {
                textView.setText(hours + ":" + Mins + " AM-" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" +
                        calendar.get(Calendar.YEAR));
            }

        } catch (Exception e) {
            Log.e("ErrorinsettingDateTime", e.getLocalizedMessage());
        }

    }
}
