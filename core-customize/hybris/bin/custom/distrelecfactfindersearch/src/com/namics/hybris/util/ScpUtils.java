package com.namics.hybris.util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ScpUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ScpUtils.class);

    public static Session createSession(String user, String host, int port, String name, String privateKey,
            String keyPassword) throws JSchException {
        try {
            JSch jsch = new JSch();

            if (privateKey != null) {
                if (keyPassword != null) {
                    jsch.addIdentity(name, privateKey.getBytes(StandardCharsets.UTF_8), null,
                            keyPassword.getBytes(StandardCharsets.UTF_8));
                } else {
                    jsch.addIdentity(name, privateKey.getBytes(StandardCharsets.UTF_8), null, null);
                }
            }

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            Session session = jsch.getSession(user, host, port);
            session.setConfig(config);
            session.connect();

            return session;
        } catch (JSchException e) {
            LOG.error("Unable to establish sftp connection", e);
            throw e;
        }
    }

    public static void copyLocalToRemote(Session session, InputStream from, String to, String fileName) throws JSchException, IOException {
        // exec 'scp -t rfile' remotely
        String command = "scp -t " + to;
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);

        // get I/O streams for remote scp
        OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();

        channel.connect();
        assertAck(in);

        byte[] data = IOUtils.toByteArray(from);

        // send "C0644 filesize filename", where filename should not include '/'
        long filesize = data.length;
        command = "C0644 " + filesize + " " + fileName + "\n";
        out.write(command.getBytes());
        out.flush();
        assertAck(in);

        // send a content
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        IOUtils.copy(bis, out);

        // send '\0'
        out.write(new byte[] { 0 }, 0, 1);
        out.flush();
        assertAck(in);

        IOUtils.closeQuietly(out);
        IOUtils.closeQuietly(bis);

        channel.disconnect();
    }

    private static void assertAck(InputStream in) throws JSchException, IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //         -1
        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');
            LOG.error(sb.toString());
            throw new JSchException(sb.toString());
        }
    }

}
