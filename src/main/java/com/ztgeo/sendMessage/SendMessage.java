package com.ztgeo.sendMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.ztgeo.dao.Dao;
import com.ztgeo.entity.ReqMsg;
import com.ztgeo.entity.RespMsg;
import com.ztgeo.entity.TelS;
import com.ztgeo.main.Main;
import com.ztgeo.staticParams.StaticParams;
import com.ztgeo.utils.FormateData;

import net.sf.json.JSONObject;


//发短息的类
public class SendMessage {
	Logger log = Logger.getLogger(SendMessage.class);
	//发送数据的组织类
	public void sendMsg(List<TelS> tels) {
		if (tels.size()==0) {
			System.out.println("当前无最新消息....");
		}
		//组织参数reqstr
		for (TelS tel : tels) {
			System.out.println("当前短信信息的ID为:"+tel.getId());
			String reqStr = getBase64Req(tel.getTel(),tel.getContent());
			//将请求字符串进行执行post请求
			System.out.println("64位编码后的数据:"+reqStr);
			String resultStr = httpPost(reqStr);
			//将结果集转换成返回对象
			RespMsg respMsg = saveResultAsRespMsg(resultStr,tel);
			//将返回对象保存到数据库
			saveRespMsg(respMsg);
			//释放资源
			respMsg = null;
			
		}	
	}
	
	private void saveRespMsg(RespMsg respMsg) {
		//连接数据库
		Dao.getConnS(StaticParams.url, StaticParams.username , StaticParams.password);
		//组织查询语句
		Object[] params = new Object[4];
		params[0] = respMsg.getMsgGroup();//msg反馈组信息
		params[1] = (true ==respMsg.isOk()?"请求成功":"请求失败")+respMsg.getRspcod();//详细信息;//存入respstatue状态 代表请求的状态
		params[2] = respMsg.getId();//id信息
		params[3] = respMsg.getId();//id信息
		String sql = "update sms_detailinfo t set (t.sendcount,t.msggroup,t.sendtime,t.reqinfo,t.remarks) =\n" + 
					"(select t.sendcount+1,?,sysdate,?,'请求已发送' from sms_detailinfo t  where t.id = ?)\n" + 
					"where t.id = ?";
		//更新表
		try {
			Dao.doExecuteUpdate(sql,params);
			System.out.println("ID为:"+respMsg.getId()+"的短信信息请求状态已更新");
			log.info("ID为:"+respMsg.getId()+"的短信信息请求状态已更新");
			//数组的释放
			
		} catch (SQLException e) {
			System.out.println("执行更新操作时发生异常!!!");
			System.out.println("ID为:"+respMsg.getId()+"的短信信息请求状态更新失败!!");
			Main.sbError.append("执行更新操作时发生异常!!!ID为:"+respMsg.getId()+"的短信信息请求状态更新失败!!");
			log.error("执行更新操作时发生异常!!!");
			log.error("ID为:"+respMsg.getId()+"的短信信息请求状态更新失败!!");
			e.printStackTrace();
		}finally {
			//关闭资源
			Dao.closeResource();
			Dao.closeConn();
			params=null;
		}
	}

	//分析结果 并保存结果
	public RespMsg saveResultAsRespMsg(String resultStr, TelS tel) {
		//解析json包
		//创建json解析器
		com.alibaba.fastjson.JSONObject jsonobj;
		//将结果保存在类中
		RespMsg resp = new RespMsg();
		try {
			
			resp.setId(tel.getId());//id信息
			jsonobj = com.alibaba.fastjson.JSONObject.parseObject(resultStr);
			if(resultStr!=null&&!"".equals(resultStr)){
				boolean  isOk = jsonobj.getBoolean("success");//是否成功
				String rspcod = jsonobj.getString("rspcod");//返回状态
				//未报异常 则保存msgGroup 状态值和信息
				resp.setMsgGroup(jsonobj.getString("msgGroup"));
				resp.setOk(isOk);
				resp.setRspcod(rspcod);
				System.out.println("状态值:"+isOk+"返回状态:"+rspcod);
				log.info("状态值:"+isOk+"返回状态:"+rspcod);
			}else{
				Main.sbError.append("返回结果不是json格式,保存失败!ID:"+tel.getId());
				System.out.println("返回结果不是json格式,保存失败!");
				log.error("返回结果不是json格式,保存失败!ID:"+tel.getId());
				//状态为false 信息为syso信息
				resp.setOk(false);
				resp.setRspcod("返回结果不是json格式,保存失败!");
			}
		} catch (JSONException e) {
			log.error("将结果转为json时失败!ID:"+tel.getId());
			Main.sbError.append("将结果转为json时失败!ID:"+tel.getId());
			System.out.println("将结果转为json时失败!");
			//状态为false 信息为syso信息
			resp.setOk(false);
			resp.setRspcod("将结果转为json时失败!");
			e.printStackTrace();
		} catch (NullPointerException e){
			log.error("接口返回的json数据信息异常!ID:"+tel.getId());
			Main.sbError.append("接口返回的json数据信息异常!ID:"+tel.getId());
			//空指针说明返回时json数据 但不是我们需要的数据
			System.out.println("接口返回的json数据信息异常!");
			//状态为false 信息为syso信息
			resp.setOk(false);
			resp.setRspcod("接口返回的json数据信息异常!");
		}
		
		return resp;
	}
	
