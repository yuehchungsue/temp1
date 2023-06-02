package com.cybersoft.common;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.ConnectException;

import org.apache.commons.vfs2.impl.StandardFileSystemManager;

import com.cybersoft.bean.FtpBean;
import com.cybersoft.bean.LogUtils;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.oreilly.servlet.Base64Decoder;
import com.oreilly.servlet.Base64Encoder;
/************************************
 * 2018.01.22 HKP change JCE to jasypt
 * */
public class SFTPutil {
	 public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
//	 private static String AESStr = "";
//	 private static String IV = "";
//	 private static String KEY = "";
//	 public static void main(String[] args) {
//		        SFTPutil sftPutil = new SFTPutil(); 
//		 		String  salt="BFC574436DCFEC0F";
//		 		sftPutil.setAESStr("25726094C42C17BE776D66BD0A66147B");
//				KEY= sftPutil.getKEY();
//				IV = sftPutil.getIV();
//				KEY = strFormat(KEY," ",16,"R");				
//				String encryptValue ="";
//				String decryptValue ="";
//				String password = "1qaz3edc!";
//				String oripassword = "";
//				String encodeKEY = Base64Encoder.encode(KEY);
//				String KEY = Base64Decoder.decode(encodeKEY);
//				password = strFormat(password," ",16,"R");				
//				try {				
//				encryptValue =  bytesToString(sftPutil.encrypt(password, KEY));
//				oripassword =  sftPutil.decrypt(sftPutil.stringToBytes(encryptValue), KEY,IV);			
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		
//		
//				
//		 SFTPutil sendMyFiles = new SFTPutil();
////		  if (args.length < 1)
////		  {
////		   System.err.println("Usage: java " + sendMyFiles.getClass().getName()+
////		     " Properties_file File_To_FTP ");
////		   System.exit(1);
////		  }
//		 // sendMyFiles.getSFTPfile(propertiesFile, fileformSFTP);
//		  FtpBean ftpBean = new FtpBean();
//		  ftpBean.setsFTPIp("172.17.241.75");
//		  ftpBean.setsFTPUserName("cybersoft");
//		  ftpBean.setsFTPUserPw("1qaz3edc!");
//		  ftpBean.setsFTPPort("54321");
//		  ftpBean.setsFTPRemotePath("/home/cybersoft/tmp/");
//		  ftpBean.setsFTPRemoteFile("BA012999990000029_201610138082.zip");
//		  ftpBean.setLocalFile("test.20160925");
//		  ftpBean.setLocalPath("d:/temp/");
//		  
//		  
//		 sendMyFiles.putSFTPfile(ftpBean);
//		 sendMyFiles.CheckRemoteFileExit(ftpBean);
//		  
//
//		 }
	 
	 
	 
	@SuppressWarnings("finally")
	public FtpBean getSFTPfile(FtpBean ftpbean){
		  InputStream inputs= null;
	      OutputStream out = null;
	      ChannelSftp sftpChannel = null;
	      Session session = null;
		  try {	
		      StandardFileSystemManager manager = new StandardFileSystemManager();		    	   
			  JSch jsch = new JSch();
		        session = jsch.getSession(ftpbean.getsFTPUserName(),ftpbean.getsFTPIp(),Integer.parseInt(ftpbean.getsFTPPort()));
		        session.setPassword(ftpbean.getsFTPUserPw());
		        session.setConfig("StrictHostKeyChecking", "no");
		        log_systeminfo.debug("建立FTP連線");
		        System.out.println("Establishing Connection...");
		        session.connect();
		        System.out.println("Connection established.");
		        System.out.println("Crating SFTP Channel.");
		        sftpChannel = (ChannelSftp) session.openChannel("sftp");
		        sftpChannel.connect();
		        System.out.println("SFTP Channel created.");		     
		        inputs= sftpChannel.get(ftpbean.getsFTPRemotePath()+ftpbean.getsFTPRemoteFile());
//		        BufferedReader br = new BufferedReader(new InputStreamReader(inputs));
		        out = new FileOutputStream(new File(ftpbean.getLocalPath()+ftpbean.getLocalFile()));
		        int read = 0;
		        byte[]bytes = new byte[1024];
		        
		        while((read = inputs.read(bytes))!=-1){
		        	out.write(bytes, 0, read);
		        }
		    }
		catch(JSchException | SftpException | IOException e)
		{
		    System.out.println(e);		    
		}
		finally{
	        sftpChannel.disconnect();
	        session.disconnect();
	        return ftpbean;
		}
	 }
	 
