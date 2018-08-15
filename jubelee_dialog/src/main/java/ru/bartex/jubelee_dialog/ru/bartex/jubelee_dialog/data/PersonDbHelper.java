package ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
        long result = sd.insert(PersonTable.TABLE_NAME, null, cv);
        return result;
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

}