	public String getBase64Req(String telNum, String content) {
		//拼装请求参数
		ReqMsg ms = new ReqMsg();
		ms.setEcName(StaticParams.ecName);//集团名称
		ms.setApId(StaticParams.apID);//用户名
		ms.setSecretKey(StaticParams.secretKey);//密码
		ms.setMobiles(telNum);//电话号码
		ms.setContent(content);//内容
		ms.setSign(StaticParams.sign);//网关签名编码
		ms.setAddSerial("");
		
		StringBuffer sb = new StringBuffer();
		sb.append(ms.getEcName());
		sb.append(ms.getApId());
		sb.append(ms.getSecretKey());
		sb.append(ms.getMobiles());
		sb.append(ms.getContent());
		sb.append(ms.getSign());
		sb.append(ms.getAddSerial());
		System.out.println("加密前:"+sb.toString());
		ms.setMac(encryptToMD5(sb.toString()));
		  //将类实例化
	      String reqTest = JSON.toJSONString(ms);//JSONObject.fromObject(ms).toString();
	      System.out.println("当前的请求数据:"+reqTest);
	      //释放资源
	      ms= null;
	      sb=null;
	      //base64位加密
	      String reqStr=null;
		try {
			reqStr = Base64.encodeBase64String(reqTest.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	     
	     //解析返回 的字符串 如果成功 或者失败则导入
		return reqStr;
	}
	
	public static String encryptToMD5(String password) {
		byte[] digesta = null;
		String result = null;
		try {

			// 得到一个MD5的消息摘要
			MessageDigest mdi = MessageDigest.getInstance("MD5");
			// 添加要进行计算摘要的信息
			mdi.update(password.getBytes("utf-8"));
			// 得到该摘要
			digesta = mdi.digest();
			result = byteToHex(digesta);
			System.out.println("md5加密后的数据:"+result);
		} catch (NoSuchAlgorithmException e) {

		} catch (UnsupportedEncodingException e) {
			// TODO 自动生成�?catch �?
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 将二进制转化为16进制字符串
	 */
	public static String byteToHex(byte[] pwd) {
		StringBuilder hs = new StringBuilder("");
		String temp = "";
		for (int i = 0; i < pwd.length; i++) {
			temp = Integer.toHexString(pwd[i] & 0XFF);
			if (temp.length() == 1) {
				hs.append("0").append(temp);
			} else {
				hs.append(temp);
			}
		}
		return hs.toString().toLowerCase();
	}
	
	public String httpPost(String reqStr){
		OutputStreamWriter out = null;
		
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(StaticParams.webSUrl);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("contentType","utf-8");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new OutputStreamWriter(conn.getOutputStream());
			out.write(reqStr);
			out.flush();
			
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += "\n" + line;
				System.out.println("接口返回结果:"+result);
			}
		} catch (Exception e) {
			Main.sbError.append("---发送接口请求时异常!!");
			System.out.println("---发送接口请求时异常!!");
			System.out.println(e.getLocalizedMessage());
			log.error("---发送接口请求时异常!!");
			log.error(e.getLocalizedMessage());
			e.printStackTrace();
			
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				Main.sbError.append("--关闭post输入流时发生异常!!");
				ex.printStackTrace();
			}
		}
		return result;
		
	}
	
	//获得需要发送信息的list集合
	public List<TelS> getSendData() {
		//连接数据库
		Dao.getConnS(StaticParams.url, StaticParams.username , StaticParams.password);
		//组织查询语句
		String sql = "select T.PHONENUMBER,T.CONTENT,T.ID from sms_detailinfo T where remarks is null";
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
			log.error("处理set集合时发生问题!!!");
			log.error(e.getLocalizedMessage());
			System.out.println("处理set集合时发生问题!!!");
			e.printStackTrace();
		}
		return tels;
	}

	
}
