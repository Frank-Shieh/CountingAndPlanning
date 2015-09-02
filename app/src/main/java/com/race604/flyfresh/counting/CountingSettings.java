package com.race604.flyfresh.counting;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.race604.flyrefresh.sample.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CountingSettings extends AppCompatActivity {
    private List<String> type = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    public String typ;
    private SafeProgressDialog progressDialog;
    public CountingData cd =new CountingData();
    EditText editText;
    Spinner spinner;
    EditText editText2;


    EditText editText3;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;
    Button button;

    String adding;
    String riqi;
    String count;
    String inout;
    int cid;

    JSONParser jsonParser = new JSONParser();
    // url to create new product
    private static String url_create_product = "http://10.0.2.2/countingdata/create_countdata.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.counting_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settingtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        editText = (EditText) findViewById(R.id.date);
        spinner = (Spinner) findViewById(R.id.spinner);
        editText2 = (EditText) findViewById(R.id.money);
        editText3 = (EditText) findViewById(R.id.adding);
        radioGroup = (RadioGroup) findViewById(R.id.inout);
        radioButton1 = (RadioButton) findViewById(R.id.input);
        radioButton2 = (RadioButton) findViewById(R.id.output);
        button = (Button) findViewById(R.id.confirm);


        final Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);


        //  closeStrictMode();


        editText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(CountingSettings.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                editText.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
        //第一步：添加一个下拉列表项的type，这里添加的项就是下拉列表的菜单项
        type.add("购物");
        type.add("餐饮");
        type.add("交通");
        type.add("通讯");
        type.add("运动");
        type.add("学习");
        type.add("其他");
        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的type。
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        spinner.setAdapter(adapter);
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                typ = adapter.getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                typ = null;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (editText.getText().toString().length() != 0) {
                    riqi = editText.getText().toString();

                    count = editText2.getText().toString();
                    adding = editText3.getText().toString();
                    int radioButtonId = radioGroup.getCheckedRadioButtonId();
                    RadioButton rb = (RadioButton) findViewById(radioButtonId);
                    inout = rb.getText().toString();

                    new createNewCountData().execute();


               /*     Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("date", riqi);
                    bundle.putString("type", typ);
                    bundle.putString("money", count);
                    bundle.putString("adding", adding);
                    bundle.putString("inout", inout);
                    bundle.putInt("cid", cd.cid);
                    intent.putExtras(bundle);
                    System.out.println("This is the cid:" + cd.cid);
                    //通过Intent对象返回结果，调用setResult方法
                    setResult(1, intent);
                    finish();*/


                }
            }
        });


    }

    public static void closeStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll().penaltyLog().build());
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Intent intent = new Intent();
            setResult(0, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class createNewCountData extends AsyncTask<String, String, String> {
        //Before starting background thread Show Progress Dialog
        @Override
        protected void onPreExecute() {

            progressDialog = new SafeProgressDialog(CountingSettings.this);
            progressDialog.setMessage("Creating...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();


            params.add(new BasicNameValuePair("date", riqi));
            params.add(new BasicNameValuePair("money", count));
            params.add(new BasicNameValuePair("type", typ));
            params.add(new BasicNameValuePair("inorout", inout));

            params.add(new BasicNameValuePair("adding", adding));
            System.out.println(params.size());
            // getting JSON Object
            // Note that create product url accepts POST method
            try {
                JSONObject json = jsonParser.makeHttpRequest(url_create_product,
                        "POST", params);
                // System.out.println(json.toString());
                if (json == null) {
                    System.out.println("Shit!Json is null");

                }
                String id=json.getString("cid");

               cid=Integer.valueOf(id);
               cd.cid=cid;
                System.out.println("This is the cid:"+cd.cid);
                return id;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
            // check for success tag

        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String message) {
            // dismiss the dialog once done
            super.onPostExecute(message);

            progressDialog.dismiss();
            jumpToMainActivity();
            //message 为接收doInbackground的返回值
        }


    }
public void   jumpToMainActivity(){
    Intent intent = new Intent();
    Bundle bundle = new Bundle();
    bundle.putString("date", riqi);
    bundle.putString("type", typ);
    bundle.putString("money", count);
    bundle.putString("adding", adding);
    bundle.putString("inout", inout);
    bundle.putInt("cid", cd.cid);
    intent.putExtras(bundle);
    System.out.println("This is the cid:" + cd.cid);
    //通过Intent对象返回结果，调用setResult方法
    setResult(1, intent);
    finish();



}
    class SafeProgressDialog extends ProgressDialog {
        Activity mParentActivity;

        public SafeProgressDialog(Context context) {
            super(context);
            mParentActivity = (Activity) context;
        }

        @Override
        public void dismiss() {
            if (mParentActivity != null && !mParentActivity.isFinishing()) {
                super.dismiss();    //调用超类对应方法
            }
        }
    }
}



