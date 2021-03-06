package com.dariaemacs.cmoney;


import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "Main";

    NfcAdapter nfc = null;
    DatabaseHelper db_helper = null;
    SQLiteDatabase db = null;

    boolean isExist = false;

    long CARD_ID = -1;
    long VALUE = 10;

    TextView cardView;
    TextView dbCountView;
    TextView dbRowsView;

    ImageRadioGroup radioButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_main2);

        nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc == null) {
            Toast.makeText(MainActivity.this, "Your device doesn't support adapter",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        initViews();
        initDB();
        showDB();
    }

    void initViews() {
        cardView = (TextView) findViewById(R.id.card_id);
        cardView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);

        dbCountView = (TextView) findViewById(R.id.db_count);
        dbRowsView = (TextView) findViewById(R.id.db_rows);

        radioButtons = new ImageRadioGroup(this);
    }

    void initDB(){
        db_helper = new DatabaseHelper(this);
        db = db_helper.getWritableDatabase();
    }

    private void showDB(){
        if(db == null){
            return;
        }
        Cursor result = db.query(CardEntry.TABLE_NAME, null, null, null,
                                null, null, null, String.valueOf(5));
        long cnt = DatabaseUtils.queryNumEntries(db, CardEntry.TABLE_NAME);

        dbCountView.setText("count: " + String.valueOf(cnt));
        if(cnt == 0){
            return;
        }
        if(result != null){
            result.moveToFirst();

            StringBuilder sb = new StringBuilder("");
            if (CARD_ID != -1) {
                String last = getValue();
                sb.append("Last row: " + CARD_ID + " -> " + last + " \n\n");
            }
            while (true) {
                String id = result.getString(0);
                String value = result.getString(1);

                sb.append("row: " + String.valueOf(id) + " -> " + String.valueOf(value) + " \n");

                if (result.isLast()) {
                    break;
                } else {
                    result.moveToNext();
                }
            }
            dbRowsView.setText(sb.toString());
            result.close();
        }
    }


    protected void onRadioButtonClicked(View view){
        radioButtons.radioClicked(view);
    }

    protected void onImageButtonClicked(View view) {
        radioButtons.imageClicked(view);
    }


    void setValue(long value){
        VALUE = value;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] arr = tag.getId();
            long index = new BigInteger(arr).longValue();

            CARD_ID = index;
            cardView.setText(String.valueOf(index));

            String card_item = getValue();
            if (!card_item.isEmpty()) {
                VALUE = Integer.valueOf(card_item);
                isExist = true;

                showMessage(card_item);
            }else{
                isExist = false;
            }
        }
    }

    void showMessage(String card_item) {
        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        ImageView view = new ImageView(this);
        int imageRes = radioButtons.getImageById(card_item);
        if (imageRes == 0) {
            view.setImageResource(R.drawable.credits_none);
        } else {
            view.setImageResource(radioButtons.getImageById(card_item));
        }
        toast.setView(view);
        toast.show();
    }


    protected void onAddButton(View view) {
        if (CARD_ID == -1) {
            Toast.makeText(MainActivity.this, "Card id is not found. Value is " + String.valueOf(VALUE),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isExist) {
            ContentValues values = new ContentValues();
            values.put(CardEntry.COLUMN_NAME_ENTRY_ID, CARD_ID);
            values.put(CardEntry.COLUMN_NAME_VALUE, VALUE);

            db.insert(CardEntry.TABLE_NAME, null, values);
            Toast.makeText(this, "Inserting..." + " ID #" + String.valueOf(CARD_ID) +
                    " VALUE #" + String.valueOf(VALUE) + " row id: ",
                    Toast.LENGTH_LONG).show();

            showDB();
        } else {
            Toast.makeText(this, "Inserting..." + " ID #" + String.valueOf(CARD_ID) +
                    " this primary key EXISTS!", Toast.LENGTH_LONG).show();
        }
    }

    protected void onDeleteButton(View v) {
        int delCount = db.delete(CardEntry.TABLE_NAME, " id = " + String.valueOf(CARD_ID), null);

        if (delCount > 0) {
            Toast.makeText(MainActivity.this, "Deleting... id #" + cardView.getText(),
                    Toast.LENGTH_LONG).show();
            CARD_ID = -1;
            cardView.setText("");

            showDB();
        } else {
            Toast.makeText(MainActivity.this, "There is nothing to delete... id #" + cardView.getText(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private String getValue() {
        String value_item = "";
        // Для выполнения запроса: SELECT id FROM cards WHERE id = CARD_ID
        // db.query(
        // "cards"  имя таблицы ,
        //  new String[] { "value" } название колонки, которую нужно вернуть, null, если вернуть все,
        //  "id = ?" where или selection ,
        //  new String[] { "1181447569228161" } это аргументы запроса, которые заместят ? ,
        //  null  groupBy ,
        //  null  having  ,
        //  null  orderBy ,
        //  null  limit
        //);

        String[] args = new String[]{String.valueOf(CARD_ID)};
        Cursor result = db.query(CardEntry.TABLE_NAME, CardEntry.RETURN_VALUE_COLUMN,
                CardEntry.SELECTION, args, null, null, null, null);
        if (result != null) {
            if (result.getCount() > 0) {
                result.moveToFirst();
                value_item = result.getString(result.getColumnIndex(CardEntry.COLUMN_NAME_VALUE));
            }
            result.close();
        }
        return value_item;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter discovery = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] tagFilters = new IntentFilter[]{discovery};
        Intent i = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        if (nfc != null) {
            nfc.enableForegroundDispatch(this, pi, tagFilters, null);
        }
    }

    @Override
    protected void onPause(){
        if(isFinishing()) {
            if (nfc != null) {
                nfc.disableForegroundDispatch(this);
            }
            nfc = null;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
        db = null;

        db_helper = null;

        cardView = null;
        dbCountView = null;
        dbRowsView = null;

        radioButtons = null;
    }
}
