package com.pleiades.pleione.kittencare.ui.fragment.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.pleiades.pleione.kittencare.Converter;
import com.pleiades.pleione.kittencare.KittenService;
import com.pleiades.pleione.kittencare.R;
import com.pleiades.pleione.kittencare.controller.DeviceController;
import com.pleiades.pleione.kittencare.controller.PrefsController;
import com.pleiades.pleione.kittencare.ui.activity.MainActivity;
import com.pleiades.pleione.kittencare.ui.activity.game.DinosaurActivity;
import com.pleiades.pleione.kittencare.ui.activity.game.PajamasActivity;
import com.pleiades.pleione.kittencare.ui.activity.game.PleiadesActivity;

import static android.content.Context.MODE_PRIVATE;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_2021;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_DINOSAUR;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_PAJAMAS;
import static com.pleiades.pleione.kittencare.Config.COSTUME_CODE_PLEIONE;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_2021;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_ALCYONE;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_COMPLETE_HAPPINESS_TUTORIAL;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_COMPLETE_TUTORIAL;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_ESCAPE;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_FINISH_GAME;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_HAPPINESS;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_OVERLAY;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_PLEIONE;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_SKIP_OPENING;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_SKIP_TUTORIAL;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_START_DINOSAUR;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_START_PAJAMAS;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_START_PLEIADES;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_TICKET;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_WIN_DINOSAUR;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_WIN_PAJAMAS;
import static com.pleiades.pleione.kittencare.Config.DIALOG_TYPE_WIN_PLEIADES;
import static com.pleiades.pleione.kittencare.Config.GAME_CODE_DINOSAUR;
import static com.pleiades.pleione.kittencare.Config.GAME_CODE_PAJAMAS;
import static com.pleiades.pleione.kittencare.Config.GAME_CODE_PLEIADES;
import static com.pleiades.pleione.kittencare.Config.HISTORY_TYPE_COSTUME_FOUND;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_2021;
import static com.pleiades.pleione.kittencare.Config.KEY_COSTUME_PLEIONE;
import static com.pleiades.pleione.kittencare.Config.KEY_IS_HAPPINESS_TUTORIAL_COMPLETED;
import static com.pleiades.pleione.kittencare.Config.KEY_IS_TUTORIAL_COMPLETED;
import static com.pleiades.pleione.kittencare.Config.PREFS;
import static com.pleiades.pleione.kittencare.Config.URL_ALCYONE;
import static com.pleiades.pleione.kittencare.Config.URL_KITTEN;

public class DefaultDialogFragment extends androidx.fragment.app.DialogFragment {
    private Context context;
    private final int type;

