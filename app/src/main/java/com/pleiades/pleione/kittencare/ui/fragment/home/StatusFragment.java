package com.pleiades.pleione.kittencare.ui.fragment.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pleiades.pleione.kittencare.Converter;
import com.pleiades.pleione.kittencare.KittenService;
import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.AnimationController;
import com.pleiades.pleione.kittencare.controller.DeviceController;
import com.pleiades.pleione.kittencare.controller.PrefsController;
import com.pleiades.pleione.kittencare.object.Costume;
import com.pleiades.pleione.kittencare.object.History;
import com.pleiades.pleione.kittencare.ui.activity.HistoryActivity;
import com.pleiades.pleione.kittencare.ui.fragment.dialog.DefaultDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static com.pleiades.pleione.kittencare.Config.BUFF_CODE_EXPERIENCE;
import static com.pleiades.pleione.kittencare.Config.BUFF_CODE_ITEM;
import static com.pleiades.pleione.kittencare.Config.DEFAULT_SHOW_POSITION;
import static com.pleiades.pleione.kittencare.Config.DELAY_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_HAPPINESS;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_OVERLAY;
import static com.pleiades.pleione.kittencare.Config.EXPERIENCE_MAGNIFICATION;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_BUFF;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_COSTUME_FOUND;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_COSTUME_REWARD;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_FIRST_EXPLORE;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_ITEM_FOUND;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_ITEM_REWARD;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_ITEM_UPDATE_REWARD;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_ITEM_USED;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_LEVEL_UP;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_MINT_CAKE;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_MINT_ICE_CREAM;
import static com.pleiades.pleione.kittencare.Config.KEY_EXPERIENCE;
import static com.pleiades.pleione.kittencare.Config.KEY_HAPPINESS;
import static com.pleiades.pleione.kittencare.Config.KEY_HISTORY_SIZE_LIMIT;
import static com.pleiades.pleione.kittencare.Config.KEY_IS_DRESS_RANDOMLY;
import static com.pleiades.pleione.kittencare.Config.KEY_IS_EXPLORED;
import static com.pleiades.pleione.kittencare.Config.KEY_IS_HAPPINESS_TUTORIAL_COMPLETED;
import static com.pleiades.pleione.kittencare.Config.KEY_LAST_CONSUMPTION_DATE_STRING;
import static com.pleiades.pleione.kittencare.Config.KEY_LAST_HIDE_DATE_STRING;
import static com.pleiades.pleione.kittencare.Config.KEY_LEVEL;
import static com.pleiades.pleione.kittencare.Config.KEY_NAME;
import static com.pleiades.pleione.kittencare.Config.KEY_SCREEN_HEIGHT;
import static com.pleiades.pleione.kittencare.Config.KEY_SCREEN_WIDTH;
import static com.pleiades.pleione.kittencare.Config.KEY_SHOW_POSITION;
import static com.pleiades.pleione.kittencare.Config.KEY_WEARING_COSTUME;
import static com.pleiades.pleione.kittencare.Config.LEVEL_MAX;
import static com.pleiades.pleione.kittencare.Config.PREFS;

public class StatusFragment extends Fragment {
    private Context context;
    private View rootView;

    private ArrayList<History> historyArrayList;
    private HistoryRecyclerAdapter historyRecyclerAdapter;
    private int historySizeLimit = -1; // set -1 to calculate first child height

