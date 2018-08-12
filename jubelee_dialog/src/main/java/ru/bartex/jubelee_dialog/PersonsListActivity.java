package ru.bartex.jubelee_dialog;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonTable;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;

public class PersonsListActivity extends AppCompatActivity {

    private static final int DELETE_ID = 1;
    private static final int CHANGE_ID = 2;
    private static final int CANCEL_ID = 3;

    public final String TAG = "33333";
    final int NEW_ACTIVITY_ADD_REQUEST = 1;
    final int NEW_ACTIVITY_CHANGE_REQUEST = 2;
    static final int request_code = 111;
    static final int SEARCH_ACTIVITY = 2222;
    //Имя файла для списка имён
    final String FILENAME = "file_ru_bartex_jubelee_dialog";
    final String ATTR_NAME = "ru.bartex.jubelee_dialog.name_list";
    final String ATTR_DR = "ru.bartex.jubelee_dialog.dr_list";
    final String ATTR_PAST_DAYS = "ru.bartex.jubelee_dialog.days_list";

    ListView mListView;

    // csList -это список ArrayList строк вида
    //  String namesDateLived = String.format("%s  %s  %s", name, dr, past_days);
    //при этом   String dr = String.format("%s.%s.%s",day,mounth,year);
    ArrayList<String> csList = new ArrayList<>();
    //список для поискового запроса
    ArrayList<String> tempList;
    //список ArrayList вида строка-значение для строки списка персональных данных
    ArrayList<Map<String, Object>> data;
    //адаптер для отображения имени, даты,прожитых дней в строках списка персональных данных
    SimpleAdapter sara;
    //Виджет поиска
    SearchView searchView;

    int pos; // первый видимый элемент списка
    int offset; // для точного позиционирования, вдруг он виден не полностью
    int place = 2; //добавление в список:  1-в начало, 2-в конец
    int sort = 1;  //Сортировка: 1-поимени возр, 2- по имени убыв,3-по дате возр, 4 - по дате убыв
    boolean isSort = false; //Список отсортирован?
    private static final String KEY_POS = "POS";
    private static final String KEY_OFFSET = "OFFSET";

    private SharedPreferences shp;
    private SharedPreferences prefSetting;

