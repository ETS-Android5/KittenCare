package com.pleiades.pleione.kittencare.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pleiades.pleione.kittencare.Converter;
import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.AnimationController;
import com.pleiades.pleione.kittencare.controller.PrefsController;
import com.pleiades.pleione.kittencare.controller.ToastController;
import com.pleiades.pleione.kittencare.object.Costume;
import com.pleiades.pleione.kittencare.ui.activity.AdvertisementActivity;
import com.pleiades.pleione.kittencare.ui.activity.MainActivity;
import com.pleiades.pleione.kittencare.ui.fragment.dialog.DefaultDialogFragment;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_2021;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_ALCYONE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_CHOCO;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_GAME_MACHINE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_GIFT;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_PLEIONE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_SAILOR;
import static com.pleiades.pleione.kittencare.Config.COSTUME_TYPE_FREE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_TYPE_PAID;
import static com.pleiades.pleione.kittencare.Config.COSTUME_TYPE_SPECIAL;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_2021;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_ALCYONE;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_PLEIONE;
import static com.pleiades.pleione.kittencare.Config.KEY_WEARING_COSTUME;
import static com.pleiades.pleione.kittencare.Config.PREFS;

public class CostumeFragment extends Fragment {
    private Context context;

    private ArrayList<Costume> costumeArrayList;
    private CostumesRecyclerAdapter costumesRecyclerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_costume, container, false);

        // set has options menu
        setHasOptionsMenu(true);

        // initialize costumes recycler view
        RecyclerView costumesRecyclerView = rootView.findViewById(R.id.recycler_costume);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);
        costumesRecyclerView.setLayoutManager(gridLayoutManager);

        // initialize costumes recycler adapter
        costumesRecyclerAdapter = new CostumesRecyclerAdapter();
        costumesRecyclerAdapter.setHasStableIds(true);
        costumesRecyclerView.setAdapter(costumesRecyclerAdapter);

        // initialize advertisement button
        Button advertisementButton = rootView.findViewById(R.id.button_costume);
        advertisementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdvertisementActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        costumeArrayList = new PrefsController(context).getInitializedCostumeArrayList();
        costumesRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

        // initialize costume array list
