package ru.bartex.jubelee_dialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonTable;

public class ListDialog_CheckBox extends AppCompatActivity {

    public final String TAG = "33333";
    ListView mListView;
    private SharedPreferences prefSetting;
    int sort = 1;  //Сортировка: 1-поимени возр, 2- по имени убыв,3-по дате возр, 4 - по дате убыв
    boolean isSort = false; //Список отсортирован?
    Button createList;
    public static final int REQUEST_LIST_DIALOG_FIND = 4; //риквест код

    ArrayList<Person> mPersonArrayList = new ArrayList<Person>();
    FindAdapterPerson findAdapter;
    SimpleAdapter mSimpleAdapter;
    ArrayList<Map<String,Object>> data = new ArrayList<>();
    Map<String,Object> m;


    final static String ATTR_NAME1 ="text1";
    final static String ATTR_NAME2 ="text2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_dialog__check_box);

        //получаем файл с настройками для приложения
        prefSetting = PreferenceManager.getDefaultSharedPreferences(this);
        sort = Integer.parseInt(prefSetting.getString("ListSort", "1"));
        isSort = prefSetting.getBoolean("cbSort", false);

        Intent intent = getIntent();
        final int request = intent.getIntExtra(FindDatesActivity.REQUEST_FIND,3);

        createList = (Button) findViewById(R.id.toggleButton_createList);
        createList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // http://www.easyinfogeek.com/2014/01/android-tutorial-two-methods-of-passing.html
                ArrayList<Person> pp = findAdapter.getCheckedPersonList();

                Intent intent = new Intent(ListDialog_CheckBox.this, FindDatesActivity.class);
                Bundle mChecked = new Bundle();
                mChecked.putSerializable(FindDatesActivity.LINE_CHECKED, pp);
                //intent.putExtra(FindDatesActivity.REQUEST_FIND, request);
                intent.putExtras(mChecked);
                startActivityForResult(intent,REQUEST_LIST_DIALOG_FIND);
            }
        });

        mListView = (ListView) findViewById(R.id.listViewDialog_CheckBOx);
        //находим View, которое выводит текст Список пуст
        View empty = findViewById(R.id.emptyList);
        mListView.setEmptyView(empty);

        //получаем экземпляр PersonDbHelper для работы с базой данных
        PersonDbHelper mPersonDbHelper = new PersonDbHelper(this);
        //получаем список объектов
        mPersonArrayList = mPersonDbHelper.getAllContactsChoose();
        //подключаем свой курсор для отображения на экране
        findAdapter = new FindAdapterPerson(this,mPersonArrayList);
        mListView.setAdapter(findAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "ListDialog_CheckBox onActivityResult");
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_LIST_DIALOG_FIND){
                //получаем и передаём дальше id выбранной пары
                Long id1 =getIntent().getLongExtra(ATTR_NAME1,55);
                Long id2 =getIntent().getLongExtra(ATTR_NAME2,55);
                Log.d(TAG, "id1 = " + id1 + "  id2 = " +id2 );
                // передаём
                Intent intent1 = new Intent();
                intent1.putExtra(JointActivity.ATTR_ID1,id1);
                intent1.putExtra(JointActivity.ATTR_ID2,id2);
                setResult(RESULT_OK, intent1);

                finish();
            }
    }
}
}
