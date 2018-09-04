package ru.bartex.jubelee_dialog;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;

public class CheckedActivity extends AppCompatActivity {

    public static final String TAG = "33333";

    final static String CHECKED = "checked";
    ListView mListView;
    Button mButtonOne;
    Button mButtonTwo;
    long[] mChecked ;
    ArrayList<String> mList = new ArrayList<>();
    PersonDbHelper mDBHelper = new PersonDbHelper(this);
    Map<String,Object> m;
    ArrayList<Map<String,Object>> data = new ArrayList<>();
    final String ATTR_NAME ="name";
    final String ATTR_CHOOSE ="choose";
    final String ATTR_ID ="id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checked);

        mListView = (ListView) findViewById(R.id.listView_Checked);
        //находим View, которое выводит текст Список пуст
        View empty = findViewById(R.id.emptyList_Checked);
        mListView.setEmptyView(empty);

        //вывод данных из сериализованного списка объектов
        ArrayList<Person> mArrayListChecked = (ArrayList<Person>)getIntent().
                getSerializableExtra(CHECKED);

        int size = mArrayListChecked.size();
        for (int i = 0; i<size; i++){
            for (int k = 1; k<size; k++){
                long id_1 = mArrayListChecked.get(i).getPerson_id();
                long id_2 = mArrayListChecked.get(k).getPerson_id();
            }


        }

        String[] from = {ATTR_NAME, ATTR_CHOOSE};
        int[] to = {R.id.name_list_test, R.id.checkBox_test};

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, data,
                R.layout.list_name_choose,from, to);
        mListView.setAdapter(mSimpleAdapter);
    }
}