	@SuppressWarnings("finally")
	public FtpBean putSFTPfile(FtpBean ftpBean){
		  if(!CheckFtpParam(ftpBean)){
			  ftpBean.setRespmsg("FTP參數檢查錯誤");
			  ftpBean.setRespcode("999");
			  log_systeminfo.debug("FTP參數檢查錯誤");
		  }
			  
	      ChannelSftp sftpChannel = null;
	      Session session = null;
		  try {			      		  	  	   
			   JSch jsch = new JSch();
		        session = jsch.getSession(ftpBean.getsFTPUserName(),ftpBean.getsFTPIp(),Integer.parseInt(ftpBean.getsFTPPort()));
		        session.setPassword(ftpBean.getsFTPUserPw());
		        session.setConfig("StrictHostKeyChecking", "no");
		        System.out.println("建立SFTP連線");
		        log_systeminfo.debug("建立SFTP連線");
		        session.connect();
		        System.out.println("建立SFTP連線成功");
		        log_systeminfo.debug("建立SFTP連線成功");
		        System.out.println("建立SFTP連線Cannel");
		        log_systeminfo.debug("建立SFTP連線Cannel");
		        sftpChannel = (ChannelSftp) session.openChannel("sftp");
		        sftpChannel.connect();
		        System.out.println("SFTP連線Cannel已建立");
		        log_systeminfo.debug("SFTP連線Cannel已建立");		        	      
		        File sendfile = new File(ftpBean.getLocalPath()+ftpBean.getLocalFile());
		        System.out.println("SFTP切換目錄");
		        log_systeminfo.debug("SFTP切換目錄");
		        sftpChannel.cd(ftpBean.getsFTPRemotePath());
		        System.out.println("SFTP切換目錄成功");
		        log_systeminfo.debug("SFTP切換目錄成功");
		        System.out.println("SFTP上傳檔案");
		        log_systeminfo.debug("SFTP上傳檔案");
		        sftpChannel.put(new FileInputStream(sendfile),ftpBean.getsFTPRemoteFile());
		        System.out.println("SFTP上傳檔案成功");		        
		        log_systeminfo.debug("SFTP上傳檔案成功");		        
		        ftpBean.setRespcode("0000");
		        System.out.println("SFTP上傳成功");
		        ftpBean.setRespmsg("SFTP上傳成功");
		        ftpBean = CheckRemoteFileExit(ftpBean);

		    }
		    catch(JSchException | SftpException | IOException e)
		    {
		    e.printStackTrace();
		    System.out.println("SFTP上傳檔案失敗"+e.getMessage());
		    log_systeminfo.debug("SFTP上傳檔案失敗"+e.getMessage());
		    }
		    finally{
		        sftpChannel.disconnect();
		        session.disconnect();
		        return ftpBean;
		    }
	 }
	
	public boolean CheckFtpParam(FtpBean ftpbean){
		boolean checkflag = true;
		log_systeminfo.debug("FTPIP:"+ftpbean.getsFTPIp());
		if(null==ftpbean.getsFTPIp() || "".equals(ftpbean.getsFTPIp().trim())){
		      checkflag = false;
		      return checkflag;
		}
		log_systeminfo.debug("FTPPORT:"+ftpbean.getsFTPPort());
		if(null==ftpbean.getsFTPPort() || "".equals(ftpbean.getsFTPPort().trim())){
			  checkflag = false;
		      return checkflag;
		}
		log_systeminfo.debug("FTPUserName:"+ftpbean.getsFTPUserName());
		if(null==ftpbean.getsFTPUserName() || "".equals(ftpbean.getsFTPUserName().trim())){
			  checkflag = false;
			  return checkflag;
		}
		log_systeminfo.debug("FTPUserPw:"+ftpbean.getsFTPUserPw());
		if(null==ftpbean.getsFTPUserPw() || "".equals(ftpbean.getsFTPUserPw().trim())){
			  checkflag = false;
			  return checkflag;
		}
		log_systeminfo.debug("FTPRemotePath:"+ftpbean.getsFTPRemotePath());
		if(null==ftpbean.getsFTPRemotePath() || "".equals(ftpbean.getsFTPRemotePath().trim())){
			  checkflag = false;
			  return checkflag;
		}
		log_systeminfo.debug("FTPRemoteFile:"+ftpbean.getsFTPRemoteFile());
		if(null==ftpbean.getsFTPRemoteFile() || "".equals(ftpbean.getsFTPRemoteFile().trim())){
			  checkflag = false;
			  return checkflag;
		}
		log_systeminfo.debug("LocalPath:"+ftpbean.getLocalPath());
		if(null==ftpbean.getLocalPath() || "".equals(ftpbean.getLocalPath().trim())){
			  checkflag = false;
			  return checkflag;
			}
		log_systeminfo.debug("LocalFil:"+ftpbean.getLocalFile());
		if(null==ftpbean.getLocalFile() || "".equals(ftpbean.getLocalFile().trim())){
			  checkflag = false;
			  return checkflag;
			}
		
		return checkflag;
	}
	
