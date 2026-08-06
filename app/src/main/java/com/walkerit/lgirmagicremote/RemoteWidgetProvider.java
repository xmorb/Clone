package com.walkerit.lgirmagicremote;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.Map;

/** Home-screen widget exposing the most frequently used television controls. */
public final class RemoteWidgetProvider extends AppWidgetProvider {
    public static final String ACTION_COMMAND = "com.walkerit.lgirmagicremote.WIDGET_COMMAND";
    private static final String EXTRA_COMMAND = "command";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, buildRemoteViews(context));
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (!ACTION_COMMAND.equals(intent.getAction())) {
            return;
        }

        String commandName = intent.getStringExtra(EXTRA_COMMAND);
        if (commandName == null) {
            return;
        }

        try {
            LgCommand command = LgCommand.valueOf(commandName);
            new IrTransmitter(context).transmit(command);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private static RemoteViews buildRemoteViews(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.remote_widget);

        Intent openApp = new Intent(context, MainActivity.class);
        PendingIntent openPendingIntent = PendingIntent.getActivity(
                context,
                1000,
                openApp,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_title, openPendingIntent);

        Map<Integer, LgCommand> controls = new LinkedHashMap<>();
        controls.put(R.id.widget_power, LgCommand.POWER);
        controls.put(R.id.widget_input, LgCommand.INPUT);
        controls.put(R.id.widget_home, LgCommand.HOME);
        controls.put(R.id.widget_up, LgCommand.UP);
        controls.put(R.id.widget_left, LgCommand.LEFT);
        controls.put(R.id.widget_ok, LgCommand.OK);
        controls.put(R.id.widget_right, LgCommand.RIGHT);
        controls.put(R.id.widget_down, LgCommand.DOWN);
        controls.put(R.id.widget_back, LgCommand.BACK);
        controls.put(R.id.widget_volume_up, LgCommand.VOLUME_UP);
        controls.put(R.id.widget_mute, LgCommand.MUTE);
        controls.put(R.id.widget_channel_up, LgCommand.CHANNEL_UP);
        controls.put(R.id.widget_volume_down, LgCommand.VOLUME_DOWN);
        controls.put(R.id.widget_settings, LgCommand.SETTINGS);
        controls.put(R.id.widget_channel_down, LgCommand.CHANNEL_DOWN);

        int requestCode = 1;
        for (Map.Entry<Integer, LgCommand> control : controls.entrySet()) {
            Intent commandIntent = new Intent(context, RemoteWidgetProvider.class);
            commandIntent.setAction(ACTION_COMMAND);
            commandIntent.putExtra(EXTRA_COMMAND, control.getValue().name());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode++,
                    commandIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(control.getKey(), pendingIntent);
        }

        return views;
    }

    public static void refreshAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName provider = new ComponentName(context, RemoteWidgetProvider.class);
        manager.updateAppWidget(provider, buildRemoteViews(context));
    }
}
