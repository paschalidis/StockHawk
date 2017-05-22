package com.udacity.stockhawk.ui;


import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int loader_id = 1;
    private LineChartView lineChartView;
    private String stockName;
    private TextView StockName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        stockName = getIntent().getStringExtra("stockname");
        Bundle bundleForLoader = new Bundle();
        bundleForLoader.putString("symbol", stockName);
        lineChartView  = (LineChartView) findViewById(R.id.lcv_stock_history);
        getSupportLoaderManager().initLoader(loader_id, bundleForLoader, this);
        StockName = (TextView) findViewById(R.id.test);
        StockName.setText(stockName + " Historical Data");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                Contract.Quote.COLUMN_SYMBOL + " = ?",
                new String[]{stockName}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        String label = data.getString(data.getColumnIndexOrThrow(Contract.Quote.COLUMN_HISTORY));
        Map<String, Float> historical_data = processHistoricalData(label);
        Map.Entry<String, Float> entry = historical_data.entrySet().iterator().next();

        if(data.getCount() != 0)
            renderChart(historical_data);

    }
    public void renderChart(Map<String,Float> data) {
        LineSet lineSet = new LineSet();
        float minimumPrice = Float.MAX_VALUE;
        float maximumPrice = Float.MIN_VALUE;

        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String millisecond = (String) pair.getKey();
            String date =  DateFormat.format("MMM d", Long.parseLong(millisecond)).toString();
            lineSet.addPoint(date, (Float) pair.getValue());
            minimumPrice = Math.min(minimumPrice, (Float) pair.getValue());
            maximumPrice = Math.max(maximumPrice, (Float) pair.getValue());
            it.remove();
        }


        lineSet.setColor(Color.parseColor("#758cbb"))
                .setFill(Color.parseColor("#2d374c"))
                .setDotsColor(Color.parseColor("#758cbb"))
                .setThickness(4)
                .setDashed(new float[]{10f, 10f});


        lineChartView.setBorderSpacing(Tools.fromDpToPx(15))
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#6a84c3"))
                .setXAxis(true)
                .setYAxis(true)
                .setAxisBorderValues(Math.round(Math.max(0f, minimumPrice - 5f)), Math.round(maximumPrice + 5f))
                .addData(lineSet);

        Animation anim = new Animation();

        if (lineSet.size() > 1)
            lineChartView.show(anim);
        else
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public Map<String, Float> processHistoricalData(String data){
        String[] rows = data.split("\\n");
        Map<String, Float> values = new HashMap<String, Float>();
        for(int i = 0; i < 8;i++){
            String[] separated_row = rows[i].split(",");
//           String date = DateFormat.format("MMM d", Long.parseLong(separated_row[0])).toString();

            values.put(separated_row[0], Float.parseFloat(separated_row[1]));
        }
        Map<String, Float> treeMap = new TreeMap<String, Float>(values);
        return treeMap;
    }
}
