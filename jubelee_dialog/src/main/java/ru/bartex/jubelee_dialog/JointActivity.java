package ru.bartex.jubelee_dialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;

public class JointActivity extends AppCompatActivity implements TextWatcher{

    public final String TAG = "33333";
    public static final String ID_SQL = "sqlJointActivity";
    public static final String ID_SQL_second = "sql_second_JointActivity";
    public static final int REQUEST_JOINT_PERSON1 = 1; //риквест код от JointActivity для персоны 1
    public static final int REQUEST_JOINT_PERSON2 = 2; //риквест код от JointActivity для персоны 2
    public static final int REQUEST_JOINT_FIND = 3; //риквест код от JointActivity для поиска совместных дат
    public static final String REQUEST_JOINT_CHOOSE = "choose_JointActivity";;

    final static String ATTR_ID1 ="id1";
    final static String ATTR_ID2 ="id2";

    int daysNext; //количество совместно прожитых дней для расчёта
    long id_sql;  // id строки из базы данных
    long id_sql_second;  // id другой строки из базы данных
    long millisForTwo; // количество прожитых миллисекунд на двоих
    long daysPast; //Количество уже прожитых дней на двоих

    Button mPersonButton1;
    Button mPersonButton2;
    Button mCount;

    TextView mWillBe;//расчётное количество совместно прожитых дней
    TextView forTwo_Days; //уже прожито дней
    EditText mDays;//задаём количество совместно прожитых дней

