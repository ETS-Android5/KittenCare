package com.pleiades.pleione.kittencare.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pleiades.pleione.kittencare.Converter;
import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.PrefsController;
import com.pleiades.pleione.kittencare.controller.ToastController;
import com.pleiades.pleione.kittencare.object.Item;
import com.pleiades.pleione.kittencare.ui.activity.AdvertisementActivity;
import com.pleiades.pleione.kittencare.ui.activity.charm.AlchemyActivity;
import com.pleiades.pleione.kittencare.ui.activity.charm.DivinationActivity;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;
import static com.pleiades.pleione.kittencare.Config.FILTER_POSITION_ITEM_ALL;
import static com.pleiades.pleione.kittencare.Config.FILTER_POSITION_ITEM_CHARM;
import static com.pleiades.pleione.kittencare.Config.FILTER_POSITION_ITEM_CONSUMPTION;
import static com.pleiades.pleione.kittencare.Config.FILTER_POSITION_ITEM_TOY;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_ALCHEMY;
import static com.pleiades.pleione.kittencare.Config.ITEM_CODE_CRYSTAL_BALL;
import static com.pleiades.pleione.kittencare.Config.ITEM_TYPE_CHARM;
import static com.pleiades.pleione.kittencare.Config.ITEM_TYPE_CONSUMPTION;
import static com.pleiades.pleione.kittencare.Config.ITEM_TYPE_TOY;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_ALCHEMIST;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_SEER;
import static com.pleiades.pleione.kittencare.Config.KEY_HAPPINESS;
import static com.pleiades.pleione.kittencare.Config.PREFS;

public class ItemFragment extends Fragment {
    private Context context;

    private ArrayList<Item> itemArrayList;
    private ItemsRecyclerAdapter itemsRecyclerAdapter;

