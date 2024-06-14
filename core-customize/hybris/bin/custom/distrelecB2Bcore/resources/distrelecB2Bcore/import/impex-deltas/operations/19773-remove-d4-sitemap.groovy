import de.hybris.platform.servicelayer.config.ConfigurationService
import org.apache.commons.io.FileUtils

import java.util.logging.Logger

final Logger LOG = Logger.getLogger("19733-remove-d4-sitemap")

final ConfigurationService configurationService = spring.getBean(ConfigurationService.class)

String sitemapFolderName = "distrelec_FR"

String rootDir = configurationService.configuration.getProperty("websitemap.data.rootdir")

String sitemapFolderPath = rootDir + File.separator + sitemapFolderName;
File sitemapFolder = new File(sitemapFolderPath)

LOG.info("remove all files from " + sitemapFolderPath)
FileUtils.cleanDirectory(sitemapFolder)
LOG.info("files were removed")
