package com.example.hciproject;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

public class widgetProvider extends AppWidgetProvider {
    private static final String GO_LEFT_TAG = "Left click";
    private static final String GO_RIGHT_TAG = "Right click";
    private static final String GO_TO_TT_TAG = "Go to timetable";
    private static final String GO_TO_ASSIGNMENTS_TAG = "Go to assignments";
    public static final String MODE_TIMETABLE = "timetable";
    public static final String MODE_ASSIGNMENTS = "assignments";
    private static int day;
    SharedPreferences prefs;
    String daysList[] = {"Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday","Sunday"};
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int appWidgetId:appWidgetIds){
            prefs = context.getSharedPreferences("com.example.hciproject",Context.MODE_PRIVATE);
            day=prefs.getInt("day", 0);

            Intent serviceIntent=new Intent(context,widgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
            serviceIntent.putExtra("Day",0);
            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews views=new RemoteViews(context.getPackageName(),R.layout.widget_layout);
            views.setRemoteAdapter(R.id.listView,serviceIntent);
            views.setOnClickPendingIntent(R.id.left,getPendingSelfIntent(context, GO_LEFT_TAG));
            views.setOnClickPendingIntent(R.id.right,getPendingSelfIntent(context, GO_RIGHT_TAG));
            views.setOnClickPendingIntent(R.id.option_timetable,getPendingSelfIntent(context, GO_TO_TT_TAG));
            views.setOnClickPendingIntent(R.id.option_assignment,getPendingSelfIntent(context, GO_TO_ASSIGNMENTS_TAG));
            views.setEmptyView(R.id.listView,R.id.emptyTextView);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }
//TODO: If no classes then show "No events"
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        prefs = context.getSharedPreferences("com.example.hciproject",Context.MODE_PRIVATE);
        day=prefs.getInt("day", 0);
        if(intent.getAction().equals(GO_LEFT_TAG)){
            if(day==0) day=6;
            else day--;
            prefs.edit().putInt("day", day).commit();
            updateTT(context, intent, MODE_TIMETABLE);
        }
        else if( intent.getAction().equals(GO_RIGHT_TAG)){
            if(day==6) day=0;
            else day++;
            prefs.edit().putInt("day", day).commit();
            updateTT(context,intent, MODE_ASSIGNMENTS);

        }
        else if(intent.getAction().equals(GO_TO_TT_TAG)){
            updateTT(context,intent, MODE_TIMETABLE);
        }
        else if(intent.getAction().equals(GO_TO_ASSIGNMENTS_TAG)){
            updateTT(context,intent, MODE_ASSIGNMENTS);
        }
    }
    void updateTT(Context context, Intent intent, String mode){

        RemoteViews views=new RemoteViews(context.getPackageName(),R.layout.widget_layout);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds= appWidgetManager.getAppWidgetIds(new ComponentName(context, widgetProvider.class));

        Intent serviceIntent=new Intent(context,widgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetIds[0]);
        serviceIntent.putExtra("Day",day);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.listView,serviceIntent);
        views.setTextViewText(R.id.day, daysList[day]);
        if(mode.equals("timetable")){
            views.setViewVisibility(R.id.day_layout, View.VISIBLE);
            serviceIntent.putExtra("mode", MODE_TIMETABLE);
        }
        else if(mode.equals("assignments")){
            views.setViewVisibility(R.id.day_layout, View.GONE);
            serviceIntent.putExtra("mode", MODE_ASSIGNMENTS);
        }

        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