    PersonDbHelper mPersonDbHelper;
    Cursor mCursor;
    SimpleCursorAdapter scAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persons_list);
        Log.d(TAG, "PersonsListActivity onCreate");
        //обработка интента, если он есть
        handleIntent(getIntent());

        //получаем файл с настройками для приложения
        prefSetting = PreferenceManager.getDefaultSharedPreferences(this);

        mListView = (ListView) findViewById(R.id.listView);
        //находим View, которое выводит текст Список пуст
        View empty = findViewById(R.id.emptyList);
        mListView.setEmptyView(empty);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            String s;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //определяем размер списка данных в адаптере
                int n = parent.getCount();
                Log.d(TAG, "Размер списка " + n);
                //если размер списка равен размеру списка из файла, берём данные из этого списка
                if (n == csList.size()) {
                    s = csList.get(position);
                    //в противном случае берём данные из списка совпадений по поиску
                } else {
                    s = tempList.get(position);
                }
                Log.d(TAG, "Нажато в списке " + s);
                Log.d(TAG, "position = " +position +" id = " + id);
                Intent intent = new Intent(PersonsListActivity.this, BioritmActivity.class);
                intent.putExtra(BioritmActivity.STRING_DATA, s);
                startActivity(intent);
                finish();
            }
        });
        //объявляем о регистрации контекстного меню
        registerForContextMenu(mListView);

        //Читаем список с персональными данными из файла
        readArrayList(csList, FILENAME);
        //Заполняем первую строку списка, если список пуст
        String s = "Анжелина Джоли  4.6.1975  10000";
        //Log.d(TAG, "csList.contains(s) " + csList.contains(s));
        if ((csList.size() == 0)) {
            csList.add(0, s);
            //сохраняем список в файл
            writeArrayList(csList);
        }
        Log.d(TAG, "PersonsListActivity onCreate " + csList.get(csList.size() - 1));
        //получаем адаптер с данными из списка строк csList
        sara = doDataFromList(csList);
        //присваиваем адаптер списку экрана и обновляем данные на экране
        mListView.setAdapter(sara);
        //Загружаем сохранённую позицию списка
        loadPos();
        //устанавливаем список в позицию
        mListView.setSelectionFromTop(pos, offset);

        //создаём экземрляр класса PersonDbHelper
        mPersonDbHelper = new PersonDbHelper(this);
        //получаем данные в курсоре
        mCursor = mPersonDbHelper.getAllData();
        //поручаем активности присмотреть за курсором
        startManagingCursor(mCursor);

       // String dr = PersonTable.COLUMN_DAY + "." + PersonTable.COLUMN_MONTH +
         //       "." + PersonTable.COLUMN_YEAR;

        // формируем столбцы сопоставления
        String[] from = new String[] {PersonTable.COLUMN_NAME,
                PersonTable.COLUMN_DR, PersonTable.COLUMN_PAST_DAYS };
        int[] to = new int[] { R.id.name_list, R.id.was_born, R.id.past_Days };

        // создааем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.list_name_date, mCursor, from, to);
        mListView.setAdapter(scAdapter);

    }

    //Если в манифесте установить для android:launchMode значение "singleTop" ,
    // то поисковая активность получает намерение ACTION_SEARCH с вызовом onNewIntent(Intent) ,
    // передавая здесь новое намерение ACTION_SEARCH
    //Алгоритм  в этом случае: onNewIntent-->handleIntent-->doMySearch
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "PersonsListActivity onNewIntent");
        setIntent(intent);
        handleIntent(intent);
    }

    //обработка интента для поиска, посылаемого системой
    private void handleIntent(Intent intent) {
        Log.d(TAG, "PersonsListActivity handleIntent");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "PersonsListActivity handleIntent equals");
            doMySearch(query);

        } else Log.d(TAG, "PersonsListActivity handleIntent not equals");
    }

    //здесь производим поиск по поисковому запросу
    public void doMySearch(String query) {
        Log.d(TAG, "PersonsListActivity doMySearch: String query = " + query);
        //ищем строки в csList, в которых есть query и формируем searchList
        ArrayList<String> searchList = searchInListQueryString(csList,query);
        //отправляем интент в SeachActivity в составе с searchList и query
        //чтобы затем в onActivityResult запустить Biorytmactivity с правильными данными
        Intent intent = new Intent(PersonsListActivity.this, SeachActivity.class);
        intent.putExtra(SeachActivity.LIST_DATA_SEARCH, searchList);
        intent.putExtra(SeachActivity.LIST_DATA_QUERY, query);
        startActivityForResult(intent, SEARCH_ACTIVITY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "PersonsListActivity onStart");
        //вывод в лог базы данных по людям
        displayDatabaseInfo();
    }

