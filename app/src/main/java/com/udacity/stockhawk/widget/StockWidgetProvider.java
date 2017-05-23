package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.MainActivity;

public class StockWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        String symbol = "GOOG";
        String price = "$56.00";

        //Perform this loop procedure for each Stock widget
        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.widget_stock;
            RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);

            //Add Data To Remote View
            views.setTextViewText(R.id.tv_widget_stock_symbol, symbol);
            views.setTextViewText(R.id.tv_widget_stock_price, price);

            //Create Intent to launch MainActivity
            Intent launchIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.tv_widget_label, pendingIntent);

            //Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}
