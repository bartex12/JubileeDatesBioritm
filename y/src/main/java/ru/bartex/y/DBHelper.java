package ru.bartex.y;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "33333";

    //Имя файла базы данных
    private static final String DATABASE_NAME = "city_info.db";
    // Версия базы данных. При изменении схемы увеличить на единицу
    private static final int DATABASE_VERSION = 1;

    public final static String TABLE_NAME = "persons_info";

    private final static String _ID = BaseColumns._ID;
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_CITY = "city";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Строка для создания таблицы
        String SQL_CREATE_PERSONS_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_CITY + " TEXT NOT NULL );";
        // Запускаем создание таблицы
        db.execSQL(SQL_CREATE_PERSONS_TABLE);
        Log.w(TAG, "Создана таблица persons_info");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Метод для добавления нового человека в список
     */
    public long addPerson(String name, String city) {
        // создаём объект ContentValues
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_CITY, city);
        // получаем базу данных для записи и пишем
        SQLiteDatabase sd = getWritableDatabase();
        //the row ID of the newly inserted row, or -1 if an error occurred
        long row_id = sd.insert(TABLE_NAME, null, cv);
        // закрываем соединение с базой
        sd.close();
        return  row_id;
    }

    public void deletePerson(long rowId){
        SQLiteDatabase sd = getWritableDatabase();
        sd.delete(TABLE_NAME, _ID + " =?", new String[]{String.valueOf(rowId)});
        sd.close();
    }

    public Cursor getPerson(long person_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_NAME, COLUMN_CITY},
                _ID + " =? ",
                new String[]{String.valueOf(person_id)},
                null, null, null);
        if (cursor!=null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public boolean updatePerson (long person_id, String name, String city){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_CITY, city);
        int result = db.update(TABLE_NAME, values, _ID + " = " + person_id, null);
        return result>0;
    }

    //метод поиска в базе данных из строки поиска по поисковому запросу query
    public Cursor searchInSQLite (String query){

        query = query.toLowerCase();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,  //имя таблицы, к которой передается запрос
                new String[] {          //список имен возвращаемых полей
                        _ID, COLUMN_NAME, COLUMN_CITY},
                COLUMN_CITY + " LIKE" + "'%" + query + "%'", // условие выбора
                null,  //значения аргументов фильтра
                null,//фильтр для группировки
                null, //фильтр для группировки, формирующий выражение HAVING
                null ); //порядок сортировки

        if (cursor != null) {
            Log.d(TAG, "cursor1.getCount() = " + cursor.getCount() );
            while (cursor.moveToNext()) {
                String s = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                Log.d(TAG, "Найдена строка " + s);
            }
        }else Log.d(TAG, "cursor1 = " + "null");

        return cursor;
    }

}
