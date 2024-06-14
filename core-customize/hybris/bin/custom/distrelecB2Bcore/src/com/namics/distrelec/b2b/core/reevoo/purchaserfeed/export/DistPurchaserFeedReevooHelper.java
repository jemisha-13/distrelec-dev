package com.namics.distrelec.b2b.core.reevoo.purchaserfeed.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.configuration.Configuration;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.export.DistZipService;
import com.namics.distrelec.b2b.core.model.jobs.DistReevooPurchaserFeedCronJobModel;
import com.namics.distrelec.b2b.core.reevoo.productfeed.export.DistReevooExportServiceException;
import com.namics.distrelec.b2b.core.reevoo.productfeed.export.DistRevooCsvTransformationService;
import com.namics.distrelec.b2b.core.reevoo.util.ScpUtils;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.media.MediaIOException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

public class DistPurchaserFeedReevooHelper {
	
	private static final Logger LOG = LoggerFactory.getLogger(DistPurchaserFeedReevooHelper.class);

	private static final String[] HEADER = { "sku", "title", "first_name", "email", "country", "Language",
			"purchase_date", "order_ref", "customer_ref", "price", "currency", "delivery_date", "sales_channel" };

	private static final String CSV_FILE_SUFFIX = ".csv";
	private static final String FILE_NAME_PATTERN = "yyyyMMdd-HHmm";
	private MediaService mediaService;
	private ModelService modelService;
	private DistZipService distZipService;
	private ConfigurationService configurationService;
	private CatalogService catalogService;
	private DistRevooCsvTransformationService distRevooCsvTransformationService;

	public void saveExportData(Map<LanguageModel, List<String[]>> resultMap,
			final DistReevooPurchaserFeedCronJobModel cronJob) throws DistReevooPurchaserExportServiceException, DistReevooExportServiceException {
		Collection<MediaModel> feedList = new ArrayList<MediaModel>();
		for (Map.Entry<LanguageModel, List<String[]>> entry : resultMap.entrySet()) {
			List<String[]> rows = entry.getValue();
			if (rows != null && rows.size() > 0) {
				final String exportName = getMediaCode(cronJob.getMediaPrefix(), entry.getKey());
				InputStream exportDataStream = null;
				try {
					exportDataStream = getDistRevooCsvTransformationService().transform(HEADER, entry.getValue());

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					org.apache.commons.io.IOUtils.copy(exportDataStream, baos);
					byte[] bytes = baos.toByteArray();
					final String exportFileName = exportName + CSV_FILE_SUFFIX;
					// create media

					final MediaModel exportMedia = getModelService().create(MediaModel.class);
					exportMedia.setCode(exportName);
					exportMedia
							.setCatalogVersion(catalogService.getCatalogVersion("distrelecProductCatalog", "Online"));
					getModelService().save(exportMedia);
					// Zip output
					final byte[] exportData = getDistZipService().zip(new ByteArrayInputStream(bytes), exportFileName);
					// save the zipped output, consider using pipes when zipfile
					// should get to big

					getMediaService().setDataForMedia(exportMedia, exportData);
					feedList.add(exportMedia);
					Map<String,String> directoryMap=cronJob.getReevooExportDirectoryMap();
					if(directoryMap.get(entry.getKey().getIsocode())!=null) {
						saveExternal(new ByteArrayInputStream(bytes),getFullExportPath(directoryMap.get(entry.getKey().getIsocode())),exportFileName);
					}else {
						saveExternal(new ByteArrayInputStream(bytes),getFullExportPath (cronJob.getExportDirectory()),exportFileName);
					}
					
				} catch (final MediaIOException e) {
					String message = "Failed writing export data for file [" + exportName + "] to media.";
					LOG.error(message, e);
					throw new DistReevooPurchaserExportServiceException(message, e);
				} catch (final ModelSavingException e) {
					String message = "Failed writing export data for file [" + exportName + "] to media.";
					LOG.error(message, e);
					throw new DistReevooPurchaserExportServiceException(message, e);
				} catch (IOException e) {
					String message = "Failed writing export data for file [" + exportName + "] to media.";
					LOG.error(message, e);
					throw new DistReevooPurchaserExportServiceException(message, e);
				} finally {
					try {
						exportDataStream.close();
					} catch (IOException e) {
						LOG.error("Error while closing stream for Reevoo Purchaser Feed", e);
					}
				}
			}
		}
		cronJob.setFeedFiles(feedList);
		getModelService().save(cronJob);
	}
	
