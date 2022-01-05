package com.pleiades.pleione.kittencare.ui.fragment.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.AdvertisementController;
import com.pleiades.pleione.kittencare.controller.PrefsController;
import com.pleiades.pleione.kittencare.controller.ToastController;
import com.pleiades.pleione.kittencare.ui.activity.game.DinosaurActivity;
import com.pleiades.pleione.kittencare.ui.activity.game.PajamasActivity;
import com.pleiades.pleione.kittencare.ui.activity.game.PleiadesActivity;
import com.pleiades.pleione.kittencare.ui.fragment.dialog.DefaultDialogFragment;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_ALCYONE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_DINOSAUR;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_GAME_MACHINE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_PAJAMAS;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_PLEIONE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_SUNFLOWER;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_SWEATER;
import static com.pleiades.pleione.kittencare.Config.DELAY_FACE_MAINTAIN_LONG;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_TICKET;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_1;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_2;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_BLINK_3;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_DEFAULT;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_MEOW_1;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_MEOW_2;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SLEEP;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SPARKLE;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SURPRISED;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SWEAT_1;
import static com.pleiades.pleione.kittencare.Config.FACE_CODE_SWEAT_2;
import static com.pleiades.pleione.kittencare.Config.GAME_CODE_DINOSAUR;
import static com.pleiades.pleione.kittencare.Config.GAME_CODE_PAJAMAS;
import static com.pleiades.pleione.kittencare.Config.GAME_CODE_PLEIADES;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_COSTUME_FOUND;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_ALCYONE;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_DINOSAUR;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_GAME_MACHINE;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_PAJAMAS;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_PLEIONE;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_SUNFLOWER;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_SWEATER;
import static com.pleiades.pleione.kittencare.Config.KEY_GAME_TICKET_DINOSAUR;
import static com.pleiades.pleione.kittencare.Config.KEY_GAME_TICKET_PAJAMAS;
import static com.pleiades.pleione.kittencare.Config.KEY_GAME_TICKET_PLEIADES;
import static com.pleiades.pleione.kittencare.Config.KEY_GAME_WIN_COUNT;
import static com.pleiades.pleione.kittencare.Config.KEY_HAPPINESS;
import static com.pleiades.pleione.kittencare.Config.KEY_IS_GAME_DIFFICULTY_HARD;
import static com.pleiades.pleione.kittencare.Config.PERIOD_CHANGE_FACE_GAME_FRAGMENT;
import static com.pleiades.pleione.kittencare.Config.PREFS;
import static com.pleiades.pleione.kittencare.Config.TICKET_MAX;
import static com.pleiades.pleione.kittencare.Converter.getCostumeResourceId;
import static com.pleiades.pleione.kittencare.Converter.getFaceResourceId;

// TODO update game
public class GameFragment extends Fragment {
    private Context context;

    private ArrayList<Game> gameArrayList;
    private GamesRecyclerAdapter gamesRecyclerAdapter;

