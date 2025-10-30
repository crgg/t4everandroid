package com.t4app.t4everandroid.main.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.databinding.FragmentNotificationsBinding;
import com.t4app.t4everandroid.main.Models.NotificationItem;
import com.t4app.t4everandroid.main.Models.Question;
import com.t4app.t4everandroid.main.adapter.CategoriesAdapter;
import com.t4app.t4everandroid.main.adapter.NotificationAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    private List<NotificationItem> notifications;
    private NotificationAdapter adapter;

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

        notifications = new ArrayList<>();

        notifications.add(new NotificationItem("1", "New Conversation Started",
                "Emma Pérez has started a conversation with you.",
                "27 oct 2025, 14:00",
                "Action available", "information",false, false));
        notifications.add(new NotificationItem("2",
                "System", "Your account has been updated.",
                "27 oct 2025, 12:00", "View changes", "information",true, false));

        notifications.add(new NotificationItem("3",
                "Pending response", "You have 3 unanswered questions from Sophia Anderson",
                "27 oct 2025, 12:00", "View changes", "warning",true, false));

        notifications.add(new NotificationItem("4",
                "Profile completed", "The profile of Michael Brown has been completed successfully",
                "27 oct 2025, 12:00", "View changes", "success",true, false));



        adapter = new NotificationAdapter(requireContext(), notifications, new ListenersUtils.OnNotificationClickListener() {
            @Override
            public void onItemChecked(NotificationItem item, boolean isChecked) {
                //TODO
            }

            @Override
            public void onMarkRead(NotificationItem item, int position) {
                //TODO
            }

            @Override
            public void onDeleteClicked(NotificationItem item, int position) {
                //TODO
            }
        });
        binding.notificationsRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.notificationsRv.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.itemSearchNotification.searchValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                String value = s.toString();
                if (!value.isEmpty()) {
                    List<NotificationItem> searchResourceList = new ArrayList<>();
                    for (NotificationItem object : notifications) {
                        String nameDevice = object.getTitle();
                        if (nameDevice.toLowerCase().trim().contains(value.toLowerCase().trim())) {
                            searchResourceList.add(object);
                        }
                    }

//                    checkFoundData(dataSearch, noFoundDataTv, getString(R.string.customer));
                    adapter.updateList(searchResourceList);
                } else {
                    adapter.updateList(notifications);
                }
            }
        });

        binding.itemSearchNotification.typeNotification.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showCategories(true);
            }
        });

        binding.itemSearchNotification.categoriesAuto.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                showCategories(false);
            }
        });
    }


    public void showCategories(boolean isType) {
        BottomSheetDialog categoriesBottomSheet = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_categories, null);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCategories);

        List<String> items;
        if (isType){
            items = Arrays.asList(
                    getString(R.string.all_types),
                    getString(R.string.success),
                    getString(R.string.information),
                    getString(R.string.warning),
                    getString(R.string.error)
            );
        }else{
            items = Arrays.asList(
                    getString(R.string.all),
                    getString(R.string.unread),
                    getString(R.string.read)
            );
        }

        CategoriesAdapter catAdapter = new CategoriesAdapter(items);
        recyclerView.setAdapter(catAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        catAdapter.setOnItemClickListener(category -> {
            if (isType){
                binding.itemSearchNotification.typeNotification.setText(category);
                if (!category.equalsIgnoreCase(getString(R.string.all_types))) {
                    List<NotificationItem> searchResourceList = new ArrayList<>();
                    for (NotificationItem object : notifications) {
                        String nameDevice = object.getType();
                        if (nameDevice.equalsIgnoreCase(category)) {
                            searchResourceList.add(object);
                        }
                    }
//                    checkFoundData(dataSearch, noFoundDataTv, getString(R.string.customer));
                    adapter.updateList(searchResourceList);
                } else {
                    adapter.updateList(notifications);
                }
            }else{
                binding.itemSearchNotification.categoriesAuto.setText(category);
                List<NotificationItem> searchResourceList = new ArrayList<>();

                if (category.equalsIgnoreCase(getString(R.string.read))){
                    for (NotificationItem object : notifications) {
                        if (object.isRead()) {
                            searchResourceList.add(object);
                        }
                    }
                    adapter.updateList(searchResourceList);
                } else if (category.equalsIgnoreCase(getString(R.string.unread))){
                    for (NotificationItem object : notifications) {
                        if (!object.isRead()) {
                            searchResourceList.add(object);
                        }
                    }
                    adapter.updateList(searchResourceList);
                }else{
                    adapter.updateList(notifications);
                }

            }
            categoriesBottomSheet.dismiss();
        });

        categoriesBottomSheet.setContentView(view);
        categoriesBottomSheet.show();
    }

}