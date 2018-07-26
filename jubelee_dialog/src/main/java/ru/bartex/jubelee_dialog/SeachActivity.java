package ru.bartex.jubelee_dialog;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SeachActivity extends AppCompatActivity {

    public static final String LIST_DATA_SEARCH = "ru.bartex.jubelee_dialog.list_data_search";
    public static final String LIST_DATA_POSITION = "ru.bartex.jubelee_dialog.list_data_position";
    public static final String LIST_DATA_QUERY = "ru.bartex.jubelee_dialog.list_data_query";

    TextView tvSlovo;
    TextView tvPositions;
    ListView mListViewSearch;
    ArrayAdapter ara;
    ArrayList<String>  searchList;

    public final String TAG = "33333";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seach);
        Log.d(TAG, "SeachActivity onCreate");

        //Делаем стрелку Назад на панели действий
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true );
        act.setHomeButtonEnabled(true);

        Intent intent = getIntent();

        tvSlovo = (TextView)findViewById(R.id.textViewSearchSlovo) ;
        tvPositions = (TextView)findViewById(R.id.textViewSearchPositions) ;
        mListViewSearch = (ListView)findViewById(R.id.listViewSearch);
        //получаем из интента список совпадений
        searchList = intent.getStringArrayListExtra(LIST_DATA_SEARCH);
        //выводим список совпадений на экран
        ArrayAdapter ara = new ArrayAdapter(this,android.R.layout.simple_list_item_1, searchList);
        mListViewSearch.setAdapter(ara);
        //получаем из интента поисковый запрос и показываем его на экране
        if (searchList.size()>0){
            tvSlovo.setText(intent.getStringExtra(LIST_DATA_QUERY));
            tvPositions.setText(Integer.toString(searchList.size()));
        }else {
            tvSlovo.setText(intent.getStringExtra(LIST_DATA_QUERY));
            tvPositions.setText("0");
        }
        //при щелчке на списке отправляем выбранную строку списка в интенте на BioritmActivity
        mListViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s = searchList.get(position);
                Log.d(TAG, "Нажато в списке " + s);
                Intent intentSearch = new Intent();
                intentSearch.putExtra(BioritmActivity.STRING_DATA, s);
                setResult(RESULT_OK,intentSearch);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            //чтобы работала стрелка Назад, а не происходил крах приложения
            case android.R.id.home:
                Log.d(TAG, "Домой");
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "SeachActivity onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "SeachActivity onResume");
    }
}
