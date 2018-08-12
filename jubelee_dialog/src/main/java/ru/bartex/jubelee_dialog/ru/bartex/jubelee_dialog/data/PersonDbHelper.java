package ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Андрей on 11.08.2018.
 */
public class PersonDbHelper extends SQLiteOpenHelper{

    public static final String TAG = "33333";

    /**
     * Имя файла базы данных
     */
    private static final String DATABASE_NAME = "bioritmDataBase.db";

    /**
     * Версия базы данных. При изменении схемы увеличить на единицу
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Конструктор {@link PersonDbHelper}.
     *
     * @param context Контекст приложения
     */

    public PersonDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Вызывается при создании базы данных
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Строка для создания таблицы
        String SQL_CREATE_PERSONS_TABLE = "CREATE TABLE " + PersonContract.PersonEntry.TABLE_NAME + " ("
                + PersonContract.PersonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PersonContract.PersonEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + PersonContract.PersonEntry.COLUMN_DAY + " INTEGER NOT NULL DEFAULT 0, "
                + PersonContract.PersonEntry.COLUMN_MONTH + " INTEGER NOT NULL DEFAULT 0, "
                + PersonContract.PersonEntry.COLUMN_YEAR + " INTEGER NOT NULL DEFAULT 0);";

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

}
