package com.cybersoft.bean;

public class FtpBean {
	private String sFTPIp;
	private String sFTPPort;
	private String sFTPUserName;
	private String sFTPUserPw;
	private String sFTPRemotePath;
	private String sFTPRemoteFile;
	private String localPath;
	private String localFile;
	private String respcode;
	private String respmsg;
	public String getRespmsg() {
		return respmsg;
	}
	public void setRespmsg(String respmsg) {
		this.respmsg = respmsg;
	}
	public String getsFTPIp() {
		return sFTPIp;
	}
	public void setsFTPIp(String sFTPIp) {
		this.sFTPIp = sFTPIp;
	}
	public String getsFTPPort() {
		return sFTPPort;
	}
	public void setsFTPPort(String sFTPPort) {
		this.sFTPPort = sFTPPort;
	}
	public String getsFTPUserName() {
		return sFTPUserName;
	}
	public void setsFTPUserName(String sFTPUserName) {
		this.sFTPUserName = sFTPUserName;
	}
	public String getsFTPUserPw() {
		return sFTPUserPw;
	}
	public void setsFTPUserPw(String sFTPUserPw) {
		this.sFTPUserPw = sFTPUserPw;
	}
	public String getsFTPRemotePath() {
		return sFTPRemotePath;
	}
	public void setsFTPRemotePath(String sFTPRemotePath) {
		this.sFTPRemotePath = sFTPRemotePath;
	}
	public String getsFTPRemoteFile() {
		return sFTPRemoteFile;
	}
	public void setsFTPRemoteFile(String sFTPRemoteFile) {
		this.sFTPRemoteFile = sFTPRemoteFile;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public String getLocalFile() {
		return localFile;
	}
	public void setLocalFile(String localFile) {
		this.localFile = localFile;
	}
	public String getRespcode() {
		return respcode;
	}
	public void setRespcode(String respcode) {
		this.respcode = respcode;
	}
}