    Person mPerson;
    PersonDbHelper mDbHelper = new PersonDbHelper(this);
    private static final String KEY_ID_SQL = "ID_SQL";
    private static final String KEY_ID_SQL_SECOND = "ID_SQL_SECOND";
    int request=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint);

        Log.d(TAG, "JointActivity onCreate");
        //только портретная ориентация
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setHomeButtonEnabled(true);

        mPersonButton1 = (Button) findViewById(R.id.buttonNamePerson1);
        mPersonButton2 = (Button) findViewById(R.id.buttonNamePerson2);

        mDays = (EditText) findViewById(R.id.jointDays);
        mDays.addTextChangedListener(this);

        forTwo_Days = (TextView) findViewById(R.id.forTwo_Days);
        mWillBe = (TextView)findViewById(R.id.jointWillBe);

        mCount = (Button) findViewById(R.id.jointFind);
        mCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ss = mDays.getText().toString();
                //если строка не пустая и от 0 до 100000
                if (getDays(ss)) {
                    //считываем количество дней для расчёта
                    daysNext = Integer.parseInt(ss);
                    //Количество уже прожитых дней на двоих
                    daysPast = millisForTwo/86400000;
                    //половина разницы
                    long deltaDays = (daysNext-daysPast)/2;
                    Log.d(TAG, "deltaDays " + deltaDays);
                    //получаем экземпляр календаря
                    Calendar firstCalendar = new GregorianCalendar();
                    long date_now = System.currentTimeMillis();
                    //устанавливаем календарь в текущую дату, выраженную в милисекундах
                    firstCalendar.setTimeInMillis(System.currentTimeMillis());
                    //Добавляем к указанной на экране дате указанное количество дней
                     firstCalendar.add(Calendar.DAY_OF_YEAR, (int) deltaDays);
                    long date_next = firstCalendar.getTimeInMillis();
                    //получаем день месяца расчётной даты
                    int dayNumber = firstCalendar.get(Calendar.DAY_OF_MONTH);
                    //получаем месяц расчётной даты
                    int  mounthNumber = firstCalendar.get(Calendar.MONTH);
                    //получаем год расчётной даты
                    int  yearNumber = firstCalendar.get(Calendar.YEAR);

                    TextView txt = (TextView)findViewById(R.id.textView6);
                    //текст в зависимости от соотношения расчётной даты и сейчас
                    if (date_next>date_now){
                        txt.setText("Это будет");
                    }else if (date_next<date_now){
                        txt.setText("Это было");
                    }else {
                        txt.setText("Это сегодня ");
                    }

                    Log.d(TAG, "Расчётная дата MainActivity onFindDateSimple= " + dayNumber + "." +
                            (mounthNumber + 1) + "." + yearNumber);
                    String s1 = String.format("%02d.%02d.%04d",
                            dayNumber, mounthNumber + 1, yearNumber);
                    mWillBe.setText(s1);
                    mCount.setEnabled(false);
                }
            }
        });
        //если что то было сохранено, грузим отсюда
        if (savedInstanceState != null) {
            id_sql = savedInstanceState.getLong(KEY_ID_SQL);
            id_sql_second = savedInstanceState.getLong(KEY_ID_SQL_SECOND);
            Log.d(TAG,"savedInstanceState:  id_sql = " + id_sql + "   id_sql_second = " + id_sql_second);
        }
            //Загружаем данные из интента
            Intent intent = getIntent();
            request = intent.getIntExtra(REQUEST_JOINT_CHOOSE, request);
            if (request == FindDatesActivity.REQUEST_CHOOSE){
                id_sql = intent.getLongExtra(ATTR_ID1,1);
                id_sql_second = intent.getLongExtra(ATTR_ID2,id_sql);
                Log.d(TAG, "JointActivity onCreate:  id1 = " + id_sql + "  id2 = " +id_sql_second);

                //получаем данные персоны 1 из базы данных
                mPerson = mDbHelper.getPersonObjectData(id_sql);
                if (mPerson!=null){
                    Log.d(TAG, "JointActivity onCreate:  mPersonButton1.setText = " +
                            mPerson.getPerson_name() + "  /" + mPerson.getPerson_past_days()+"/");
                }else  Log.d(TAG, "JointActivity onCreate mPerson1 = null");

                mPersonButton1.setText(mPerson.getPerson_name() + "  /" + mPerson.getPerson_past_days()+"/");

                //получаем данные персоны 2 из базы данных
                mPerson = mDbHelper.getPersonObjectData(id_sql_second);
                if (mPerson!=null){
                    Log.d(TAG, "JointActivity onCreate:  mPersonButton2.setText = " +
                            mPerson.getPerson_name() + "  /" + mPerson.getPerson_past_days()+"/");
                }else  Log.d(TAG, "JointActivity onCreate mPerson2 = null");
                mPersonButton2.setText(mPerson.getPerson_name() + "  /" + mPerson.getPerson_past_days()+"/");

                // количество прожитых миллисекунд на двоих
                millisForTwo = mDbHelper.getMillisForTwo(id_sql,id_sql_second);
                if (millisForTwo>0){
                    Log.d(TAG, "JointActivity onCreate:  forTwo_Days.setText = " +
                            "На двоих прожито  " + millisForTwo/86400000 + "  дней");
                }else {
                    Log.d(TAG, "JointActivity onCreate:  forTwo_Days  millisForTwo>0)");
                    forTwo_Days.setText("На двоих прожито  " + millisForTwo / 86400000 + "  дней");
                }
            }else {
                id_sql = intent.getLongExtra(ID_SQL,1);
                id_sql_second = intent.getLongExtra(ID_SQL_second,id_sql);
            }

            Log.d(TAG,"Intent :  id_sql = " + id_sql + "   id_sql_second = " + id_sql_second);


        //получаем данные в соответствии с первым id
        mPerson = mDbHelper.getPersonObjectData(id_sql);

        mPersonButton1.setText(mPerson.getPerson_name() + "  /" + mPerson.getPerson_past_days()+"/");
        mPersonButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //запускаем интент и получаем обратно id персоны 1
                Intent intentPerson1  = new Intent(JointActivity.this, ListDialog.class);
                intentPerson1.putExtra(ListDialog.REQUEST_PERSON, REQUEST_JOINT_PERSON1);
                startActivityForResult(intentPerson1,REQUEST_JOINT_PERSON1);
            }
        });

        //получаем данные в соответствии со вторым id
        mPerson = mDbHelper.getPersonObjectData(id_sql_second);

        mPersonButton2.setText(mPerson.getPerson_name() + "  /" + mPerson.getPerson_past_days()+"/");
        mPersonButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //запускаем интент и получаем обратно id персоны 2
                Intent intentPerson2  = new Intent(JointActivity.this, ListDialog.class);
                intentPerson2.putExtra(ListDialog.REQUEST_PERSON, REQUEST_JOINT_PERSON2);
                startActivityForResult(intentPerson2,REQUEST_JOINT_PERSON2);
            }
        });

        // количество прожитых миллисекунд на двоих
        millisForTwo = mDbHelper.getMillisForTwo(id_sql, id_sql_second);
        // количество прожитых дней на двоих
        forTwo_Days.setText("На двоих прожито  " + millisForTwo/86400000 + "  дней" );
        //делаем кнопку Рассчитань недоступной
        mCount.setEnabled(false);
    }

    //сохраняем значения id на случай поворота экрана
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_ID_SQL, id_sql);
        outState.putLong(KEY_ID_SQL_SECOND, id_sql_second);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "JointActivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "JointActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "JointActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "JointActivity onDestroy");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "JointActivity onActivityResult");
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_JOINT_PERSON1){
                Log.d(TAG, "JointActivity onActivityResult REQUEST_JOINT_PERSON 1");
                //получаем id выбранной персоны1
                id_sql = data.getLongExtra(ID_SQL,id_sql);
                //получаем данные персоны1 из базы данных
                mPerson = mDbHelper.getPersonObjectData(id_sql);
                mPersonButton1.setText(mPerson.getPerson_name() + "  /" + mPerson.getPerson_past_days()+"/");

            }else if (requestCode == REQUEST_JOINT_PERSON2){
                Log.d(TAG, "JointActivity onActivityResult REQUEST_JOINT_PERSON 2");
                //получаем id выбранной персоны2
                id_sql_second = data.getLongExtra(ID_SQL_second, id_sql_second);
                //получаем данные персоны2 из базы данных
                mPerson = mDbHelper.getPersonObjectData(id_sql_second);
                mPersonButton2.setText(mPerson.getPerson_name() + "  /" + mPerson.getPerson_past_days()+"/");

            }else if (requestCode == REQUEST_JOINT_FIND){
                Log.d(TAG, "JointActivity onActivityResult REQUEST_JOINT_FIND");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.joint, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                Log.d(TAG, "Домой");
                onBackPressed();
                return true;

            case R.id.action_find_dates:
                Log.d(TAG, "OptionsItem = action_find_dates");
                Intent intentFindDates = new Intent(this, ListDialog_CheckBox.class);
                startActivity(intentFindDates);
                return true;

            case R.id.action_settings:
                Log.d(TAG, "OptionsItem = action_settings");
                Intent intentSettings = new Intent(this, PrefActivity.class);
                startActivity(intentSettings);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
    @Override
    public void afterTextChanged(Editable editable) {
        //пишем прочерки в поле mWillBe
        mWillBe.setText(R.string.better_late);
        //Делаем доступной кнопку Рассчитать, если изменили данные в любом EditText
        mCount.setEnabled(true);
    }

    boolean getDays (String s){
        boolean ds;
        if (s.equals("")) {
            daysNext = 0;
            ds = false;
            myToast ("Введите желаемое число прожитых дней");
        }else {
            int i = Integer.parseInt(s);
            if (i>=0 && i<= 100000){
                daysNext = i;
                ds = true;
            }else {
                ds = false;
                myToast ("Количество прожитых дней\nЧисло от 0 до 100000");
            }
        }
        return ds;
    }

    void myToast (String s){
        Toast mToast = Toast.makeText(JointActivity.this,s, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER,0,0);
        mToast.show();
    }

}