package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract.Quote;
import com.udacity.stockhawk.ui.HistoryActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class StockWidgetService extends RemoteViewsService {

    private final DecimalFormat dollarFormat;
    private final DecimalFormat dollarFormatWithPlus;

    public StockWidgetService(){
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new RemoteViewsFactory() {

            private Cursor data = null;


            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(Quote.URI,
                        null,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION
                        || data == null
                        || !(data.moveToPosition(position))) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.list_item_quote);

                // Get stock data from cursor
                String symbol = data.getString(Quote.POSITION_SYMBOL);
                float priceFloat = data.getFloat(Quote.POSITION_PRICE);
                float absoluteChange = data.getFloat(Quote.POSITION_ABSOLUTE_CHANGE);
                String history = data.getString(Quote.POSITION_HISTORY);

                // Format Values
                String change = dollarFormatWithPlus.format(absoluteChange);
                String price = dollarFormat.format(priceFloat);

                // Set stock data to view
                views.setTextViewText(R.id.symbol, symbol);
                views.setTextViewText(R.id.price, price);
                views.setTextViewText(R.id.change, change);

                // Set Background to change view
                if(absoluteChange > 0){
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }

                // Create Intent to history activity
                final Intent intentHistory = new Intent();
                intentHistory.putExtra(HistoryActivity.STOCK_SYMBOL, symbol);
                intentHistory.putExtra(HistoryActivity.STOCK_PRICE, price);
                intentHistory.putExtra(HistoryActivity.STOCK_HISTORY, history);

                views.setOnClickFillInIntent(R.id.stock, intentHistory);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position)) {
                    return data.getLong(Quote.POSITION_ID);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }
        };
    }
}