/*
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "PersonsListActivity onResume");

        //place задаётся вручную в коде ниже (case5)
       // place = Integer.parseInt(prefSetting.getString("AddPerson", "2"));
        sort = Integer.parseInt(prefSetting.getString("ListSort", "1"));
        isSort = prefSetting.getBoolean("cbSort", false);

        //если была развёрнута строка поиска и был сформирован список результатов поиска
        if ((searchView!=null)&&(tempList!=null)) {
            Log.d(TAG, "PersonsListActivity Query " + searchView.getQuery());
            //получаем адаптер с данными из списка tempList
            sara = doDataFromList(tempList);
            //присваиваем адаптер списку экрана и обновляем данные на экране
            mListView.setAdapter(sara);

            //если НЕ была развёрнута строка поиска и НЕ был сформирован список результатов поиска
        } else{
            Log.d(TAG, "PersonsListActivity Query = null" );

            if (isSort) {
                switch (sort) {
                    case 1:
                        Log.d(TAG, "PersonsListActivity Сортировка по имени по возрастанию");
                        String[] s1 = new String[csList.size()];
                        for (int i = 0; i < csList.size(); i++) {
                            s1[i] = csList.get(i);
                        }
                        //сортируем массив строк данных
                        Arrays.sort(s1);
                        //делаем новый список из сортированного массива
                        csList = new ArrayList<String>(Arrays.asList(s1));
                        //получаем адаптер с данными из списка строк csList
                        sara = doDataFromList(csList);
                        //присваиваем адаптер списку экрана и обновляем данные на экране
                        mListView.setAdapter(sara);
                        //sara.notifyDataSetChanged();
                        //Загружаем сохранённую позицию списка
                        loadPos();
                        //устанавливаем список в позицию
                        mListView.setSelectionFromTop(pos, offset);
                        break;
                    case 2:
                        Log.d(TAG, "PersonsListActivity Сортировка по имени по убыванию");
                        String[] s2 = new String[csList.size()];
                        for (int i = 0; i < csList.size(); i++) {
                            s2[i] = csList.get(i);
                        }
                        Arrays.sort(s2);
                        //делаем новый список из сортированного массива
                        csList = new ArrayList<String>(Arrays.asList(s2));
                        //Переворачиваем массив= сортировка по убыванию
                        Collections.reverse(csList);
                        //получаем адаптер с данными из списка строк csList
                        sara = doDataFromList(csList);
                        //присваиваем адаптер списку экрана и обновляем данные на экране
                        mListView.setAdapter(sara);
                        //Загружаем сохранённую позицию списка
                        loadPos();
                        //устанавливаем список в позицию
                        mListView.setSelectionFromTop(pos, offset);
                        break;
                    case 3:
                        Log.d(TAG, "PersonsListActivity Сортировка по дате по возрастанию ");

                        int sizeMap = csList.size();
                        int[] s_int = new int[sizeMap];
                        Map<String, Object> m;

                        for (int i = 0; i < sizeMap; i++) {
                            //получаем пары ключ-значение для i строки
                            m = data.get(i);
                            //формируем массив прожитых дней для сортировки
                            s_int[i] = Integer.parseInt((String) m.get(ATTR_PAST_DAYS));
                        }
                        //сортировка
                        for (int i = 0; i < sizeMap; i++) {
                            for (int j = 0; j < sizeMap - 1; j++) {
                                int one = s_int[j];
                                int two = s_int[j + 1];
                                if (one > two) {
                                    int tempInt = s_int[j];
                                    String tempString = csList.get(j);
                                    //сортируем список
                                    csList.set(j, csList.get(j + 1));
                                    csList.set(j + 1, tempString);
                                    //сортируем вспомогательный массив
                                    s_int[j] = s_int[j + 1];
                                    s_int[j + 1] = tempInt;
                                }
                            }
                        }
                        //получаем адаптер с данными из списка строк csList
                        sara = doDataFromList(csList);
                        //присваиваем адаптер списку экрана и обновляем данные на экране
                        mListView.setAdapter(sara);
                        //Загружаем сохранённую позицию списка
                        loadPos();
                        //устанавливаем список в позицию
                        mListView.setSelectionFromTop(pos, offset);
                        break;

                    case 4:
                        Log.d(TAG, "PersonsListActivity Сортировка по дате по убыванию ");
                        int sizeMapDown = csList.size();
                        int[] s_int_down = new int[sizeMapDown];

                        for (int i = 0; i < sizeMapDown; i++) {
                            //получаем пары ключ-значение для i строки
                            m = data.get(i);
                            //формируем массив прожитых дней для сортировки
                            s_int_down[i] = Integer.parseInt((String) m.get(ATTR_PAST_DAYS));
                        }
                        //сортировка
                        for (int i = 0; i < sizeMapDown; i++) {
                            for (int j = 0; j < sizeMapDown - 1; j++) {
                                int one = s_int_down[j];
                                int two = s_int_down[j + 1];
                                if (one > two) {
                                    int tempInt = s_int_down[j];
                                    String tempString = csList.get(j);
                                    //сортируем список
                                    csList.set(j, csList.get(j + 1));
                                    csList.set(j + 1, tempString);
                                    //сортируем вспомогательный массив
                                    s_int_down[j] = s_int_down[j + 1];
                                    s_int_down[j + 1] = tempInt;
                                }
                            }
                        }
                        //Переворачиваем массив= сортировка по убыванию
                        Collections.reverse(csList);
                        //получаем адаптер с данными из списка строк csList
                        sara = doDataFromList(csList);
                        //присваиваем адаптер списку экрана и обновляем данные на экране
                        mListView.setAdapter(sara);
                        //Загружаем сохранённую позицию списка
                        loadPos();
                        //устанавливаем список в позицию
                        mListView.setSelectionFromTop(pos, offset);

                        break;

                    case 5:
                        place = 1;
                        Log.d(TAG, "PersonsListActivity Без сортировки/Вставка в начало  place = " + place);
                        //Загружаем сохранённую позицию списка
                        loadPos();
                        //устанавливаем список в позицию
                        mListView.setSelectionFromTop(pos, offset);
                        break;

                    default:
                        place = 2;
                        Log.d(TAG, "PersonsListActivity Без сортировки/default  place = " + place);
                        //Загружаем сохранённую позицию списка
                        loadPos();
                        //устанавливаем список в позицию
                        mListView.setSelectionFromTop(pos, offset);
                        break;
                }

            } else {
                Log.d(TAG, "PersonsListActivity Без сортировки");
            }
        }

        Log.d(TAG, "PersonsListActivity place = " + place + " Есть сортировка ? " + isSort +
                " Сортировка = " + sort);
    }
*/

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "PersonsListActivity onPause");
        //запоминаем позицию выбранной строки списка
        pos = mListView.getFirstVisiblePosition();
        View v = mListView.getChildAt(0);
        if (v != null) {
            offset = v.getTop() - mListView.getPaddingTop();
        }
        savePos();
        Log.d(TAG, "PersonsListActivity onPause" + "KEY_POS = " +
                pos + "KEY_OFFSET" + offset);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "PersonsListActivity onStop");
    }

    //вызов диалогового окна при нажатии на кнопку Назад
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.d(TAG, "PersonsListActivity onBackPressed");
        openQuitDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "PersonsListActivity onDestroy");
        writeArrayList(csList);
        mCursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.person, menu);

        // Получите SearchView и настройте настраиваемую для поиска конфигурацию
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //до SearchView можно добраться так (см menu person.xml)
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // А можно так - через MenuItem
        // MenuItem searchItem = menu.findItem(R.id.menu_search);
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //Вызов getSearchableInfo() получает объект SearchableInfo который создается из файла XML
        // с возможностью поиска. Когда поисковая конфигурация правильно связана с вашим SearchView
        // SearchView запускает действие с намерением ACTION_SEARCH когда пользователь отправляет запрос
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //сворачиваем строку поиска (true)
        searchView.setIconifiedByDefault(true);
        //пишем подсказку в строке поиска
        searchView.setQueryHint("Поиск");
        //устанавливаем в панели действий кнопку ( > )для отправки поискового запроса
        searchView.setSubmitButtonEnabled(true);
        //иконка закрытия поиска
        int searchCloseId = searchView.getContext().getResources().
                getIdentifier("android:id/search_close_btn", null, null);
        ImageView searchClose = (ImageView) searchView.findViewById(searchCloseId);
        searchClose.setImageResource(R.drawable.ic_clear_white_24dp);
        //иконка  поиска
        int searchIconId = searchView.getContext().getResources().
                getIdentifier("android:id/search_button", null, null);
        ImageView searchIcon = (ImageView) searchView.findViewById(searchIconId);
        searchIcon.setImageResource(R.drawable.ic_search_white_36dp);
        //иконка голосового ввода поиска
        int searchMicId = searchView.getContext().getResources().
                getIdentifier("android:id/search_voice_btn", null, null);
        ImageView searchMic = (ImageView) searchView.findViewById(searchMicId);
        searchMic.setImageResource(R.drawable.ic_mic_white_36dp);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //ищем строки в csList, в которых есть newText и формируем tempList
                tempList = searchInListQueryString(csList,newText);
                //получаем адаптер с данными из списка строк tempList
                sara = doDataFromList(tempList);
                //присваиваем адаптер списку экрана и обновляем данные на экране
                mListView.setAdapter(sara);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add:
                Log.d(TAG, "OptionsItem = action_add");

                //тестовые две строки для проверки базы данных
                //insertPerson();
                //displayDatabaseInfo();

                Intent intent = new Intent(this, NewActivity.class);
                startActivityForResult(intent, NEW_ACTIVITY_ADD_REQUEST);
                return true;
            case R.id.menu_search:
                Log.d(TAG, "OptionsItem = menu_search");
                //вызываем строку поиска - работает и без этого при щелчке на лупе в панели действий!!?
                //onSearchRequested();
                //Видимо, функция startSearch, зашитая в onSearchRequested срабатывает для виджета автоматически?
                // Или дело в том, что так как в манифесте есть строка android:launchMode="singleTop"
                //всё идёт через onNewIntent
                return true;
            case R.id.action_settings:
                Log.d(TAG, "OptionsItem = action_settings");
                Intent intentSettings = new Intent(this, PrefActivity.class);
                startActivity(intentSettings);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Реализовано два варианта:
    // 1 - если вернулось из NEW_ACTIVITY по нажатию в PersonsListActivity кнопки  Добавить
    // 2 - если вернулось из NEW_ACTIVITY по выбору в PersonsListActivity из контексного меню строки Изменить
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            int position = data.getIntExtra("position", 0);
            String namesDate = data.getStringExtra("namesDateLived");
            Log.d(TAG, "PersonsListActivity onActivityResult ADD_REQUEST= " + namesDate);

            //если вернулось из NEW_ACTIVITY по нажатию здесь кнопки  Добавить
            if (requestCode == NEW_ACTIVITY_ADD_REQUEST) {
                // если в настройках выбрана вставка в начало списка
                if (place == 1) {
                    //заносим в начало списка новую строку с данными
                    csList.add(0, namesDate);
                    // иначе заносим в конец списка новую строку с данными
                } else csList.add(namesDate);
                //writeArrayList(csList);
                Log.d(TAG, "onActivityResult csList size after= " + csList.size() +
                        "  " + csList.get(csList.size() - 1));

                //если вернулось из NEW_ACTIVITY по выбору здесь из контексного меню строки Изменить
            } else if (requestCode == NEW_ACTIVITY_CHANGE_REQUEST) {
                //при условии, что в списке что-то было
                if (csList.size() > 0) {
                    //замена элемента в позиции listPosition
                    csList.set(position, namesDate);
                    //Можно было и так - добавление вставкой и удаление старого
                    //namesDateList.add(listPosition, namesDate);
                    //namesDateList.remove(listPosition + 1);

                    //если список был пуст, записываем как первый элемент
                } else csList.add(0, namesDate);

            } else if (requestCode == SEARCH_ACTIVITY) {
                String dataSearch = data.getStringExtra(BioritmActivity.STRING_DATA);
                Intent intent = new Intent(PersonsListActivity.this, BioritmActivity.class);
                intent.putExtra(BioritmActivity.STRING_DATA, dataSearch);
                startActivity(intent);
                finish();
            }
            //сохраняем список в файл
            writeArrayList(csList);
            //заполняем адаптер данными
            sara = doDataFromList(csList);
            //обновляем данные на экране
            mListView.setAdapter(sara);
        }
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
            Log.d(TAG, "PersonsListActivity CM_DELETE_ID");

            AlertDialog.Builder deleteDialog = new AlertDialog.Builder(PersonsListActivity.this);
            deleteDialog.setTitle("Удалить: Вы уверены?");
            deleteDialog.setPositiveButton("Нет", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            deleteDialog.setNegativeButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // удаляем пункт, используя позицию пункта в списке
                    data.remove(acmi.position);
                    // уведомляем, что данные data изменились для вывода на экран
                    sara.notifyDataSetChanged();
                    //удаляем строку списка csList с тем же номером, что и позиция данных для адаптера
                    csList.remove(acmi.position);
                    //пишем в файл обновлённый список, чтобы сохранить изменения
                    writeArrayList(csList);

                    //Удаление записи из базы данных
                    // пока ничего
                }
            });

            deleteDialog.show();
            return true;

            //если выбран пункт Изменить запись
        } else if (item.getItemId() == CHANGE_ID) {

            Log.d(TAG, "PersonsListActivity CM_CHANGE_ID");
            csList.get(acmi.position);

            String userName = getDataFromList(csList, acmi.position)[0];
            String day = getDataFromList(csList, acmi.position)[1];
            String mounth = getDataFromList(csList, acmi.position)[2];
            String year = getDataFromList(csList, acmi.position)[3];

            Intent intent = new Intent(PersonsListActivity.this, NewActivity.class);
            //вставляем в интент имя, дату рожд, request_code =111(признак того, что нужно изменить)
            //и номер позиции в списке, который потом передадим обратно
            intent.putExtra(NewActivity.PERSON_NAME, userName);
            intent.putExtra(NewActivity.DAY_NUMBER, day);
            intent.putExtra(NewActivity.MOUNTH_NUMBER, mounth);
            intent.putExtra(NewActivity.YEAR_NUMBER, year);
            intent.putExtra(NewActivity.REQUEST_CODE, request_code);
            intent.putExtra(NewActivity.POSITION, acmi.position);

            startActivityForResult(intent, NEW_ACTIVITY_CHANGE_REQUEST);

            return true;
        }
        //если ничего не выбрано
        return super.onContextItemSelected(item);
    }

