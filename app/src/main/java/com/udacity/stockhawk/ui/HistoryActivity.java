package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
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

    public final static String STOCK_SYMBOL = "stock_symbol";
    public final static String STOCK_PRICE = "stock_price";
    public final static String STOCK_HISTORY = "stock_history";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if(intent != null){
            if(intent.hasExtra(STOCK_SYMBOL)){
                mStockLabel.setText(intent.getStringExtra(STOCK_SYMBOL));
            }
            if(intent.hasExtra(STOCK_PRICE)){
                mStockPrice.setText(intent.getStringExtra(STOCK_PRICE));
            }
        }

    }
}
