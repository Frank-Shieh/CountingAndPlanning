package com.race604.planning;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
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

import com.race604.flyfresh.counting.CountingData;
import com.race604.flyfresh.counting.JSONParser;
import com.race604.flyrefresh.sample.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlanningSettings extends AppCompatActivity {

    private SafeProgressDialog progressDialog;
    public    PlanningData pd =new PlanningData();

    EditText title;
    EditText startDate;
    EditText endDate;
    EditText content;
    Button button;



    int pid;
    String name;
    String startdate;
    String enddate;
    String s_content;

    JSONParser jsonParser = new JSONParser();
    // url to create new product
    private static String url_create_product = "http://10.0.2.2/planningdata/create_planningdata.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.planning_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.plansettoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title = (EditText) findViewById(R.id.title);
        startDate = (EditText) findViewById(R.id.startdate);
        endDate = (EditText) findViewById(R.id.enddate);
        content = (EditText)findViewById(R.id.content);
        button = (Button) findViewById(R.id.planconfirm);


        final Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);


        //  closeStrictMode();

        startDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(PlanningSettings.this,
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
                DatePickerDialog dialog = new DatePickerDialog(PlanningSettings.this,
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

                    new createNewPlanData().execute();
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

    class createNewPlanData extends AsyncTask<String, String, String> {
        //Before starting background thread Show Progress Dialog
        @Override
        protected void onPreExecute() {

            progressDialog = new SafeProgressDialog(PlanningSettings.this);
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


            params.add(new BasicNameValuePair("startdate", startdate));
            params.add(new BasicNameValuePair("enddate", enddate));
            params.add(new BasicNameValuePair("title", name));
            params.add(new BasicNameValuePair("content", s_content));
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

               pid=Integer.valueOf(id);
               pd.pid=pid;
                System.out.println("This is the pid:"+pd.pid);
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
    bundle.putString("startdate", startdate);
    bundle.putString("enddate", enddate);
    bundle.putString("title", name);
    bundle.putString("content", s_content);
    bundle.putInt("pid", pd.pid);
    intent.putExtras(bundle);
    System.out.println("This is the pid:" + pd.pid);
    //通过Intent对象返回结果，调用setResult方法
    setResult(3, intent);
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



