package com.udacity.stockhawk.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.udacity.stockhawk.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryActivity extends AppCompatActivity {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tv_stock_label)
    TextView mStockLabel;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tc_stock_price)
    TextView mStockPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        mStockLabel.setText("Yahoo");
        mStockPrice.setText("$65.000");
    }
}
