package com.objectpool.core.jdbc;

public class JdbcConfig {

	private String user;
	private String pass;
	private String driverClass;
	private String url;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "JdbcConfig [user=" + user + ", pass=" + pass + ", driverClass="
				+ driverClass + ", url=" + url + "]";
	}
}
