package ru.bartex.jubelee_dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.app.FragmentManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonContract;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;

public class NewActivity extends AppCompatActivity  {

    public final String TAG = "33333";
    public static final String REQUEST_CODE = "request_codeNewActivity";
    public static final String PERSON_NAME = "personNameNewActivity";
    public static final String DAY_NUMBER = "dayNumberNewActivity";
    public static final String MOUNTH_NUMBER = "mounthNumberNewActivity";
    public static final String YEAR_NUMBER = "yearNumberNewActivity";
    public static final String DAYS_NUMBER = "daysNumberNewActivity";
    public static final String POSITION = "positionNewActivity";

    static EditText etName, etDay, etMounth, etYear;

    int dayNumber,mounthNumber, yearNumber, daysNumber;
    int requestCode;
    int position=0;

    Button btnOK;
    Button buttonCancel;
    Button buttonClear;
    ImageButton btnDateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        etName = (EditText) findViewById(R.id.etName);
        etDay = (EditText) findViewById(R.id.etDay);
        etMounth = (EditText) findViewById(R.id.etMonth);
        etYear = (EditText) findViewById(R.id.etYear);


        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            //requestCode = extras.getInt(REQUEST_CODE);
            if (extras.getInt(REQUEST_CODE) == 111) {
                etName.setText(extras.getString(PERSON_NAME));
                etDay.setText(extras.getString(DAY_NUMBER));
                etMounth.setText(extras.getString(MOUNTH_NUMBER));
                etYear.setText(extras.getString(YEAR_NUMBER));
                position = extras.getInt(POSITION);
            }
        }

        buttonClear  = (Button) findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setText("");
                //Устанавливаем фокус ввода в поле etName
                etName.requestFocus();
                //Вызываем экранную клавиатуру -метод работает как в 4.0.3, так и в 6.0
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etName, 0);
                //так тоже работает
                //imm.showSoftInput(etName, InputMethodManager.SHOW_IMPLICIT);
                //вызов диалога выбора клавиатуры
                //imm.showInputMethodPicker();

            }
        });
        btnOK = (Button) findViewById(R.id.btnOK);
        //Слушатель на кнопку ОК
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean tempDay = getDay();
                boolean tempMounth = getMounth();
                boolean tempYear = getYear();
                //boolean temp4 = getDays();
                boolean tempName = getName();

                if (tempDay && tempMounth && tempYear && tempName) {

                    String name = etName.getText().toString();
                    String day = etDay.getText().toString();
                    String mounth = etMounth.getText().toString();
                    String year = etYear.getText().toString();
                    //формируем строку даты
                    String dr = String.format("%s.%s.%s",day,mounth,year);
                    //экземпляр календаря с данными из списка
                    Calendar firstCalendar = new GregorianCalendar(Integer.parseInt(year),
                            Integer.parseInt(mounth) - 1,Integer.parseInt(day));
                    //получаем дату в милисекундах
                    long firstCalendarMillis = firstCalendar.getTimeInMillis();
                    long nowTimeMillis = System.currentTimeMillis();
                    //количество прошедших дней с даты рождения
                    long beenDays = (nowTimeMillis-firstCalendarMillis)/86400000;
                    //количество прожитых дней
                    String past_days = Long.toString(beenDays);

                    //формируем строку имя__день.месяц.год__прожитоДней
                    String namesDateLived = String.format("%s  %s  %s", name, dr, past_days);
                    Log.d(TAG, "Введено NewActivity btnOK = " + namesDateLived);

                    Intent intent = new Intent();
                    intent.putExtra("namesDateLived", namesDateLived);
                    intent.putExtra("position", position);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                insertPerson();
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


        btnDateDialog = (ImageButton) findViewById(R.id.buttonDateDialog1);
        //Слушатель на кнопку ввода диалога DatePickerFragment
        btnDateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager manager = getFragmentManager();
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(manager,"DatePicker");
            }
        });
    }

    public static class DatePickerFragment extends DialogFragment implements
                             DatePickerDialog.OnDateSetListener{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            DatePickerDialog dpd = new DatePickerDialog(getActivity(),this,1980,6,19);
            return dpd;
        }
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            etDay.setText(Integer.toString(dayOfMonth));
            etMounth.setText(Integer.toString(month+1));
            etYear.setText(Integer.toString(year));
        }
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

    boolean getDay(){
        boolean d;
        if (etDay.getText().toString().equals("")) {
            dayNumber = 0;
            d = false;
            myToast ("Введите день месяца");
        }else {
            int i = Integer.parseInt(etDay.getText().toString());
            if (i>0 && i<=31) {
                dayNumber = i;
                d = true;
            }else {
                d = false;
                myToast ("День месяца\nВведите число в диапазоне 1-31");
            }
        }
        return d;
    }

    boolean getMounth(){
        boolean m;
        if (etMounth.getText().toString().equals("")) {
            mounthNumber = 0;
            m = false;
            myToast ("Введите месяц рождения");
        }else {
            int i = Integer.parseInt(etMounth.getText().toString());
            if (i>=1 && i<=12) {
                mounthNumber = i;
                m = true;
            }else {
                m = false;
                myToast ("Месяц:\nВведите число в диапазоне 1-12");
            }
        }
        return m;
    }

    boolean getYear(){
        boolean y;
        if (etYear.getText().toString().equals("")) {
            yearNumber = 0;
            y = false;
            myToast ("Введите год рождения");
        }else {

            int i = Integer.parseInt(etYear.getText().toString());
            if (i>=1900 && (i<= (new GregorianCalendar()).get(Calendar.YEAR))) {
                //Log.d(TAG,"YEAR = " + (new GregorianCalendar()).get(Calendar.YEAR));
                yearNumber = i;
                y = true;
            }else {
                y = false;
                myToast ("Год рождения:\nЧисло от 1900 до  " + (new GregorianCalendar()).get(Calendar.YEAR));
            }
        }
        return y;
    }
