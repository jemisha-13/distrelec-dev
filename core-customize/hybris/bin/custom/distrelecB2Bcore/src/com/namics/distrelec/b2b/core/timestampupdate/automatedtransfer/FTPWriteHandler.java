package com.namics.distrelec.b2b.core.timestampupdate.automatedtransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeoutException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.apache.commons.io.IOUtils;

import com.namics.distrelec.b2b.core.timestampupdate.automatedtransfer.TransportException;

public class FTPWriteHandler {

	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(FTPWriteHandler.class.getName() );
	
	public void transferFileOnFTPServer(String excelfilepath, String ftpdir,String host,String user,String password) throws TimeoutException
	{

		InputStream inputStream = null;
		
		FTPClient client = new FTPClient();
		try
		{
			// connect to the ftp server
			client.connect(host);

			// timeout after 30 seconds
			client.setSoTimeout(300 * 1000);
			client.setDataTimeout(30 * 1000);			

			int reply = client.getReplyCode();

			if (FTPReply.isPositiveCompletion(reply))
			{
				if (!client.login(user,password))
				{
					throw new TransportException("error logging into ftp server");
				}

				//Set the client Mode to Binary
				
				client.setFileType(FTP.BINARY_FILE_TYPE);
				client.enterLocalPassiveMode();
				
				String[] pathElements = ftpdir.split("/");
				
				if (pathElements != null && pathElements.length > 0) {
					 for (String singleDir : pathElements) {
						 client.changeWorkingDirectory(singleDir);
					 }
				}
				if(!excelfilepath.equals("")){
					File file = new File(excelfilepath);
					inputStream = new FileInputStream(file);
					boolean success = client.storeFile(file.getName(), inputStream);	
					if(success){
						LOG.info("file transfere successs");
					}
				}
			}
			else
			{
				client.disconnect();
			}

			client.logout();

		}
		catch (IOException io)
		{
			throw new TransportException("error uploading file", io);
		}
		finally
		{
			// logout of the FTP session 
			if (client.isConnected())
			{
				try
				{
					client.disconnect();
				}
				catch (IOException ioe)
				{
					// failed to logout - nothing to do here
					LOG.info("--IOException thrown during client connection-- " + ioe);
					throw new TransportException("error during disconnect ftp file", ioe);
				}
			}

			if (inputStream != null)
			{
				IOUtils.closeQuietly(inputStream);
			}
		}
	}
	
	
	


}