    public DefaultDialogFragment(int type) {
        this.type = type;
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // initialize builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // initialize activity
        Activity activity = getActivity();

        if (activity != null) {
            // initialize dialog view
            View dialogView = activity.getLayoutInflater().inflate(R.layout.fragment_dialog_default, null);

            // initialize and set message
            TextView messageTextView = dialogView.findViewById(R.id.message_dialog_default);
            switch (type) {
                case DIALOG_TYPE_OVERLAY:
                    messageTextView.setText(R.string.dialog_message_overlay);
                    break;
                case DIALOG_TYPE_ALCYONE:
                    messageTextView.setText(R.string.costume_condition_alcyone);
                    break;
                case DIALOG_TYPE_PLEIONE:
                    messageTextView.setText(R.string.costume_condition_pleione);
                    break;
                case DIALOG_TYPE_2021:
                    messageTextView.setText(R.string.costume_condition_2021);
                    break;
                case DIALOG_TYPE_COMPLETE_TUTORIAL:
                    messageTextView.setText(R.string.dialog_message_complete_tutorial);
                    break;
                case DIALOG_TYPE_SKIP_TUTORIAL:
                    messageTextView.setText(R.string.dialog_message_skip_tutorial);
                    break;
                case DIALOG_TYPE_ESCAPE:
                    messageTextView.setText(R.string.dialog_message_escape);
                    break;
                case DIALOG_TYPE_FINISH_GAME:
                    messageTextView.setText(R.string.dialog_message_finish);
                    break;
                case DIALOG_TYPE_SKIP_OPENING:
                    messageTextView.setText(R.string.dialog_message_skip_opening);
                    break;
                case DIALOG_TYPE_START_PAJAMAS:
                    messageTextView.setText(R.string.dialog_message_win_ttakji);
                    break;
                case DIALOG_TYPE_START_PLEIADES:
                    messageTextView.setText(R.string.dialog_message_move_destination);
                    break;
                case DIALOG_TYPE_START_DINOSAUR:
                    messageTextView.setText(R.string.dialog_message_chase);
                    break;
                case DIALOG_TYPE_WIN_PAJAMAS:
                case DIALOG_TYPE_WIN_PLEIADES:
                case DIALOG_TYPE_WIN_DINOSAUR:
                    String message = String.format(getString(R.string.history_contents_item_reward), getTag());
                    messageTextView.setText(message);
                    break;
                case DIALOG_TYPE_TICKET:
                    messageTextView.setText(R.string.dialog_message_ticket);
                    break;
                case DIALOG_TYPE_HAPPINESS:
                    messageTextView.setText(R.string.dialog_message_happiness);
                    break;
                case DIALOG_TYPE_COMPLETE_HAPPINESS_TUTORIAL:
                    messageTextView.setText(R.string.dialog_message_complete_happiness_tutorial);
                    break;
            }

            // set positive listener
            dialogView.findViewById(R.id.positive_dialog_default).setOnClickListener(new onPositiveClickListener());

            // set negative listener
            dialogView.findViewById(R.id.negative_dialog_default).setOnClickListener(new onNegativeClickListener());

            // set negative visibility
            TextView negativeTextView = dialogView.findViewById(R.id.negative_dialog_default);
            switch (type) {
                case DIALOG_TYPE_COMPLETE_TUTORIAL:
                case DIALOG_TYPE_START_PAJAMAS:
                case DIALOG_TYPE_START_PLEIADES:
                case DIALOG_TYPE_START_DINOSAUR:
                case DIALOG_TYPE_WIN_PAJAMAS:
                case DIALOG_TYPE_WIN_PLEIADES:
                case DIALOG_TYPE_WIN_DINOSAUR:
                case DIALOG_TYPE_COMPLETE_HAPPINESS_TUTORIAL:
                    negativeTextView.setVisibility(View.INVISIBLE);
                    break;
                default:
                    negativeTextView.setVisibility(View.VISIBLE);
                    break;
            }

            // set dialog view
            builder.setView(dialogView);
        }

        // create dialog
        Dialog dialog = builder.create();

        // set transparent background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // set cancelable
        switch (type) {
            case DIALOG_TYPE_COMPLETE_TUTORIAL:
            case DIALOG_TYPE_START_PAJAMAS:
            case DIALOG_TYPE_START_PLEIADES:
            case DIALOG_TYPE_START_DINOSAUR:
            case DIALOG_TYPE_WIN_PAJAMAS:
            case DIALOG_TYPE_WIN_PLEIADES:
            case DIALOG_TYPE_WIN_DINOSAUR:
            case DIALOG_TYPE_HAPPINESS:
            case DIALOG_TYPE_COMPLETE_HAPPINESS_TUTORIAL:
                dialog.setCanceledOnTouchOutside(false);
                break;
            default:
                dialog.setCanceledOnTouchOutside(true);
                break;
        }

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        int margin = getResources().getDimensionPixelSize(R.dimen.margin_dialog_default);
        int width = (new DeviceController(context)).getWidthMax() - margin;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(width, height);

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (type == DIALOG_TYPE_ESCAPE) {
                        Activity parentActivity = getActivity();
                        if (parentActivity != null)
                            parentActivity.finish();
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        dismiss();
    }

    private class onPositiveClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            SharedPreferences.Editor editor = context.getSharedPreferences(PREFS, MODE_PRIVATE).edit();

            if (type == DIALOG_TYPE_OVERLAY) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                startActivity(intent);
            } else if (type == DIALOG_TYPE_ALCYONE) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(URL_ALCYONE));
                startActivity(intent);
            } else if (type == DIALOG_TYPE_PLEIONE) {
                editor.putBoolean(KEY_COSTUME_PLEIONE, true);
                editor.apply();

                new PrefsController(context).addHistoryPrefs(HISTORY_TYPE_COSTUME_FOUND, COSTUME_CODE_PLEIONE);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(URL_KITTEN));
                startActivity(intent);
            } else if (type == DIALOG_TYPE_2021) {
                editor.putBoolean(KEY_COSTUME_2021, true);
                editor.apply();

                new PrefsController(context).addHistoryPrefs(HISTORY_TYPE_COSTUME_FOUND, COSTUME_CODE_2021);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, URL_KITTEN);

                Intent chooserIntent = Intent.createChooser(intent, null);
                startActivity(chooserIntent);
            } else if (type == DIALOG_TYPE_COMPLETE_TUTORIAL) {
                editor.putBoolean(KEY_IS_TUTORIAL_COMPLETED, true);
                editor.apply();

                Activity parentActivity = getActivity();
                if (parentActivity != null)
                    parentActivity.onBackPressed();
            } else if (type == DIALOG_TYPE_SKIP_TUTORIAL) {
                // show complete tutorial dialog
                DefaultDialogFragment defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_COMPLETE_TUTORIAL);
                defaultDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), Integer.toString(DIALOG_TYPE_COMPLETE_TUTORIAL));
            } else if (type == DIALOG_TYPE_ESCAPE) {
                context.stopService(new Intent(context, KittenService.class)); // hide kitten and prevent zombie notification

                Activity parentActivity = getActivity();
                if (parentActivity != null) {
                    parentActivity.finishAndRemoveTask(); // prevent dialog error when click application at history
                    parentActivity.finishAffinity(); // finish as many as possible
                }
                System.runFinalization(); // memory deallocate after finish affinity
                System.exit(0); // shut down
            } else if (type == DIALOG_TYPE_FINISH_GAME) {
                Activity parentActivity = getActivity();
                if (parentActivity != null) {
                    parentActivity.setResult(-1);
                    parentActivity.finish();
                }
            } else if (type == DIALOG_TYPE_SKIP_OPENING) {
                Activity parentActivity = getActivity();
                String dialogTag = getTag();
                if (parentActivity != null && dialogTag != null) {
                    if (dialogTag.equals(Converter.getCostumeName(context, COSTUME_CODE_PAJAMAS))) {
                        PajamasActivity pajamasActivity = (PajamasActivity) parentActivity;
                        pajamasActivity.cancelAnimator();

                        pajamasActivity.isOpeningSkipped = true;
                        pajamasActivity.alternateLayout();
                    } else if (dialogTag.equals(Converter.getCostumeName(context, COSTUME_CODE_PLEIONE))) {
                        PleiadesActivity pleiadesActivity = (PleiadesActivity) parentActivity;
                        pleiadesActivity.skipOpening();
                    } else if (dialogTag.equals(Converter.getCostumeName(context, COSTUME_CODE_DINOSAUR))) {
                        DinosaurActivity dinosaurActivity = (DinosaurActivity) parentActivity;
                        dinosaurActivity.skipOpening();
                    }
                }
            } else if (type == DIALOG_TYPE_START_PAJAMAS) {
                PajamasActivity pajamasActivity = (PajamasActivity) getActivity();
                if (pajamasActivity != null)
                    pajamasActivity.startGame();
            } else if (type == DIALOG_TYPE_START_PLEIADES) {
                PleiadesActivity pleiadesActivity = (PleiadesActivity) getActivity();
                if (pleiadesActivity != null)
                    pleiadesActivity.startGame();
            } else if (type == DIALOG_TYPE_START_DINOSAUR) {
                DinosaurActivity dinosaurActivity = (DinosaurActivity) getActivity();
                if (dinosaurActivity != null)
                    dinosaurActivity.startGame();
            } else if (type == DIALOG_TYPE_WIN_PAJAMAS) {
                Activity parentActivity = getActivity();
                if (parentActivity != null) {
                    parentActivity.setResult(GAME_CODE_PAJAMAS);
                    parentActivity.finish();
                }
            } else if (type == DIALOG_TYPE_WIN_PLEIADES) {
                Activity parentActivity = getActivity();
                if (parentActivity != null) {
                    parentActivity.setResult(GAME_CODE_PLEIADES);
                    parentActivity.finish();
                }
            } else if (type == DIALOG_TYPE_WIN_DINOSAUR) {
                Activity parentActivity = getActivity();
                if (parentActivity != null) {
                    parentActivity.setResult(GAME_CODE_DINOSAUR);
                    parentActivity.finish();
                }
            } else if (type == DIALOG_TYPE_TICKET) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.requestTicketRewardedAd();
                }
