package ru.bartex.jubelee_dialog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;

public class FindAdapter extends BaseAdapter {

    public static final String TAG = "33333";
    Context ctx;
    LayoutInflater mLayoutInflater;
    ArrayList<PersonFind> mPersonList;

    TextView name;
    TextView dr;
    TextView past_days;
    CheckBox cb_Find;

    public FindAdapter(Context context, ArrayList<PersonFind> personArrayList){
        ctx = context;
        mPersonList = personArrayList;
        mLayoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mPersonList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPersonList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Log.d(TAG, " name1 = " + mPersonList.get(0).getPerson_name() +
          //      " name2 " + mPersonList.get(1).getPerson_name());

        PersonFind p = getPerson(position);

        View v = convertView;
        if (v == null) {
            v = mLayoutInflater.inflate(R.layout.list_name_date_checkbox, parent, false);
        }

        dr = (TextView) v.findViewById(R.id.was_born_find);
        dr.setText(p.getPerson_dr());

        past_days = (TextView)v.findViewById(R.id.past_Days_find);
        past_days.setText(p.getPerson_past_days());

        name = (TextView)v.findViewById(R.id.name_list_find);
        name.setText(p.getPerson_name());

        cb_Find = (CheckBox) v.findViewById(R.id.checkBox_find);
        //записываем тэг для того, чтобы потом получить позицию списка в обработчике чекбокса
        cb_Find.setTag(position);
        cb_Find.setChecked(p.isSelect_find());
        //присваиваем слушатель чекбоксу
        cb_Find.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //позицию списка получаем из тэга, преобразовав его в (Integer)
                PersonFind p1 = (PersonFind)getItem((Integer) buttonView.getTag());
                // здесь присваиваем PersonFind.select_find значение isChecked
                p1.setSelect_find(isChecked);
            }
        });
        return v;
    }

    // Метод getCheckedPersonList проверяет, какие персоны отмечены галками
    // и формирует из них коллекцию-список.
    //помещаем в ArrayList только те персоны, которые имеют установленную галку
    //галка выбора пперсоны устанавливается в методе обработки слушателя галки,
    // который прописан в методе View getView()  адаптера FindAdapter

    public  ArrayList<PersonFind> getCheckedPersonList() {

        ArrayList<PersonFind> checkedList = new ArrayList<PersonFind>();

        for (PersonFind p : mPersonList) {
            // если в корзине
            if (p.isSelect_find())
                checkedList.add(p);
        }
        return checkedList;
    }

    private PersonFind getPerson(int position){
        return ((PersonFind)getItem(position));
    }
}
