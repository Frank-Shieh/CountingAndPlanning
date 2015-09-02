package com.race604.flyfresh.counting;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CountingEdit extends AppCompatActivity {
    private List<String> type = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    public String typ;

    EditText editText;
    Spinner spinner;
    EditText editText2;
    EditText editText3;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;
    Button button;

    CountingData cd;

    String adding;
    String riqi;
    String count;
    String inout;
    int cid;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    // url to update product
    private static final String url_update_product = "http://10.0.2.2/countingdata/update_countdata.php";
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counting_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.edittoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        editText = (EditText) findViewById(R.id.editdate);
        spinner = (Spinner) findViewById(R.id.editspinner);
        editText2 = (EditText) findViewById(R.id.editmoney);
        editText3 = (EditText) findViewById(R.id.editadding);
        radioGroup = (RadioGroup) findViewById(R.id.editinout);
        radioButton1 = (RadioButton) findViewById(R.id.editinput);
        radioButton2 = (RadioButton) findViewById(R.id.editoutput);
        button = (Button) findViewById(R.id.editconfirm);

        Intent intent = this.getIntent();
        cd = (CountingData) intent.getSerializableExtra("counting");


        final Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        editText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(CountingEdit.this,
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
                    cid = cd.cid;
                    System.out.println("This is the cid:" + cid);
                    new SaveProductDetails().execute();

                }
            }
        });


        setPrimaryData();
    }

    private void setPrimaryData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        editText.setText(sdf.format(cd.date));
        spinner.setSelection(adapter.getPosition(cd.type));
        editText2.setText(String.valueOf(cd.money));
        editText3.setText(cd.adding);
        String inout = cd.inOut;
        String judge = "收入";
        if (inout.equals(judge)) {
            radioButton1.setChecked(true);
        } else {
            radioButton2.setChecked(true);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_counting_edit, menu);
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

    class SaveProductDetails extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            System.out.println(cid + riqi + count + typ);
            params.add(new BasicNameValuePair("date", riqi));
            params.add(new BasicNameValuePair("money", count));
            params.add(new BasicNameValuePair("type", typ));
            params.add(new BasicNameValuePair("inorout", inout));
            params.add(new BasicNameValuePair("cid", String.valueOf(cid)));
            params.add(new BasicNameValuePair("adding", adding));
// sending modified data through http request
            // Notice that update product url accepts POST method

            try {
                JSONObject json = jsonParser.makeHttpRequest(url_update_product,
                        "POST", params);
                // System.out.println(json.toString());
                if (json == null) {
                    System.out.println("Shit!Json is null");

                }
                System.out.println(json.getString("message"));
                return json.getString(TAG_SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SafeProgressDialog(CountingEdit.this);
            pDialog.setMessage("Updating CountData details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            // dismiss the dialog once done
            super.onPostExecute(s);
            jumpToShowActivity();
            pDialog.dismiss();
        }
    }

    public void jumpToShowActivity() {
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
        setResult(2, intent);
        System.out.println(riqi+typ+count+adding+inout);


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
