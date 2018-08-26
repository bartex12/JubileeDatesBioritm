package ru.bartex.jubelee_dialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonTable;

public class FindDatesActivity extends AppCompatActivity {

    public final String TAG = "33333";
    ListView mListView;
    private SharedPreferences prefSetting;
    int sort = 1;  //Сортировка: 1-поимени возр, 2- по имени убыв,3-по дате возр, 4 - по дате убыв
    boolean isSort = false; //Список отсортирован?
    ArrayList<Map<String, Object>> data = new ArrayList<>();
    Map mMap;
    final String NAME1 = "ru.bartex.jubelee_dialog.currentName1";
    final String NAME2 = "ru.bartex.jubelee_dialog.currentName2";
    final String PAST_DAYS = "ru.bartex.jubelee_dialog.currentPastDays";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_dates);

        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setHomeButtonEnabled(true);

        //получаем файл с настройками для приложения
        prefSetting = PreferenceManager.getDefaultSharedPreferences(this);
        sort = Integer.parseInt(prefSetting.getString("ListSort", "1"));
        isSort = prefSetting.getBoolean("cbSort", false);

        mListView = (ListView)findViewById(R.id.listViewDialogJoint);
        //находим View, которое выводит текст Список пуст
        View empty = findViewById(R.id.emptyList);
        mListView.setEmptyView(empty);

        //получаем экземпляр PersonDbHelper для работы с базой данных
        PersonDbHelper mPersonDbHelper = new PersonDbHelper(this);
        Cursor mCursor = mPersonDbHelper.getAllData();
        int idColumnIndex = mCursor.getColumnIndex(PersonTable._ID);
        int nameColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_NAME);
            // идём к первой строке
        mCursor.moveToFirst() ;
            //получаем id первой строки
            int currentID_1 = mCursor.getInt(idColumnIndex);
            String currentName1 = mCursor.getString(nameColumnIndex);
            //идём к следующей строке
            while (mCursor.moveToNext()) {
                //получаем id последующих строк
                int currentID_2 = mCursor.getInt(idColumnIndex);
                String currentName2 = mCursor.getString(nameColumnIndex);
                // количество прожитых дней  на двоих
                long forTwo_Days = (mPersonDbHelper.getMillisForTwo(currentID_1, currentID_2))/86400000;
                Log.d(TAG, "FindDatesActivity  " +
                        "  currentID_1 = " + currentID_1 + "  currentName1 = " + currentName1 +
                        "  currentID_2 = " + currentID_2 + "  currentName2 = " + currentName2 +
                        "  forTwo_Days = " + forTwo_Days);

                mMap = new HashMap();
                mMap.put(NAME1,currentName1);
                mMap.put(NAME2, currentName2);
                mMap.put(PAST_DAYS,forTwo_Days);
                data.add(mMap);
            }

        //получаем курсор с отсортированными в соответствии с настройками данными
        //Cursor mCursorAll = mPersonDbHelper.getCursorWithSort(true,4);

        // формируем столбцы сопоставления
        String[] from = new String[]{NAME1, PAST_DAYS, NAME2};
        int[] to = new int[]{R.id.name_list_one, R.id.past_Days_joint, R.id.name_list_two};
        // создааем адаптер и настраиваем список
        SimpleAdapter scAdapter = new SimpleAdapter(this,data, R.layout.list_name_for_two,from, to);
        mListView.setAdapter(scAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_dates,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                Log.d(TAG, "Домой");
                Intent intent = new Intent(this,PersonsListActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_settings:
                Log.d(TAG, "OptionsItem = action_settings");
                Intent intentSettings = new Intent(this, PrefActivity.class);
                startActivity(intentSettings);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
