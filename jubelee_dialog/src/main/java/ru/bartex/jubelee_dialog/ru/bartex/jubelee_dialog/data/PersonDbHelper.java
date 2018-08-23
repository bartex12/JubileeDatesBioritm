package ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.GregorianCalendar;

/**
 * Created by Андрей on 11.08.2018.
 */
public class PersonDbHelper extends SQLiteOpenHelper{

    public static final String TAG = "33333";

    //Имя файла базы данных
    private static final String DATABASE_NAME = "bioritmDataBase2.db";

     // Версия базы данных. При изменении схемы увеличить на единицу
    private static final int DATABASE_VERSION = 1;


     //Конструктор
    public PersonDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Вызывается при создании базы данных
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Строка для создания таблицы
        String SQL_CREATE_PERSONS_TABLE = "CREATE TABLE " + PersonTable.TABLE_NAME + " ("
                + PersonTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PersonTable.COLUMN_NAME + " TEXT NOT NULL, "
                + PersonTable.COLUMN_DAY + " TEXT NOT NULL, "
                + PersonTable.COLUMN_MONTH + " TEXT NOT NULL, "
                + PersonTable.COLUMN_YEAR + " TEXT NOT NULL, "
                + PersonTable.COLUMN_DR + " TEXT NOT NULL, "
                + PersonTable.COLUMN_PAST_DAYS + " TEXT NOT NULL);";

