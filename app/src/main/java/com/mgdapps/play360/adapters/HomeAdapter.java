package com.mgdapps.play360.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.RecyclerView;

import com.mgdapps.play360.R;
import com.mgdapps.play360.callbacks.AllMatchesCallback;
import com.mgdapps.play360.models.CreateMatchModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MViewHolder> {

    Context context;
    List<CreateMatchModel> createMatchModelList;
    AllMatchesCallback allMatchesCallback;

    public HomeAdapter(Context context, List<CreateMatchModel> createMatchModelList, AllMatchesCallback allMatchesCallback) {
        this.context = context;
        this.createMatchModelList = createMatchModelList;
        this.allMatchesCallback = allMatchesCallback;
    }

    @NonNull
    @Override
    public MViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_game_select_row, parent, false);

        return new MViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MViewHolder holder, final int position) {

        CreateMatchModel createMatchModel = createMatchModelList.get(position);

        holder.tv_homeRow_joinedCount.setText(createMatchModel.getJoined() + "/100 joined");
        holder.sb_homeRow_seekbar.setProgress(createMatchModel.getJoined());
        String type = "";
        String mode = "";
        String map = "";

        if (createMatchModel.getMatchAA() != 0) {
            type = context.getResources().getStringArray(R.array.aaItems)[createMatchModel.getMatchAA()];
            mode = context.getResources().getStringArray(R.array.modeItems)[createMatchModel.getMatchMode()];
            map = context.getResources().getStringArray(R.array.serverItems)[createMatchModel.getMatchServer()];

        } else {
            type = context.getResources().getStringArray(R.array.typeItems)[createMatchModel.getMatchType()];
            mode = context.getResources().getStringArray(R.array.modeItems)[createMatchModel.getMatchMode()];
            map = context.getResources().getStringArray(R.array.mapItems)[createMatchModel.getMatchMap()];
        }

        holder.tv_homeRow_Type.setText(type);
        holder.tv_homeRow_Mode.setText(mode);
        holder.tv_homeRow_Map.setText(map);
        holder.tv_homeRow_title.setText(createMatchModel.getMatchTitle());
        Date date = new Date(String.valueOf(createMatchModel.getMatchTime()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setDateAndTimeToText(calendar, holder.tv_homeRow_date);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allMatchesCallback.clicked(position);
            }
        });

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            holder.tv_homeRowCompleted.setVisibility(View.VISIBLE);
        } else {
            holder.tv_homeRowCompleted.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return createMatchModelList.size();
    }

    public class MViewHolder extends RecyclerView.ViewHolder {

        TextView tv_homeRow_title, tv_homeRow_date, tv_homeRow_Type, tv_homeRow_fee, tv_homeRow_Mode, tv_homeRow_Map,
                tv_homeRow_joinedCount, tv_homeRowCompleted;
        AppCompatSeekBar sb_homeRow_seekbar;

        public MViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_homeRow_title = itemView.findViewById(R.id.tv_homeRow_title);
            tv_homeRow_date = itemView.findViewById(R.id.tv_homeRow_date);
            tv_homeRow_Type = itemView.findViewById(R.id.tv_homeRow_Type);
            tv_homeRow_fee = itemView.findViewById(R.id.tv_homeRow_fee);
            tv_homeRow_Mode = itemView.findViewById(R.id.tv_homeRow_Mode);
            tv_homeRow_Map = itemView.findViewById(R.id.tv_homeRow_Map);
            sb_homeRow_seekbar = itemView.findViewById(R.id.sb_homeRow_seekbar);
            tv_homeRow_joinedCount = itemView.findViewById(R.id.tv_homeRow_joinedCount);
            tv_homeRowCompleted = itemView.findViewById(R.id.tv_homeRowCompleted);

            sb_homeRow_seekbar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
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
                textView.setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" +
                        calendar.get(Calendar.YEAR) + " " + hours + ":" + Mins + " PM");
            } else {
                textView.setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" +
                        calendar.get(Calendar.YEAR) + " " + hours + ":" + Mins + " AM");
            }

        } catch (Exception e) {
            Log.e("ErrorinsettingDateTime", e.getLocalizedMessage());
        }

    }
}
