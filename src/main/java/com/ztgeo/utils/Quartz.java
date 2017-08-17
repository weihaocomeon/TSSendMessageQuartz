package com.ztgeo.utils;


import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.ztgeo.entity.TelS;
import com.ztgeo.main.Main;
import com.ztgeo.sendMessage.SendMessage;
import com.ztgeo.staticParams.StaticParams;

//定时程序
public class Quartz implements Job {
	//Logger log = Logger.getLogger(Quartz.class);
	//声明变量 读取xml
	public static void startQuartz(){
		
		try {
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			//任务具体
			JobDetail job = JobBuilder.newJob(Quartz.class).withIdentity("job1","group1").build();
			//触发器
			Trigger trigger = TriggerBuilder.newTrigger()
					.withSchedule(CronScheduleBuilder.cronSchedule(StaticParams.QuartzTime))
					.forJob("job1","group1").build();
			scheduler.scheduleJob(job,trigger);
			scheduler.start();
			System.out.println("方法准备执行!请确保配置的启动时间有效!");
		} catch (SchedulerException e) {
			e.printStackTrace();
			System.out.println("定时程序报错,请检查执行时间格式是否正确!!");
		}
	}
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String dateS = FormateData.getNowTime();
			System.out.println("方法执行当前日期:"+dateS);
		Main.sbError.setLength(0);
		SendMessage mg = new SendMessage();
			//拿到需要写入的数据
			List<TelS> tels = mg.getSendData();
			//将tels集合进行发送短信处理
			mg.sendMsg(tels);
		
			
	
		
	}


}
