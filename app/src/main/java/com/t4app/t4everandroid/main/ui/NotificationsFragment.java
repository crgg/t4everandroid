package com.t4app.t4everandroid.main.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.databinding.FragmentNotificationsBinding;
import com.t4app.t4everandroid.main.Models.NotificationItem;
import com.t4app.t4everandroid.main.adapter.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;


public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public NotificationsFragment() {
    }

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        List<NotificationItem> notifications = new ArrayList<>();

        notifications.add(new NotificationItem("1", "New Conversation Started",
                "Emma Pérez has started a conversation with you.",
                "27 oct 2025, 14:00",
                "Action available", false));
        notifications.add(new NotificationItem("2",
                "System", "Your account has been updated.",
                "27 oct 2025, 12:00", "View changes", true));


        NotificationAdapter adapter = new NotificationAdapter(requireContext(), notifications, new NotificationAdapter.OnNotificationClickListener() {
            @Override
            public void onItemChecked(NotificationItem item, boolean isChecked) {

            }

            @Override
            public void onEditClicked(NotificationItem item) {

            }

            @Override
            public void onDeleteClicked(NotificationItem item) {

            }
        });
        binding.notificationsRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.notificationsRv.setAdapter(adapter);

        return view;
    }
}