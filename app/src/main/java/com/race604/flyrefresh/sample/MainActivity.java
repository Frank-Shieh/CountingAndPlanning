package com.race604.flyrefresh.sample;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.race604.ItemTouchHelper.ItemTouchHelperAdapter;
import com.race604.ItemTouchHelper.ItemTouchHelperViewHolder;
import com.race604.ItemTouchHelper.SimpleItemTouchHelperCallback;
import com.race604.flyfresh.counting.CountingData;
import com.race604.flyfresh.counting.CountingSettings;
import com.race604.flyfresh.counting.CountingShow;
import com.race604.flyfresh.counting.JSONParser;
import com.race604.flyrefresh.FlyRefreshLayout;
import com.race604.planning.PlanningData;
import com.race604.planning.PlanningSettings;
import com.race604.planning.PlanningShow;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements FlyRefreshLayout.OnPullRefreshListener {

    private FlyRefreshLayout mFlylayout;
    private RecyclerView mListView;

    public ItemAdapter mAdapter;
    public ArrayList<CountingData> countingDataSet = new ArrayList<>();
    public ArrayList<PlanningData> planningDataSet = new ArrayList<>();
    public ArrayList<CountingData> mDataSet = new ArrayList<>();
    private Handler mHandler = new Handler();
    private LinearLayoutManager mLayoutManager;
    public int ItemCount = 0;
    public int editPosition;
    public int deletePosition;
    private ItemTouchHelper mItemTouchHelper;

    // url to delete product
    private static final String url_delete_countdata = "http://10.0.2.2/countingdata/delete_countdata.php";
    private static final String url_delete_plandata = "http://10.0.2.2/planningdata/delete_plandata.php";
    private static final String url_all_countdata = "http://10.0.2.2/countingdata/get_all_countdata.php";
    private static final String url_all_plandata = "http://10.0.2.2/planningdata/get_all_plandata.php";

    // Progress Dialog
    private ProgressDialog pDialog;
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    JSONArray datas = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mFlylayout = (FlyRefreshLayout) findViewById(R.id.fly_layout);

        mFlylayout.setOnPullRefreshListener(this);

        mListView = (RecyclerView) findViewById(R.id.list);

        mLayoutManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(mLayoutManager);
        //  mListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ItemAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setHasFixedSize(true);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mListView);
        //解决删除数据后，纸飞机飞回来就出现BUG的问题。
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        initDataSet();
    }

    private void initDataSet() {
        new LoadAllCountData().execute();
        //         mDataSet.add(new CountingData(Color.GRAY, R.mipmap.ic_assessment_white_24dp, "Shopping", new Date()));
        ItemCount = mDataSet.size();
        //  countingDataSet.add(new CountingData(Color.GRAY, R.mipmap.ic_assessment_white_24dp, "Shopping", new Date()));
    }

    private void addItemData() {

        //mDataSet.add(0,new CountingData(Color.GRAY, R.mipmap.ic_folder_white_24dp, "Photos", new Date(2014 - 1900, 0, 9)));
        mAdapter.notifyItemInserted(0);
        mLayoutManager.scrollToPosition(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_counting) {
            Intent intent = new Intent(this, CountingSettings.class);
            startActivityForResult(intent, 1);

        }
        if (id == R.id.action_planning) {
            Intent intent = new Intent(this, PlanningSettings.class);
            startActivityForResult(intent, 3);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        //当resultCOde为1时，为创建一个新的countingData.为0时，没有发生操作。为2时，数据进行了更改。
        if (resultCode == 1) {
            if (requestCode == resultCode) {
                System.out.println("成功执行onActivityResult，resultCode等于1！");
                Bundle bundle = data.getExtras();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                Date insertDate;
                try {
                    insertDate = format.parse(bundle.getString("date"));
                } catch (ParseException e) {
                    insertDate = new Date();
                    e.printStackTrace();
                }
                CountingData cd = new CountingData(Color.parseColor("#FFC970"), R.mipmap.ic_assessment_white_24dp, bundle.getString("type"), insertDate);
                cd.adding = bundle.getString("adding");
                cd.inOut = bundle.getString("inout");
                String m = bundle.getString("money");
                cd.money = Double.parseDouble(m);
                cd.cid = bundle.getInt("cid");
                System.out.println("This is the cid:" + cd.cid);
                mDataSet.add(0, new CountingData(Color.parseColor("#FFC970"), R.mipmap.ic_assessment_white_24dp, bundle.getString("type"), insertDate));
                countingDataSet.add(0, cd);
                addItemData();
                ItemCount = mDataSet.size();
            }

        }

        if (resultCode == 2) {
            if (requestCode == resultCode) {

                System.out.println("成功执行onActivityResult，resultCode等于2！");
                Bundle bundle = data.getExtras();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date insertDate;
                try {
                    insertDate = format.parse(bundle.getString("date"));
                } catch (ParseException e) {
                    insertDate = new Date();
                    e.printStackTrace();
                }
                CountingData cd = new CountingData(Color.parseColor("#FFC970"), R.mipmap.ic_assessment_white_24dp, bundle.getString("type"), insertDate);
                cd.adding = bundle.getString("adding");
                cd.inOut = bundle.getString("inout");
                String m = bundle.getString("money");
                cd.money = Double.parseDouble(m);
                cd.cid = bundle.getInt("cid");
                System.out.println("This is the cid:" + cd.cid);
                System.out.println("This is the ItemCount:" + ItemCount + "This is the editPosition:" + editPosition);
                mDataSet.set(editPosition, new CountingData(Color.parseColor("#FFC970"), R.mipmap.ic_assessment_white_24dp, bundle.getString("type"), insertDate));
                countingDataSet.set(editPosition, cd);
                mAdapter.notifyItemChanged(editPosition);
                //mLayoutManager.scrollToPosition(editPosition);
                //  mListView.setLayoutManager(mLayoutManager);
                mListView.setAdapter(mAdapter);
                mListView.setItemAnimator(new SampleItemAnimator());
                ItemCount = mDataSet.size();
            }
        }
        if (resultCode == 3) {
            if (resultCode == requestCode) {
                System.out.println("成功执行onActivityResult，resultCode等于3！");
                Bundle bundle = data.getExtras();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate;
                Date endDate;
                try {
                    startDate = format.parse(bundle.getString("startdate"));
                    endDate = format.parse(bundle.getString("enddate"));
                } catch (ParseException e) {
                    startDate = new Date();
                    endDate = new Date();
                    e.printStackTrace();
                }
                CountingData pd = new CountingData(Color.GRAY, R.mipmap.ic_assessment_white_24dp, bundle.getString("title"), startDate);
                pd.startDate = startDate;
                pd.endDate = endDate;
                pd.title = bundle.getString("title");
                pd.content = bundle.getString("content");
                pd.pid = bundle.getInt("pid");
                mDataSet.set(0, new CountingData(Color.GRAY, R.mipmap.ic_assessment_white_24dp, bundle.getString("title"), endDate));
                countingDataSet.add(0, pd);
                addItemData();
                ItemCount = mDataSet.size();
            }
        }

        if (resultCode == 4) {
            if (requestCode == resultCode) {

                System.out.println("成功执行onActivityResult，resultCode等于4！");
                Bundle bundle = data.getExtras();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate;
                Date endDate;
                try {
                    startDate = format.parse(bundle.getString("startdate"));
                    endDate = format.parse(bundle.getString("enddate"));
                } catch (ParseException e) {
                    startDate = new Date();
                    endDate = new Date();
                    e.printStackTrace();
                }
                CountingData pd = new CountingData(Color.GRAY, R.mipmap.ic_assessment_white_24dp, bundle.getString("title"), startDate);
                pd.startDate = startDate;
                pd.endDate = endDate;
                pd.title = bundle.getString("title");
                pd.content = bundle.getString("content");
                pd.pid = bundle.getInt("pid");
                mDataSet.set(editPosition, new CountingData(Color.GRAY, R.mipmap.ic_assessment_white_24dp, bundle.getString("title"), endDate));
                countingDataSet.set(editPosition, pd);
                mAdapter.notifyItemChanged(editPosition);
                mListView.setAdapter(mAdapter);
                mListView.setItemAnimator(new SampleItemAnimator());
                ItemCount = mDataSet.size();
            }
        }
    }

    @Override
    public void onRefresh(FlyRefreshLayout view) {
        View child = mListView.getChildAt(0);
        if (child != null) {
            bounceAnimateView(child.findViewById(R.id.icon));
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFlylayout.onRefreshFinish();
            }
        }, 2000);
    }

    private void bounceAnimateView(View view) {
        if (view == null) {
            return;
        }

        Animator swing = ObjectAnimator.ofFloat(view, "rotationX", 0, 30, -20, 0);
        swing.setDuration(400);
        swing.setInterpolator(new AccelerateInterpolator());
        swing.start();
    }

    @Override
    public void onRefreshAnimationEnd(FlyRefreshLayout view) {

    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> implements ItemTouchHelperAdapter {

        private LayoutInflater mInflater;
        private DateFormat dateFormat;


        public ItemAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            dateFormat = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, Locale.ENGLISH);
        }

        // 用于创建onCreateViewHolder
        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.view_list_item, viewGroup, false);
            return new ItemViewHolder(view);
        }

        // 为ViewHolder设置数据
        @Override
        public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
            final CountingData data = mDataSet.get(i);
            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(data.color);
            itemViewHolder.icon.setBackgroundDrawable(drawable);
            itemViewHolder.icon.setImageResource(data.icon);
            itemViewHolder.title.setText(data.type);
            itemViewHolder.subTitle.setText(dateFormat.format(data.date));
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            CountingData prev = mDataSet.remove(fromPosition);
            CountingData prevCountingData = countingDataSet.remove(fromPosition);
            mDataSet.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
            countingDataSet.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prevCountingData);
            notifyItemMoved(fromPosition, toPosition);
            editPosition = toPosition;
        }

        @Override
        public void onItemDismiss(int position) {
            Log.d("Position", String.valueOf(position));
            editPosition = position;
            CountingData countingData =countingDataSet.get(position);
           if(countingData.color !=Color.GRAY){
            System.out.println(countingData.cid);
           deletePosition=countingData.cid;
               new DeleteCountData().execute();}
            else{
               System.out.println(countingData.pid);
           deletePosition=countingData.pid;
               new DeletePlanData().execute();}
            mDataSet.remove(position);
            countingDataSet.remove(position);
            notifyItemRemoved(position);

        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        ImageView icon;
        TextView title;
        TextView subTitle;

        public ItemViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.title);
            subTitle = (TextView) itemView.findViewById(R.id.subtitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("xxx", "当前点击的位置" + getPosition());
                    editPosition = getPosition();
                    CountingData cd = countingDataSet.get(getPosition());
                    Intent intent = new Intent();
                    if (cd.color != Color.GRAY) {
                        intent.setClass(MainActivity.this, CountingShow.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("counting", cd);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 2);
                    } else {

                        intent.setClass(MainActivity.this, PlanningShow.class);
                        Bundle bundle = new Bundle();
                        PlanningData pd = new PlanningData();
                        pd.startDate = cd.startDate;
                        pd.endDate = cd.endDate;
                        pd.pid = cd.pid;
                        pd.title = cd.title;
                        pd.content = cd.content;
                        bundle.putSerializable("planning", pd);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 4);
                    }

                }
            });

        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    /*****************************************************************
     * Background Async Task to Delete Product
     */
    class DeleteCountData extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SafeProgressDialog(MainActivity.this);
            pDialog.setMessage("Deleting CountingData...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deleting product
         */
        protected String doInBackground(String... args) {

            // Check for success tag

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                JSONObject json;
                    params.add(new BasicNameValuePair("cid", String.valueOf(deletePosition)));
                    // getting product details by making HTTP request
                     json = jsonParser.makeHttpRequest(
                            url_delete_countdata, "POST", params);
                    Log.d("Delete CountData cid", String.valueOf(deletePosition));
                    // check your log for json response
                    Log.d("Delete CountData", json.toString());

                // json success tag
                return json.getString("success");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();

        }

    }

    /*****************************************************************
     * Background Async Task to Delete PlanData
     */
    class DeletePlanData extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SafeProgressDialog(MainActivity.this);
            pDialog.setMessage("Deleting PlanData...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Deleting product
         */
        protected String doInBackground(String... args) {

            // Check for success tag

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                JSONObject json;
                    params.add(new BasicNameValuePair("pid",String.valueOf(deletePosition)));
                    json = jsonParser.makeHttpRequest(
                            url_delete_plandata, "POST", params);
                    Log.d("Delete PlanData pid", String.valueOf(deletePosition));
                    // check your log for json response
                    Log.d("Delete PlanData", json.toString());
                // json success tag
                return json.getString("success");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();

        }

    }
    class LoadAllCountData extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SafeProgressDialog(MainActivity.this);
            pDialog.setMessage("LoadingCountData.Please wait ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = jsonParser.makeHttpRequest(url_all_countdata, "GET", params);

            String json1 = null;
            try {
                json1 = HttpUtil.getRequest(url_all_countdata);

            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Log.d("All countData", json1.toString());

            try {
                int success = json.getInt("success");
                if (success == 1) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    datas = json.getJSONArray("data");
                    Date insertDate;
                    for (int i = 0; i < datas.length(); i++) {
                        JSONObject c = datas.getJSONObject(i);
                        CountingData cd = new CountingData();
                        try {
                            insertDate = sdf.parse(c.getString("date"));
                        } catch (ParseException e) {
                            insertDate = new Date();
                            e.printStackTrace();
                        }
                        cd.cid = c.getInt("cid");
                        cd.date = insertDate;
                        cd.money = Double.parseDouble(c.getString("money"));
                        cd.type = c.getString("type");
                        cd.inOut = c.getString("inorout");
                        cd.adding = c.getString("adding");

                        countingDataSet.add(0, cd);
                        mDataSet.add(0, new CountingData(Color.parseColor("#FFC970"), R.mipmap.ic_assessment_white_24dp, cd.type, cd.date));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //loadPlanData
            json = jsonParser.makeHttpRequest(url_all_plandata, "GET", params);
            json1 = null;
            try {
                json1 = HttpUtil.getRequest(url_all_plandata);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Log.d("All planData", json1.toString());
            try {
                int success = json.getInt("success");
                if (success == 1) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    datas = json.getJSONArray("data");
                    Date startDate;
                    Date endDate;
                    for (int i = 0; i < datas.length(); i++) {
                        JSONObject c = datas.getJSONObject(i);
                        CountingData cd = new CountingData();
                        try {
                            startDate = sdf.parse(c.getString("startdate"));
                            endDate = sdf.parse(c.getString("enddate"));
                        } catch (ParseException e) {
                            startDate = new Date();
                            endDate = new Date();
                            e.printStackTrace();
                        }
                        cd.pid = c.getInt("pid");
                        cd.startDate = startDate;
                        cd.endDate = endDate;
                        cd.title = c.getString("title");
                        cd.content = c.getString("content");
                        cd.color=Color.GRAY;
                        countingDataSet.add(0, cd);
                        mDataSet.add(0, new CountingData(Color.GRAY, R.mipmap.ic_assessment_white_24dp, cd.title, cd.endDate));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            mAdapter.notifyDataSetChanged();
        }
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
