package com.mgdapps.play360.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mgdapps.play360.R;
import com.mgdapps.play360.adapters.JoinedUsersAdapter;
import com.mgdapps.play360.callbacks.AllMatchesCallback;
import com.mgdapps.play360.helper.Constants;
import com.mgdapps.play360.models.JoinAcceptModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotifications extends Fragment implements AllMatchesCallback {

    RecyclerView rv_notifications;
    TextView tv_notificationsNodata, tv_notificationsClearAll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        rv_notifications = view.findViewById(R.id.rv_notifications);
        tv_notificationsNodata = view.findViewById(R.id.tv_notificationsNodata);
        tv_notificationsClearAll = view.findViewById(R.id.tv_notificationsClearAll);

        rv_notifications.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_notifications.setHasFixedSize(true);
        final List<JoinAcceptModel> joinAcceptModelList = new ArrayList<>();
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.notifications, Context.MODE_PRIVATE);
        final Set<String> notificationsSet = sharedPreferences.getStringSet(Constants.notificationsList, new HashSet<String>());
        if (notificationsSet.size() > 0) {
            for (String s : notificationsSet) {
                JoinAcceptModel joinAcceptModel = new JoinAcceptModel();
                joinAcceptModel.setNAME(s);
                joinAcceptModelList.add(joinAcceptModel);
            }
        } else {
            tv_notificationsNodata.setVisibility(View.VISIBLE);
        }

        final JoinedUsersAdapter joinedUsersAdapter = new JoinedUsersAdapter(getActivity(), false, false, joinAcceptModelList, this);
        rv_notifications.setAdapter(joinedUsersAdapter);

        tv_notificationsClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putStringSet(Constants.notificationsList, null);
                editor.commit();
                joinAcceptModelList.clear();
                notificationsSet.clear();
                joinedUsersAdapter.notifyDataSetChanged();
                tv_notificationsNodata.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    @Override
    public void clicked(int position) {

    }
}
