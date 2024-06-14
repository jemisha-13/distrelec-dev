package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.model.process.BomFileNotificationEmailModel;
import com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

public class BomNotificationEmailContext extends AbstractDistEmailContext {

	private static final Logger LOG = LogManager.getLogger(BomNotificationEmailContext.class);

	private BomFileNotificationEmailModel bomNotifyModel;
	private ConfigurationService configurationService;
	private CommonI18NService commonI18NService;
	private String host;

	private final static String LOGO_MEDIA_CODE = "/images/theme/distrelec_logo.png";

	private static final int OFFSET = 0;
	private static final int SIZE = 8;

	private final static String INFO_ICON = "infoIcon";

	@Autowired
	private SessionService sessionService;

	@Autowired
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Autowired
	private MediaService mediaService;

	@Override
	public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {

		if (businessProcessModel instanceof BomFileNotificationEmailModel) {
			bomNotifyModel = (BomFileNotificationEmailModel) businessProcessModel;
		}
		final BaseSiteModel baseSite = getSite(bomNotifyModel);

		final SiteBaseUrlResolutionService siteBaseUrlResolutionService = getSiteBaseUrlResolutionService();

		put(BASE_URL, siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, false, ""));
		put(SECURE_BASE_URL, siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, true, ""));
		put(MEDIA_BASE_URL, siteBaseUrlResolutionService.getMediaUrlForSite(baseSite, false));
		put(MEDIA_SECURE_BASE_URL, siteBaseUrlResolutionService.getMediaUrlForSite(baseSite, true));

		final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss z");
		Date date = new Date(System.currentTimeMillis());
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.WEEK_OF_MONTH, 2);
		date = c.getTime();
		put("deleteDate", formatter.format(date));
		put("site", baseSite);
		put("country", bomNotifyModel.getCustomer().getContactAddress().getCountry().getIsocode());
		put(THEME, null);
		put(FROM_EMAIL, emailPageModel.getFromEmail());
		put(DISPLAY_NAME, bomNotifyModel.getNotifiedCustomer().getDisplayName());
		put(FROM_DISPLAY_NAME, emailPageModel.getFromName());
		put(EMAIL, bomNotifyModel.getNotifiedCustomer().getUid());
		put(EMAIL_LANGUAGE, getEmailLanguage(bomNotifyModel));
		put("urlLang", getEmailLanguage(bomNotifyModel).getIsocode());
		put("customerName", bomNotifyModel.getNotifiedCustomer().getName());

		put("unusedFiles", bomNotifyModel.getUnusedfilenames());
		put("StringEscapeUtils", StringEscapeUtils.class);
		put("uid", bomNotifyModel.getNotifiedCustomer().getUid());
		setHost(configurationService.getConfiguration().getString("website." + baseSite.getUid() + ".https"));
		put("secureUrl", siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, true, ""));

		// get logo image URL from media model
		final CMSSiteModel site = (CMSSiteModel) getProcessContextResolutionStrategy().getCmsSite(bomNotifyModel);
		final MediaModel logoMediaModel = getSiteMedia(site, LOGO_MEDIA_CODE);
		final MediaModel infoIconMediaModel = getSiteMedia(site, INFO_ICON);

		put("logoURL", logoMediaModel.getUrl());
		put("infoIconURL", infoIconMediaModel.getUrl());
	}

	private MediaModel getSiteMedia(final CMSSiteModel siteModel, final String mediaCode) {
		final List<ContentCatalogModel> contentCatalogs = siteModel.getContentCatalogs();
		final ListIterator<ContentCatalogModel> iterator = contentCatalogs.listIterator(contentCatalogs.size());
		while (iterator.hasPrevious()) {
			final ContentCatalogModel contentCatalog = iterator.previous();
			final CatalogVersionModel catalogVersion = contentCatalog.getActiveCatalogVersion();
			try {
				return getMediaService().getMedia(catalogVersion, mediaCode);
			} catch (final UnknownIdentifierException e) {
				// media is not find, loop
			}
		}

		// media is not found, throw exception
		final String errMsg = String.format("Media does not exist with code %s and site %s", mediaCode,
				siteModel.getUid());
		throw new IllegalArgumentException(errMsg);
	}

	@Override
	protected BaseSiteModel getSite(final BusinessProcessModel businessProcessModel) {
		if (businessProcessModel instanceof BomFileNotificationEmailModel) {
			return ((BomFileNotificationEmailModel) businessProcessModel).getSite();
		}
		return null;
	}

	@Override
	protected LanguageModel getEmailLanguage(final BusinessProcessModel businessProcessModel) {
		if (businessProcessModel instanceof BomFileNotificationEmailModel) {
			return bomNotifyModel.getNotifiedCustomer().getSessionLanguage();
		}
		return null;
	}

	@Override
	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	@Override
	@Required
	public void setConfigurationService(final ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	@Override
	public CommonI18NService getCommonI18NService() {
		return commonI18NService;
	}

	@Override
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService) {
		this.commonI18NService = commonI18NService;
	}

	@Override
	protected CustomerModel getCustomer(final BusinessProcessModel businessProcessModel) {
		return bomNotifyModel.getNotifiedCustomer();
	}

	public String getHost() {
		return host;
	}

	public void setHost(final String host) {
		this.host = host;
	}

	public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService() {
		return b2bUnitService;
	}

	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService) {
		this.b2bUnitService = b2bUnitService;
	}

	public SessionService getSessionService() {
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService) {
		this.sessionService = sessionService;
	}

	public ProcessContextResolutionStrategy getProcessContextResolutionStrategy() {
		return (ProcessContextResolutionStrategy) Registry.getApplicationContext()
				.getBean("processContextResolutionStrategy");
	}

	public MediaService getMediaService() {
		return mediaService;
	}

	public void setMediaService(final MediaService mediaService) {
		this.mediaService = mediaService;
	}

}