//                GameFragment gameFragment = (GameFragment) getTargetFragment();
//                if (gameFragment != null)
//                    gameFragment.requestTicketRewardedAd();
            } else if (type == DIALOG_TYPE_HAPPINESS) {
                editor.putBoolean(KEY_IS_HAPPINESS_TUTORIAL_COMPLETED, true);
                editor.apply();
                // TODO start happiness tutorial activity
            } else if (type == DIALOG_TYPE_COMPLETE_HAPPINESS_TUTORIAL){
                editor.putBoolean(KEY_IS_HAPPINESS_TUTORIAL_COMPLETED, true);
                editor.apply();
            }

            dismiss();
        }
    }

    private class onNegativeClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            if (type == DIALOG_TYPE_ESCAPE) {
                Activity parentActivity = getActivity();
                if (parentActivity != null)
                    parentActivity.finish();
            } else {
                if (type == DIALOG_TYPE_HAPPINESS) {
                    // show complete tutorial dialog
                    DefaultDialogFragment defaultDialogFragment = new DefaultDialogFragment(DIALOG_TYPE_COMPLETE_HAPPINESS_TUTORIAL);
                    defaultDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), Integer.toString(DIALOG_TYPE_COMPLETE_HAPPINESS_TUTORIAL));
                }
                dismiss();
            }
        }
    }
}
