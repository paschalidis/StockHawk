package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.udacity.stockhawk.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryActivity extends AppCompatActivity {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_stock_label)
    TextView mStockLabel;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tc_stock_price)
    TextView mStockPrice;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_history_error)
    TextView mErrorView;

    private LineChartView mLineChartView;

    public final static String STOCK_SYMBOL = "stock_symbol";
    public final static String STOCK_PRICE = "stock_price";
    public final static String STOCK_HISTORY = "stock_history";

    /**
     * The quantity of history data to show in chart
     */
    private final static int HISTORY_DATA_QUANTITY = 8;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        mLineChartView = (LineChartView) findViewById(R.id.lcv_history_data);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(STOCK_SYMBOL)) {
                mStockLabel.setText(intent.getStringExtra(STOCK_SYMBOL));
            }
            if (intent.hasExtra(STOCK_PRICE)) {
                mStockPrice.setText(intent.getStringExtra(STOCK_PRICE));
            }
            if (intent.hasExtra(STOCK_HISTORY)) {
                String stockHistory = intent.getStringExtra(STOCK_HISTORY);
                Map<String, Float> chartData = parseHistoryData(stockHistory);
                if (chartData.isEmpty()) {
                    mErrorView.setVisibility(View.VISIBLE);
                } else {
                    mErrorView.setVisibility(View.GONE);
                    fillChart(chartData);
                }
            }
        }
    }

    /**
     * Parse history data to TreeMap with key, value
     * with stock timestamp and price
     *
     * @param historyData String The History Data To Parse
     * @return TreeMap<> with values
     */
    private Map<String, Float> parseHistoryData(String historyData) {
        final String historyDataSeparator = "\\n";
        final String rowSeparator = ",";
        final int timestampIndex = 0;
        final int priceIndex = 1;

        String[] rows = historyData.split(historyDataSeparator);
        Map<String, Float> values = new HashMap<>();

        for (int i = 0; i < HISTORY_DATA_QUANTITY; i++) {
            String[] row = rows[i].split(rowSeparator);
            values.put(row[timestampIndex], Float.parseFloat(row[priceIndex]));
        }

        return new TreeMap<>(values);
    }

    /**
     * Fill The Chart With Data
     *
     * @param chartData Map<String, Float>
     */
    private void fillChart(Map<String, Float> chartData) {

        final String dateFormat = "MMM d";

        LineSet lineSet = new LineSet();
        float minimumPrice = Float.MAX_VALUE;
        float maximumPrice = Float.MIN_VALUE;

        Iterator iterator = chartData.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();

            String timestamp = (String) pair.getKey();
            String date = DateFormat.format(dateFormat, Long.parseLong(timestamp)).toString();

            Float price = (Float) pair.getValue();
            lineSet.addPoint(date, price);

            minimumPrice = Math.min(minimumPrice, price);
            maximumPrice = Math.max(maximumPrice, price);

            iterator.remove();
        }

        int mainColor = ContextCompat.getColor(this, R.color.chart_color_main);
        int fillColor = ContextCompat.getColor(this, R.color.chart_color_fill);
        int labelColor = ContextCompat.getColor(this, R.color.chart_color_label);

        lineSet.setColor(mainColor)
                .setFill(fillColor)
                .setDotsColor(mainColor)
                .setThickness(4)
                .setDashed(new float[]{10f, 10f});

        int xAxisMinValue = Math.round(Math.max(0f, minimumPrice - 5f));
        int xAxisMaxValue = Math.round(maximumPrice + 5f);

        mLineChartView.setBorderSpacing(Tools.fromDpToPx(15))
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(labelColor)
                .setXAxis(true)
                .setYAxis(true)
                .setAxisBorderValues(xAxisMinValue, xAxisMaxValue)
                .addData(lineSet);

        Animation anim = new Animation();
        mLineChartView.show(anim);
    }
}
