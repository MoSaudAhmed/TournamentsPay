package com.mgdapps.play360.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mgdapps.play360.R;

import java.util.List;

public class RulesAdapter extends RecyclerView.Adapter<RulesAdapter.MViewHolder> {
    List<String> itemsList;

    public RulesAdapter(List<String> itemsList) {
        this.itemsList = itemsList;
    }

    @NonNull
    @Override
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_row, parent, false);
        return new MViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MViewHolder holder, int position) {

        holder.tv_item.setText(itemsList.get(position));
        holder.img_spinner_row.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class MViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item;
        ImageView img_spinner_row;

        public MViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_item = itemView.findViewById(R.id.tv_spinner_row);
            img_spinner_row = itemView.findViewById(R.id.img_spinner_row);
        }
    }
}
