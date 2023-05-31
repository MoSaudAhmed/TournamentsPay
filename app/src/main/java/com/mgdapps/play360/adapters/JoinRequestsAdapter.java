package com.mgdapps.play360.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mgdapps.play360.R;
import com.mgdapps.play360.callbacks.JoinRejectCallback;
import com.mgdapps.play360.models.JoinRequestModel;
import com.squareup.picasso.Picasso;

import java.util.List;


public class JoinRequestsAdapter extends RecyclerView.Adapter<JoinRequestsAdapter.MViewHolder> {
    Context context;
    List<JoinRequestModel> joinRequestModelList;
    JoinRejectCallback joinRejectCallback;

    public JoinRequestsAdapter(Context context, JoinRejectCallback joinRejectCallback, List<JoinRequestModel> joinRequestModelList) {
        this.context = context;
        this.joinRequestModelList = joinRequestModelList;
        this.joinRejectCallback = joinRejectCallback;
    }

    @NonNull
    @Override
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requested_users_row, parent, false);
        return new MViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MViewHolder holder, final int position) {

        holder.tv_requestedUsers_name.setText(joinRequestModelList.get(position).getNAME());
        holder.tv_requestedUsers_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinRejectCallback.joinClicked(position);
            }
        });

        holder.tv_requestedUsers_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinRejectCallback.rejectClicked(position);
            }
        });

        if (!TextUtils.isEmpty(joinRequestModelList.get(position).getProfilePic())) {
            Picasso.get().load(joinRequestModelList.get(position).getProfilePic()).error(R.drawable.ic_person).into(holder.img_requestedUsers_pic);
        }
    }

    @Override
    public int getItemCount() {
        return joinRequestModelList.size();
    }

    public class MViewHolder extends RecyclerView.ViewHolder {
        TextView tv_requestedUsers_accept, tv_requestedUsers_name, tv_requestedUsers_reject;
        ImageView img_requestedUsers_pic;

        public MViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_requestedUsers_accept = itemView.findViewById(R.id.tv_requestedUsers_accept);
            tv_requestedUsers_name = itemView.findViewById(R.id.tv_requestedUsers_name);
            tv_requestedUsers_reject = itemView.findViewById(R.id.tv_requestedUsers_reject);
            img_requestedUsers_pic = itemView.findViewById(R.id.img_requestedUsers_pic);
        }
    }
}
