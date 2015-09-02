package com.race604.planning;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import com.race604.flyfresh.counting.CountingData;
import com.race604.flyfresh.counting.JSONParser;
import com.race604.flyrefresh.sample.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlanningEdit extends AppCompatActivity {
    EditText title;
    EditText startDate;
    EditText endDate;
    EditText content;
    Button button;

    PlanningData pd;
    int pid;
    String name;
    String startdate;
    String enddate;
    String s_content;
    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    // url to update product
    private static final String url_update_product = "http://10.0.2.2/planningdata/update_plandata.php";
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.planedittoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title = (EditText) findViewById(R.id.title);

        startDate = (EditText) findViewById(R.id.startdate);
        endDate = (EditText) findViewById(R.id.enddate);
        content = (EditText)findViewById(R.id.content);
        button = (Button) findViewById(R.id.planconfirm);

        Intent intent = this.getIntent();
        pd = (PlanningData) intent.getSerializableExtra("planning");


        final Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        startDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(PlanningEdit.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                startDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(PlanningEdit.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                endDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.getText().toString().length() != 0) {
                    startdate = startDate.getText().toString();
                    enddate = endDate.getText().toString();
                    name = title.getText().toString();
                    s_content=content.getText().toString();

                    pid =pd.pid;
                    System.out.println("This is the pid:" + pid);
                    new SaveProductDetails().execute();

                }
            }
        });


        setPrimaryData();
    }

    private void setPrimaryData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        startDate.setText(sdf.format(pd.startDate));
        endDate.setText(sdf.format(pd.endDate));
        title.setText(pd.title);
        content.setText(pd.content);

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

            System.out.println("doInBackground:"+pid + startdate + enddate +name+ s_content);
            params.add(new BasicNameValuePair("startdate", startdate));
            params.add(new BasicNameValuePair("enddate", enddate));
            params.add(new BasicNameValuePair("title", name));
            params.add(new BasicNameValuePair("content", s_content));
            params.add(new BasicNameValuePair("pid", String.valueOf(pid)));
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
            pDialog = new SafeProgressDialog(PlanningEdit.this);
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
        bundle.putString("startdate", startdate);
        bundle.putString("enddate", enddate);
        bundle.putString("title", name);
        bundle.putString("content", s_content);
        bundle.putInt("pid", pd.pid);
        intent.putExtras(bundle);
        System.out.println("This is the pid:" + pd.pid);
        //通过Intent对象返回结果，调用setResult方法
        setResult(4, intent);
        System.out.println(pid + startdate + enddate + s_content);


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
