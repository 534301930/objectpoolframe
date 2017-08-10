package com.objectpool.core.ftp;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.objectpool.core.impl.base.DefaultObjectFactory;
import com.objectpool.util.Utils;

public class FtpConnectionFactory extends DefaultObjectFactory<FTPClient> {

	private Logger logger = LoggerFactory.getLogger(FtpConnectionFactory.class);
	
	@Override
	public FTPClient makeObject() {
		StringBuffer buffer = new StringBuffer("------------make ftp connection------------");
		FTPClient client = new FTPClient();
		int port = 2121;
		String host = "127.0.0.1";
		String username = "admin";
		String password = "admin";
		try {
			client.connect(host, port);
			client.login(username, password);
			int replyCode = client.getReplyCode();
			if (!FTPReply.isPositiveCompletion(replyCode)) {
				buffer.append(Utils.lineSeparator + "Receiving a negative reply code,login failed!");
				return null;
			}
			client.setFileTransferMode(FTPClient.BINARY_FILE_TYPE);
			return client;
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		buffer.append(Utils.lineSeparator + "--------------------------");
		logger.debug(buffer.toString());
		return client;
	}

	@Override
	public void destroyObject(FTPClient client) {
		StringBuffer buffer = new StringBuffer("------------destroy ftp connection------------");
		try {
			if (client != null) {
				client.disconnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		buffer.append(Utils.lineSeparator + "client: " + client.toString());
		buffer.append(Utils.lineSeparator + "--------------------------");
		logger.debug(buffer.toString());
	}

	@Override
	public boolean validObject(FTPClient client) {
		StringBuffer buffer = new StringBuffer("------------valid ftp connection------------");
		boolean send = false;
		try {
			send = client.sendNoOp();
		} catch (IOException e) {
			e.printStackTrace();
		}
		buffer.append(Utils.lineSeparator + "client: " + client.toString());
		buffer.append(Utils.lineSeparator + "valid: " + send);
		buffer.append(Utils.lineSeparator + "--------------------------");
		logger.debug(buffer.toString());
		return send;
	}

}