//        costumeArrayList = new PrefsController(context).getInitializedCostumeArrayList();
//        costumesRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private class CostumesRecyclerAdapter extends RecyclerView.Adapter<CostumesRecyclerAdapter.CostumesViewHolder> {

        class CostumesViewHolder extends RecyclerView.ViewHolder {
            ImageView bodyImageView, faceImageView, costumeImageView;
            TextView typeTextView, titleTextView, contentsTextView;
            View kittenView;

            CostumesViewHolder(View view) {
                super(view);

                // initialize views
                kittenView = view.findViewById(R.id.kitten_recycler_costume);
                bodyImageView = kittenView.findViewById(R.id.body_kitten);
                faceImageView = kittenView.findViewById(R.id.face_kitten);
                costumeImageView = kittenView.findViewById(R.id.costume_kitten);

                typeTextView = view.findViewById(R.id.type_recycler_costume);
                titleTextView = view.findViewById(R.id.title_recycler_costume);
                contentsTextView = view.findViewById(R.id.contents_recycler_costume);

                // TODO update costume
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // initialize position
                        int position = getBindingAdapterPosition();
                        if (position == RecyclerView.NO_POSITION)
                            return;

                        // initialize costume
                        Costume costume = costumeArrayList.get(position);

                        // check unlocked
                        if (costume.isUnlocked) {
                            SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();

                            // change kitten costume
                            AnimationController.changeKittenCostume(costume.costumeCode);

                            int wearingCostumeCode = prefs.getInt(KEY_WEARING_COSTUME, COSTUME_CODE_DEFAULT);
                            for (int i = 0; i < costumeArrayList.size(); i++) {
                                // find wearing costume position
                                if (costumeArrayList.get(i).costumeCode == wearingCostumeCode) {
                                    // apply wearing costume
                                    editor.putInt(KEY_WEARING_COSTUME, costume.costumeCode);
                                    editor.apply();

                                    // add worn prefs
                                    new PrefsController(context).addWornPrefs(costume.costumeCode);

                                    notifyItemChanged(i); // put off
                                    notifyItemChanged(position); // put on
                                    break;
                                }
                            }
                        } else {
                            // case free costume
                            if (costume.costumeType == COSTUME_TYPE_FREE) {
                                new ToastController(context).showCostumeConditionToast(costume);
                            }
                            // case special costume
                            else if (costume.costumeType == COSTUME_TYPE_SPECIAL) {
                                DefaultDialogFragment defaultDialogFragment;

                                switch (costume.costumeCode) {
                                    case COSTUME_CODE_ALCYONE:
                                        defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_ALCYONE);
                                        defaultDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), Integer.toString(DIALOG_TYPE_ALCYONE));
                                        break;
                                    case COSTUME_CODE_PLEIONE:
                                        defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_PLEIONE);
                                        defaultDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), Integer.toString(DIALOG_TYPE_PLEIONE));
                                        break;
                                    case COSTUME_CODE_2021:
                                        defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_2021);
                                        defaultDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), Integer.toString(DIALOG_TYPE_2021));
                                        break;
                                    case COSTUME_CODE_GIFT:
                                    case COSTUME_CODE_CHOCO:
                                    case COSTUME_CODE_GAME_MACHINE:
                                    case COSTUME_CODE_SAILOR:
                                        new ToastController(context).showCostumeConditionToast(costume);
                                        break;
                                }
                            }
                            // case paid costume
                            else if (costume.costumeType == COSTUME_TYPE_PAID) {
                                Activity activity = getActivity();
                                if (activity == null)
                                    return;

                                ((MainActivity) activity).purchaseCostume(costume.costumeCode);
                            }
                        }
                    }
                });
            }
        }

        @NonNull
        @Override
        public CostumesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_costume, viewGroup, false);
            return new CostumesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CostumesViewHolder holder, int position) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
            Costume costume = costumeArrayList.get(position);

            // set type text view
            if (costume.isUnlocked) {
                holder.typeTextView.setTextColor(ContextCompat.getColor(context, R.color.color_accent));

                if (costume.costumeCode == COSTUME_CODE_DEFAULT)
                    holder.typeTextView.setText(null);
                else if (costume.isUnlocked && !new PrefsController(context).getWornArrayListPrefs().contains(costume.costumeCode))
                    holder.typeTextView.setText("N");
                else
                    holder.typeTextView.setText(null);
            } else {
                holder.typeTextView.setTextColor(ContextCompat.getColor(context, R.color.color_signature_black));

                if (costume.costumeType == COSTUME_TYPE_SPECIAL)
                    holder.typeTextView.setText("S");
                else if (costume.costumeType == COSTUME_TYPE_PAID)
                    holder.typeTextView.setText("P");
                else
                    holder.typeTextView.setText(null);
            }

            // set costume image view
            if (costume.costumeCode == COSTUME_CODE_DEFAULT)
                holder.costumeImageView.setImageDrawable(null);
            else
                holder.costumeImageView.setImageResource(Converter.getCostumeResourceId(costume.costumeCode));

            // set title text view
            holder.titleTextView.setText(Converter.getCostumeName(context, costume.costumeCode));

            // set contents text view
            if (costume.costumeCode == prefs.getInt(KEY_WEARING_COSTUME, COSTUME_CODE_DEFAULT))
                holder.contentsTextView.setText(R.string.costume_contents_wearing);
            else
                holder.contentsTextView.setText(null);

            // set color filter
            if (costume.isUnlocked) {
                holder.bodyImageView.setColorFilter(Color.argb(0, 0, 0, 0));
                holder.faceImageView.setColorFilter(Color.argb(0, 0, 0, 0));
                holder.costumeImageView.setColorFilter(Color.argb(0, 0, 0, 0));
            } else {
                holder.bodyImageView.setColorFilter(Color.argb(72, 0, 0, 0));
                holder.faceImageView.setColorFilter(Color.argb(72, 0, 0, 0));
                holder.costumeImageView.setColorFilter(Color.argb(72, 0, 0, 0));
            }
        }

        @Override
        public int getItemCount() {
            return costumeArrayList.size();
        }

        @Override
        public long getItemId(int position) {
            return costumeArrayList.get(position).costumeCode;
        }

    }

}