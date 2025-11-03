package com.t4app.t4everandroid.main.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.databinding.FragmentEmailBinding;
import com.t4app.t4everandroid.main.Models.EmailTest;
import com.t4app.t4everandroid.main.Models.NotificationItem;
import com.t4app.t4everandroid.main.adapter.EmailAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmailFragment extends Fragment {
    private static final String TAG = "EMAIL_FRAG";
    private FragmentEmailBinding binding;

    private EmailAdapter adapter;
    private List<EmailTest> testEmails;
    private List<EmailTest> deleted;
    private List<EmailTest> checkedEmails;
    private AddEmailBottomSheet bottomSheet;

    public EmailFragment() {
    }

    public static EmailFragment newInstance() {
        return new EmailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEmailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.searchEmail.categoriesAuto.setVisibility(View.INVISIBLE);
        binding.searchEmail.searchValue.addTextChangedListener(new TextWatcher() {
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
                    List<EmailTest> searchResourceList = new ArrayList<>();
                    for (EmailTest object : testEmails) {
                        String nameDevice = object.getContactName();
                        String email = object.getEmail();
                        if (nameDevice.toLowerCase().trim().contains(value.toLowerCase().trim())) {
                            searchResourceList.add(object);
                        }else if (email.toLowerCase().trim().contains(value.toLowerCase().trim())) {
                            searchResourceList.add(object);
                        }
                    }

//                    checkFoundData(dataSearch, noFoundDataTv, getString(R.string.customer));
                    adapter.updateList(searchResourceList);
                } else {
                    adapter.updateList(testEmails);
                }
            }
        });

        testEmails = new ArrayList<>();
        deleted = new ArrayList<>();
        checkedEmails = new ArrayList<>();
        testEmails.add(new EmailTest(
                "Emma Johnson",
                "emmaDJohnson@test.com",
                "Weekend Plans",
                "Hey! I was thinking maybe we could go hiking this Saturday morning. There's a new trail near the lake,\n" +
                        " and people say the view at sunrise is stunning. Let me know if you're in — we can grab breakfast after too!",
                "2025-11-02",
                "personal",
                false, false, false, false, true
        ));
        testEmails.add(new EmailTest(
                "Mom",
                "SonyaAGibbs@test.com",
                "Family Dinner on Sunday",
                "Hi honey, just reminding you about the family dinner this Sunday. Your aunt is coming and she really \n" +
                "wants to see you — she keeps asking about your job and says she’s proud of you. Please try not to be late,\n"+
                "and let me know if you want me to prepare your favorite dessert. Love you ❤",
            "2025-10-25",
                "family",
                true, false, false, false, true));
        testEmails.add(new EmailTest(
                "HR Department",
                "hrDepartmentInc@test.com",
                "Interview Confirmation",
                "Dear applicant,\n\nWe are pleased to confirm your interview scheduled for Wednesday at 10:00 AM via Google Meet. " +
                        "Please ensure your microphone and camera are working beforehand. If you need to reschedule, reply to this email.\n\n" +
                        "Best regards,\nTechWorks HR Team",
                "2025-09-12",
                "career",
                false, false, false, false, false));

        testEmails.add(new EmailTest(
                "Daniel Cooper",
                "danielRCooper@test.com",
                "Remember That Night?",
                "Do you remember that crazy road trip night back in college? We got lost in the middle of nowhere, " +
                        "the GPS froze, and we ended up camping under the stars with nothing but chips and two bottles of water. " +
                        "Those were good times — miss that energy haha.",
                "2025-07-08",
                "memories",
                true, false, false, false, true));

        testEmails.add(new EmailTest(
                "Google Photos",
                "google@test.com",
                "Your Memories from This Day",
                "We found photos from this day three years ago. Looks like you were at the beach with friends — sunny skies, " +
                        "blue ocean, and lots of smiles. Tap to revisit the moment 🌊☀️",
                "2025-11-01",
                "Photos",
                false, false, false, false, false));

        testEmails.add(new EmailTest(
                "Grandma",
                "angelaRDavis@test.com",
                "Chocolate Cake Recipe",
                "Sweetheart, here is the chocolate cake recipe you asked for 🧁\n\n" +
                        "- 2 cups flour\n- 1 cup sugar\n- 1/2 cup cocoa\n- 2 eggs\n- 1 cup milk\n\n" +
                        "Mix slowly and bake for 35 minutes. Remember to taste the frosting — that's the secret 😉",
                "2025-08-02",
                "Recipes",
                false, false, true, false, true));

        testEmails.add(new EmailTest(
                "Spotify",
                "spotify_no_reply@test.com",
                "Your Weekly Mix is Ready",
                "Your Weekly Discovery playlist is here! Based on your recent listening, we've added indie-pop, soft rock, " +
                        "and lo-fi gems. Hit play and let us know your favorites 🎧",
                "2025-10-22",
                "music",
                false, false, false, true, true));

        testEmails.add(new EmailTest(
                "Liam Anderson",
                "liamAnderson@test.com",
                "Japan Trip ✈️",
                "We finally booked our trip to Tokyo! Sushi, cherry blossoms, Akihabara, temples — it's going to be amazing. " +
                        "I'm already planning a Kyoto day-trip and maybe even a visit to Nara to feed the deer. Start practicing your chopstick skills 😄",
                "2025-11-03",
                "travels",
                true, false, true, false, false));

        testEmails.add(new EmailTest(
                "Professor Carter",
                "carter@test.com",
                "Thesis Progress Update",
                "Hello,\n\nI'm sending you a quick update regarding my thesis progress. I’ve completed the data collection phase and " +
                        "have started writing the methodology chapter. The results section should be ready by the end of next week. " +
                        "Please let me know if you'd like to schedule a short call to review my outline and next steps.\n\nThank you!",
                "2025-10-15",
                "career",
                false, true, false, false, true
        ));

        testEmails.add(new EmailTest(
                "Olivia",
                "oliviaCarston@test.com",
                "Photos from the Wedding 💍",
                "Hey! Finally sending you the wedding photos. The photographer sent over more than 400 shots, and some of them " +
                        "are absolutely stunning — especially the ones from the garden during golden hour. Sharing the link below:\n\n" +
                        "https://photos.t4ever.com/wedding-gallery\n\nLet me know which ones you want framed, I'm thinking of printing a couple too! 😊",
                "2025-09-29",
                "Photos",
                true, true, false, false, true
        ));


        int unread = 0;
        for (EmailTest emailTest : testEmails){
            if (!emailTest.isRead()){
                unread++;
            }
            if (emailTest.isDeleted()){
                deleted.add(emailTest);
            }
        }

        testEmails.removeAll(deleted);

        binding.emailQuantityText.setText(testEmails.size() + "emails total, " + unread + " unread");

        adapter = new EmailAdapter(testEmails, requireContext(), new EmailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(EmailTest item, int position) {
                showEmailDialog(requireActivity(), item, position);
            }

            @Override
            public void onItemChecked(EmailTest item, int position, boolean b) {
                if (b){
                    if (!checkedEmails.contains(item)){
                        checkedEmails.add(item);
                    }
                }else{
                    checkedEmails.remove(item);
                }

                if (!checkedEmails.isEmpty()){
                    binding.searchEmail.markUnread.setVisibility(View.VISIBLE);
                    binding.searchEmail.markRead.setVisibility(View.VISIBLE);
                    binding.searchEmail.deleteAll.setVisibility(View.VISIBLE);
                }else{
                    binding.searchEmail.markUnread.setVisibility(View.GONE);
                    binding.searchEmail.markRead.setVisibility(View.GONE);
                    binding.searchEmail.deleteAll.setVisibility(View.GONE);
                }
            }
        });
        binding.emailRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.emailRv.setAdapter(adapter);

        bottomSheet = new AddEmailBottomSheet((to, subject, message) -> {
            EmailTest emailTest = new EmailTest(
                    getNameFromEmail(to),
                    to,
                    subject,
                    message,
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(new Date()),
                    "personal",
                    false,
                    true,
                    false,
                    false,
                    true
            );

            adapter.addItem(emailTest);
            binding.emailRv.scrollToPosition(0);
        });

        binding.searchEmail.markUnread.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                for (EmailTest emailTest : checkedEmails){
                    emailTest.setRead(false);
                    testEmails.set(testEmails.indexOf(emailTest), emailTest);
                }
                checkedEmails = new ArrayList<>();
                adapter.notifyDataSetChanged();
                binding.searchEmail.markUnread.setVisibility(View.GONE);
                binding.searchEmail.markRead.setVisibility(View.GONE);
                binding.searchEmail.deleteAll.setVisibility(View.GONE);
            }
        });

        binding.searchEmail.markRead.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                for (EmailTest emailTest : checkedEmails){
                    emailTest.setRead(true);
                    testEmails.set(testEmails.indexOf(emailTest), emailTest);
                }
                checkedEmails = new ArrayList<>();
                adapter.notifyDataSetChanged();
                binding.searchEmail.markUnread.setVisibility(View.GONE);
                binding.searchEmail.markRead.setVisibility(View.GONE);
                binding.searchEmail.deleteAll.setVisibility(View.GONE);
            }
        });

        binding.searchEmail.deleteAll.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                for (EmailTest emailTest : checkedEmails){
                    emailTest.setDeleted(true);
                }
                testEmails.removeAll(checkedEmails);
                deleted.addAll(checkedEmails);
                checkedEmails = new ArrayList<>();
                adapter.notifyDataSetChanged();
                binding.searchEmail.markUnread.setVisibility(View.GONE);
                binding.searchEmail.markRead.setVisibility(View.GONE);
                binding.searchEmail.deleteAll.setVisibility(View.GONE);
            }
        });

        return view;
    }

    public String getNameFromEmail(String email) {
        if (email == null || !email.contains("@")) return "";

        String namePart = email.substring(0, email.indexOf("@"));
        return namePart;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.itemAddEmail.inboxBtn.post(() -> binding.itemAddEmail.inboxBtn.performClick());

        binding.itemAddEmail.composeEmail.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                bottomSheet.show(getActivity().getSupportFragmentManager(), "ComposeEmailBottomSheet");
            }
        });

        Chip chipPersonal = binding.itemAddEmail.chipPersonal;
        Chip chipCareer = binding.itemAddEmail.chipCareer;
        Chip chipFamily = binding.itemAddEmail.chipFamily;
        Chip chipMemories = binding.itemAddEmail.chipMemories;
        Chip chipMusic = binding.itemAddEmail.chipMusic;
        Chip chipPhotos = binding.itemAddEmail.chipPhotos;
        Chip chipTravel = binding.itemAddEmail.chipTravel;
        Chip chipRecipes = binding.itemAddEmail.chipRecipes;

        chipPersonal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chipCloseBtnVisibility(chipPersonal, "personal");
        });

        chipCareer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chipCloseBtnVisibility(chipCareer, "career");
        });

        chipFamily.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chipCloseBtnVisibility(chipFamily, "family");
        });

        chipMemories.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chipCloseBtnVisibility(chipMemories, "memories");
        });

        chipMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chipCloseBtnVisibility(chipMusic, "music");
        });

        chipPhotos.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chipCloseBtnVisibility(chipPhotos, "photos");
        });

        chipTravel.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chipCloseBtnVisibility(chipTravel, "travel");
        });

        chipRecipes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            chipCloseBtnVisibility(chipRecipes, "recipes");
        });

        binding.itemAddEmail.inboxBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                adapter.updateList(testEmails);
            }
        });

        binding.itemAddEmail.sentBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                List<EmailTest> emailTestListFiltered = new ArrayList<>();
                for (EmailTest emailTest : testEmails){
                    if (emailTest.isSent()){
                        emailTestListFiltered.add(emailTest);
                    }
                }
                adapter.updateList(emailTestListFiltered);
            }
        });

        binding.itemAddEmail.draftBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                List<EmailTest> emailTestListFiltered = new ArrayList<>();
                adapter.updateList(emailTestListFiltered);
            }
        });

        binding.itemAddEmail.trashBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                adapter.updateList(deleted);
            }
        });

        binding.itemAddEmail.spamBtn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                List<EmailTest> emailTestListFiltered = new ArrayList<>();
                for (EmailTest emailTest : testEmails){
                    if (emailTest.isSpam()){
                        emailTestListFiltered.add(emailTest);
                    }
                }
                adapter.updateList(emailTestListFiltered);
            }
        });


    }

    private void parseByTag(String type){
        List<EmailTest> emailTestsFiltered = new ArrayList<>();
        for (EmailTest emailTest : testEmails){
            if (emailTest.getType().equalsIgnoreCase(type)){
                emailTestsFiltered.add(emailTest);
            }
        }
        adapter.updateList(emailTestsFiltered);
    }

    private void chipCloseBtnVisibility(Chip chip, String tag){
        chip.setCloseIconVisible(chip.isChecked());
        if (chip.isChecked()){
            parseByTag(tag);
        }else{
            adapter.updateList(testEmails);
        }
    }

    private void showEmailDialog(Context context, EmailTest email, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_email_layout, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        TextView title = view.findViewById(R.id.title_email);
        TextView name = view.findViewById(R.id.name_contact);
        TextView emailText = view.findViewById(R.id.email_contact);
        TextView date = view.findViewById(R.id.date_email);
        TextView content = view.findViewById(R.id.email_content);

        ImageView favoriteIcon = view.findViewById(R.id.favorite_email_icon);
        ImageButton btnClose = view.findViewById(R.id.btn_close);
        MaterialButton btnReply = view.findViewById(R.id.reply_btn);
        MaterialButton btnForward = view.findViewById(R.id.forward_btn);
        MaterialButton btnDelete = view.findViewById(R.id.cancel_btn);

        title.setText(email.getTitle());
        name.setText(email.getContactName());
        emailText.setText(email.getEmail());
        date.setText(email.getDate());
        content.setText(email.getContent());

        if (email.isFavorite()){
            favoriteIcon.setImageResource(R.drawable.ic_star_off);
            favoriteIcon.
                    setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_hint)));
        }else {
            favoriteIcon.setImageResource(R.drawable.ic_star);
            favoriteIcon.
                    setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.alert_color)));
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnReply.setOnClickListener(v -> {
            dialog.dismiss();
        });

        btnForward.setOnClickListener(v -> {
            dialog.dismiss();
        });

        btnDelete.setOnClickListener(v -> {
            adapter.deleteItem(pos);
            deleted.add(email);
            dialog.dismiss();
        });

        dialog.show();
    }


}