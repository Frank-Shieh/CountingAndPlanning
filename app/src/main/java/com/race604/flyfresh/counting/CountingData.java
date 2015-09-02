package com.race604.flyfresh.counting;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by FRANK_SHIEH on 2015/8/19.
 */
public class CountingData  implements Serializable {
    public int cid;
    public int color;
    public int icon;
    public Date date; //日期
    public double money; //数额
    public String type;  //类别
    public String adding; //备注
    public String inOut; //收入与支出


    public int pid;
    public Date startDate; //初始日期
    public Date endDate; //结束日期
    public String title;  //计划标题
    public String content; //计划内容

   public CountingData(int color,int icon,String type,Date date){
       this.color=color;
       this.icon=icon;
       this.type=type;
       this.date=date;


   }

   public CountingData(){


   }

}
