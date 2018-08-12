package ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Андрей on 11.08.2018.
 */
public class PersonDbHelper extends SQLiteOpenHelper{

    public static final String TAG = "33333";

    //Имя файла базы данных
    private static final String DATABASE_NAME = "bioritmDataBase1.db";

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
                + PersonTable.COLUMN_DR + " TEXT NOT NULL, "
                + PersonTable.COLUMN_PAST_DAYS + " INTEGER NOT NULL DEFAULT 0);";

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

    // метод для добавления человека
    public long addPerson(String name, String dr, long pastDays) {
        // создаём объект ContentValues
        ContentValues cv = new ContentValues();
        cv.put(PersonTable.COLUMN_NAME, name);
        cv.put(PersonTable.COLUMN_DR, dr);
        cv.put(PersonTable.COLUMN_PAST_DAYS, pastDays);
        // получаем базу данных для записи и пишем
        SQLiteDatabase sd = getWritableDatabase();
        //the row ID of the newly inserted row, or -1 if an error occurred
        long result = sd.insert(PersonTable.TABLE_NAME, null, cv);
        return result;
    }

    // получить все данные из таблицы TABLE_NAME
    public Cursor getAllData() {
        SQLiteDatabase sd = getReadableDatabase();
        return sd.query(PersonTable.TABLE_NAME, null, null, null, null, null, null);
    }

}
