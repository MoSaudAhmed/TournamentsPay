package com.mgdapps.play360.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mgdapps.play360.R;
import com.mgdapps.play360.callbacks.UpdateResultCallback;
import com.mgdapps.play360.models.JoinAcceptModel;

import java.util.ArrayList;
import java.util.List;

public class MatchResultAdapter extends RecyclerView.Adapter<MatchResultAdapter.MViewHolder> {

    List<JoinAcceptModel> joinUsersModelList = new ArrayList<>();
    UpdateResultCallback updateResultCallback;
    Context context;

    public MatchResultAdapter(Context context, List<JoinAcceptModel> joinAcceptModels, UpdateResultCallback updateResultCallback) {
        this.context = context;
        this.updateResultCallback = updateResultCallback;
        this.joinUsersModelList = joinAcceptModels;
    }

    @NonNull
    @Override
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.matchresult_row, parent, false);
        return new MViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MViewHolder holder, final int position) {

        holder.tv_matchResultName.setText(joinUsersModelList.get(position).getNAME() + "");
        holder.et_matchResultKills.setText(joinUsersModelList.get(position).getKills() + "");
        holder.tv_matchResultUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (joinUsersModelList.get(position).getKills() != Integer.parseInt(holder.et_matchResultKills.getText().toString())) {
                    updateResultCallback.UpdateResultClicked(position, Integer.parseInt(holder.et_matchResultKills.getText().toString()));
                } else {
                    Toast.makeText(context, "Cannot update same number of kills again", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return joinUsersModelList.size();
    }

    public class MViewHolder extends RecyclerView.ViewHolder {

        TextView tv_matchResultUpdate, tv_matchResultName;
        EditText et_matchResultKills;

        public MViewHolder(@NonNull View itemView) {
            super(itemView);

            et_matchResultKills = itemView.findViewById(R.id.et_matchResultKills);
            tv_matchResultUpdate = itemView.findViewById(R.id.tv_matchResultUpdate);
            tv_matchResultName = itemView.findViewById(R.id.tv_matchResultName);
        }
    }
}
