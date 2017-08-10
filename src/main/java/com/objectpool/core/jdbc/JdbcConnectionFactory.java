package com.objectpool.core.jdbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.objectpool.core.impl.base.DefaultObjectFactory;
import com.objectpool.util.Utils;

/**
 * Jdbc连接工厂
 * @author Lee
 * @param <T>
 */
public class JdbcConnectionFactory extends DefaultObjectFactory<Connection> {

	private Logger logger = LoggerFactory.getLogger(JdbcConnectionFactory.class);
	
	private String prefix = "jdbc.";
	private String fileName = "jdbc.properties";
	private String path = "classpath";
	
	@Override
	public Connection makeObject() {
		StringBuffer buffer = new StringBuffer("----------make object----------");
		JdbcConfig config = getConfig();
		String password = config.getPass();
		String user = config.getUser();
		String url = config.getUrl();
		String driverClass = config.getDriverClass();
		Connection connection = null;
		try {
			Class.forName(driverClass);
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		buffer.append(Utils.lineSeparator + "--------------------");
		logger.debug(buffer.toString());
		return connection;
	}

	/**
	 * 获取jdbc配置
	 * @return
	 */
	protected JdbcConfig getConfig() {
		StringBuffer buffer = new StringBuffer("----------Get jdbc config----------");
		Properties properties = new Properties();
		InputStream in = null;
		JdbcConfig config = null;
		if (Utils.equals(path, "classpath")) {
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		} else {
			String last = path.substring(path.length() - 1, path.length());
			if (Utils.equals(last, File.separator)) {
				path = path.substring(0, path.length() - 1);
			}
			try {
				in = new FileInputStream(path + File.separator + fileName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		try {
			properties.load(new InputStreamReader(in));
			config = new JdbcConfig();
			config.setDriverClass(properties.getProperty(prefix + "driverClass"));
			config.setPass(properties.getProperty(prefix + "pass"));
			config.setUrl(properties.getProperty(prefix + "url"));
			config.setUser(properties.getProperty(prefix + "user"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		buffer.append(Utils.lineSeparator + "{prefix : " + prefix + ", path : "
				+ path + ", fileName : " + fileName + "}");
		buffer.append(Utils.lineSeparator + config.toString());
		buffer.append(Utils.lineSeparator + "--------------------");
		logger.debug(buffer.toString());
		return config;
	}

	@Override
	public void destroyObject(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean validObject(Connection connection) {
		boolean valid = false;
		StringBuffer buffer = new StringBuffer("------------valid connection-------------");
		String sql = "select 1 from dual";
		String result = null;
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
			}
			valid = Utils.equals("1", result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		buffer.append(Utils.lineSeparator + "sql: " + sql);
		buffer.append(Utils.lineSeparator + "result: " + result);
		buffer.append(Utils.lineSeparator + "-------------------------");
		logger.debug(buffer.toString());
		return valid;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
}