/*
    boolean getDays (){
        boolean ds;
        if (etDays.getText().toString().equals("")) {
            daysNumber = 0;
            ds = false;
            myToast ("Введите желаемое число прожитых дней");
        }else {

            int i = Integer.parseInt(etDays.getText().toString());
            if (i>=0 && i<= 50000){
                daysNumber = i;
                ds = true;
            }else {
                ds = false;
                myToast ("Количество прожитых дней\nЧисло от 0 до 50000");
            }
        }
        return ds;
    }
    */

    void myToast (String s){
        Toast mToast = Toast.makeText(NewActivity.this,s, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER,0,0);
        mToast.show();
    }

    private void insertPerson() {

        String name = etName.getText().toString().trim();
        String dayString = etDay.getText().toString().trim();
        String monthString = etMounth.getText().toString().trim();
        String yearString = etYear.getText().toString().trim();

        int day = Integer.parseInt(dayString);
        int month = Integer.parseInt(monthString);
        int year = Integer.parseInt(yearString);

        PersonDbHelper mDbHelper = new PersonDbHelper(this);
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Создаем объект ContentValues, где имена столбцов ключи,
        // а информация о госте является значениями ключей
        ContentValues values = new ContentValues();
        values.put(PersonContract.PersonEntry.COLUMN_NAME, name);
        values.put(PersonContract.PersonEntry.COLUMN_DAY, day);
        values.put(PersonContract.PersonEntry.COLUMN_MONTH, month);
        values.put(PersonContract.PersonEntry.COLUMN_YEAR, year);

        // Вставляем новый ряд в базу данных и запоминаем его идентификатор
        long newRowId = db.insert(PersonContract.PersonEntry.TABLE_NAME, null, values);

        // Выводим сообщение в успешном случае или при ошибке
        if (newRowId == -1) {
            // Если ID  -1, значит произошла ошибка
            Toast.makeText(this, "Ошибка при заведении новой персоны", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Ошибка при заведении новой персоны ");
        } else {
            Toast.makeText(this, "Персона заведена под номером: " + newRowId, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Персона заведена под номером: "  + newRowId );
        }
    }

}
