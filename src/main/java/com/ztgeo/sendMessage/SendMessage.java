package com.ztgeo.sendMessage;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.csapi.www.schema.sms.DeliveryInformation;
import org.junit.Test;

import com.ztgeo.dao.Dao;
import com.ztgeo.entity.SynDataUse;
import com.ztgeo.entity.TelS;
import com.ztgeo.main.Main;
import com.ztgeo.staticParams.StaticParams;
import com.ztgeo.utils.ErrorLog;
import com.ztgeo.utils.FormateData;
import com.ztgeo.utils.InfoLog;
import com.ztgeo.utils.ReadXml;
import com.ztgeo.utils.WebService;



//发短息的类
public class SendMessage {
	Logger log = Logger.getLogger(SendMessage.class);
	//发送数据的组织类
	public void sendMsg(List<TelS> tels) {
		if (tels.size()==0) {
			System.out.println("当前无最新消息....");
		}
		//追条插入信息
		for (TelS tel : tels) {
			System.out.println("当前短信信息的ID为:"+tel.getId());
			//将结果集转存到mysql
			sendMessage(tel);
			//释放资源
			
		}
		//查询库中未同步数据,进行获取反馈 
		synData();
		
	}
	
	/*System.out.println(aas.length);
	for (DeliveryInformation aa : aas) {
		System.out.println((aa.getDeliveryStatus().getValue()));
	}*/
	
	private void synData() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		System.out.println("--同步反馈数据开始执行-----");
			Dao.getConnO(StaticParams.url, StaticParams.username , StaticParams.password);
		String findsyn = "select mysqlid,syncount from SMS_DETAILINFO where status=0 and remarks='请求成功'";
			//将结果集进行保存成list
			//将list进行解析后连接mysql库 并拿到相关结果 
			ResultSet set =Dao.getData(findsyn);
			findsyn=null;
			List<SynDataUse> sets =resulttoList(set);
			if(sets.size()>0&&sets.get(0)!=null){
				//获取相应更新数据
				getResult(sets);
				System.out.println("----本次定时任务执行完毕-----");
				//将集合遍历 去更新每条数据回执  
			}else{
				System.out.println("---未发现需要同步的数据!");
				log.info("---未发现需要同步的数据!");
			}
			
