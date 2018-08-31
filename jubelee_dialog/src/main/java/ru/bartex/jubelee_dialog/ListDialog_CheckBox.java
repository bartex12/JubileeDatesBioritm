package ru.bartex.jubelee_dialog;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

public class ListDialog_CheckBox extends AppCompatActivity {

    public final String TAG = "33333";
    ListView mListView;
    private SharedPreferences prefSetting;
    int sort = 1;  //Сортировка: 1-поимени возр, 2- по имени убыв,3-по дате возр, 4 - по дате убыв
    boolean isSort = false; //Список отсортирован?
    Button createList;

    ArrayList<PersonFind> mPersonArrayList = new ArrayList<PersonFind>();
    FindAdapter findAdapter;
    SimpleAdapter mSimpleAdapter;
    ArrayList<Map<String,Object>> data;
    Map<String,Object> m;


    final String ATTR_NAME ="text";
    final String ATTR_DR ="bool";
    final String ATTR_PAST_DAYS ="imag";
    final String ATTR_SELECT ="select";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_dialog__check_box);

        //получаем файл с настройками для приложения
        prefSetting = PreferenceManager.getDefaultSharedPreferences(this);
        sort = Integer.parseInt(prefSetting.getString("ListSort", "1"));
        isSort = prefSetting.getBoolean("cbSort", false);

        createList = (Button) findViewById(R.id.toggleButton_createList);
        createList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<PersonFind> pp = findAdapter.getCheckedPersonList();

                String result = "Отмечены персоны";
                for (PersonFind p: pp) {
                    if (p.isSelect_find()) {
                        result += "\n" + p.getPerson_name();
                    }
                }
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

            }
        });

        mListView = (ListView) findViewById(R.id.listViewDialog_CheckBOx);
        //находим View, которое выводит текст Список пуст
        View empty = findViewById(R.id.emptyList);
        mListView.setEmptyView(empty);

        //получаем список всех персон с пустыми галками через запрос к базе данных и
        //использовании этих данных для формирования  ArrayList mPersonArrayList
        PersonDbHelper personDbHelper = new PersonDbHelper(this);
        List<PersonFind> personFinds= personDbHelper.getAllPersonsWithCheckbox(isSort, sort);
        this.mPersonArrayList.addAll(personFinds);

        for (int i = 0; i < mPersonArrayList.size(); i++) {
            Log.d(TAG, " name1 = " + mPersonArrayList.get(i).getPerson_name() +
                            " id = " + mPersonArrayList.get(i).getPerson_id() +
                    " i = " + i);
        }
/*
        for (int i = 0; i<20; i++){
            mPersonArrayList.add(new PersonFind("name " + i, "15",
                     "6", "1999", false));
             Log.d(TAG, "ListDialog_CheckBox   name = " + mPersonArrayList.get(i).getPerson_name()
                    + "  i = " + i);

        }

        for (int i = 0; i < mPersonArrayList.size(); i++) {

            m = new HashMap<>();
            m.put(ATTR_NAME, mPersonArrayList.get(i).getPerson_name());
            m.put(ATTR_DR, mPersonArrayList.get(i).getPerson_dr());
            m.put(ATTR_PAST_DAYS, mPersonArrayList.get(i).getPerson_past_days());
            m.put(ATTR_SELECT, mPersonArrayList.get(i).isSelect_find());
            data.add(m);
    }
        String[] from = {ATTR_NAME, ATTR_DR, ATTR_PAST_DAYS, ATTR_SELECT};
        int[] to = {R.id.name_list_find, R.id.was_born_find, R.id.past_Days_find, R.id.checkBox_find};

        mSimpleAdapter = new SimpleAdapter(this, data,
                R.layout.list_name_date_checkbox,from, to);

        */

        //передаём данные адаптеру и подключаем адаптер к списку для вывода на экран
        findAdapter= new FindAdapter(this,mPersonArrayList);
        mListView.setAdapter(findAdapter);

    }

}