//****************************ФУНКЦИИ***********************//

    void savePos() {
        shp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor edit = shp.edit();
        edit.putInt(KEY_POS, pos);
        edit.apply();
        edit.putInt(KEY_OFFSET, offset);
        edit.apply();
    }

    void loadPos() {
        shp = getPreferences(MODE_PRIVATE);
        pos = shp.getInt(KEY_POS, 0);
        offset = shp.getInt(KEY_OFFSET, 0);
    }

    //Получить адаптер с данными из списка строк csList
    public SimpleAdapter doDataFromList(ArrayList<String> csList) {

        int size = csList.size();
        //Резервируем память под массивы данных
        String[] name = new String[size];
        String[] day = new String[size];
        String[] mounth = new String[size];
        String[] year = new String[size];
        String[] dr = new String[size];
        String[] past_days = new String[size];
        Log.d(TAG, "PersonsListActivity doDataFromList size = " + size);

        if (size != 0) {
            for (int i = 0; i < size; i++) {
                name[i] = getDataFromList(csList, i)[0];
                day[i] = getDataFromList(csList, i)[1];
                mounth[i] = getDataFromList(csList, i)[2];
                year[i] = getDataFromList(csList, i)[3];
                dr[i] = String.format("%s.%s.%s", day[i], mounth[i], year[i]);
                //экземпляр календаря с данными из списка
                Calendar firstCalendar = new GregorianCalendar(Integer.parseInt(year[i])
                        , Integer.parseInt(mounth[i]) - 1, Integer.parseInt(day[i]));
                //получаем дату в милисекундах
                long firstCalendarMillis = firstCalendar.getTimeInMillis();
                long nowTimeMillis = System.currentTimeMillis();
                //количество прошедших дней с даты рождения
                long beenDays = (nowTimeMillis - firstCalendarMillis) / 86400000;
                past_days[i] = Long.toString(beenDays);
            }
        } else Log.d(TAG, "doDataFromList size =" + csList.size());

        data = new ArrayList<Map<String, Object>>(name.length);
        Map<String, Object> m;
        for (int i = 0; i < name.length; i++) {
            m = new HashMap<>();
            m.put(ATTR_NAME, name[i]);
            m.put(ATTR_DR, dr[i]);
            m.put(ATTR_PAST_DAYS, past_days[i]);
            //m.put(ATTR_BACKGROUND,colorLayout);
            data.add(m);
        }
        //Делаем массивы откуда-куда
        String[] from = {ATTR_NAME, ATTR_DR, ATTR_PAST_DAYS};
        int[] to = {R.id.name_list, R.id.was_born, R.id.past_Days};
        //подключаем данные к адаптеру
        SimpleAdapter sara = new SimpleAdapter(this, data, R.layout.list_name_date, from, to);
        //sara.setViewBinder(new MyBinder());
        return sara;
    }

    //Записать список имён с данными  в файл
    void writeArrayList(ArrayList<String> arrayList) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput(FILENAME, MODE_PRIVATE)));
            for (String line : arrayList) {
                //функция write не работает для CharSequence, поэтому String
                bw.write(line);
                // тут мог бы быть пробел если надо в одну строку
                //сли не включать эту строку, то в файле будет всего одна строчка, а нужен массив
                bw.write(System.getProperty("line.separator"));
            }
            Log.d(TAG, "Файл ArrayList записан ");
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Прочитать список имён с данными из файла
    void readArrayList(ArrayList<String> arrayList, String fileName) {
        try {
            //ArrayList<String> arcsIn = new ArrayList<String>();
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    openFileInput(fileName)));
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    arrayList.add(line);
                }
                //Выводим в лог весь файл построчно
                //for (String s:arrayList){
                // Log.d(TAG, s);
                //}
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //Создать и открыть диалог выхода из программы
    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle("Выход: Вы уверены?");

        quitDialog.setPositiveButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        quitDialog.setNegativeButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                //system.exit(0);
                //finishAffinity();  // с API 16
                //System.runFinalizersOnExit(true);
                //System.exit(0);
            }
        });

        quitDialog.show();
    }

    //метод получения данных из строки списка (просто из строки - в MainActivity))
    public static String[] getDataFromList(ArrayList<String> ara, int position) {

        if (ara.size() > 0) {
            //получаем выбранную строку по позиции
            String ss = (String) ara.get(position);
            //индекс последней точки в строке
            int i1 = ss.lastIndexOf(".");
            String stringYear = ss.substring(i1 + 1, i1 + 5);
            String stringNoYear = ss.substring(0, ss.lastIndexOf("."));
            //индекс первой точки в строке
            int i2 = stringNoYear.lastIndexOf(".");
            String stringNoYearMonth = ss.substring(0, stringNoYear.lastIndexOf("."));
            String stringMonth = stringNoYear.substring(i2 + 1, stringNoYear.length());
            //индекс последнего пробела
            int i3 = stringNoYearMonth.lastIndexOf(" ");
            String stringDay = stringNoYearMonth.substring(i3 + 1, stringNoYearMonth.length());
            String stringName = stringNoYearMonth.substring(0, i3 - 1);
            //индекс последнего пробела в строке
            int i4 = ss.lastIndexOf(" ");
            String stringDays = ss.substring(i4 + 1, ss.length());

            String[] data = {stringName, stringDay, stringMonth, stringYear, stringDays};

            return data;
        }
        return new String[]{"Без имени", "19", "7", "1980", "10000"};
    }

    private ArrayList<String> searchInListQueryString(ArrayList<String> csList, String query) {

        query = query.toLowerCase();
        ArrayList<String> searchList = new ArrayList<>();
        for (int i = 0; i < csList.size(); i++) {
            String searchString = csList.get(i).toLowerCase();
            if (searchString.contains(query)) {
                Log.d(TAG, "String " + searchString + " contains query= " + query);
                //Добавляем в список очередное совпадение
                searchList.add(csList.get(i));
            }
        }
        return searchList;
    }

    private void displayDatabaseInfo() {
        // Создадим и откроем для чтения базу данных
        SQLiteDatabase db = mPersonDbHelper.getReadableDatabase();

        // Зададим условие для выборки - список столбцов
        String[] projection = {
                PersonTable._ID,
                PersonTable.COLUMN_NAME,
                PersonTable.COLUMN_DR,
                PersonTable.COLUMN_PAST_DAYS};

        // Делаем запрос
        Cursor cursor = db.query(
                PersonTable.TABLE_NAME,   // таблица
                projection,            // столбцы
                null,                  // столбцы для условия WHERE
                null,                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // порядок сортировки


        try {

            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(PersonTable._ID);
            int nameColumnIndex = cursor.getColumnIndex(PersonTable.COLUMN_NAME);
            int drColumnIndex = cursor.getColumnIndex(PersonTable.COLUMN_DR);
            int pastDaysColumnIndex = cursor.getColumnIndex(PersonTable.COLUMN_PAST_DAYS);

            // Проходим через все ряды
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentDr = cursor.getString(drColumnIndex);
                int currentPastDays = cursor.getInt(pastDaysColumnIndex);

                // Выводим значения каждого столбца
                Log.d(TAG, "\n" + currentID + " - " +
                        currentName + " - " +
                        currentDr + " - " +
                        currentPastDays);
            }
        } finally {
            // Всегда закрываем курсор после чтения
            cursor.close();
        }
    }

/*
    private void insertPerson() {

        // Gets the database in write mode
        SQLiteDatabase db = mPersonDbHelper.getWritableDatabase();
        // Создаем объект ContentValues, где имена столбцов ключи,
        // а информация о госте является значениями ключей
        ContentValues values = new ContentValues();
        values.put(PersonTable.COLUMN_NAME, "Яя");
        values.put(PersonTable.COLUMN_DAY, 17);
        values.put(PersonTable.COLUMN_MONTH, 5);
        values.put(PersonTable.COLUMN_YEAR, 1961);

        long newRowId = db.insert(PersonTable.TABLE_NAME, null, values);
        Log.d(TAG, "PersonsListActivity insertGuest newRowId = " + newRowId);
    }
*/
}