	private String getFullExportPath(final String path) {
		 final Configuration config = getConfigurationService().getConfiguration();
		 return config.getString(DistConstants.PropKey.Reevoo.EXPORT_UPLOAD_PATH_PREFIX)+path;
	}

	/**
	 * Media code, e.g. cronJob.mediaPrefix +
	 * "distrelec_D_7310_ch_de_20131022-111121"
	 */
	private String getMediaCode(final String exportName,LanguageModel language) {
		final StringBuilder mediaCode = new StringBuilder();
		mediaCode.append(exportName);
		mediaCode.append(DistConstants.Punctuation.DASH);
		mediaCode.append(language.getIsocode());
		mediaCode.append(DistConstants.Punctuation.DASH);
		mediaCode.append("Purchaser");
		mediaCode.append(DistConstants.Punctuation.DASH);
		mediaCode.append(FastDateFormat.getInstance(FILE_NAME_PATTERN).format(new Date()));

		return mediaCode.toString();
	}

	public void saveExternal(final InputStream input, final String exportPath,
			 String exportName) throws DistReevooPurchaserExportServiceException, DistReevooExportServiceException {
		//Copy File to FTP Location
        Boolean exportEnabled=getConfigurationService().getConfiguration().getBoolean("reevoo.export.ftp.enabled",true);
    	if(exportEnabled.booleanValue()) {
    		try {
    			uploadToScp(input,exportPath,exportName);
	        }catch (final IOException e) {
	            String message = "";
	            message = "Failed writing export to SFTP Server [" + exportName + "].";
	            LOG.error(message, e);
	            throw new DistReevooExportServiceException(message, e);
	        }
    	}
	}
	
	private void uploadToScp(InputStream from,String directory ,String fileName) throws IOException {
		try {
	        final Configuration config = getConfigurationService().getConfiguration();
	        final String user = config.getString(DistConstants.PropKey.Reevoo.EXPORT_UPLOAD_SCP_USER);
	        final String host = config.getString(DistConstants.PropKey.Reevoo.EXPORT_UPLOAD_SCP_HOST);
	        final int port = config.getInt(DistConstants.PropKey.Reevoo.EXPORT_UPLOAD_SCP_PORT, 22);
	        final String to = directory;
	        final String privateKey = config.getString(DistConstants.PropKey.Reevoo.EXPORT_UPLOAD_SCP_PRIVATEKEY);
	        final String keyPassword = config.getString(DistConstants.PropKey.Reevoo.EXPORT_UPLOAD_SCP_KEYPASSWORD);
	
	        LOG.info("Upload {} to {}@{}:{}", fileName, user, host, to);
	
	        Session session = null;
	        try {
	            session = ScpUtils.createSession(user, host, port, privateKey, keyPassword);
	            ScpUtils.copyLocalToRemote(session, from, to, fileName);
	        } finally {
	            if (session != null) {
	                session.disconnect();
	            }
	        }
		}catch(JSchException ex) {
   		 LOG.error("Cannot close FTP Client", ex);
   	}
    }

	// BEGIN GENERATED CODE

	protected MediaService getMediaService() {
		return mediaService;
	}

	@Required
	public void setMediaService(final MediaService mediaService) {
		this.mediaService = mediaService;
	}

	protected ModelService getModelService() {
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService) {
		this.modelService = modelService;
	}

	public DistZipService getDistZipService() {
		return distZipService;
	}

	@Required
	public void setDistZipService(final DistZipService distZipService) {
		this.distZipService = distZipService;
	}

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public DistRevooCsvTransformationService getDistRevooCsvTransformationService() {
		return distRevooCsvTransformationService;
	}

	public void setDistRevooCsvTransformationService(
			DistRevooCsvTransformationService distRevooCsvTransformationService) {
		this.distRevooCsvTransformationService = distRevooCsvTransformationService;
	}

}
