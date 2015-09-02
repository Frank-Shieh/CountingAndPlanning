package com.race604.planning;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.race604.flyfresh.counting.CountingData;
import com.race604.flyfresh.counting.CountingEdit;
import com.race604.flyfresh.counting.CountingShowAdapter;
import com.race604.flyrefresh.sample.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanningShow extends AppCompatActivity {
   PlanningData pd;
    ListView listView;
    int result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning_show);

        Toolbar toolbar = (Toolbar) findViewById(R.id.planshowtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        Intent intent = this.getIntent();
        pd = (PlanningData) intent.getSerializableExtra("planning");

        listView = (ListView) findViewById(R.id.planshowlist);
        List<Map<String, Object>> list = getData();
        listView.setAdapter(new PlanningShowAdapter(this, list));


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
                    intent.setClass(getApplicationContext(),PlanningEdit.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("planning", pd);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,4);
                    break;

            }


            return true;
        }
    };


    public List<Map<String, Object>> getData() {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startdate;
        Date enddate;
        startdate = pd.startDate;
        enddate=pd.endDate;

        Map<String, Object> map;

        map = new HashMap<String, Object>();
        map.put("list_image", R.drawable.ic_attach_money_48pt_3x);
        map.put("list_type", "计划名称");
        map.put("list_text", pd.title);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("list_image", R.drawable.ic_av_timer_18pt_3x);
        map.put("list_type", "开始日期");
        map.put("list_text", sdf.format(startdate));
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("list_image", R.drawable.ic_backspace_white_48pt_3x);
        map.put("list_type", "结束日期");
        map.put("list_text", sdf.format(enddate));
        list.add(map);


        map = new HashMap<String, Object>();
        map.put("list_image", R.drawable.musical52);
        map.put("list_type", "计划内容");
        map.put("list_text", pd.content);
        list.add(map);



        return list;


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 4) {
            if (requestCode == resultCode) {
                result=4;
                Bundle bundle = data.getExtras();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                Date startDate;
                Date endDate;
                try {
                    startDate = format.parse(bundle.getString("startdate"));
                    endDate=format.parse(bundle.getString("enddate"));
                } catch (ParseException e) {
                    startDate = new Date();
                    endDate= new Date();
                    e.printStackTrace();
                }
                pd.startDate=startDate;
                pd.endDate=endDate;
                pd.title=bundle.getString("title");
                pd.content=bundle.getString("content");
                pd.pid = bundle.getInt("pid");
                List<Map<String, Object>> list = getData();
                listView.setAdapter(new CountingShowAdapter(this, list));

            }


        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
         if(result==4){
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
        bundle.putString("startdate",sdf.format(pd.startDate) );
        bundle.putString("enddate", sdf.format(pd.endDate));
        bundle.putString("title", pd.title);
        bundle.putString("content", pd.content);
        bundle.putInt("pid", pd.pid);
        intent.putExtras(bundle);
        System.out.println("This is the pid:" + pd.pid);
        //通过Intent对象返回结果，调用setResult方法
        setResult(4, intent);
        finish();

    }
    public void jumpToMainActivityNoResult() {
        Intent intent = new Intent();
        //通过Intent对象返回结果，调用setResult方法
        setResult(0, intent);
        finish();

    }
}
