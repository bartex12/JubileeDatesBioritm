package ru.bartex.jubelee_dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;

public class TimeActivity extends AppCompatActivity implements
        TextWatcher {

    public final String TAG = "33333";
    public static final String ID_SQL = "sqlTimeActivity";

    TextView willBe, lastDays, userName, dataBorn;
    static EditText  days;
    Button findDate;//кнопка Рассчитать

    int dayNumber,mounthNumber, yearNumber, daysNumber;
    int dayNumberNext,mounthNumberNext, yearNumberNext;

    long id_sql;  // id строки из базы данных

    private Calendar firstCalendar;
    private Timer mTimer;
    private TimerTask mTimerTask;

    Toast mToast;

    long nowTimeMillis;
    long firstCalendarMillis;
    long beenDays;
    long beenDays1;
    private long mKvant = 100;//время в мс между срабатываниями TimerTask


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        //только портретная ориентация
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true );
        act.setHomeButtonEnabled(true);

        findDate = (Button)findViewById(R.id.buttonFind);
        findDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "findDate.setOnClickListener ");
                onFindDateSimple();
                onFindDateКеу();
            }
        });

        willBe = (TextView)findViewById(R.id.textViewWillBe);
        lastDays = (TextView)findViewById(R.id.textViewLastDays);
        userName   = (TextView)findViewById(R.id.textViewUserName);
        dataBorn = (TextView)findViewById(R.id.textViewDataBorn);

        days = (EditText)findViewById(R.id.editTextDays);
        days.addTextChangedListener(this);

        //Загружаем данные из интента
        Intent intent = getIntent();
        id_sql = intent.getLongExtra(ID_SQL,0);

        //получаем экхземпляр PersonDbHelper
        PersonDbHelper mPersonDbHelper = new PersonDbHelper(this);
        //получаем  данные для строки с id
        Person person = mPersonDbHelper.getPersonObjectData(id_sql);

        //рассчитываем дату рождения
        dayNumber = Integer.parseInt(person.getPerson_day());
        mounthNumber = Integer.parseInt(person.getPerson_month());
        yearNumber = Integer.parseInt(person.getPerson_year());

        //пишем имя
        userName.setText(person.getPerson_name());
        //пишем дату рождения
        dataBorn.setText(person.getPerson_dr());

        days.setText("10000");
        //устанавливаем фокус в конце строки
        days.requestFocus();

        mToast = new Toast(this);
        mToast.setGravity(Gravity.CENTER,0,0);

        //вызов обработчика кнопки Рассчитать без скрытия клавиатуры
        //здесь запускаем myTimerTask
        onFindDateSimple();
    }

    //==================================Конец onCreate====================================//


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Сохраняем текст
        //saveText();
        if (mTimer != null) mTimer.cancel();

    }

    //вызов диалогового окна при нажатии на кнопку Назад
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "MainActivity onBackPressed");
        //Intent intent = new Intent(MainActivity.this, BioritmActivity.class);
        //startActivity(intent);
        //finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        /*
        //если делать через shareActionProvider, в 4.0.4 не работает,
        //придётся через меню case R.id.action_share: разница только в способе обработки
        MenuItem item = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setNewIntent("This is example text");
        */
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            //чтобы работала стрелка Назад, а не происходил крах приложения
            case android.R.id.home:
                Log.d(TAG, "Домой");
                onBackPressed();
                return true;

            case R.id.action_settings:
                Log.d(TAG, "OptionsItem = action_settings");
                Intent intentSettings = new Intent(this, PrefActivity.class);
                startActivity(intentSettings);
                return true;

            case R.id.action_table:
                Log.d(TAG, "action_table");
                Intent intent = new Intent(TimeActivity.this,TableActivity.class);
                intent.putExtra(TableActivity.ID_SQL,id_sql);
                startActivity(intent);
                return true;

            case R.id.action_share:
                Log.d(TAG, "action_share");
                Bitmap bm = screenShot(this);
                File file = saveBitmap(bm, "screen_image.png");
                Log.d(TAG, "AbsolutePath: " + file.getAbsolutePath());
                Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
                Log.d(TAG, "uri: " + uri);
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my app.");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "share via"));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //=================================Функции====================================//

    //Сделать Bitmap по Activity
    private Bitmap screenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    //сохранить на SD картинку - скриншот экрана
    private  File saveBitmap(Bitmap bm, String fileName){

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return null;
        }
        final String path = Environment.getExternalStorageDirectory() + "/Screenshots";
        Log.d(TAG, "isExternalStorageEmulated = " + Environment.isExternalStorageEmulated() +
                "    getExternalStorageState = " + Environment.getExternalStorageState());
        Log.d(TAG, "path: " + path);

        File dir = new File(path);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, fileName);
        //file.setReadable(true);
        //File file = new File(this.getFilesDir(), fileName);
        //File file = new File(this.getCacheDir(), fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            //FileOutputStream fOut = openFileOutput(fileName, Context.MODE_PRIVATE);
            bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "file.length: " + file.length() + "  getFreeSpace: "+ file.getFreeSpace());
        return file;
    }

    boolean getDays (String s){
        boolean ds;
        if (s.equals("")) {
            daysNumber = 0;
            ds = false;
            //ds = true;
            myToast ("Введите желаемое число прожитых дней");
        }else {
            int i = Integer.parseInt(s);
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

    //обработка нажатий на кнопку Рассчитать для метода onCreate
    public void onFindDateSimple(){

        String ss = days.getText().toString();
        if (getDays(ss)) {
            daysNumber = Integer.parseInt(ss);

            Log.d(TAG, "daysNumber " + daysNumber);
            //экземпляр календаря с данными с экрана
            firstCalendar = new GregorianCalendar(yearNumber, mounthNumber - 1, dayNumber);
            //получаем дату в милисекундах
            firstCalendarMillis = firstCalendar.getTimeInMillis();
            //Добавляем к указанной на экране дате указанное количество дней
            firstCalendar.add(Calendar.DAY_OF_YEAR, daysNumber);
            //получаем день месяца расчётной даты
            dayNumberNext = firstCalendar.get(Calendar.DAY_OF_MONTH);
            //получаем месяц расчётной даты
            mounthNumberNext = firstCalendar.get(Calendar.MONTH);
            //получаем год расчётной даты
            yearNumberNext = firstCalendar.get(Calendar.YEAR);
            Log.d(TAG, "Расчётная дата MainActivity onFindDateSimple= " + dayNumberNext + "." +
                    (mounthNumberNext + 1) + "." + yearNumberNext);

            if (mTimer != null) mTimer.cancel();
            mTimer = new Timer();
            mTimerTask = new myTimerTask();
            //запускаем задачу по расписанию = контроль каждые mKvant (100мс)
            mTimer.scheduleAtFixedRate(mTimerTask, mKvant, mKvant);

            findDate.setEnabled(false);
        }
    }

    public void onFindDateКеу(){
        //Прячем экранную клавиатуру
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //действия в случае ручного обновления текстовых полей на экране
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        findDate.setEnabled(false);
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}
    @Override
    //Запрещаем вывод после обновления информации до нажатия на кнопку Рассчитать
    public void afterTextChanged(Editable s) {
        //buttonShowTable.setEnabled(false);
        if (mTimer != null) mTimer.cancel();
        willBe.setText(R.string.better_late);
        //Делаем доступной кнопку Рассчитать, если изменили данные в любом EditText
        findDate.setEnabled(true);
    }

    public class myTimerTask extends TimerTask{
        @Override
        public void run() {
            //текущеевремя
            nowTimeMillis = System.currentTimeMillis();
            //количество прошедших дней с даты рождения
            beenDays = (nowTimeMillis-firstCalendarMillis)/86400000;
            //количество долей текущего дня
            beenDays1 = (nowTimeMillis-firstCalendarMillis)%86400000*10/864;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //показываем результаты в пользовательском потоке каждые 100мс
                    String s = String.format("%d.%06d",beenDays,beenDays1);
                    lastDays.setText(s);
                    String s1 = String.format("%02d.%02d.%04d",
                            dayNumberNext, mounthNumberNext + 1, yearNumberNext);
                    willBe.setText(s1);
                }
            });
        }
    }

    void myToast (String s){
        Toast mToast = Toast.makeText(TimeActivity.this,s, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER,0,0);
        mToast.show();
    }

}
