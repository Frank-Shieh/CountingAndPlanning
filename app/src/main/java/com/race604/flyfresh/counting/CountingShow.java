package com.race604.flyfresh.counting;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.race604.flyrefresh.sample.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountingShow extends AppCompatActivity {
    CountingData cd;
    ListView listView;
    int result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counting_show);

        Toolbar toolbar = (Toolbar) findViewById(R.id.showtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        Intent intent = this.getIntent();
        cd = (CountingData) intent.getSerializableExtra("counting");

        listView = (ListView) findViewById(R.id.list);
        List<Map<String, Object>> list = getData();
        listView.setAdapter(new CountingShowAdapter(this, list));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_counting_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), CountingEdit.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("counting", cd);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,2);
                    break;

            }


            return true;
        }
    };


    public List<Map<String, Object>> getData() {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        date = cd.date;


        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list_image", R.drawable.ic_av_timer_18pt_3x);
        map.put("list_type", "日期");
        map.put("list_text", sdf.format(date));
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("list_image", R.drawable.ic_backspace_white_48pt_3x);
        map.put("list_type", "类别");
        map.put("list_text", cd.type);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("list_image", R.drawable.ic_attach_money_48pt_3x);
        map.put("list_type", "数额");
        map.put("list_text", String.valueOf(cd.money));
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("list_image", R.drawable.musical52);
        map.put("list_type", "备注");
        map.put("list_text", cd.adding);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("list_image", R.drawable.shopping207);
        map.put("list_type", "收支");
        map.put("list_text", cd.inOut);
        list.add(map);

        return list;


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 2) {
            if (requestCode == resultCode) {
                result=2;
                Bundle bundle = data.getExtras();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                Date insertDate;
                try {
                    insertDate = format.parse(bundle.getString("date"));
                } catch (ParseException e) {
                    insertDate = new Date();
                    e.printStackTrace();
                }
                cd.date = insertDate;
                cd.type = bundle.getString("type");
                cd.adding = bundle.getString("adding");
                cd.inOut = bundle.getString("inout");
                String m = bundle.getString("money");
                cd.money = Double.parseDouble(m);
                cd.cid = bundle.getInt("cid");
                System.out.println(cd.type+cd.adding+cd.inOut+cd.money);
                List<Map<String, Object>> list = getData();
                listView.setAdapter(new CountingShowAdapter(this, list));

            }


        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
         if(result==2){
             jumpToMainActivityForResult();
             result=0;
         }else {
             jumpToMainActivityNoResult();

         }

        return super.onKeyDown(keyCode, event);
    }

    public void jumpToMainActivityForResult() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("date", sdf.format(cd.date));
        bundle.putString("type", cd.type);
        bundle.putString("money", String.valueOf(cd.money));
        bundle.putString("adding", cd.adding);
        bundle.putString("inout", cd.inOut);
        bundle.putInt("cid", cd.cid);
        intent.putExtras(bundle);
        System.out.println("This is the cid:" + cd.cid);
        //通过Intent对象返回结果，调用setResult方法
        setResult(2, intent);
        finish();

    }
    public void jumpToMainActivityNoResult() {
        Intent intent = new Intent();
        //通过Intent对象返回结果，调用setResult方法
        setResult(0, intent);
        finish();

    }
}
