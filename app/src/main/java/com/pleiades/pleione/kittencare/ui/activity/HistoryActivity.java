package com.pleiades.pleione.kittencare.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pleiades.pleione.kittencare.Converter;
import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.PrefsController;
import com.pleiades.pleione.kittencare.object.History;

import java.util.ArrayList;
import java.util.Calendar;

import static com.pleiades.pleione.kittencare.Config.BUFF_CODE_EXPERIENCE;
import static com.pleiades.pleione.kittencare.Config.BUFF_CODE_ITEM;
import static com.pleiades.pleione.kittencare.Config.HISTORY_SIZE_LIMIT;
import static com.pleiades.pleione.kittencare.Config.FILTER_POSITION_HISTORY_ALL;
import static com.pleiades.pleione.kittencare.Config.FILTER_POSITION_HISTORY_COSTUME;
import static com.pleiades.pleione.kittencare.Config.FILTER_POSITION_HISTORY_ITEM;
import static com.pleiades.pleione.kittencare.Config.FILTER_POSITION_HISTORY_STATE;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_BUFF;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_COSTUME_FOUND;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_COSTUME_REWARD;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_FIRST_EXPLORE;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_ITEM_FOUND;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_ITEM_REWARD;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_ITEM_UPDATE_REWARD;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_ITEM_USED;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_LEVEL_UP;
import static com.pleiades.pleione.kittencare.Config.KEY_NAME;
import static com.pleiades.pleione.kittencare.Config.PREFS;

public class HistoryActivity extends AppCompatActivity {
    private Context context;

    private ArrayList<History> historyArrayList;
    private HistoryRecyclerAdapter historyRecyclerAdapter;

    private int filterPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // initialize context
        context = HistoryActivity.this;

        // set navigation color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
            getWindow().setNavigationBarColor(ContextCompat.getColor(context, R.color.color_navigation_background));

        // initialize appbar
        View appbar = findViewById(R.id.appbar_history);
        Toolbar toolbar = appbar.findViewById(R.id.toolbar_sub);
        setSupportActionBar(toolbar);

        // initialize action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // initialize history recycler view
        RecyclerView historyRecyclerView = findViewById(R.id.recycler_history);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        historyRecyclerView.setLayoutManager(linearLayoutManager);
//        historyRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        // initialize history recycler adapter
        historyRecyclerAdapter = new HistoryRecyclerAdapter(false);
        historyRecyclerView.setAdapter(historyRecyclerAdapter);

        // initialize swipe refresh layout
        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_history);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.color_accent));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // initialize history array list
                initializeHistoryArrayList();
                historyRecyclerAdapter.notifyDataSetChanged();

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initializeHistoryArrayList() {
        ArrayList<History> originHistoryArrayList = new PrefsController(context).getHistoryArrayListPrefs();
        ArrayList<History> filteredHistoryArrayList = new ArrayList<>();

        if (filterPosition == FILTER_POSITION_HISTORY_ITEM) {
            for (History history : originHistoryArrayList) {
                switch (history.historyType) {
                    case HISTORY_TYPE_ITEM_FOUND:
                    case HISTORY_TYPE_ITEM_USED:
                    case HISTORY_TYPE_ITEM_REWARD:
                    case HISTORY_TYPE_ITEM_UPDATE_REWARD:
                        filteredHistoryArrayList.add(history);
                        break;
                }
            }
        } else if (filterPosition == FILTER_POSITION_HISTORY_COSTUME) {
            for (History history : originHistoryArrayList) {
                switch (history.historyType) {
                    case HISTORY_TYPE_COSTUME_FOUND:
                    case HISTORY_TYPE_COSTUME_REWARD:
                        filteredHistoryArrayList.add(history);
                        break;
                }
            }
        } else if (filterPosition == FILTER_POSITION_HISTORY_STATE) {
            for (History history : originHistoryArrayList) {
                switch (history.historyType) {
                    case HISTORY_TYPE_LEVEL_UP:
                    case HISTORY_TYPE_FIRST_EXPLORE:
                    case HISTORY_TYPE_BUFF:
                        filteredHistoryArrayList.add(history);
                        break;
                }
            }
        } else
            filteredHistoryArrayList = originHistoryArrayList;

        // set history array list
        historyArrayList = filteredHistoryArrayList;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // initialize history array list
        initializeHistoryArrayList();
        historyRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_filter_history) {
            // initialize popup menu
            Context themeContext = new ContextThemeWrapper(context, R.style.AppTheme);
            PopupMenu popupMenu = new PopupMenu(themeContext, findViewById(R.id.action_filter_history));
            popupMenu.inflate(R.menu.menu_history_filter);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();

                    if (id == R.id.action_history_all)
                        filterPosition = FILTER_POSITION_HISTORY_ALL;
                    else if (id == R.id.action_history_item)
                        filterPosition = FILTER_POSITION_HISTORY_ITEM;
                    else if (id == R.id.action_history_costume)
                        filterPosition = FILTER_POSITION_HISTORY_COSTUME;
                    else if (id == R.id.action_history_state)
                        filterPosition = FILTER_POSITION_HISTORY_STATE;

                    // initialize history array list
                    initializeHistoryArrayList();
                    historyRecyclerAdapter.notifyDataSetChanged();

                    // check item
                    item.setChecked(true);
                    return true;
                }
            });

            // initialize checked item
            MenuItem popupItem = popupMenu.getMenu().getItem(filterPosition);
            popupItem.setChecked(true);

            // show popup menu
            popupMenu.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryViewHolder> {
        boolean isLimited;

        public HistoryRecyclerAdapter(boolean isLimited) {
            this.isLimited = isLimited;
        }

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
        public void onBindViewHolder(@NonNull HistoryRecyclerAdapter.HistoryViewHolder holder, int position) {
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
                    holder.dateTextView.setText(String.format(getString(R.string.history_date), Converter.getHistoryMonth(context, history.month, isLimited), history.date));
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
            if (isLimited)
                return Math.min(historyArrayList.size(), HISTORY_SIZE_LIMIT);
            else
                return historyArrayList.size();
        }

    }
}
