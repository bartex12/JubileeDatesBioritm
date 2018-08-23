package ru.bartex.jubelee_dialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;

public class JointActivity extends AppCompatActivity {

    public final String TAG = "33333";
    public static final String ID_SQL = "sqlJointActivity";
    public static final String ID_SQL_second = "sql_second_JointActivity";
    public static final int REQUEST_JOINT_PERSON1 = 1; //риквест код от JointActivity для персоны 1
    public static final int REQUEST_JOINT_PERSON2 = 2; //риквест код от JointActivity для персоны 2

    long id_sql;  // id строки из базы данных
    long id_sql_second;  // id другой строки из базы данных
    long millisForTwo; // количество прожитых миллисекунд на двоих

    String[] dataFromDB = new String[6];

    Button mPerson1;
    Button mPerson2;
    Button mCount;

    TextView mWillBe;
    TextView forTwo_Days;
    EditText mDays;

    PersonDbHelper mDbHelper = new PersonDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joint);

        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setHomeButtonEnabled(true);

        forTwo_Days = (TextView) findViewById(R.id.forTwo_Days);
        mWillBe = (TextView)findViewById(R.id.jointWillBe);
        mDays = (EditText) findViewById(R.id.jointDays);
        mCount = (Button) findViewById(R.id.jointFind);

        //Загружаем данные из интента
        Intent intent = getIntent();
        id_sql = intent.getLongExtra(ID_SQL,1);
        id_sql_second = intent.getLongExtra(ID_SQL_second,id_sql);
        Log.d(TAG,"id_sql = " + id_sql + "   id_sql_second = " + id_sql_second);

        //получаем данные в соответствии с id 1
        dataFromDB = mDbHelper.getPersonData(id_sql);
        mPerson1 = (Button) findViewById(R.id.buttonNamePerson1);
        mPerson1.setText(dataFromDB[0] + "  /" + dataFromDB[5]+"/");
        mPerson1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //запускаем интент и получаем обратно id персоны 1
                Intent intentPerson1  = new Intent(JointActivity.this, ListDialog.class);
                intentPerson1.putExtra(ListDialog.REQUEST_PERSON, REQUEST_JOINT_PERSON1);
                startActivityForResult(intentPerson1,REQUEST_JOINT_PERSON1);
            }
        });

        //получаем данные в соответствии с id 2
        dataFromDB = mDbHelper.getPersonData(id_sql_second);
        mPerson2 = (Button) findViewById(R.id.buttonNamePerson2);
        mPerson2.setText(dataFromDB[0] + "  /" + dataFromDB[5]+"/");
        mPerson2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //запускаем интент и получаем обратно id персоны 2
                Intent intentPerson2  = new Intent(JointActivity.this, ListDialog.class);
                intentPerson2.putExtra(ListDialog.REQUEST_PERSON, REQUEST_JOINT_PERSON2);
                startActivityForResult(intentPerson2,REQUEST_JOINT_PERSON2);
            }
        });

        // количество прожитых миллисекунд на двоих
        millisForTwo = mDbHelper.getMillisForTwo(id_sql,id_sql_second);
        forTwo_Days.setText("На двоих прожито  " + millisForTwo/86400000 + "  дней" );
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
                dataFromDB = mDbHelper.getPersonData(id_sql);
                mPerson1.setText(dataFromDB[0] + "  /" + dataFromDB[5]+"/");

            }else if (requestCode == REQUEST_JOINT_PERSON2){
                Log.d(TAG, "JointActivity onActivityResult REQUEST_JOINT_PERSON 2");
                //получаем id выбранной персоны2
                id_sql_second = data.getLongExtra(ID_SQL_second, id_sql_second);
                //получаем данные персоны2 из базы данных
                dataFromDB = mDbHelper.getPersonData(id_sql_second);
                mPerson2.setText(dataFromDB[0] + "  /" + dataFromDB[5]+"/");
            }

            // количество прожитых миллисекунд на двоих
            millisForTwo = mDbHelper.getMillisForTwo(id_sql,id_sql_second);
            forTwo_Days.setText("На двоих прожито  " + millisForTwo/86400000 + "  дней" );
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

            case R.id.action_settings:
                Log.d(TAG, "OptionsItem = action_settings");
                Intent intentSettings = new Intent(this, PrefActivity.class);
                startActivity(intentSettings);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}