    private long showTime;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_status, container, false);
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        final DeviceController deviceController = new DeviceController(context);

        // initialize show button
        Button showButton = rootView.findViewById(R.id.button_status);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start kitten service
                if (KittenService.kittenView == null) {
                    // check overlay permission
                    if (Settings.canDrawOverlays(context)) {
                        // dress random costume
                        if (prefs.getBoolean(KEY_IS_DRESS_RANDOMLY, false)) {
                            PrefsController prefsController = new PrefsController(context);
                            ArrayList<Costume> unlockedCostumeArrayList = prefsController.getUnlockedCostumeArrayList();

                            Random random = new Random();
                            int randomValue = random.nextInt(unlockedCostumeArrayList.size());

                            Costume costume = unlockedCostumeArrayList.get(randomValue);
                            editor.putInt(KEY_WEARING_COSTUME, costume.costumeCode);
                            editor.apply();
                        }

                        // start kitten service
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            context.startForegroundService(new Intent(context, KittenService.class));
                        else
                            context.startService(new Intent(context, KittenService.class));

                        // case first exploring
                        if (!prefs.getBoolean(KEY_IS_EXPLORED, false)) {
                            PrefsController prefsController = new PrefsController(context);

                            prefsController.addItemPrefs(ITEM_CODE_MINT_ICE_CREAM, 1);
                            prefsController.addItemPrefs(ITEM_CODE_MINT_CAKE, 1);

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(KEY_IS_EXPLORED, true);
                            editor.apply();

                            // add history
                            prefsController.addHistoryPrefs(HISTORY_TYPE_FIRST_EXPLORE, 0);

                            // refresh history
                            historyArrayList = prefsController.getHistoryArrayListPrefs();
                            historyRecyclerAdapter.notifyDataSetChanged();
                        }

                        showTime = SystemClock.elapsedRealtime();
                    } else {
                        // show overlay dialog
                        DefaultDialogFragment defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_OVERLAY);
                        defaultDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), Integer.toString(DIALOG_TYPE_OVERLAY));
                    }
                }
                // stop kitten service
                else {
                    // initialize show position
                    int showPosition = prefs.getInt(KEY_SHOW_POSITION, DEFAULT_SHOW_POSITION);

                    // case double click
                    if (SystemClock.elapsedRealtime() - showTime < (AnimationController.calculateDurationGravity(context, showPosition, true) + DELAY_DEFAULT))
                        return;

                    // stop kitten service
                    context.stopService(new Intent(context, KittenService.class));
                }
            }
        });

        // initialize history recycler view
        final RecyclerView historyRecyclerView = rootView.findViewById(R.id.recycler_status);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        historyRecyclerView.setLayoutManager(linearLayoutManager);

        // initialize history size limit
        if (prefs.getInt(KEY_HISTORY_SIZE_LIMIT, -1) != -1 &&
                deviceController.getWidthMax() == prefs.getInt(KEY_SCREEN_WIDTH, 0) &&
                deviceController.getHeightMax() == prefs.getInt(KEY_SCREEN_HEIGHT, 0)) {

            // set history size limit
            historySizeLimit = prefs.getInt(KEY_HISTORY_SIZE_LIMIT, -1);

            // reconstrain history
            reconstrainHistory();
        } else {
            historyRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // get history recycler height
                    int historyRecyclerHeight = historyRecyclerView.getHeight(); // 727

                    // get child at first index
                    View firstChildView = historyRecyclerView.getChildAt(0);
                    if (firstChildView != null) {
                        // get history item height
                        int historyItemHeight = firstChildView.getHeight(); // 188

                        // set history size limit
                        for (int i = 1; i <= historyArrayList.size(); i++) {
                            if (i * historyItemHeight > historyRecyclerHeight) {
                                historySizeLimit = i - 1;
                                historyRecyclerAdapter.notifyDataSetChanged();
                                break;
                            }
                        }

                        // apply screen width, height, history size limit
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt(KEY_SCREEN_WIDTH, deviceController.getWidthMax());
                        editor.putInt(KEY_SCREEN_HEIGHT, deviceController.getHeightMax());
                        editor.putInt(KEY_HISTORY_SIZE_LIMIT, historySizeLimit);
                        editor.apply();

                        // reconstrain history
                        reconstrainHistory();
                    }

                    // remove layout listener
                    historyRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

        // initialize history recycler adapter
        historyRecyclerAdapter = new HistoryRecyclerAdapter();
        historyRecyclerView.setAdapter(historyRecyclerAdapter);

        // initialize history layout
        LinearLayout historyLayout = rootView.findViewById(R.id.history_status);
        historyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HistoryActivity.class);
                startActivity(intent);
            }
        });

        // case is not happiness tutorial completed
        if (!prefs.getBoolean(KEY_IS_HAPPINESS_TUTORIAL_COMPLETED, false)) {
            DefaultDialogFragment defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_HAPPINESS);
            defaultDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), Integer.toString(DIALOG_TYPE_HAPPINESS));
        }

        return rootView;
    }

    private void initializeStatus() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // initialize level, experience, max experience
        int level = prefs.getInt(KEY_LEVEL, 1);
        int experience = prefs.getInt(KEY_EXPERIENCE, 0);
        int maxExperience = (int) (100 * (Math.pow(EXPERIENCE_MAGNIFICATION, level - 1)));

        // initialize level, experience text view
        TextView levelTextView = rootView.findViewById(R.id.level_status);
        TextView experiencePercentageTextView = rootView.findViewById(R.id.experience_status);
        if (level == LEVEL_MAX) {
            levelTextView.setText(String.format(getString(R.string.block_label_level_max), level));
            experiencePercentageTextView.setText(String.format(Locale.getDefault(), "%.2f %%", 99.99));
        } else {
            levelTextView.setText(String.format(getString(R.string.block_label_level), level));
            experiencePercentageTextView.setText(String.format(Locale.getDefault(), "%.2f %%", 100 * (double) experience / maxExperience));
        }

        // initialize experience progress bar
        ProgressBar experienceProgressBar = rootView.findViewById(R.id.experience_progress_status);
        experienceProgressBar.setMax(maxExperience);
        if (level == LEVEL_MAX)
            experienceProgressBar.setProgress(maxExperience);
        else
            experienceProgressBar.setProgress(experience);

        // initialize costume progress bar
        ProgressBar costumeProgressBar = rootView.findViewById(R.id.costume_progress_status);
        ArrayList<Costume> costumeArrayList = new PrefsController(context).getInitializedCostumeArrayList();
        costumeProgressBar.setMax(costumeArrayList.size());
        int costumeProgress = 0;
        for (Costume costume : costumeArrayList) {
            if (costume.isUnlocked)
                costumeProgress++;
        }
        costumeProgressBar.setProgress(costumeProgress);

        // initialize costume progress text view
        TextView costumeProgressTextView = rootView.findViewById(R.id.costume_status);
        costumeProgressTextView.setText(String.format(Locale.getDefault(), "%02d/%02d", costumeProgress, costumeProgressBar.getMax()));

        // initialize happiness
        int happiness = prefs.getInt(KEY_HAPPINESS, 100);

        // initialize last hide date string, last consumption date string, last current date string
        String lastHideDateString = prefs.getString(KEY_LAST_HIDE_DATE_STRING, null);
        String lastConsumptionDateString = prefs.getString(KEY_LAST_CONSUMPTION_DATE_STRING, null);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy/MM/dd HH:mm:ss", Locale.US);
        String currentDateString = simpleDateFormat.format(new Date());

        // update happiness using last hide date
        if (lastHideDateString != null) {
            try {
                Date lastHideDate = simpleDateFormat.parse(lastHideDateString);
                Date currentDate = simpleDateFormat.parse(currentDateString);

                long timeDifference = currentDate.getTime() - lastHideDate.getTime();
                happiness = Math.max(0, happiness - (int) TimeUnit.MILLISECONDS.toHours(timeDifference));

                // apply last hide date string
                editor.putString(KEY_LAST_HIDE_DATE_STRING, currentDateString);
                editor.apply();
            } catch (ParseException e) {
                // ignore exception block
            }
        }

        // update happiness using last consumption date
        if (lastConsumptionDateString != null) {
            try {
                Date lastConsumptionDate = simpleDateFormat.parse(lastConsumptionDateString);
                Date currentDate = simpleDateFormat.parse(currentDateString);

                long timeDifference = currentDate.getTime() - lastConsumptionDate.getTime();
                happiness = Math.max(0, happiness - (int) TimeUnit.MILLISECONDS.toHours(timeDifference) / 12 * 10);

                // apply last consumption date string
                editor.putString(KEY_LAST_CONSUMPTION_DATE_STRING, currentDateString);
                editor.apply();
            } catch (ParseException e) {
                // ignore exception block
            }
        }

        // apply happiness
        editor.putInt(KEY_HAPPINESS, happiness);
        editor.apply();

        // initialize happiness progress bar
        ProgressBar happinessProgressBar = rootView.findViewById(R.id.happiness_progress_status);
        happinessProgressBar.setProgress(happiness);
        happinessProgressBar.setProgressDrawable(AppCompatResources.getDrawable(context, happiness >= 50 ? R.drawable.drawable_progress : R.drawable.drawable_progress_blue));

        // initialize happiness progress text view
        TextView happinessProgressTextView = rootView.findViewById(R.id.happiness_status);
        happinessProgressTextView.setText(String.format(Locale.getDefault(), "%02d/100", happiness));
    }

    private void reconstrainHistory() {
        // initialize constraint set
        ConstraintLayout statusLayout = rootView.findViewById(R.id.layout_status);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(statusLayout);

        // clear history constrain
        constraintSet.clear(R.id.history_status, ConstraintSet.BOTTOM);

        // constrain history width 0dp to WRAP_CONTENT
        constraintSet.constrainWidth(R.id.history_status, ConstraintSet.WRAP_CONTENT);

        // apply constraint set
        constraintSet.applyTo(statusLayout);
    }

    @Override
    public void onStart() {
        super.onStart();

        // initialize name
        String name = context.getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_NAME, getString(R.string.default_name));
        ((Button) rootView.findViewById(R.id.button_status)).setText(String.format(getString(R.string.button_show_hide), name));

        // initialize status
        initializeStatus();

        // initialize history array list
        historyArrayList = new PrefsController(context).getHistoryArrayListPrefs();
        historyRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryViewHolder> {
        private class HistoryViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView, contentsTextView, timeTextView, dateTextView;

            public HistoryViewHolder(@NonNull View itemView) {
                super(itemView);

                titleTextView = itemView.findViewById(R.id.title_recycler_history);
                contentsTextView = itemView.findViewById(R.id.contents_recycler_history);
                timeTextView = itemView.findViewById(R.id.time_recycler_history);
                dateTextView = itemView.findViewById(R.id.date_recycler_history);
            }
        }

        @NonNull
        @Override
        public HistoryRecyclerAdapter.HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_history, parent, false);
            return new HistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final HistoryRecyclerAdapter.HistoryViewHolder holder, int position) {
            History history = historyArrayList.get(position);
            String name = context.getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_NAME, getString(R.string.default_name));

            // set title, contents
            switch (history.historyType) {
                case HISTORY_TYPE_ITEM_FOUND:
                    holder.titleTextView.setText(R.string.history_title_item_get);
                    holder.contentsTextView.setText(String.format(getString(R.string.history_contents_item_found), Converter.getItemName(context, history.reference)));
                    break;
                case HISTORY_TYPE_ITEM_USED:
                    holder.titleTextView.setText(R.string.history_title_item_use);
                    holder.contentsTextView.setText(String.format(getString(R.string.history_contents_item_used), Converter.getItemName(context, history.reference)));
                    break;
                case HISTORY_TYPE_ITEM_REWARD:
                    holder.titleTextView.setText(R.string.history_title_item_get);
                    holder.contentsTextView.setText(String.format(getString(R.string.history_contents_item_reward), Converter.getItemName(context, history.reference)));
                    break;
                case HISTORY_TYPE_ITEM_UPDATE_REWARD:
                    holder.titleTextView.setText(R.string.history_title_item_update_reward);
                    holder.contentsTextView.setText(String.format(getString(R.string.history_contents_item_reward), Converter.getItemName(context, history.reference)));
                    break;
                case HISTORY_TYPE_COSTUME_FOUND:
                    holder.titleTextView.setText(R.string.history_title_costume_get);
                    holder.contentsTextView.setText(String.format(getString(R.string.history_contents_costume_found), Converter.getCostumeName(context, history.reference)));
                    break;
                case HISTORY_TYPE_COSTUME_REWARD:
                    holder.titleTextView.setText(R.string.history_title_costume_get);
                    holder.contentsTextView.setText(String.format(getString(R.string.history_contents_costume_reward), Converter.getCostumeName(context, history.reference)));
                    break;
                case HISTORY_TYPE_LEVEL_UP:
                    holder.titleTextView.setText(R.string.history_title_level);
                    holder.contentsTextView.setText(String.format(getString(R.string.history_contents_level_up), name, history.reference));
                    break;
                case HISTORY_TYPE_FIRST_EXPLORE:
                    holder.titleTextView.setText(R.string.history_title_explore);
                    holder.contentsTextView.setText(String.format(getString(R.string.history_contents_explore), name));
                    break;
                // TODO update buff
                case HISTORY_TYPE_BUFF:
                    holder.titleTextView.setText(R.string.history_title_buff_get);
                    if (history.reference == BUFF_CODE_EXPERIENCE)
                        holder.contentsTextView.setText(R.string.history_contents_buff_get_experience);
                    else if (history.reference == BUFF_CODE_ITEM)
                        holder.contentsTextView.setText(R.string.history_contents_buff_get_item);
                    break;
            }

            // set date
            Calendar calendar = Calendar.getInstance();
            int todayMonth = calendar.get(Calendar.MONTH) + 1;
            int todayDate = calendar.get(Calendar.DATE);

            // case today
            if (history.month == todayMonth && history.date == todayDate)
                holder.dateTextView.setText(R.string.history_date_today);
            else {
                calendar.add(Calendar.DATE, -1);
                int yesterdayMonth = calendar.get(Calendar.MONTH) + 1;
                int yesterdayDate = calendar.get(Calendar.DATE);

                // case yesterday
                if (history.month == yesterdayMonth && history.date == yesterdayDate)
                    holder.dateTextView.setText(R.string.history_date_yesterday);
                else
                    holder.dateTextView.setText(String.format(getString(R.string.history_date), Converter.getHistoryMonth(context, history.month, true), history.date));
            }

            // set time
            int hour = history.hour;

            // case AM
            if (hour < 12) {
                if (hour == 0)
                    hour = 12;
                holder.timeTextView.setText(String.format(getString(R.string.history_time_am), hour, history.minute));
            }
            // case PM
            else {
                hour = hour - 12;
                holder.timeTextView.setText(String.format(getString(R.string.history_time_pm), hour, history.minute));
            }
        }

        @Override
        public int getItemCount() {
            if (historySizeLimit == -1)
                return historyArrayList.size();
            else
                return historySizeLimit;
        }

    }

}