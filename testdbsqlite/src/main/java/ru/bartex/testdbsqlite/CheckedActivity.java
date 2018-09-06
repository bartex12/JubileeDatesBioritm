package ru.bartex.testdbsqlite;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.bartex.testdbsqlite.DBHelper.COLUMN_ONE;
import static ru.bartex.testdbsqlite.DBHelper.TABLE_NAME;

public class CheckedActivity extends AppCompatActivity {

    public static final String TAG = "33333";

    final static String CHECKED = "checked";
    ListView mListView;
    Button mButtonLeft;
    Button mButtonRight;
    long[] mChecked ;
    ArrayList<String> mList = new ArrayList<>();
    DBHelper mDBHelper = new DBHelper(this);
    Map<String,Object> m;
    ArrayList<Map<String,Object>> data = new ArrayList<>();
    final String ATTR_NAME ="name";
    final String ATTR_CHOOSE ="choose";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checked);

        mListView = (ListView) findViewById(R.id.listView_checked);
        //находим View, которое выводит текст Список пуст
        View empty = findViewById(R.id.emptyList_checked);
        mListView.setEmptyView(empty);

        mButtonLeft = (Button) findViewById(R.id.buttonOne);
        mButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Выводим данные нового столбца
                SQLiteDatabase mDb = mDBHelper.getWritableDatabase();
                Cursor mCursor = mDb.query(TABLE_NAME,
                        null, null, null, null, null, null);
                int oneColumnIndex = mCursor.getColumnIndex(COLUMN_ONE);
                try {
                    // Проходим через все ряды
                    while (mCursor.moveToNext()) {
                        // Используем индекс для получения строки или числа
                        String one = mCursor.getString(oneColumnIndex);
                        // Выводим построчно значения каждого столбца
                        Log.d(TAG, "\n" + one );
                    }
                } finally {
                    // Всегда закрываем курсор после чтения
                    mCursor.close();
                }

         /*
                Log.w(TAG, "Reading all contacts..");
                List<Person> contacts = mDBHelper.getAllContacts();
                for (Person pn : contacts) {
                    String log = "Id: "+pn.getPerson_id()+
                            " ,Name: " + pn.getPerson_name() + " ,Choose: " + pn.getPerson_choose();
                    Log.w(TAG, log);
                }
                */
         finish();
            }
        });

        mButtonRight = (Button) findViewById(R.id.buttonTwo);
        mButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                //пишем новый столбец COLUMN_ONE
                SQLiteDatabase mDb = mDBHelper.getWritableDatabase();
                mDb.execSQL("ALTER TABLE " + TABLE_NAME +
                        " ADD COLUMN " + COLUMN_ONE + " text");
                */

                Intent intent = new Intent(CheckedActivity.this, BottonNavigateActivity.class);
                startActivity(intent);
            }
        });

        //=================вывод данных из сериализованного списка объектов =============//
        ArrayList<Person> mArrayListChecked = (ArrayList<Person>)getIntent().
                getSerializableExtra(CHECKED);

        for (int i = 0; i<mArrayListChecked.size(); i++){
            m = new HashMap<>();
            m.put(ATTR_NAME, mArrayListChecked.get(i).getPerson_name());
            m.put(ATTR_CHOOSE, mArrayListChecked.get(i).getPerson_choose());
            data.add(m);
        }

        String[] from = {ATTR_NAME, ATTR_CHOOSE};
        int[] to = {R.id.name_list_test, R.id.checkBox_test};

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, data,
                R.layout.list_name_choose,from, to);
        mListView.setAdapter(mSimpleAdapter);
    }
}