    private int filterPosition;
    private int happiness;

    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item, container, false);
        final SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);

        // set has options menu
        setHasOptionsMenu(true);

        // initialize items recycler view
        RecyclerView itemsRecyclerView = rootView.findViewById(R.id.recycler_item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        itemsRecyclerView.setLayoutManager(linearLayoutManager);

        // initialize happiness
        happiness = prefs.getInt(KEY_HAPPINESS, 100);

        // initialize items recycler adapter
        itemsRecyclerAdapter = new ItemsRecyclerAdapter();
        itemsRecyclerAdapter.setHasStableIds(true);
        itemsRecyclerView.setAdapter(itemsRecyclerAdapter);

        // initialize swipe refresh layout
        final SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.swipe_item);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.color_accent));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            initializeItemArrayList();
            happiness = prefs.getInt(KEY_HAPPINESS, 100);
            itemsRecyclerAdapter.notifyDataSetChanged();

            swipeRefreshLayout.setRefreshing(false);
        });

        // initialize advertisement button
        Button advertisementButton = rootView.findViewById(R.id.button_item);
        advertisementButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, AdvertisementActivity.class);
            startActivity(intent);
        });

        return rootView;
    }

    private void initializeItemArrayList() {
        ArrayList<Item> originItemArrayList = new PrefsController(context).getItemArrayListPrefs();
        ArrayList<Item> filteredItemArrayList = new ArrayList<>();

        // filter item array list
        if (filterPosition == FILTER_POSITION_ITEM_CONSUMPTION) {
            for (Item item : originItemArrayList) {
                if (item.itemType == ITEM_TYPE_CONSUMPTION)
                    filteredItemArrayList.add(item);
            }
        } else if (filterPosition == FILTER_POSITION_ITEM_CHARM) {
            for (Item item : originItemArrayList) {
                if (item.itemType == ITEM_TYPE_CHARM)
                    filteredItemArrayList.add(item);
            }
        } else if (filterPosition == FILTER_POSITION_ITEM_TOY) {
            for (Item item : originItemArrayList) {
                if (item.itemType == ITEM_TYPE_TOY)
                    filteredItemArrayList.add(item);
            }
        } else
            filteredItemArrayList = originItemArrayList;

        // set item array list
        itemArrayList = filteredItemArrayList;

        // sort item array list
        Collections.sort(itemArrayList);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onStart() {
        super.onStart();

        // initialize item array list
        initializeItemArrayList();
        itemsRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_items, menu);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_filter_items) {
            // initialize parent activity
            Activity parentActivity = getActivity();
            if (parentActivity == null)
                return false;

            // initialize popup menu
            Context themeContext = new ContextThemeWrapper(context, R.style.AppTheme);
            PopupMenu popupMenu = new PopupMenu(themeContext, parentActivity.findViewById(R.id.action_filter_items));
            popupMenu.inflate(R.menu.menu_items_filter);
            popupMenu.setOnMenuItemClickListener(item1 -> {
                int id1 = item1.getItemId();

                if (id1 == R.id.action_items_all)
                    filterPosition = FILTER_POSITION_ITEM_ALL;
                else if (id1 == R.id.action_items_consumption)
                    filterPosition = FILTER_POSITION_ITEM_CONSUMPTION;
                else if (id1 == R.id.action_items_charm)
                    filterPosition = FILTER_POSITION_ITEM_CHARM;
                else if (id1 == R.id.action_items_toy)
                    filterPosition = FILTER_POSITION_ITEM_TOY;

                // initialize item array list
                initializeItemArrayList();
                itemsRecyclerAdapter.notifyDataSetChanged();

                // check item
                item1.setChecked(true);
                return true;
            });

            // initialize checked item
            MenuItem popupItem = popupMenu.getMenu().getItem(filterPosition);
            popupItem.setChecked(true);

            // show popup menu
            popupMenu.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private class ItemsRecyclerAdapter extends RecyclerView.Adapter<ItemsRecyclerAdapter.ItemsViewHolder> {

        private class ItemsViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView, effectTextView, quantityTextView;
            ImageView arrowImageView, iconImageView;

            @SuppressLint("NotifyDataSetChanged")
            public ItemsViewHolder(@NonNull final View itemView) {
                super(itemView);

                // initialize views
                nameTextView = itemView.findViewById(R.id.title_recycler_item);
                effectTextView = itemView.findViewById(R.id.contents_recycler_item);
                quantityTextView = itemView.findViewById(R.id.quantity_recycler_item);
                arrowImageView = itemView.findViewById(R.id.happiness_arrow_item);
                iconImageView = itemView.findViewById(R.id.icon_recycler_item);

                // TODO update item
                itemView.setOnClickListener(v -> {
                    // initialize position
                    int position = getBindingAdapterPosition();
                    if (position == RecyclerView.NO_POSITION)
                        return;

                    // initialize item object
                    Item item = itemArrayList.get(position);

                    // case consumption
                    if (item.itemType == ITEM_TYPE_CONSUMPTION) {
                        // use item
                        PrefsController prefsController = new PrefsController(context);
                        prefsController.useItem(itemArrayList, position);

                        int prevHappiness = happiness;
                        happiness = context.getSharedPreferences(PREFS, MODE_PRIVATE).getInt(KEY_HAPPINESS, 100);
                        if ((prevHappiness < 50 && happiness >= 50) || (prevHappiness >= 50 && happiness < 50)) {
                            initializeItemArrayList();
                            itemsRecyclerAdapter.notifyDataSetChanged();
                        } else {
                            // case out of item
                            if (item.quantity <= 0) {
                                // remove item from list
                                itemArrayList.remove(position);

                                // notify item removed
                                notifyItemRemoved(position);
                            } else {
                                // notify item changed
                                notifyItemChanged(position);
                            }
                        }
                    }

                    // case alchemy
                    else if (item.itemCode == ITEM_CODE_ALCHEMY) {
                        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);

                        if (prefs.getBoolean(KEY_COSTUME_ALCHEMIST, false)) {
                            Intent intent = new Intent(context, AlchemyActivity.class);
                            startActivity(intent);
                        } else
                            new ToastController(context).showToast(getString(R.string.toast_need_alchemist), Toast.LENGTH_SHORT);
                    }

                    // case crystal ball
                    else if (item.itemCode == ITEM_CODE_CRYSTAL_BALL) {
                        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);

                        if (prefs.getBoolean(KEY_COSTUME_SEER, false)) {
                            Intent intent = new Intent(context, DivinationActivity.class);
                            startActivity(intent);
                        } else
                            new ToastController(context).showToast(getString(R.string.toast_need_seer), Toast.LENGTH_SHORT);
                    }
                });
            }
        }

        @NonNull
        @Override
        public ItemsRecyclerAdapter.ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
            return new ItemsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemsRecyclerAdapter.ItemsViewHolder holder, int position) {
            Item item = itemArrayList.get(position);

            // set name
            holder.nameTextView.setText(Converter.getItemName(context, item.itemCode));

            // set arrow
            if (item.itemType == ITEM_TYPE_CONSUMPTION) {
                holder.arrowImageView.setImageResource(happiness >= 50 ? R.drawable.icon_arrow_up : R.drawable.icon_arrow_down);
                holder.arrowImageView.setColorFilter(ContextCompat.getColor(context, happiness >= 50 ? R.color.color_accent : R.color.color_accent_blue));
            } else
                holder.arrowImageView.setImageDrawable(null);

            // set effect
            holder.effectTextView.setText(Converter.getItemEffect(context, item.itemCode));

            // set icon
            holder.iconImageView.setImageResource(Converter.getItemIconResourceId(item.itemType, item.itemCode));

            // set quantity
            if (item.itemType == ITEM_TYPE_CHARM)
                holder.quantityTextView.setText(R.string.item_type_charm);
            else if (item.itemType == ITEM_TYPE_TOY)
                holder.quantityTextView.setText(R.string.item_type_toy);
            else
                holder.quantityTextView.setText(String.format(getString(R.string.item_quantity), itemArrayList.get(position).quantity));
        }

        @Override
        public int getItemCount() {
            return itemArrayList.size();
        }

        @Override
        public long getItemId(int position) {
            return itemArrayList.get(position).itemCode;
        }

    }

}