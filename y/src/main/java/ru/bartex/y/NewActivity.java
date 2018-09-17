package ru.bartex.y;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class NewActivity extends AppCompatActivity {
    public static final String TAG = "33333";
    Button btnOK;
    Button buttonCancel;
     EditText etName;
     EditText etCity;
    public static final String REQUEST_CODE = "ru.bartex.y.request_code";
    public static final String SQL_ID = "ru.bartex.y.sql_id";
    int request_code;
    long person_id;

    DBHelper mDbHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        etName = findViewById(R.id.etName_test);
        etCity = findViewById(R.id.etCity_test);

        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            request_code = extras.getInt(REQUEST_CODE);
            Log.d(TAG, "NewActivity onCreate request_code"  + request_code );
        }

        if (request_code == 111){
            person_id = extras.getLong(SQL_ID);
            Cursor cursor = mDbHelper.getPerson(person_id);
            if (cursor!=null){
               String name =  cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));
               String city = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CITY));
                etName.setText(name);
                etCity.setText(city);
            }
        }

        btnOK = findViewById(R.id.btnOK);
        //Слушатель на кнопку ОК
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getName()&&getCity()) {
                    String name = etName.getText().toString();
                    String city = etCity.getText().toString();
                    if (request_code == 111){
                       boolean change =  mDbHelper.updatePerson(person_id,name,city);
                        if (!change){
                            Log.d(TAG, "Ошибка при редактировании персоны " + person_id);
                        }else {
                            Log.d(TAG, "Отредактирована персона  под номером: "  + person_id);
                        }

                    }else {
                        //пишем в таблицу базы новую строку
                        long newRowId = mDbHelper.addPerson(name,city);
                        // Выводим сообщение в успешном случае или при ошибке
                        if (newRowId == -1) {
                            // Если ID  -1, значит произошла ошибка
                            Log.d(TAG, "Ошибка при заведении новой персоны ");
                        } else {
                            Log.d(TAG, "Персона заведена под номером: "  + newRowId );
                        }
                    }

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        buttonCancel = findViewById(R.id.buttonCancel);
        //Слушатель на кнопку Cancel
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    boolean getName(){
        boolean n;
        String s = etName.getText().toString();
        if (s.length() == 0) {
            n = false;
            myToast ("Введите имя");
        } else if (s.equals("Новое имя")){
            Random rnd = new Random(System.currentTimeMillis());
            int q = rnd.nextInt(1000);
            n = true;
            s =s + " " + q;
            etName.setText(s);
        }else n = true;
        return n;
    }

    boolean getCity(){
        boolean n;
        String s = etCity.getText().toString();
        if (s.length() == 0) {
            n = false;
            myToast ("Введите город");
        } else if (s.equals("Новое имя")){
            Random rnd = new Random(System.currentTimeMillis());
            int q = rnd.nextInt(1000);
            n = true;
            s =s + " " + q;
            etCity.setText(s);
        }else n = true;
        return n;
    }

    void myToast (String s){
        Toast mToast = Toast.makeText(NewActivity.this,s, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER,0,0);
        mToast.show();
    }
}