			sets=null;
			
		
	}

	private void updataStatus(String result, String mysqlid) {
				String sql =  "update  sms_detailinfo t set(t.status,t.remarks,t.sendcount,t.sendtime,t.errormsg)=\n" +
			            "(select 1,?,(t.sendcount+1), sysdate,? from sms_detailinfo t where t.mysqlid =?)\n" + 
									" where t.mysqlid=?";
				String[] params = new String[4];
				params[0]= ("Delivered".equals(result)||"DeliveryToTerminal".equals(result))?"发送成功":"发送失败";
				params[1]= FormateData.getresult(result);//错误提醒
				params[2]= mysqlid;
				params[3]= mysqlid;
				Dao.getConnO(StaticParams.url, StaticParams.username , StaticParams.password);
				try {
					Dao.doExecuteUpdate(sql,params);
					System.out.println("mysqlID为:"+mysqlid+"的短信回执状态已更新");
					log.info("mysqlID为:"+mysqlid+"的短信回执状态已更新");
					//数组的释放
					
				} catch (SQLException e) {
					System.out.println("mysqlID为:"+mysqlid+"的短信回执状态更新失败");
					log.error("mysqlID为:"+mysqlid+"的短信回执状态更新失败",e);
					e.printStackTrace();
				}finally {
					//关闭资源
					Dao.closeResource();
					Dao.closeConn();
					params=null;
					sql=null;
			}
	}

	private void getResult(List<SynDataUse> syndatause) {
		//拼接字符串
			for (int i = 0; i < syndatause.size(); i++) {
				//判断次数  先  如果发现有 > 允许查找次数的 更新状态为失败
				if(syndatause.get(i).getSyncount()>=StaticParams.synCount){
					//更改发送状态为延时未接收到信息  
					updataO(syndatause.get(i).getMysqlid());
				}else{
					//调用service服务查询同步数据  
					//参数返回
					DeliveryInformation[] results = WebService.getResult(syndatause.get(i).getMysqlid());
					if(results.length>0){
						//获取内容 并将结果进行保存
						updataStatus(results[0].getDeliveryStatus().getValue(),syndatause.get(i).getMysqlid());
					}else{
						//拿到了空数据 进行更新循环查询次数  
						updataCount(syndatause.get(i).getMysqlid());
						
					}
					
				}
			}
			
			
			
			
		
	}

	private void updataCount(String mysqlid) {
		String sql = "update sms_detailinfo t set t.syncount =(t.syncount+1) where t.mysqlid='"+mysqlid+"'";
		Dao.getConnO(StaticParams.url, StaticParams.username , StaticParams.password);
		try {
			Dao.doExecuteUpdate(sql,new String[0]);
			System.out.println("MYSQLID为:"+mysqlid+"的短信信息请求状态(同步无有效信息同步次数加1)已更新");
			log.info("MYSQLID为:"+mysqlid+"的短信信息请求状态(同步无有效信息同步次数加1)已更新");
			//数组的释放
			
		} catch (SQLException e) {
			System.out.println("MYSQLID为:"+mysqlid+"的短信信息请求状态更新(同步无有效信息同步)失败!!");
			log.error("MYSQLID为:"+mysqlid+"的短信信息请求状态更新(同步无有效信息同步)失败!!");
			e.printStackTrace();
		}finally {
			//关闭资源
			Dao.closeResource();
			Dao.closeConn();
		}
		
	}

	private void updataO(String mysqlid) {
		String sql = "update sms_detailinfo t set t.remarks ='发送失败',t.errormsg='多次请求发送结果,发送信息超时', t.sendcount =(t.sendcount+1),t.status=1,t.sendtime=sysdate where t.mysqlid='"+mysqlid+"'";
		Dao.getConnO(StaticParams.url, StaticParams.username , StaticParams.password);
		try {
			Dao.doExecuteUpdate(sql,new String[0]);
			System.out.println("MYSQLID为:"+mysqlid+"的短信信息请求状态(多次无结果请求默认失败)已更新");
			log.info("MYSQLID为:"+mysqlid+"的短信信息请求状态(多次无结果请求默认失败)已更新");
			//数组的释放
			
		} catch (SQLException e) {
			System.out.println("MYSQLID为:"+mysqlid+"的短信信息请求状态更新(多次无结果请求默认失败)失败!!");
			log.error("MYSQLID为:"+mysqlid+"的短信信息请求状态更新(多次无结果请求默认失败)失败!!");
			e.printStackTrace();
		}finally {
			//关闭资源
			Dao.closeResource();
			Dao.closeConn();
		}
	}

	@Test
	public void test(){

		File directory = new File("xml");//设定为当前文件夹 
		String path="";
		
		
	    path = directory.getAbsolutePath();//获取标准的路径 
	    //开发环境 该目录可用
	    ReadXml.readXmlProperty(path);
		new SendMessage().synData();
	}
	
	private List<SynDataUse> resulttoList(ResultSet data) {
		List<SynDataUse> list = new ArrayList<>();
		try {
			while(data.next()){
				list.add(new SynDataUse(data.getString("mysqlid"),data.getInt("syncount")));
			}
		} catch (SQLException e) {
			ErrorLog.log.error("--结果集中取sysdatause实体类出错---",e);
			e.printStackTrace();
		}finally {
			Dao.closeConn();
			Dao.closeResource();
			data=null;
		}
		
		return list;
	}

	private void sendMessage(TelS tel) {
		//执行发送短信的方法
		String resultMSG = WebService.sentSMS(tel);
		if(resultMSG!=null){
			System.out.println("---ID为:"+tel.getId()+"的数据请求webservice成功---");
			InfoLog.log.info("---ID为:"+tel.getId()+"的数据请求webservice成功---");
			updateMsg(tel,"请求成功",resultMSG);
		}else{
			System.out.println("---ID为:"+tel.getId()+"的数据请求webservice失败---");
			ErrorLog.log.error("---ID为:"+tel.getId()+"的数据请求webservices失败---");
			updateMsg(tel,"请求失败",resultMSG);
		}
		
	}

	private void updateMsg(TelS tel,String content, String id) {
		//连接数据库
		Dao.getConnO(StaticParams.url, StaticParams.username , StaticParams.password);
		//组织查询语句
		Object[] params = new Object[3];
		String sql = "update sms_detailinfo t set t.remarks =\n" + 
					"?,t.mysqlid=? \n" + 
					"where t.id = ?";
		params[0]=content;
		params[1]=id;
		params[2]=tel.getId();
	
		//更新表
		try {
			Dao.doExecuteUpdate(sql,params);
			System.out.println("ID为:"+tel.getId()+"的短信信息请求状态已更新(发送请求状态)");
			log.info("ID为:"+tel.getId()+"的短信信息请求状态已更新(发送请求状态)");
			//数组的释放
			
		} catch (SQLException e) {
			System.out.println("执行更新操作时发生异常!!!(发送请求状态)");
			System.out.println("ID为:"+tel.getId()+"的短信信息请求状态更新失败!!(发送请求状态)");
			log.error("执行更新操作时发生异常!!!(发送请求状态)");;
			log.error("ID为:"+tel.getId()+"的短信信息请求状态更新失败!!(发送请求状态)");
			e.printStackTrace();
		}finally {
			//关闭资源
			Dao.closeResource();
			Dao.closeConn();
			params=null;
		}
	}

	
	
	
	//获得需要发送信息的list集合
	public List<TelS> getSendData() {
		//连接数据库
		Dao.getConnO(StaticParams.url, StaticParams.username , StaticParams.password);
		//组织查询语句
		String sql = "select T.PHONENUMBER,T.CONTENT,T.ID from sms_detailinfo T where remarks  ='服务开始准备发送短信'";
		//进行查询后取值
		ResultSet set = Dao.getData(sql);
		//放入list
		List<TelS> tels = getListBySet(set);
		//关闭资源
		Dao.closeResource();
		Dao.closeConn();
		return tels;
	}

	private List<TelS> getListBySet(ResultSet set) {
		List<TelS> tels = new ArrayList<>();
		try {
			while(set.next()){
				TelS tel = new TelS(set.getString("PHONENUMBER"),set.getString("ID"),set.getString("CONTENT"));
				tels.add(tel);
			}
		} catch (SQLException e) {
			Main.sbError.append("处理获取未发短信时的set集合时发生问题!!!");
			log.error("处理set集合时发生问题!!!",e);
			System.out.println("处理set集合时发生问题!!!");
			e.printStackTrace();
		}
		return tels;
	}

	
}