        // Запускаем создание таблицы
        db.execSQL(SQL_CREATE_PERSONS_TABLE);
    }

    /**
     * Вызывается при обновлении СХЕМЫ  базы данных
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Запишем в журнал
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);
/*
        // Удаляем старую таблицу и создаём новую
        db.execSQL("DROP TABLE IF IT EXISTS " + PersonContract.PersonEntry.TABLE_NAME);
        // Создаём новую таблицу
        onCreate(db);
*/
    }

    /**
    * Метод для добавления нового человека в список
    */
    public long addPerson(String name,
                          String day,String month,String year, String dr, String pastDays) {
        // создаём объект ContentValues
        ContentValues cv = new ContentValues();
        cv.put(PersonTable.COLUMN_NAME, name);
        cv.put(PersonTable.COLUMN_DAY, day);
        cv.put(PersonTable.COLUMN_MONTH, month);
        cv.put(PersonTable.COLUMN_YEAR, year);
        cv.put(PersonTable.COLUMN_DR, dr);
        cv.put(PersonTable.COLUMN_PAST_DAYS, pastDays);
        // получаем базу данных для записи и пишем
        SQLiteDatabase sd = getWritableDatabase();
        //the row ID of the newly inserted row, or -1 if an error occurred
        return  sd.insert(PersonTable.TABLE_NAME, null, cv);
    }

    /**
     * Метод обновления строки списка
     */
    public boolean updatePerson(long rowId, String name,
                                String day,String month,String year, String dr, String pastDays) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(PersonTable.COLUMN_NAME, name);
        updatedValues.put(PersonTable.COLUMN_DAY, day);
        updatedValues.put(PersonTable.COLUMN_MONTH, month);
        updatedValues.put(PersonTable.COLUMN_YEAR, year);
        updatedValues.put(PersonTable.COLUMN_DR, dr);
        updatedValues.put(PersonTable.COLUMN_PAST_DAYS, pastDays);

        long result = db.update(PersonTable.TABLE_NAME, updatedValues, PersonTable._ID + "=" + rowId, null);
        return  result > 0;
    }

    /**
     * Удаляет элемент списка
     */
    public void deletePerson(long rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PersonTable.TABLE_NAME, PersonTable._ID + "=" + rowId, null);
        db.close();
    }

    /**
     * Возвращает курсор с указанной записи
     */
    public Cursor getPerson(long rowId) throws SQLException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.query(true, PersonTable.TABLE_NAME,
                new String[] { PersonTable._ID,PersonTable.COLUMN_NAME,
                        PersonTable.COLUMN_DAY,PersonTable.COLUMN_MONTH,PersonTable.COLUMN_YEAR,
                        PersonTable.COLUMN_DR,PersonTable.COLUMN_PAST_DAYS },
                PersonTable._ID + "=" + rowId,
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Возвращает массив строк с данными по персоне (для простоты записи)
     */
    public String[] getPersonData(long rowId) throws SQLException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.query(true, PersonTable.TABLE_NAME,
                new String[] { PersonTable._ID,PersonTable.COLUMN_NAME,
                        PersonTable.COLUMN_DAY,PersonTable.COLUMN_MONTH,PersonTable.COLUMN_YEAR,
                        PersonTable.COLUMN_DR,PersonTable.COLUMN_PAST_DAYS },
                PersonTable._ID + "=" + rowId,
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        //массив данных
        String[] data = new String[6];
        // Узнаем индекс каждого столбца
        int nameColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_NAME);
        int dayColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_DAY);
        int monthColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_MONTH);
        int yearColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_YEAR);
        int drColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_DR);
        int pastDaysColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_PAST_DAYS);

        data[0] = mCursor.getString(nameColumnIndex);  //COLUMN_NAME
        data[1] = mCursor.getString(dayColumnIndex);  //COLUMN_DAY
        data[2] = mCursor.getString(monthColumnIndex);  //COLUMN_MONTH
        data[3] = mCursor.getString(yearColumnIndex);  //COLUMN_YEAR
        data[4] = mCursor.getString(drColumnIndex);  // COLUMN_DR
        data[5] = mCursor.getString(pastDaysColumnIndex);  //COLUMN_PAST_DAYS

        //закрываем курсор
        mCursor.close();

        return data;
    }

    // Обновить данные в столбце Количество прожитых дней
    public void updatePastDays() {
        SQLiteDatabase sd = getWritableDatabase();
        Cursor cursor =  getAllData();
            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(PersonTable._ID);
            int dayColumnIndex = cursor.getColumnIndex(PersonTable.COLUMN_DAY);
            int monthColumnIndex = cursor.getColumnIndex(PersonTable.COLUMN_MONTH);
            int yearColumnIndex = cursor.getColumnIndex(PersonTable.COLUMN_YEAR);

            // Проходим через все ряды
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursor.getInt(idColumnIndex);
                String currentDay = cursor.getString(dayColumnIndex);
                String currentMonth = cursor.getString(monthColumnIndex);
                String currentYear = cursor.getString(yearColumnIndex);

                //экземпляр календаря с данными из списка
                GregorianCalendar firstCalendar = new GregorianCalendar(Integer.parseInt(currentYear),
                        Integer.parseInt(currentMonth) - 1,Integer.parseInt(currentDay));
                //получаем дату в милисекундах
                long firstCalendarMillis = firstCalendar.getTimeInMillis();
                long nowTimeMillis = System.currentTimeMillis();
                //количество прошедших дней с даты рождения
                long beenDays = (nowTimeMillis-firstCalendarMillis)/86400000;
                //количество прожитых дней как строка
                String past_days = Long.toString(beenDays);

                ContentValues updatedValues = new ContentValues();
                updatedValues.put(PersonTable.COLUMN_PAST_DAYS, past_days);

                sd.update(PersonTable.TABLE_NAME,
                        updatedValues,
                        "_id = ?",
                        new String[] {Integer.toString(currentID)});
            }
    }

    // получить курсор с данными из таблицы TABLE_NAME
    public Cursor getAllData() {
        SQLiteDatabase sd = getReadableDatabase();
        return sd.query(PersonTable.TABLE_NAME,
                new String[]{PersonTable._ID,PersonTable.COLUMN_NAME,
                        PersonTable.COLUMN_DAY,PersonTable.COLUMN_MONTH,PersonTable.COLUMN_YEAR,
                        PersonTable.COLUMN_DR,PersonTable.COLUMN_PAST_DAYS},
                null, null, null, null, null);
    }

    // получить курсор с данными из таблицы TABLE_NAME
    public Cursor getAllDataSortNameUp() {
        SQLiteDatabase sd = getReadableDatabase();
        return sd.query(PersonTable.TABLE_NAME,
                new String[]{PersonTable._ID,PersonTable.COLUMN_NAME,
                        PersonTable.COLUMN_DAY,PersonTable.COLUMN_MONTH,PersonTable.COLUMN_YEAR,
                        PersonTable.COLUMN_DR,PersonTable.COLUMN_PAST_DAYS},
                null, null, null, null, PersonTable.COLUMN_NAME);
    }

    // получить курсор с данными из таблицы TABLE_NAME
    public Cursor getAllDataSortNameDown() {
        SQLiteDatabase sd = getReadableDatabase();
        return sd.query(PersonTable.TABLE_NAME,
                new String[]{PersonTable._ID,PersonTable.COLUMN_NAME,
                        PersonTable.COLUMN_DAY,PersonTable.COLUMN_MONTH,PersonTable.COLUMN_YEAR,
                        PersonTable.COLUMN_DR,PersonTable.COLUMN_PAST_DAYS},
                null, null, null, null, PersonTable.COLUMN_NAME + " DESC");
    }

    // получить курсор с данными из таблицы TABLE_NAME
    public Cursor getAllDataSortDateUp() {
        SQLiteDatabase sd = getReadableDatabase();
        return sd.query(PersonTable.TABLE_NAME,
                new String[]{PersonTable._ID,PersonTable.COLUMN_NAME,
                        PersonTable.COLUMN_DAY,PersonTable.COLUMN_MONTH,PersonTable.COLUMN_YEAR,
                        PersonTable.COLUMN_DR,PersonTable.COLUMN_PAST_DAYS},
                null, null, null, null, PersonTable.COLUMN_PAST_DAYS);
    }

    // получить курсор с данными из таблицы TABLE_NAME
    public Cursor getAllDataSortDateDown() {
        SQLiteDatabase sd = getReadableDatabase();
        return sd.query(PersonTable.TABLE_NAME,
                new String[]{PersonTable._ID,PersonTable.COLUMN_NAME,
                        PersonTable.COLUMN_DAY,PersonTable.COLUMN_MONTH,PersonTable.COLUMN_YEAR,
                        PersonTable.COLUMN_DR,PersonTable.COLUMN_PAST_DAYS},
                null, null, null, null, PersonTable.COLUMN_PAST_DAYS + " DESC");
    }

    //метод поиска в базе данных из строки поиска по поисковому запросу query
    public Cursor searchInSQLite (String query){

        query = query.toLowerCase();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                PersonTable.TABLE_NAME,  //имя таблицы, к которой передается запрос
                new String[] {          //список имен возвращаемых полей
                        PersonTable._ID,
                        PersonTable.COLUMN_NAME,
                        PersonTable.COLUMN_DR,
                        PersonTable.COLUMN_PAST_DAYS },
                PersonTable.COLUMN_NAME + " LIKE" + "'%" + query + "%'", // условие выбора
                null,  //значения аргументов фильтра
                null,//фильтр для группировки
                null, //фильтр для группировки, формирующий выражение HAVING
                PersonTable.COLUMN_NAME ); //порядок сортировки


        if (cursor != null) {
            Log.d(TAG, "cursor1.getCount() = " + cursor.getCount() );
            while (cursor.moveToNext()) {
                String s = cursor.getString(cursor.getColumnIndex(PersonTable.COLUMN_NAME));
                Log.d(TAG, "Найдена строка " + s);
            }
        }else Log.d(TAG, "cursor1 = " + "null");

        return cursor;
    }

    //вывод в лог всех строк базы
    public void displayDatabaseInfo() {
        // Создадим и откроем для чтения базу данных
        SQLiteDatabase db = getReadableDatabase();

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

                // Выводим построчно значения каждого столбца
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

    //расчёт количества совместно прожитых миллисекунд
    public long getMillisForTwo(long id_first, long id_second){

        String[] data_first = getPersonData(id_first);
        String[] data_second = getPersonData(id_second);

        //экземпляр календаря с данными персоны1
        GregorianCalendar firstCalendar = new GregorianCalendar(Integer.parseInt(data_first[3]),
                Integer.parseInt(data_first[2]) - 1,Integer.parseInt(data_first[1]));
        //получаем дату в милисекундах
        long firstCalendarMillis = firstCalendar.getTimeInMillis();

        //экземпляр календаря с данными персоны2
        GregorianCalendar secondCalendar = new GregorianCalendar(Integer.parseInt(data_second[3]),
                Integer.parseInt(data_second[2]) - 1,Integer.parseInt(data_second[1]));
        //получаем дату в милисекундах
        long secondCalendarMillis = secondCalendar.getTimeInMillis();

        //текущее время в миллисекундах
        long nowTimeMillis = System.currentTimeMillis();

        //количество совместно прожитых миллисекунд
        long beenMillis = nowTimeMillis -firstCalendarMillis +
                nowTimeMillis - secondCalendarMillis;

        Log.d(TAG, "firstDays = " + (nowTimeMillis -firstCalendarMillis)/86400000  +
                "  secondDays = " + (nowTimeMillis -secondCalendarMillis)/86400000 +
                "  days = " + ((nowTimeMillis -firstCalendarMillis)/86400000 +
                (nowTimeMillis -secondCalendarMillis)/86400000));

        //data[0]  COLUMN_NAME
        //data[1]  COLUMN_DAY
        //data[2]  COLUMN_MONTH
        //data[3]  COLUMN_YEAR
        //data[4]  COLUMN_DR
        //data[5]  COLUMN_PAST_DAYS

        return beenMillis;
    }

}
