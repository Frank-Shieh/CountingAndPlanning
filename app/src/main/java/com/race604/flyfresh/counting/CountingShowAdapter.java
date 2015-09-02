package com.race604.flyfresh.counting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.race604.flyrefresh.sample.R;

import java.util.List;
import java.util.Map;

/**
 * Created by FRANK_SHIEH on 2015/8/20.
 */
public class CountingShowAdapter extends BaseAdapter {
     private List<Map<String,Object>> data;

    private Context context=null;
    public CountingShowAdapter(Context context,List<Map<String,Object>>data){

        this.context=context;
        this.data=data;



    }

   //组件集合，对应counting_show_list.xml中的控件

   public final class moduleSet{
       public ImageView imageView;
       public TextView typeView;
       public TextView textView;
   }



    @Override
    public int getCount() {
        return data.size();
    }
  //获得某一位置
    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View view =layoutInflater.inflate(R.layout.counting_show_list,null);



            moduleSet  moduleSet= new moduleSet();
            //获得组件，实例化组件
            moduleSet.imageView=(ImageView)view.findViewById(R.id.list_image);
            moduleSet.typeView=(TextView)view.findViewById(R.id.list_type);
            moduleSet.textView=(TextView)view.findViewById(R.id.list_text);
        //对控件赋值




        moduleSet.imageView.setBackgroundResource((Integer)data.get(i).get("list_image"));
        moduleSet.typeView.setText((String) data.get(i).get("list_type"));
        moduleSet.textView.setText((String)data.get(i).get("list_text"));

        return view;
    }
}
