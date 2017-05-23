package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

    private LineChartView lineChartView;

    public final static String STOCK_SYMBOL = "stock_symbol";
    public final static String STOCK_PRICE = "stock_price";
    public final static String STOCK_HISTORY = "stock_history";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        lineChartView = (LineChartView) findViewById(R.id.lcv_history_data);

        Intent intent = getIntent();
        if(intent != null){
            if(intent.hasExtra(STOCK_SYMBOL)){
                mStockLabel.setText(intent.getStringExtra(STOCK_SYMBOL));
            }
            if(intent.hasExtra(STOCK_PRICE)){
                mStockPrice.setText(intent.getStringExtra(STOCK_PRICE));
            }
            if(intent.hasExtra(STOCK_HISTORY)){
                String stockHistory = intent.getStringExtra(STOCK_HISTORY);
                Map<String, Float> chartData = processHistoricalData(stockHistory);
                renderChart(chartData);
            }
        }
    }

    private void renderChart(Map<String,Float> data) {
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

    private Map<String, Float> processHistoricalData(String data){
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
