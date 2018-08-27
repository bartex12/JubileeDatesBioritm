package ru.bartex.jubelee_dialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonTable;

public class FindDatesActivity extends AppCompatActivity {

    public final String TAG = "33333";
    public static final String ID_SQL = "sqlFindDatesActivity";
    public static final String ID_SQL_SECOND = "sql_srcond_FindDatesActivity";
    public static final String REQUEST_FIND = "request_find"; //риквест код
    long id_sql;
    long id_sql_second;
    int request;
    ListView mListView;
    private SharedPreferences prefSetting;
    int sort = 1;  //Сортировка: 1-поимени возр, 2- по имени убыв,3-по дате возр, 4 - по дате убыв
    boolean isSort = false; //Список отсортирован?
    ArrayList<Map<String, Object>> data = new ArrayList<>();
    Map mMap;

    //получаем экземпляр PersonDbHelper для работы с базой данных
    PersonDbHelper mPersonDbHelper = new PersonDbHelper(this);
    Cursor mCursor;
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

        Intent intent = getIntent();
        id_sql = intent.getLongExtra(ID_SQL,1);
        id_sql_second = intent.getLongExtra(ID_SQL_SECOND,1);
        request = intent.getIntExtra(REQUEST_FIND,3);

        //получаем файл с настройками для приложения
        prefSetting = PreferenceManager.getDefaultSharedPreferences(this);
        sort = Integer.parseInt(prefSetting.getString("ListSort", "1"));
        isSort = prefSetting.getBoolean("cbSort", false);

        mListView = (ListView)findViewById(R.id.listViewDialogJoint);
        //находим View, которое выводит текст Список пуст
        View empty = findViewById(R.id.emptyList);
        mListView.setEmptyView(empty);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //получаем имя из данных адаптера
                Map<String, Object> v =(Map<String, Object>)parent.getItemAtPosition(position);
                String name = (String)v.get(NAME2);
                Log.d(TAG, "onItemClick name = " + name);

                long id_from_name = mPersonDbHelper.getIdFromName(name);
                Log.d(TAG, "onItemClick id_from_name = " + id_from_name);

                Intent intent1 = new Intent();
                if (request == JointActivity.REQUEST_JOINT_FIND) {
                    intent1.putExtra(JointActivity.ID_SQL_second,id_from_name);
                }
                setResult(RESULT_OK,intent1);
                finish();
            }
        });

        mCursor = mPersonDbHelper.getAllData();
        int idColumnIndex = mCursor.getColumnIndex(PersonTable._ID);
        int nameColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_NAME);

        //Для первой выбранной персоны
            Person person = mPersonDbHelper.getPersonObjectData(id_sql);
            String currentName1 = person.getPerson_name();
            //идём к следующей строке
            while (mCursor.moveToNext()) {
                //получаем id последующих строк
                long id_sql_next1 = mCursor.getLong(idColumnIndex);
                String currentNext1 = mCursor.getString(nameColumnIndex);
                // количество прожитых дней  на двоих
                long forTwo_Days_1 = (mPersonDbHelper.getMillisForTwo(id_sql, id_sql_next1))/86400000;
                Log.d(TAG, "FindDatesActivity  " +
                        "  currentID_1 = " + id_sql + "  currentName1 = " + currentName1 +
                        "  currentID_next = " + id_sql_next1 + "  currentNameNext = " + currentNext1 +
                        "  forTwo_Days = " + forTwo_Days_1);
                //если имена не совпадают, пишем в ArrayList
                if (!currentName1.equals(currentNext1)){
                    mMap = new HashMap();
                    mMap.put(NAME1,currentName1);
                    mMap.put(NAME2, currentNext1);
                    mMap.put(PAST_DAYS,forTwo_Days_1);
                    data.add(mMap);
                }
            }
/*
        mCursor.moveToFirst();
            //Для второй выбранной персоны
        Person person2 = mPersonDbHelper.getPersonObjectData(id_sql_second);
        String currentName2 = person2.getPerson_name();
        //идём к следующей строке
        while (mCursor.moveToNext()) {
            //получаем id последующих строк
            long id_sql_next2 = mCursor.getLong(idColumnIndex);
            String currentNext2 = mCursor.getString(nameColumnIndex);
            // количество прожитых дней  на двоих
            long forTwo_Days_2 = (mPersonDbHelper.getMillisForTwo(id_sql_second, id_sql_next2))/86400000;
            Log.d(TAG, "FindDatesActivity  " +
                    "  currentID_2 = " + id_sql_second + "  currentName2 = " + currentName2 +
                    "  currentID_Next = " + id_sql_next2 + "  currentNameNext = " + currentNext2 +
                    "  forTwo_Days = " + forTwo_Days_2);
            //если имена не совпадают, пишем в ArrayList
            if (!currentName1.equals(currentName2)){
                mMap = new HashMap();
                mMap.put(NAME1,currentName2);
                mMap.put(NAME2, currentNext2);
                mMap.put(PAST_DAYS,forTwo_Days_2);
                data.add(mMap);
            }
        }
*/
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCursor.close();
        mPersonDbHelper.close();
    }
}
