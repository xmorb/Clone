package com.walkerit.lgirmagicremote;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.Map;

/** Main full-screen remote interface. */
public final class MainActivity extends Activity {
    private static final int BUTTON_MARGIN_DP = 4;
    private static final long REPEAT_START_DELAY_MS = 350L;
    private static final long REPEAT_INTERVAL_MS = 140L;

    private IrTransmitter transmitter;
    private Vibrator vibrator;
    private final Handler repeatHandler = new Handler(Looper.getMainLooper());
    private Runnable repeatRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transmitter = new IrTransmitter(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        setContentView(buildInterface());
    }

    @Override
    protected void onStop() {
        stopRepeating();
        super.onStop();
    }

    private View buildInterface() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(Color.rgb(18, 18, 18));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(12), dp(16), dp(12), dp(24));
        scrollView.addView(root, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT));

        TextView title = new TextView(this);
        title.setText(R.string.app_name);
        title.setTextColor(Color.WHITE);
        title.setTextSize(24f);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(8));
        root.addView(title);

        TextView status = new TextView(this);
        status.setText(transmitter.isAvailable()
                ? R.string.ir_ready
                : R.string.ir_unavailable);
        status.setTextColor(transmitter.isAvailable() ? Color.rgb(110, 220, 140) : Color.rgb(255, 120, 120));
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 0, 0, dp(12));
        root.addView(status);

        addRow(root,
                standardButton("POWER", LgCommand.POWER, false),
                standardButton("INPUT", LgCommand.INPUT, false),
                standardButton("SETTINGS", LgCommand.SETTINGS, false));

        addRow(root,
                standardButton("HOME", LgCommand.HOME, false),
                standardButton("GUIDE", LgCommand.GUIDE, false),
                standardButton("INFO", LgCommand.INFO, false));

        addSectionLabel(root, "Navigation");
        addRow(root, spacer(), standardButton("▲", LgCommand.UP, true), spacer());
        addRow(root,
                standardButton("◀", LgCommand.LEFT, true),
                standardButton("OK", LgCommand.OK, false),
                standardButton("▶", LgCommand.RIGHT, true));
        addRow(root, spacer(), standardButton("▼", LgCommand.DOWN, true), spacer());
        addRow(root,
                standardButton("BACK", LgCommand.BACK, false),
                standardButton("EXIT", LgCommand.EXIT, false),
                standardButton("Q.MENU", LgCommand.Q_MENU, false));

        addSectionLabel(root, "Volume and channel");
        addRow(root,
                standardButton("VOL +", LgCommand.VOLUME_UP, true),
                standardButton("MUTE", LgCommand.MUTE, false),
                standardButton("CH +", LgCommand.CHANNEL_UP, true));
        addRow(root,
                standardButton("VOL −", LgCommand.VOLUME_DOWN, true),
                standardButton("LIST", LgCommand.LIST, false),
                standardButton("CH −", LgCommand.CHANNEL_DOWN, true));

        addSectionLabel(root, "Numbers");
        Map<String, LgCommand> numbers = new LinkedHashMap<>();
        numbers.put("1", LgCommand.NUM_1);
        numbers.put("2", LgCommand.NUM_2);
        numbers.put("3", LgCommand.NUM_3);
        numbers.put("4", LgCommand.NUM_4);
        numbers.put("5", LgCommand.NUM_5);
        numbers.put("6", LgCommand.NUM_6);
        numbers.put("7", LgCommand.NUM_7);
        numbers.put("8", LgCommand.NUM_8);
        numbers.put("9", LgCommand.NUM_9);
        int index = 0;
        Button[] row = new Button[3];
        for (Map.Entry<String, LgCommand> entry : numbers.entrySet()) {
            row[index % 3] = standardButton(entry.getKey(), entry.getValue(), false);
            index++;
            if (index % 3 == 0) {
                addRow(root, row[0], row[1], row[2]);
                row = new Button[3];
            }
        }
        addRow(root,
                standardButton("TV/RADIO", LgCommand.TV_RADIO, false),
                standardButton("0", LgCommand.NUM_0, false),
                standardButton("INPUT", LgCommand.INPUT, false));

        addSectionLabel(root, "Color and playback");
        addRow(root,
                coloredButton("RED", LgCommand.RED, Color.rgb(145, 35, 35)),
                coloredButton("GREEN", LgCommand.GREEN, Color.rgb(35, 115, 55)),
                coloredButton("YELLOW", LgCommand.YELLOW, Color.rgb(145, 125, 25)),
                coloredButton("BLUE", LgCommand.BLUE, Color.rgb(35, 70, 145)));
        addRow(root,
                standardButton("⏪", LgCommand.REWIND, true),
                standardButton("▶", LgCommand.PLAY, false),
                standardButton("⏸", LgCommand.PAUSE, false),
                standardButton("⏩", LgCommand.FAST_FORWARD, true));
        addRow(root,
                spacer(),
                standardButton("■", LgCommand.STOP, false),
                standardButton("REC", LgCommand.RECORD, false),
                spacer());

        TextView note = new TextView(this);
        note.setText(R.string.magic_remote_limitations);
        note.setTextColor(Color.LTGRAY);
        note.setTextSize(12f);
        note.setPadding(dp(4), dp(16), dp(4), 0);
        root.addView(note);

        return scrollView;
    }

    private Button standardButton(String label, LgCommand command, boolean repeatable) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextColor(Color.WHITE);
        button.setTextSize(14f);
        button.setAllCaps(false);
        button.setBackgroundResource(R.drawable.remote_button);
        attachCommand(button, command, repeatable);
        return button;
    }

    private Button coloredButton(String label, LgCommand command, int color) {
        Button button = standardButton(label, command, false);
        button.setBackgroundColor(color);
        return button;
    }

    private Button spacer() {
        Button button = new Button(this);
        button.setVisibility(View.INVISIBLE);
        return button;
    }

    private void attachCommand(Button button, LgCommand command, boolean repeatable) {
        if (!repeatable) {
            button.setOnClickListener(view -> transmit(command));
            return;
        }

        button.setOnTouchListener((view, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                transmit(command);
                startRepeating(command);
                return true;
            }
            if (event.getActionMasked() == MotionEvent.ACTION_UP
                    || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
                stopRepeating();
                view.performClick();
                return true;
            }
            return false;
        });
    }

    private void startRepeating(LgCommand command) {
        stopRepeating();
        repeatRunnable = new Runnable() {
            @Override
            public void run() {
                transmit(command);
                repeatHandler.postDelayed(this, REPEAT_INTERVAL_MS);
            }
        };
        repeatHandler.postDelayed(repeatRunnable, REPEAT_START_DELAY_MS);
    }

    private void stopRepeating() {
        if (repeatRunnable != null) {
            repeatHandler.removeCallbacks(repeatRunnable);
            repeatRunnable = null;
        }
    }

    private void transmit(LgCommand command) {
        try {
            transmitter.transmit(command);
            vibrate();
        } catch (IllegalStateException exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
            stopRepeating();
        }
    }

    private void vibrate() {
        if (vibrator == null || !vibrator.hasVibrator()) {
            return;
        }
        vibrator.vibrate(VibrationEffect.createOneShot(18L, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    private void addSectionLabel(LinearLayout root, String label) {
        TextView textView = new TextView(this);
        textView.setText(label);
        textView.setTextColor(Color.LTGRAY);
        textView.setTextSize(14f);
        textView.setPadding(dp(4), dp(14), dp(4), dp(4));
        root.addView(textView);
    }

    private void addRow(LinearLayout root, Button... buttons) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        for (Button button : buttons) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(52), 1f);
            params.setMargins(dp(BUTTON_MARGIN_DP), dp(BUTTON_MARGIN_DP),
                    dp(BUTTON_MARGIN_DP), dp(BUTTON_MARGIN_DP));
            row.addView(button, params);
        }
        root.addView(row, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
