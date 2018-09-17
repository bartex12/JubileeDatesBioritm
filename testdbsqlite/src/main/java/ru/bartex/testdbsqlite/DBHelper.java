package ru.bartex.testdbsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "33333";

    //Имя файла базы данных
    private static final String DATABASE_NAME = "testDB2.db";
    // Версия базы данных. При изменении схемы увеличить на единицу
    private static final int DATABASE_VERSION = 1;

    public final static String TABLE_NAME = "persons";

    public final static String _ID = BaseColumns._ID;
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_CHOOSE = "choose";
    public final static String COLUMN_ONE = "one";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Строка для создания таблицы
        String SQL_CREATE_PERSONS_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_CHOOSE + " INTEGER );";
        // Запускаем создание таблицы
        db.execSQL(SQL_CREATE_PERSONS_TABLE);
        Log.w(TAG, "Создана таблица persons");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Обновляемся с версии " + oldVersion + " на версию " + newVersion);
    }

    // Если записей в базе нет, вносим запись
    public void createDefaultPersonIfNeed()  {
        int count = this.getPersonsCount();
        Log.i(TAG, "MyDatabaseHelper createDefaultPersonIfNeed() count before =" + count );
        if(count ==0 ) {
            Person person1 = new Person("Анжелина Джоли",false);
            this.addPerson(person1);
            count = this.getPersonsCount();
            Log.i(TAG, "MyDatabaseHelper createDefaultPersonIfNeed() count after =" + count );
        }
    }

    //проверка существования таблицы
    public boolean existsTable(String table) {

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + table;
            db.rawQuery(query, null);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    //Метод для добавления нового человека в список
    public void addPerson(Person person) {
        Log.d(TAG, "MyDatabaseHelper.addPerson ... " + person.getPerson_name());

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, person.getPerson_name());
        cv.put(COLUMN_CHOOSE, person.getPerson_choose());
        // вставляем строку
        db.insert(TABLE_NAME, null, cv);
        // закрываем соединение с базой
        db.close();
    }

    /**
     * Метод для добавления нового человека в список
     */
    public long addPerson(String name, int choose) {
        // создаём объект ContentValues
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_CHOOSE, choose);
        // получаем базу данных для записи и пишем
        SQLiteDatabase sd = getWritableDatabase();
        //the row ID of the newly inserted row, or -1 if an error occurred
        long row_id = sd.insert(TABLE_NAME, null, cv);
        // закрываем соединение с базой
        sd.close();
        return  row_id;
    }

    //получаем количество записей в базе
    public int getPersonsCount() {
        Log.i(TAG, "MyDatabaseHelper.getPersonsCount ... " );
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     * Метод обновления строки списка
     */
    public boolean updatePerson(long rowId, String name, int choose) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(COLUMN_NAME, name);
        updatedValues.put(COLUMN_CHOOSE, choose);

        long result = db.update(TABLE_NAME, updatedValues, _ID + "=" + rowId, null);
        //db.close();
        return  result > 0;
    }

    /**
     * Удаляет элемент списка
     */
    public void deletePerson(long rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, _ID + "=" + rowId, null);
        db.close();
    }

    // получить курсор с данными из таблицы TABLE_NAME
    public Cursor getAllData() {
        SQLiteDatabase sd = this.getReadableDatabase();
        return sd.query(TABLE_NAME,
                new String[]{_ID, COLUMN_NAME, COLUMN_CHOOSE},
                null, null, null, null, null);
    }

    //получаем ID по имени
    public long getIdFromName(String name){
        long currentID;
        // Создадим и откроем для чтения базу данных
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,   // таблица
                new String[] {_ID},            // столбцы
                COLUMN_NAME + "=?" ,                  // столбцы для условия WHERE
                new String[] {name},                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // порядок сортировки

        if (cursor != null) {
            cursor.moveToFirst();
            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(_ID);
            // Используем индекс для получения строки или числа
            currentID = cursor.getLong(idColumnIndex);
        }else {
            currentID = -1;
        }
        Log.d(TAG, "getIdFromName currentID = " + currentID);
        cursor.close();
        return currentID;
    }

    public ArrayList<Person> getAllContacts() {
        ArrayList<Person> contactList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.i(TAG, "MyDatabaseHelper.getAllPersons IN");

        if (cursor.moveToFirst()) {
            do {
                Person contact = new Person();
                contact.setPerson_id(cursor.getLong(0));
                contact.setPerson_name(cursor.getString(1));
                if (cursor.getInt(2) ==0 ){
                    contact.setPerson_choose(false);
                }else contact.setPerson_choose(true);
                contactList.add(contact);

                Log.i(TAG, "  id = " + contact.getPerson_id()+
                        "  name = " + contact.getPerson_name() +
                        "  choose  = " + contact.getPerson_choose());

            } while (cursor.moveToNext());
        }
        Log.i(TAG, "MyDatabaseHelper.getAllPersons OUT");
        for (int i = 0; i< contactList.size(); i++){
            Log.i(TAG, "  id = " + contactList.get(i).getPerson_id()+
                    "  name = " + contactList.get(i).getPerson_name() +
                    "  choose  = " + contactList.get(i).getPerson_choose());
        }
        Log.d(TAG, "MyDatabaseHelper.getAllPersons ... размер списка = " + contactList.size());

        cursor.close();
        return contactList;
    }

    public Cursor getPerson(long rowId) throws SQLException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.query(true, TABLE_NAME,
                new String[] { _ID, COLUMN_NAME, COLUMN_CHOOSE},
                _ID + "=" + rowId,
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
}
