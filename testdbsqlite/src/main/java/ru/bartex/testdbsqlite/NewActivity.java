package ru.bartex.testdbsqlite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

public class NewActivity extends AppCompatActivity {

    public static final String TAG = "33333";
    Button btnOK;
    Button buttonCancel;
    static EditText etName;

    DBHelper mDbHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        etName = (EditText)findViewById(R.id.etName_test);

        btnOK = (Button) findViewById(R.id.btnOK);
        //Слушатель на кнопку ОК
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getName()) {
                    String name = etName.getText().toString();
                        //пишем в таблицу базы новую строку
                        long newRowId = mDbHelper.addPerson(name,0);
                        // Выводим сообщение в успешном случае или при ошибке
                        if (newRowId == -1) {
                            // Если ID  -1, значит произошла ошибка
                            Log.d(TAG, "Ошибка при заведении новой персоны ");
                        } else {
                            Log.d(TAG, "Персона заведена под номером: "  + newRowId );
                        }

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        buttonCancel = (Button) findViewById(R.id.buttonCancel);
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

    void myToast (String s){
        Toast mToast = Toast.makeText(NewActivity.this,s, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER,0,0);
        mToast.show();
    }
}
