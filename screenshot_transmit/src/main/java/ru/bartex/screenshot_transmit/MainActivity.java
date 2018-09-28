package ru.bartex.screenshot_transmit;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "33333";
    //задачу можно разделить на 3 части: получение картинки экрана в виде объекта Bitmap и
    //сохранение картинки в файле и передача этого файла в виде вложения в сообщении эл. почты
    //проблема возникает при прикреплении файла к сообщению- система сообщает о том, что нет прав
    //хотя вроде никаких прав и не надо
    //если использовать SD карту и прописать разрешение в манифесте, то всё работает на Хуавее 4.0.3
    //а на Ксиоми 7.0, где SD карта эмулирована - образуется пустой файл, который отказывается
    //прикрепляться к вложению в  GMail

    //получение картинки можно сделать как метод внутри MainActivity,
    // реализовать как статический метод отдельного класса или сделать отдельный класс и
    //объявить здесь экземпляр класса, инициализировав поля класса в конструкторе
    //сейчас пока сделан takeScreenShotSimple

    TextView tv;
    Button mButton;
    ImageView mImageView;
    File file;
    View main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.tv1);
        main = findViewById(R.id.main);
        mButton = findViewById(R.id.buttonOnScreen);
        mImageView = findViewById(R.id.imageButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick getAbsolutePath: " + file.getAbsolutePath());
                Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
                Log.d(TAG, "onClick Uri: " + uri);
                if (file.isFile()){
                    mImageView.setImageURI(uri);
                    main.setBackgroundColor(Color.parseColor("#999999"));
                }else {
                    Log.d(TAG, "onClick uri: " + uri + " isFile = " + file.isFile());
                    Log.d(TAG, "Создайте файл ");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.share_item){

            //так не работает по неустановленной причине
            //File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            //так работает для андроид 7 но не для андроид 4 - крах
            //File path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            // получаем путь к SD для андроид 4 и 7- фойл = 0
            //File path = Environment.getExternalStorageDirectory();

            //Log.d(TAG, "Environment.getRootDirectory().getAbsolutePath()" +
            //        Environment.getRootDirectory().getAbsolutePath());

            Bitmap bitmap = takeScreenshot(main);
            file = takeScreenshot159(bitmap, "savedBitmap.png");

            if (file!=null){
                Log.d(TAG, "После file.length = : " + file.length() + " isFile: " + file.isFile());
            }else Log.d(TAG, "После  нет файла ");
            Log.d(TAG,  " После file.getAbsolutePath() = " + file.getAbsolutePath());

            Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
            Log.d(TAG, " После uri: " + uri);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Посмотрите на это.");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Отправить с помощью"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Bitmap takeScreenshot(View v){
        v = v.getRootView();
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private File takeScreenshot159(Bitmap bitmap,String fileName ) {


        //Так - сразу крах
        //File path =Environment.getExternalStorageDirectory();

        //Так - крах
        //final String path = Environment.getExternalStorageDirectory() + "/Screenshots";
        //File dir = new File(path);
        //if(!dir.exists()) {
        //    dir.mkdirs();
       // }
        //так работает для андроид 7 но не для андроид 4 - крах
        File path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        if(!path.exists()) {
            path.mkdirs();
        }

        File file = new File(path, fileName);
            try {
                FileOutputStream fos = null;
                // Make sure the Pictures directory exists.

                Log.d(TAG, " До сжатия File file = " + file.length() + " isFile: " + file.isFile());
                try {
                    fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    Log.d(TAG, " После сжатия File file = " + file.length() +
                            "  isFile: " + file.isFile() +  "file.getAbsolutePath() = " + file.getAbsolutePath());
                    return file;
                } finally {
                    if (fos != null) fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Ошибка try");
            }
        Log.d(TAG, "return null");
            return null;
        }


/*
    private Bitmap screenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private  File saveBitmap(Bitmap bm, String fileName){

        //Возвращает File, соответствующий внутренней директории приложения.
        //для 7.0 файл создаётся, картинка на экран выводится, но к почте файл не прикрепить- нет прав
        //для 4.0.4 файл создаётся, картинка на экран выводится, к почте файл как бы прикрепляется, но в почте его нет
        //final String path = this.getFilesDir()+ "/Screenshots";

        // сохранять картинку в папку кэша специально выделенную для вашей аппликации.
        //для 7.0 файл создаётся, картинка на экран выводится, но к почте файл не прикрепить- нет прав
        //для 4.0.4 файл создаётся, картинка на экран выводится, к почте файл как бы прикрепляется, но в почте его нет
        //final String path = this.getCacheDir() + "/Screenshots";

        //в корне SD-карты ***РАБОЧИЙ при условии наличия SD карты***
        //для 7.0 файл НЕ создаётся, картинка на экран НЕ выводится т.к нет SD карты физической
        //для 4.0.4 файл создаётся, картинка на экран выводится, к почте файл прикрепляется,в почте он ЕСТЬ,
        // в Телеграм тоже всё передаётся
        //т.е. проблема в том, что на 7.0 нет SD карты физической, нужно понять, как сделать на эмулированной
        final String path = Environment.getExternalStorageDirectory() + "/Screenshots";

        //To get the internal SD card you can use
        //final String path = System.getenv("EXTERNAL_STORAGE")  + "/Screenshots";
        //To get the external SD card you can use
        //final String path = System.getenv("SECONDARY_STORAGE")  + "/Screenshots";

        Log.d(TAG, "isExternalStorageEmulated = " + Environment.isExternalStorageEmulated() +
                "    getExternalStorageState = " + Environment.getExternalStorageState());
        Log.d(TAG, "path: " + path);

        //так тоже железно - работает запись в файл в каталог для картинок по пути :
        //  /storage/emulated/0/Android/data/ru.bartex.screenshot/files/Pictures
        //Context mContext = getApplicationContext();
        //File dir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File dir = new File(path);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        // запись в файл по пути : /storage/emulated/0/Screenshots/mantis_image.png
        File file = new File(dir, fileName);
        Log.d(TAG, "getAbsolutePath: " + file.getAbsolutePath());

        //file.setReadable(true);
        //File file = new File(this.getFilesDir(), fileName);
        //File file = new File(this.getCacheDir(), fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            //не работает, так как сначала нужно было сделать File
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

    private  Bitmap takeScreenShot(Activity activity)
    {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height  - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    private static void savePic(Bitmap b, String strFileName){
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
}
