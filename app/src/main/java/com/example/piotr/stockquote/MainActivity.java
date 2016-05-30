package com.example.piotr.stockquote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public final static String STOCK_SYMBOL = "com.example.piotr.stockquote.STOCK";

    private SharedPreferences stockSymbolsEntered;

    private TableLayout stockTableScrollView;

    private EditText stockSymbolEditText;

    Button enterStockSymbolButton;
    Button deleteStocksButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stockSymbolsEntered = getSharedPreferences("stocklist", MODE_PRIVATE);

        stockTableScrollView = (TableLayout) findViewById(R.id.stockTableScrollView);

        stockSymbolEditText = (EditText) findViewById(R.id.stockSymbolEditText);

        enterStockSymbolButton = (Button) findViewById(R.id.enterStockSymbolButton);
        deleteStocksButton = (Button) findViewById(R.id.deleteStocksButton);

        enterStockSymbolButton.setOnClickListener(enterStockSymbolButtonListener);
        deleteStocksButton.setOnClickListener(deleteStocksButtonListener);

        updateSavedStockList(null);

    }

    private void updateSavedStockList(String newStockSymbol){

        String[] stocks = stockSymbolsEntered.getAll().keySet().toArray(new String[0]);

        Arrays.sort(stocks, String.CASE_INSENSITIVE_ORDER);

        if (newStockSymbol != null){

            insertStockInScrollView(newStockSymbol, Arrays.binarySearch(stocks, newStockSymbol));

        } else{

            for(int i=0; i < stocks.length; i++){

                insertStockInScrollView(stocks[i], i);
            }
        }
    }

    private void saveStockSymbol(String newStock){

        String isTheStockNew = stockSymbolsEntered.getString(newStock, null);

        SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();

        preferencesEditor.putString(newStock, newStock);
        preferencesEditor.apply();

        if(isTheStockNew == null){

            updateSavedStockList(newStock);
        }
    }

    private void insertStockInScrollView(String stock, int arrayIndex){

        LayoutInflater inflater = (LayoutInflater) getSystemService((Context.LAYOUT_INFLATER_SERVICE));

        View newStockRow = inflater.inflate(R.layout.stock_quote_row, null);

        TextView newStockTextView = (TextView) newStockRow.findViewById(R.id.stockSymbolTextView);

        newStockTextView.setText(stock);

        Button stockQuoteButton = (Button) newStockRow.findViewById(R.id.stockQuoteButton);

        stockQuoteButton.setOnClickListener(getStockActivityListener);

        Button quoteFromWebButton = (Button) newStockRow.findViewById(R.id.quoteFromWebButton);

        quoteFromWebButton.setOnClickListener(getStockFromWebsiteListener);

        stockTableScrollView.addView(newStockRow, arrayIndex);
    }

     public View.OnClickListener enterStockSymbolButtonListener = new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             if(stockSymbolEditText.getText().length() > 0){

                 saveStockSymbol(stockSymbolEditText.getText().toString());

                 stockSymbolEditText.setText("");

                 InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                    imm.hideSoftInputFromWindow(stockSymbolEditText.getWindowToken(),0);

             }else{

                 AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                 builder.setTitle(R.string.invalid_stock_symbol);

                 builder.setPositiveButton(R.string.ok, null);

                 builder.setMessage(R.string.missing_stock_symbol);

                 AlertDialog theAlertDialog = builder.create();
                 theAlertDialog.show();
             }
         }
     };

        private void deleteAllStocks(){

            stockTableScrollView.removeAllViews();

    }

    public View.OnClickListener deleteStocksButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            deleteAllStocks();

            SharedPreferences.Editor preferencesEditor = stockSymbolsEntered.edit();

            preferencesEditor.clear();
            preferencesEditor.apply();

        }
    };

    public View.OnClickListener getStockActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            TableRow tableRow = (TableRow) v.getParent();

            TextView stockTextView = (TextView) tableRow.findViewById(R.id.stockSymbolTextView);

            String stockSymbol = stockTextView.getText().toString();

            Intent intent = new Intent(MainActivity.this, StockInfoActivity.class);

            intent.putExtra(STOCK_SYMBOL, stockSymbol);

            startActivity(intent);

        }
    };

    public View.OnClickListener getStockFromWebsiteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            TableRow tableRow = (TableRow) v.getParent();

            TextView stockTextView = (TextView) tableRow.findViewById(R.id.stockSymbolTextView);

            String stockSymbol = stockTextView.getText().toString();

            String stockURL = getString(R.string.yahoo_stock_url) + stockSymbol;

            Intent getStockWebPage = new Intent (Intent.ACTION_VIEW, Uri.parse(stockURL));

            startActivity(getStockWebPage);
        }
    };

}
