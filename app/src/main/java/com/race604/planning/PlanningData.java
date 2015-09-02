package com.race604.planning;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by FRANK_SHIEH on 2015/9/1.
 */
public class PlanningData implements Serializable {
    public int pid;
    public int color;
    public int icon;
    public Date startDate; //初始日期
    public Date endDate; //结束日期
    public String title;  //计划标题
    public String content; //计划内容


   public PlanningData(int color, int icon, String title, Date date){
       this.color=color;
       this.icon=icon;
       this.title=title;
       this.endDate=date;


   }

   public PlanningData(){


   }

}
