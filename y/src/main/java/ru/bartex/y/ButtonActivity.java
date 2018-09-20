package ru.bartex.y;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import static android.provider.BaseColumns._ID;
import static ru.bartex.y.DBHelper.COLUMN_CITY;
import static ru.bartex.y.DBHelper.COLUMN_NAME;
import static ru.bartex.y.DBHelper.TABLE_NAME;

public class ButtonActivity extends AppCompatActivity {

    public static final String TAG = "33333";

    Button mButton;
    Button mButton2;
    Button mButton3;
    Button mButton4;
    Button mButton5;
    ListView mListViewSelect;

    DBHelper mDBHelper;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);

        mDBHelper = new DBHelper(getApplicationContext());

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
                        finish();
                        return true;
                }
                return false;
            }
        });

        mListViewSelect = findViewById(R.id.listViewSelect);

        mButton = findViewById(R.id.buttonFirst);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "ButtonActivity onClick buttonFirst");

                //сырой запрос rawQuery
                db = mDBHelper.getReadableDatabase();
                String sel = "SELECT  * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME+ " =?";
                cursor = db.rawQuery(sel, new String[]{"Криво"});

/*
                //запрос query - вариант 1
                cursor = db.query(TABLE_NAME,
                        new String[]{_ID, COLUMN_NAME, COLUMN_CITY},
                        COLUMN_NAME + " ='Криво'",
                        null,null, null, null, null );
 */

/*
                 //запрос query - вариант 2
                cursor = db.query(TABLE_NAME,
                        new String[]{_ID, COLUMN_NAME, COLUMN_CITY},
                        COLUMN_NAME + " =?",
                        new String[]{"Криво"},
                        null, null, null, null );
*/

                //запрос к столбцу COLUMN_CITY через оператор LIKE в методе searchInSQLite
               // cursor = mDBHelper.searchInSQLite("Москва");

                Log.d(TAG, "ButtonActivity onClick cursor = " + cursor.getCount());

                //делаем массивы для адпаптера и подключаем к списку
                String[] from = {COLUMN_NAME, COLUMN_CITY};
                int[] to = new int[]{android.R.id.text1, android.R.id.text2};
                SimpleCursorAdapter cursorAdapter =
                        new SimpleCursorAdapter(getApplicationContext(),
                                 android.R.layout.two_line_list_item, cursor, from, to, 0);
                mListViewSelect.setAdapter(cursorAdapter);

            }
        });

         mButton2 = findViewById(R.id.button2);
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "ButtonActivity onClick button2");
            }
        });
         mButton3 = findViewById(R.id.button3);
        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
         mButton4 = findViewById(R.id.button4);
        mButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
         mButton5 = findViewById(R.id.button5);
        mButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        db = mDBHelper.getWritableDatabase();
        cursor = db.rawQuery("select * from " + TABLE_NAME, null);
                /*
                cursor = db.query(TABLE_NAME,
                        new String[]{_ID, COLUMN_NAME, COLUMN_CITY},
                        COLUMN_NAME + " =Криво ",
                        null,null, null, null, null );
                */
        Log.d(TAG, "ButtonActivity onClick cursor = " + cursor.getCount());

        String[] from = {COLUMN_NAME, COLUMN_CITY};
        int[] to = new int[]{android.R.id.text1, android.R.id.text2};
        SimpleCursorAdapter cursorAdapter =
                new SimpleCursorAdapter(getApplicationContext(),
                        android.R.layout.two_line_list_item, cursor, from, to, 0);
        mListViewSelect.setAdapter(cursorAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDBHelper.close();
        db.close();
    }


}