    private Timer timer;
    private TimerTask timerTask;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);

        // initialize game array list
        gameArrayList = new ArrayList<>();
        gameArrayList.add(new Game(GAME_CODE_PAJAMAS));
        gameArrayList.add(new Game(GAME_CODE_PLEIADES));
        gameArrayList.add(new Game(GAME_CODE_DINOSAUR));

        // initialize games recycler view
        RecyclerView gamesRecyclerView = rootView.findViewById(R.id.recycler_game);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        gamesRecyclerView.setLayoutManager(linearLayoutManager);

        // initialize games recycler adapter
        gamesRecyclerAdapter = new GamesRecyclerAdapter();
        gamesRecyclerView.setAdapter(gamesRecyclerAdapter);

        // initialize ad button
        Button button = rootView.findViewById(R.id.button_game);
        button.setOnClickListener(view -> {
            DefaultDialogFragment defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_TICKET);
            defaultDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), Integer.toString(DIALOG_TYPE_TICKET));
        });

        return rootView;
    }

    private void changeLeftFace(final Game game, final int faceCode, int delay) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (game.leftFaceImageView != null)
                game.leftFaceImageView.setImageResource(getFaceResourceId(faceCode));
        }, delay);
    }

    private void changeRightFace(final Game game, final int faceCode, int delay) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (game.rightFaceImageView != null)
                game.rightFaceImageView.setImageResource(getFaceResourceId(faceCode));
        }, delay);
    }

    private void cancelTimerTask() {
        // cancel timer task
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        // cancel timer
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();

        // set has options menu
        setHasOptionsMenu(true);

        // notify data set changed
        gamesRecyclerAdapter.notifyDataSetChanged();

        // load rewarded ad again
        if (AdvertisementController.ticketRewardedAd == null)
            AdvertisementController.loadTicketRewardedAd(context, false);

        // initialize timer task
        timerTask = new TimerTask() {
            @Override
            public void run() {
                for (Game game : gameArrayList) {
                    if (game.gameCode == GAME_CODE_PAJAMAS) {
                        if (game.isLeftUnlocked) {
                            changeLeftFace(game, FACE_CODE_BLINK_1, 0);
                            changeLeftFace(game, FACE_CODE_BLINK_2, 40);
                            changeLeftFace(game, FACE_CODE_BLINK_3, 80);
                            changeLeftFace(game, FACE_CODE_SLEEP, 120);
                            changeLeftFace(game, FACE_CODE_BLINK_3, 120 + DELAY_FACE_MAINTAIN_LONG);
                            changeLeftFace(game, FACE_CODE_BLINK_2, 160 + DELAY_FACE_MAINTAIN_LONG);
                            changeLeftFace(game, FACE_CODE_BLINK_1, 200 + DELAY_FACE_MAINTAIN_LONG);
                            changeLeftFace(game, FACE_CODE_DEFAULT, 240 + DELAY_FACE_MAINTAIN_LONG);
                        }
                        if (game.isRightUnlocked) {
                            changeRightFace(game, FACE_CODE_MEOW_2, 0);
                            changeRightFace(game, FACE_CODE_MEOW_1, 40);
                            changeRightFace(game, FACE_CODE_SURPRISED, 80);
                            changeRightFace(game, FACE_CODE_SPARKLE, 120);
                            changeRightFace(game, FACE_CODE_SURPRISED, 120 + DELAY_FACE_MAINTAIN_LONG);
                            changeRightFace(game, FACE_CODE_MEOW_1, 160 + DELAY_FACE_MAINTAIN_LONG);
                            changeRightFace(game, FACE_CODE_MEOW_2, 200 + DELAY_FACE_MAINTAIN_LONG);
                            changeRightFace(game, FACE_CODE_DEFAULT, 240 + DELAY_FACE_MAINTAIN_LONG);
                        }
                    } else if (game.gameCode == GAME_CODE_PLEIADES) {
                        if (game.isLeftUnlocked) {
                            changeLeftFace(game, FACE_CODE_BLINK_1, 0);
                            changeLeftFace(game, FACE_CODE_BLINK_2, 40);
                            changeLeftFace(game, FACE_CODE_BLINK_3, 80);
                            changeLeftFace(game, FACE_CODE_SLEEP, 120);
                            changeLeftFace(game, FACE_CODE_BLINK_3, 120 + DELAY_FACE_MAINTAIN_LONG);
                            changeLeftFace(game, FACE_CODE_BLINK_2, 160 + DELAY_FACE_MAINTAIN_LONG);
                            changeLeftFace(game, FACE_CODE_BLINK_1, 200 + DELAY_FACE_MAINTAIN_LONG);
                            changeLeftFace(game, FACE_CODE_DEFAULT, 240 + DELAY_FACE_MAINTAIN_LONG);
                        }
                        if (game.isRightUnlocked) {
                            changeRightFace(game, FACE_CODE_MEOW_2, 0);
                            changeRightFace(game, FACE_CODE_MEOW_1, 40);
                            changeRightFace(game, FACE_CODE_SWEAT_1, 80);
                            changeRightFace(game, FACE_CODE_SWEAT_2, 120 + DELAY_FACE_MAINTAIN_LONG); // guide line 120
                            changeRightFace(game, FACE_CODE_MEOW_1, 160 + DELAY_FACE_MAINTAIN_LONG);
                            changeRightFace(game, FACE_CODE_MEOW_2, 200 + DELAY_FACE_MAINTAIN_LONG);
                            changeRightFace(game, FACE_CODE_DEFAULT, 240 + DELAY_FACE_MAINTAIN_LONG);
                        }
                    } else if (game.gameCode == GAME_CODE_DINOSAUR) {
                        if (game.isLeftUnlocked) {
                            changeLeftFace(game, FACE_CODE_MEOW_2, 0);
                            changeLeftFace(game, FACE_CODE_MEOW_1, 40);
                            changeLeftFace(game, FACE_CODE_SURPRISED, 80);
                            changeLeftFace(game, FACE_CODE_SPARKLE, 120);
                            changeLeftFace(game, FACE_CODE_SURPRISED, 120 + DELAY_FACE_MAINTAIN_LONG);
                            changeLeftFace(game, FACE_CODE_MEOW_1, 160 + DELAY_FACE_MAINTAIN_LONG);
                            changeLeftFace(game, FACE_CODE_MEOW_2, 200 + DELAY_FACE_MAINTAIN_LONG);
                            changeLeftFace(game, FACE_CODE_DEFAULT, 240 + DELAY_FACE_MAINTAIN_LONG);
                        }
                        if (game.isRightUnlocked) {
                            changeRightFace(game, FACE_CODE_MEOW_2, 0);
                            changeRightFace(game, FACE_CODE_MEOW_1, 40);
                            changeRightFace(game, FACE_CODE_SURPRISED, 80);
                            changeRightFace(game, FACE_CODE_MEOW_1, 120 + DELAY_FACE_MAINTAIN_LONG); // guide line 120
                            changeRightFace(game, FACE_CODE_MEOW_2, 160 + DELAY_FACE_MAINTAIN_LONG);
                            changeRightFace(game, FACE_CODE_DEFAULT, 200 + DELAY_FACE_MAINTAIN_LONG);
                        }
                    }
                }
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, PERIOD_CHANGE_FACE_GAME_FRAGMENT / 2, PERIOD_CHANGE_FACE_GAME_FRAGMENT);
    }

    @Override
    public void onPause() {
        super.onPause();

        // cancel timer
        cancelTimerTask();

        // set has options menu
        setHasOptionsMenu(false);
    }

    @Override
    public void onDestroy() {
        // cancel timer
        cancelTimerTask();

        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_game_out, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_select_difficulty_game) {
            // initialize parent activity
            Activity parentActivity = getActivity();
            if (parentActivity == null)
                return false;

            SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
            final SharedPreferences.Editor editor = prefs.edit();

            // initialize popup menu
            Context themeContext = new ContextThemeWrapper(context, R.style.AppTheme);
            PopupMenu popupMenu = new PopupMenu(themeContext, parentActivity.findViewById(R.id.action_select_difficulty_game));
            popupMenu.inflate(R.menu.menu_game_out_adjust_difficulty);
            popupMenu.setOnMenuItemClickListener(item1 -> {
                int id1 = item1.getItemId();

                if (id1 == R.id.action_adjust_difficulty_easy)
                    editor.putBoolean(KEY_IS_GAME_DIFFICULTY_HARD, false);
                else if (id1 == R.id.action_adjust_difficulty_hard)
                    editor.putBoolean(KEY_IS_GAME_DIFFICULTY_HARD, true);
                editor.apply();

                // check item
                item1.setChecked(true);
                return true;
            });

            // initialize checked item
            boolean isGameDifficultyHard = prefs.getBoolean(KEY_IS_GAME_DIFFICULTY_HARD, false);
            MenuItem popupItem = popupMenu.getMenu().getItem(isGameDifficultyHard ? 1 : 0);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int happiness = prefs.getInt(KEY_HAPPINESS, 100);

        // case win (request code == result code == game code)
        if (requestCode == resultCode) {
            // set happiness
            happiness = Math.min(100, happiness + 1);

            // apply game win count
            int gameWinCount = prefs.getInt(KEY_GAME_WIN_COUNT, 0) + 1;
            editor.putInt(KEY_GAME_WIN_COUNT, gameWinCount);
            editor.apply();

            // unlock game machine costume
            if (gameWinCount == 30) {
                editor.putBoolean(KEY_COSTUME_GAME_MACHINE, true);
                editor.apply();

                new PrefsController(context).addHistoryPrefs(HISTORY_TYPE_COSTUME_FOUND, COSTUME_CODE_GAME_MACHINE);
            }
        } else if (resultCode == -1) {
            // set happiness
            happiness = Math.max(0, happiness - 1);
        }

        // apply happiness
        editor.putInt(KEY_HAPPINESS, happiness);
        editor.apply();

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class Game {
        public int gameCode;
        public boolean isLeftUnlocked, isRightUnlocked;
        public ImageView leftFaceImageView, rightFaceImageView;

        public Game(int gameCode) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
            this.gameCode = gameCode;

            if (gameCode == GAME_CODE_PAJAMAS) {
                isLeftUnlocked = prefs.getBoolean(KEY_COSTUME_PAJAMAS, false);
                isRightUnlocked = prefs.getBoolean(KEY_COSTUME_SWEATER, false);
            } else if (gameCode == GAME_CODE_PLEIADES) {
                isLeftUnlocked = prefs.getBoolean(KEY_COSTUME_ALCYONE, false);
                isRightUnlocked = prefs.getBoolean(KEY_COSTUME_PLEIONE, false);
            } else if (gameCode == GAME_CODE_DINOSAUR) {
                isLeftUnlocked = prefs.getBoolean(KEY_COSTUME_DINOSAUR, false);
                isRightUnlocked = prefs.getBoolean(KEY_COSTUME_SUNFLOWER, false);
            }
        }
    }

    private class GamesRecyclerAdapter extends RecyclerView.Adapter<GamesRecyclerAdapter.GamesViewHolder> {

        private class GamesViewHolder extends RecyclerView.ViewHolder {
            View leftKittenView, rightKittenView;
            ImageView leftBodyImageView, leftFaceImageView, leftCostumeImageView;
            ImageView rightBodyImageView, rightFaceImageView, rightCostumeImageView;
            TextView titleTextView, contentsTextView;

            public GamesViewHolder(@NonNull View itemView) {
                super(itemView);

                leftKittenView = itemView.findViewById(R.id.kitten_left_recycler_game);
                leftBodyImageView = leftKittenView.findViewById(R.id.body_kitten);
                leftFaceImageView = leftKittenView.findViewById(R.id.face_kitten);
                leftCostumeImageView = leftKittenView.findViewById(R.id.costume_kitten);
                rightKittenView = itemView.findViewById(R.id.kitten_right_recycler_game);
                rightBodyImageView = rightKittenView.findViewById(R.id.body_kitten);
                rightFaceImageView = rightKittenView.findViewById(R.id.face_kitten);
                rightCostumeImageView = rightKittenView.findViewById(R.id.costume_kitten);
                titleTextView = itemView.findViewById(R.id.title_recycler_game);
                contentsTextView = itemView.findViewById(R.id.contents_recycler_game);

                itemView.findViewById(R.id.layout_recycler_game).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // initialize position
                        int position = getBindingAdapterPosition();
                        if (position == RecyclerView.NO_POSITION)
                            return;

                        SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        Game game = gameArrayList.get(position);

                        if (game.isLeftUnlocked && game.isRightUnlocked) {
                            int tickets = 0;

                            if (game.gameCode == GAME_CODE_PAJAMAS) {
                                // initialize tickets
                                tickets = prefs.getInt(KEY_GAME_TICKET_PAJAMAS, TICKET_MAX);

                                if (tickets > 0) {
                                    // start game activity
                                    Intent intent = new Intent(context, PajamasActivity.class);
                                    startActivityForResult(intent, game.gameCode);

                                    // apply decreased ticket
                                    editor.putInt(KEY_GAME_TICKET_PAJAMAS, tickets - 1);
                                    editor.apply();
                                    notifyItemChanged(position);
                                }
                            } else if (game.gameCode == GAME_CODE_PLEIADES) {
                                // initialize tickets
                                tickets = prefs.getInt(KEY_GAME_TICKET_PLEIADES, TICKET_MAX);

                                if (tickets > 0) {
                                    // start game activity
                                    Intent intent = new Intent(context, PleiadesActivity.class);
                                    startActivityForResult(intent, game.gameCode);

                                    // apply decreased ticket
                                    editor.putInt(KEY_GAME_TICKET_PLEIADES, tickets - 1);
                                    editor.apply();
                                    notifyItemChanged(position);
                                }
                            } else if (game.gameCode == GAME_CODE_DINOSAUR) {
                                // initialize tickets
                                tickets = prefs.getInt(KEY_GAME_TICKET_DINOSAUR, TICKET_MAX);

                                if (tickets > 0) {
                                    // start game activity
                                    Intent intent = new Intent(context, DinosaurActivity.class);
                                    startActivityForResult(intent, game.gameCode);

                                    // apply decreased ticket
                                    editor.putInt(KEY_GAME_TICKET_DINOSAUR, tickets - 1);
                                    editor.apply();
                                    notifyItemChanged(position);
                                }
                            }

                            // case ticket is lack
                            if (tickets == 0) {
                                String message = String.format(getString(R.string.toast_lack_of_ticket), TICKET_MAX);
                                new ToastController(context).showToast(message, Toast.LENGTH_SHORT);
                            }
                        } else {
                            String message = getString(R.string.toast_unlock_costume_first);
                            new ToastController(context).showToast(message, Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        }

        @NonNull
        @Override
        public GamesRecyclerAdapter.GamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.recycler_game, parent, false);
            return new GamesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GamesRecyclerAdapter.GamesViewHolder holder, int position) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
            Game game = gameArrayList.get(position);

            // set title, contents, costume, is unlocked
            if (game.gameCode == GAME_CODE_PAJAMAS) {
                int pajamasTickets = prefs.getInt(KEY_GAME_TICKET_PAJAMAS, TICKET_MAX);

                holder.titleTextView.setText(R.string.game_title_pajamas);
                holder.contentsTextView.setText(String.format(getString(R.string.game_contents_tickets), pajamasTickets, TICKET_MAX));
                holder.leftCostumeImageView.setImageResource(getCostumeResourceId(COSTUME_CODE_PAJAMAS));
                holder.rightCostumeImageView.setImageResource(getCostumeResourceId(COSTUME_CODE_SWEATER));
            } else if (game.gameCode == GAME_CODE_PLEIADES) {
                int pleiadesTickets = prefs.getInt(KEY_GAME_TICKET_PLEIADES, TICKET_MAX);

                holder.titleTextView.setText(R.string.game_title_pleiades);
                holder.contentsTextView.setText(String.format(getString(R.string.game_contents_tickets), pleiadesTickets, TICKET_MAX));
                holder.leftCostumeImageView.setImageResource(getCostumeResourceId(COSTUME_CODE_ALCYONE));
                holder.rightCostumeImageView.setImageResource(getCostumeResourceId(COSTUME_CODE_PLEIONE));
            } else if (game.gameCode == GAME_CODE_DINOSAUR) {
                int dinosaurTickets = prefs.getInt(KEY_GAME_TICKET_DINOSAUR, TICKET_MAX);

                holder.titleTextView.setText(R.string.game_title_dinosaur);
                holder.contentsTextView.setText(String.format(getString(R.string.game_contents_tickets), dinosaurTickets, TICKET_MAX));
                holder.leftCostumeImageView.setImageResource(getCostumeResourceId(COSTUME_CODE_DINOSAUR));
                holder.rightCostumeImageView.setImageResource(getCostumeResourceId(COSTUME_CODE_SUNFLOWER));
            }

            // set left color filter
            if (game.isLeftUnlocked) {
                holder.leftBodyImageView.setColorFilter(Color.argb(0, 0, 0, 0));
                holder.leftFaceImageView.setColorFilter(Color.argb(0, 0, 0, 0));
                holder.leftCostumeImageView.setColorFilter(Color.argb(0, 0, 0, 0));
            } else {
                holder.leftBodyImageView.setColorFilter(Color.argb(72, 0, 0, 0));
                holder.leftFaceImageView.setColorFilter(Color.argb(72, 0, 0, 0));
                holder.leftCostumeImageView.setColorFilter(Color.argb(72, 0, 0, 0));
            }

            // set right color filter
            if (game.isRightUnlocked) {
                holder.rightBodyImageView.setColorFilter(Color.argb(0, 0, 0, 0));
                holder.rightFaceImageView.setColorFilter(Color.argb(0, 0, 0, 0));
                holder.rightCostumeImageView.setColorFilter(Color.argb(0, 0, 0, 0));
            } else {
                holder.rightBodyImageView.setColorFilter(Color.argb(72, 0, 0, 0));
                holder.rightFaceImageView.setColorFilter(Color.argb(72, 0, 0, 0));
                holder.rightCostumeImageView.setColorFilter(Color.argb(72, 0, 0, 0));
            }

            // set face image view for timer task
            game.leftFaceImageView = holder.leftFaceImageView;
            game.rightFaceImageView = holder.rightFaceImageView;
        }

        @Override
        public int getItemCount() {
            return gameArrayList.size();
        }
    }
}