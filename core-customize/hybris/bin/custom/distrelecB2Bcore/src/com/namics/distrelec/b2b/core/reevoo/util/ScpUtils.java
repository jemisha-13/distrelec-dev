package com.namics.distrelec.b2b.core.reevoo.util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ScpUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ScpUtils.class);

    public static Session createSession(String user, String host, int port, String password)
        throws JSchException {
        return createSession(user, host, port, null, null, password);
    }

    public static Session createSession(String user, String host, int port, String privateKey, String keyPassword)
            throws JSchException {
        return createSession(user, host, port, privateKey, keyPassword, null);
    }

    private static Session createSession(String user, String host, int port, String privateKey, String keyPassword,
            String password)
        throws JSchException {
        try {
            JSch jsch = new JSch();

            if (privateKey != null) {
                if (keyPassword != null) {
                    jsch.addIdentity("keyname", privateKey.getBytes(StandardCharsets.UTF_8), null,
                            keyPassword.getBytes(StandardCharsets.UTF_8));
                } else {
                    jsch.addIdentity("keyname", privateKey.getBytes(StandardCharsets.UTF_8));
                }
            }

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            Session session = jsch.getSession(user, host, port);
            session.setConfig(config);

            if (isNotBlank(password)) {
                session.setPassword(password);
            }

            session.connect();

            return session;
        } catch (JSchException e) {
            LOG.error("Unable to establish sftp connection", e);
            throw e;
        }
    }

    public static void copyLocalToRemote(Session session, InputStream from, String to, String fileName) throws JSchException, IOException {
    	try {
    	Channel channel =  session.openChannel("sftp");
		channel.connect();
		ChannelSftp channelSftp = (ChannelSftp) channel;
		channelSftp.cd(to);
		channelSftp.put(from,fileName,ChannelSftp.OVERWRITE);
		LOG.info("Copy Done . Closing session ");
    	}catch(SftpException ex) {
    		LOG.error("Unable to copy file over sftp", ex);
    	}
    }

    

}
