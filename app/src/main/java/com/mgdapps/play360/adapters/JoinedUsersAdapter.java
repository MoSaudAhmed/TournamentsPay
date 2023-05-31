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
import com.mgdapps.play360.callbacks.AllMatchesCallback;
import com.mgdapps.play360.models.JoinAcceptModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class JoinedUsersAdapter extends RecyclerView.Adapter<JoinedUsersAdapter.MViewHolder> {
    Context context;
    List<JoinAcceptModel> joinAcceptModelList;
    boolean isOwner;
    AllMatchesCallback allMatchesCallback;
    boolean isExpired = false;

    public JoinedUsersAdapter(Context context, boolean isOwner, boolean isExpired, List<JoinAcceptModel> joinAcceptModelList, AllMatchesCallback allMatchesCallback) {
        this.context = context;
        this.joinAcceptModelList = joinAcceptModelList;
        this.isOwner = isOwner;
        this.allMatchesCallback = allMatchesCallback;
        this.isExpired = isExpired;
    }

    @NonNull
    @Override
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.joinrequest_row, parent, false);
        return new MViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MViewHolder holder, final int position) {

        if (isOwner) {
            holder.img_JoinRequestRemove.setVisibility(View.VISIBLE);
            //          holder.tv_JoinRequestKills.setVisibility(View.VISIBLE);
        } else {
            holder.img_JoinRequestRemove.setVisibility(View.GONE);
//            holder.tv_JoinRequestKills.setVisibility(View.GONE);
        }
        if (isExpired) {
            holder.tv_JoinRequestKills.setVisibility(View.VISIBLE);
        } else {
            holder.tv_JoinRequestKills.setVisibility(View.GONE);
        }
        holder.tv_JoinRequestName.setText(joinAcceptModelList.get(position).getNAME());
        holder.tv_JoinRequestKills.setText(joinAcceptModelList.get(position).getKills() + " Kills");
        holder.img_JoinRequestRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allMatchesCallback.clicked(position);
            }
        });

        if (!TextUtils.isEmpty(joinAcceptModelList.get(position).getProfilePic())) {
            Picasso.get().load(joinAcceptModelList.get(position).getProfilePic()).error(R.drawable.ic_person).into(holder.img_JoinRequestProfilePic);
        }
    }

    @Override
    public int getItemCount() {
        return joinAcceptModelList.size();
    }

    public class MViewHolder extends RecyclerView.ViewHolder {
        ImageView img_JoinRequestRemove, img_JoinRequestProfilePic;
        TextView tv_JoinRequestName, tv_JoinRequestKills;

        public MViewHolder(@NonNull View itemView) {
            super(itemView);
            img_JoinRequestProfilePic = itemView.findViewById(R.id.img_JoinRequestProfilePic);
            tv_JoinRequestName = itemView.findViewById(R.id.tv_JoinRequestName);
            img_JoinRequestRemove = itemView.findViewById(R.id.img_JoinRequestRemove);
            tv_JoinRequestKills = itemView.findViewById(R.id.tv_JoinRequestKills);
        }
    }
}