	@SuppressWarnings("finally")
	public FtpBean CheckRemoteFileExit(FtpBean ftpBean){
	if(!CheckFtpParam(ftpBean)){
		  ftpBean.setRespmsg("FTP參數檢查錯誤");
		  ftpBean.setRespcode("999");
		  log_systeminfo.debug("FTP參數檢查錯誤");
	  }
		  
    ChannelSftp sftpChannel = null;
    Session session = null;
	  try {			      		  	  	   
		   JSch jsch = new JSch();
	        session = jsch.getSession(ftpBean.getsFTPUserName(),ftpBean.getsFTPIp(),Integer.parseInt(ftpBean.getsFTPPort()));
	        session.setPassword(ftpBean.getsFTPUserPw());
	        session.setConfig("StrictHostKeyChecking", "no");
	        System.out.println("建立SFTP連線");
	        log_systeminfo.debug("建立SFTP連線");
	        session.connect();
	        System.out.println("建立SFTP連線成功");
	        log_systeminfo.debug("建立SFTP連線成功");
	        System.out.println("建立SFTP連線Cannel");
	        log_systeminfo.debug("建立SFTP連線Cannel");
	        sftpChannel = (ChannelSftp) session.openChannel("sftp");
	        sftpChannel.connect();
	        System.out.println("SFTP連線Cannel已建立");
	        log_systeminfo.debug("SFTP連線Cannel已建立");		        	      
	        System.out.println("SFTP檢查上傳檔案是否存在");
	        log_systeminfo.debug("SFTP檢查上傳檔案是否存在");	        
	        SftpATTRS exitflag = sftpChannel.lstat(ftpBean.getsFTPRemotePath()+("/".equals(ftpBean.getsFTPRemotePath().substring(ftpBean.getsFTPRemotePath().length()-1))?"":"/")+ftpBean.getsFTPRemoteFile());	       
	        log_systeminfo.debug("SFTP檔案檢查成功");
	        ftpBean.setRespcode("9999");
	        ftpBean.setRespmsg("FTP檔案上傳成功，確認成功");
	    }
	    catch(Exception e)
	    {
	    e.printStackTrace();
	    System.out.println("SFTP上傳檔案檢查失敗"+e.getMessage());
	    log_systeminfo.debug("SFTP上傳檔案檢查失敗"+e.getMessage());
	    ftpBean.setRespcode("1111");
	    ftpBean.setRespmsg(e.getMessage());
	    }
	    finally{
	        sftpChannel.disconnect();
	        session.disconnect();
	        return ftpBean;
	    }
	}
	/**2018.01.22**/
	/*
	 public byte[] encrypt(String plainText, String encryptionKey) throws Exception {
	    //forTEST
		//Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
		//for Production
	    Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "IBMJCE");
	    SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
	    cipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(IV.getBytes("UTF-8")));
	    return cipher.doFinal(plainText.getBytes("BIG5"));
	  }

	 public String decrypt(byte[] cipherText, String encryptionKey,String iv) throws Exception{		 
		 Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "IBMJCE");
		 //Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
	    SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
	    cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(iv.getBytes("UTF-8")));
	    return new String(cipher.doFinal(cipherText),"BIG5");
	  }
	 */
	 /**2018.01.22**/
	  private static String strFormat(String str,String pastword,int strLength,String strDir){
		 //** 123
			str = str == ""?"": str.trim();		
			pastword = "".equals(pastword)||null == pastword?" ":pastword;
			strDir = strDir == ""||null == strDir?"L":strDir;			
			if(str.length() == strLength)
			{
				return str;
			}
			else
			{
				for(int i = str.length();i < strLength;i++)
				{
					if(strDir.equals("L"))
					{
						str = pastword + str; 
					}
					else
					{
						str = str + pastword;
					}
				}
				return str;	
			}		
	  }
	  
		private static String bytesToString(byte[] b) {
		    byte[] b2 = new byte[b.length];
		    System.arraycopy(b, 0, b2, 0, b.length);
		    return new BigInteger(b2).toString(16);
		}
		
		public byte[] stringToBytes(String s) {
		    byte[] b2 = new BigInteger(s, 16).toByteArray();				  
		    return b2;
		}
	  	/**2018.01.22**/
	  	/*
		 public String getIV() {
			 if("".equals(AESStr)||AESStr.length()!=32){
				 return "";
			 }
			 else{
				return AESStr.substring(0,16);
			 }
		}		
		public String getKEY() {
			if("".equals(AESStr)||AESStr.length()!=32){
				 return "";
			 }
			 else{
				return AESStr.substring(16,32);
			 }
		}
		public static String getAESStr() {
				return AESStr;
			}
		public void setAESStr(String aESStr) {
				AESStr = aESStr;
			}
		*/
		}
		
