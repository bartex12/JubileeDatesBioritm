package ru.bartex.testdbsqlite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static ru.bartex.testdbsqlite.DBHelper.COLUMN_CHOOSE;
import static ru.bartex.testdbsqlite.DBHelper.COLUMN_NAME;
import static ru.bartex.testdbsqlite.DBHelper.COLUMN_ONE;
import static ru.bartex.testdbsqlite.DBHelper.TABLE_NAME;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "33333";
    final int NEW_ACTIVITY_ADD_REQUEST = 1;
    private static final int DELETE_ID = 1;
    private static final int CHANGE_ID = 2;
    private static final int CANCEL_ID = 3;

    ListView mListView;
    Button ok;

    DBHelper mPersonDbHelper = new DBHelper(this);
    Cursor mCursor;
    SimpleCursorAdapter scAdapter;
    ArrayList<Person> mArrayList= new ArrayList<>();
    FindAdapterTest mFindAdapterTest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d(TAG, "MainActivity onCreate");

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "OptionsItem = action_add");
                Intent intentAdd = new Intent(MainActivity.this, NewActivity.class);
                startActivityForResult(intentAdd, NEW_ACTIVITY_ADD_REQUEST);
            }
        });

        mListView = findViewById(R.id.listView_Test);
        //находим View, которое выводит текст Список пуст
        View empty = findViewById(R.id.emptyList);
        mListView.setEmptyView(empty);

        ok = findViewById(R.id.toggleButton_test) ;
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // http://www.easyinfogeek.com/2014/01/android-tutorial-two-methods-of-passing.html
                ArrayList<Person> mArrayListChecked = mFindAdapterTest.getCheckedPersonList();
                Intent intent = new Intent(MainActivity.this, CheckedActivity.class);
                Bundle mChecked = new Bundle();
                mChecked.putSerializable(CheckedActivity.CHECKED, mArrayListChecked);
                intent.putExtras(mChecked);
                startActivity(intent);
            }
        });

        //если таблицы с именем TABLE_NAME не существует, создаём её
        if (!(mPersonDbHelper.existsTable(TABLE_NAME))){
            mPersonDbHelper.onCreate(mPersonDbHelper.getWritableDatabase());
            Log.d(TAG, "MainActivity existsTable = false");
        }else {
            Log.d(TAG, "MainActivity existsTable = true");

        }
        //если в базе нет записей, добавляем одну с анжелиной джоли
        mPersonDbHelper.createDefaultPersonIfNeed();

        registerForContextMenu(mListView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "MainActivity onStart");
/*
                //пишем новый столбец COLUMN_CHOOSE
                SQLiteDatabase mDb = mPersonDbHelper.getWritableDatabase();
                mDb.execSQL("ALTER TABLE " + TABLE_NAME +
                        " ADD COLUMN " + COLUMN_ONE + " TEXT");

*/

        //Пишем во все строки столбца COLUMN_CHOOSE значение 0
        SQLiteDatabase mDb = mPersonDbHelper.getWritableDatabase();
        mDb.execSQL("update " + TABLE_NAME +
                " set " + COLUMN_ONE + " ='one'");

        //Выводим в лог данные нового столбца - пока оставить для памяти
        Cursor mCursor = mDb.query(TABLE_NAME,
                null, null, null, null, null, null);
        int chooseColumnIndex = mCursor.getColumnIndex(COLUMN_ONE);
        try {
            // Проходим через все ряды
            while (mCursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                String choose = mCursor.getString(chooseColumnIndex);
                // Выводим построчно значения  столбца
                Log.d(TAG, "\n" + choose );
            }
        } finally {
            // Всегда закрываем курсор после чтения
            mCursor.close();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume");

        mArrayList = mPersonDbHelper.getAllContacts();
        mFindAdapterTest = new FindAdapterTest(this,mArrayList);
        mListView.setAdapter(mFindAdapterTest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivity onDestroy");
       // удаляем таблицу
        //mPersonDbHelper.getWritableDatabase().execSQL("DROP TABLE " + TABLE_NAME);  //удаление таблицы
        mCursor.close();
        mPersonDbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, "Удалить запись");
        menu.add(0, CHANGE_ID, 0, "Изменить запись");
        menu.add(0, CANCEL_ID, 0, "Отмена");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // получаем инфу о пункте списка
        final AdapterView.AdapterContextMenuInfo acmi =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //если выбран пункт Удалить запись
        if (item.getItemId() == DELETE_ID) {
            Log.d(TAG, "MainActivity CM_DELETE_ID");

            AlertDialog.Builder deleteDialog = new AlertDialog.Builder(MainActivity.this);
            deleteDialog.setTitle("Удалить: Вы уверены?");
            deleteDialog.setPositiveButton("Нет", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            deleteDialog.setNegativeButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Удаление записи из базы данных
                    mPersonDbHelper.deletePerson(acmi.id);
                    Log.d(TAG, "MainActivity удалена позиция с ID " + acmi.id);

                    onResume();

                }
            });
            deleteDialog.show();
            return true;

            //если выбран пункт Изменить запись
        } else if (item.getItemId() == CHANGE_ID) {
            Log.d(TAG, "MainActivity CM_CHANGE_ID");
/*
            Intent intent = new Intent(MainActivity.this, NewActivity.class);
            intent.putExtra(NewActivity.REQUEST_CODE, request_code);
            intent.putExtra(NewActivity.ID_SQL, acmi.id);
            startActivityForResult(intent, NEW_ACTIVITY_CHANGE_REQUEST);
            return true;
            */
            return super.onContextItemSelected(item);
        }
        //если ничего не выбрано
        return super.onContextItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //если вернулось из NEW_ACTIVITY по нажатию здесь кнопки  Добавить
            if (requestCode == NEW_ACTIVITY_ADD_REQUEST) {
                //здесь пока ничего
                onResume();
            }
        }
    }


}