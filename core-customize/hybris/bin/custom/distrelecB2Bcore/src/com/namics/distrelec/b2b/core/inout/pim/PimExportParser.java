package com.namics.distrelec.b2b.core.inout.pim;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimExportParserHelper;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.composite.ParseResult;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler.PimImportElementHandler;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler.PimImportElementHandlerFactory;
import com.namics.distrelec.b2b.core.model.DistAudioMediaModel;
import com.namics.distrelec.b2b.core.model.DistDownloadMediaModel;
import com.namics.distrelec.b2b.core.model.DistImage360Model;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;
import com.namics.distrelec.b2b.core.model.pim.config.PimImportConfigModel;
import com.namics.distrelec.b2b.core.pim.config.service.PimImportConfigService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.mutable.MutableInt;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.Pim.ATTRIBUTE_PIM_DEFAULT_LANGUAGE;
import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimExportParserHelper.FAMILY_TYPECODE;

public abstract class PimExportParser<T extends ImportContext> {

    private static final Logger LOG = LoggerFactory.getLogger(PimExportParser.class);

    private static final int LOG_INTERVAL = 30000;

    private static final int CANCEL_TIMER_INTERVAL = 5000;

    private static final String CFG_GLOBAL_PIM_SKIP_HASHCHECK = "pim.global.skip.hashcheck";

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ModelService modelService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private PimExportParserHelper pimExportParserHelper;

    @Autowired
    private CatalogVersionService catalogVersionService;

    @Autowired
    private ClassificationSystemService classificationSystemService;

    @Autowired
    private PimImportConfigService pimImportConfigService;

    @Autowired
    private PimImportElementHandlerFactory pimImportElementHandlerFactory;

    protected abstract void parseSpecific(final T importContext);

    protected abstract T getImportContextInstance();

    protected abstract void registerHandler(final SAXReader reader, final T importContext);

    public ParseResult parseFile(final File file) {
        Assert.notNull(file, "File passed to parser was null!");
        LOG.info("start parsing file [" + file.getAbsolutePath() + "]");
        boolean success = false;
        String fileName = file.getName();
        T importContext = null;
        try {
            // Create import context.
            importContext = createImportContext(file);

            startStatisticsLoggingTimer(importContext);
            startCancelTimer(importContext);

            final SAXReader reader = new SAXReader();
            registerHandler(reader, importContext);
            reader.read(file);

            parseSpecific(importContext);

            // Store some timing info at the Language model
            final String defaultLangIso = configurationService.getConfiguration().getString(ATTRIBUTE_PIM_DEFAULT_LANGUAGE, "en");
            final LanguageModel currentLanguage = commonI18NService.getLanguage(defaultLangIso);
            currentLanguage.setPimXmlStiboExportDate(importContext.getStiboExportDate());
            currentLanguage.setPimXmlHybrisImportStartDate(new Date(importContext.getStartTime()));
            currentLanguage.setPimXmlHybrisImportEndDate(new Date());
            modelService.save(currentLanguage);

            LOG.info("Successfully parsed file [" + fileName + "]");
            logInfoStatistics(importContext, LOG_INTERVAL);

            success = true;
        } catch (final DocumentException e) {
            LOG.error("Exception while reading XML file: " + fileName, e);
            LOG.error("Nested exception while reading XML file: " + fileName, e.getNestedException());
        } catch (final Exception e) {
            LOG.error("Error during PIM import: " + fileName, e);
        } finally {
            if (importContext != null) {
                stopStatisticsLoggingTimer(importContext);
                stopCancelTimer(importContext);
            }
        }

        return new ParseResult(success, getEndLogStatistics(importContext));
    }

    public String getEndLogStatistics(T importContext) {
        return buildLogStatistics(importContext, LOG_INTERVAL);
    }

    private void startCancelTimer(final T importContext) {
        // Reset "cancelled" property
        configurationService.getConfiguration().setProperty(DistConstants.PropKey.Import.CANCELLED, Boolean.FALSE.toString());

        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (configurationService.getConfiguration().getBoolean(DistConstants.PropKey.Import.CANCELLED, false)) {
                    importContext.setCancelled(true);
                }
            }
        };

        final Timer timer = new Timer();
        timer.schedule(task, CANCEL_TIMER_INTERVAL, CANCEL_TIMER_INTERVAL);
        importContext.setCancelTimer(timer);
    }

    private void startStatisticsLoggingTimer(final T importContext) {
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                logDebugStatistics(importContext, LOG_INTERVAL);
            }
        };

        final Timer timer = new Timer();
        timer.schedule(task, LOG_INTERVAL, LOG_INTERVAL);
        importContext.setLoggingTimer(timer);
    }

    private void stopStatisticsLoggingTimer(final T importContext) {
        if (importContext.getLoggingTimer() != null) {
            importContext.getLoggingTimer().cancel();
        }
    }

    private void stopCancelTimer(final T importContext) {
        if (importContext.getCancelTimer() != null) {
            importContext.getCancelTimer().cancel();
        }
    }

    protected void logDebugStatistics(T importContext, int duration) {
        LOG.debug(buildLogStatistics(importContext, duration));
    }

    protected void logInfoStatistics(T importContext, int duration) {
        LOG.info(buildLogStatistics(importContext, duration));
    }

    private String buildLogStatistics(T importContext, int duration) {
        final StringBuilder message = new StringBuilder(1000);

        if(importContext == null)
        {
            message.append("import failed, context null. duration:"+duration);
            return message.toString();
        }

        message.append("Processed items for file [");
        message.append(importContext.getFilename());
        message.append("] exported from Stibo at [");
        message.append(importContext.getStiboExportDate() != null ? formatter.format(importContext.getStiboExportDate()) : "unknown");
        message.append("]:\n");
        final Iterator<Map.Entry<String, MutableInt>> iter = importContext.getCounters().entrySet().iterator();
        int itemsImported = 0;
        while (iter.hasNext()) {
            final Map.Entry<String, MutableInt> entry = iter.next();
            message.append("\t").append(entry.getKey()).append(": ").append(entry.getValue().toString());
            message.append("\n");
            itemsImported += entry.getValue().intValue();
        }

        final Float itemsPerSecond = (float) (itemsImported - importContext.getItemsImported()) / (duration / 1000);

        message.append("\t---\n");
        message.append("\tImported: " + itemsImported + " items.\n");
        message.append("\tSpeed: " + String.format("%.1f", itemsPerSecond) + " items/s.\n");
        message.append("\tDuration: " + DurationFormatUtils.formatPeriod(importContext.getStartTime(), System.currentTimeMillis(), "HH'h' mm'm' ss's'"));

        importContext.setItemsImported(itemsImported);
        return message.toString();
    }

    protected T createImportContext(final File file) {
        final Configuration configuration = configurationService.getConfiguration();

        final T importContext = getImportContextInstance();
        importContext.setStartTime(System.currentTimeMillis());
        importContext.setItemsImported(0);

        importContext.setFilename(file.getName());

        importContext.setIgnoreRootAttributes(configuration.getBoolean(DistConstants.PropKey.Import.IGNORE_ROOT_ATTRIBUTES, false));
        importContext.setProductCatalogVersion(getProductCatalogVersion());

        final ClassificationSystemVersionModel classificationSystemVersion = getClassificationSystemVersion();
        importContext.setClassificationSystemVersion(classificationSystemVersion);
        importContext.setRootClassificationClass(getRootClassificationClass(classificationSystemVersion));

        importContext.setCategoryCodePrefix(configuration.getString(DistConstants.PropKey.Import.CATEGORY_CODE_PREFIX));
        importContext.setCategoryCodeSuffix(configuration.getString(DistConstants.PropKey.Import.CATEGORY_CODE_SUFFIX));
        importContext.setClassificationClassCodePrefix(configuration.getString(DistConstants.PropKey.Import.CLASSIFICATION_CLASS_CODE_PREFIX));
        importContext.setRootCategoryParentIds(Arrays.asList(configuration.getString(DistConstants.PropKey.Import.ROOT_CATEGORY_PARENT_IDS).split(";")));

        // for new incremental PIM import hash verification will be disabled
        boolean skip_hashcheck = configurationService.getConfiguration().getBoolean(CFG_GLOBAL_PIM_SKIP_HASHCHECK, true);
        if(!skip_hashcheck) {
            importContext.getHashValues().put(ProductModel._TYPECODE, pimExportParserHelper.getProductHashes());
            importContext.getHashValues().put(ClassificationAttributeUnitModel._TYPECODE,
                    pimExportParserHelper.getUnitHashes(importContext.getClassificationSystemVersion()));
            importContext.getHashValues().put(MediaContainerModel._TYPECODE, pimExportParserHelper.getMediaContainerHashes());
            importContext.getHashValues().put(DistAudioMediaModel._TYPECODE, pimExportParserHelper.getAudioMediaHashes());
            importContext.getHashValues().put(DistDownloadMediaModel._TYPECODE, pimExportParserHelper.getDownloadMediaHashes());
            importContext.getHashValues().put(DistVideoMediaModel._TYPECODE, pimExportParserHelper.getVideoMediaHashes());
            importContext.getHashValues().put(DistManufacturerModel._TYPECODE, pimExportParserHelper.getManufacturerHashes());
            importContext.getHashValues().put(DistImage360Model._TYPECODE, pimExportParserHelper.getImage360Hashes());
            importContext.getHashValues().put(FAMILY_TYPECODE, pimExportParserHelper.getProductFamilyHashes());
        }
        PimImportConfigModel pimImportConfig = pimImportConfigService.getPimImportConfig();
        if (pimImportConfig != null) {
            importContext.setGlobalHashTimestamp(pimImportConfig.getGlobalHashTimestamp());
        }
        importContext.getCodes().put(DistManufacturerModel._TYPECODE, pimExportParserHelper.getManufacturerCodes());

        importContext.setBlacklistedProductFeatures(pimExportParserHelper.getBlacklistedProductFeatures());
        importContext.setWhitelistedRootProductFeatures(pimExportParserHelper.getWhitelistedRootProductFeatures());

        // Tells whether we should dynamically detect the root product features.
        importContext.setUseDynamicRootProductFeatures(
                configurationService.getConfiguration().getBoolean(DistConstants.PropKey.Import.USE_DYNAMIC_ROOT_ATTRIBUTES, false));
        importContext.setNonLocalizedProductFeatures(pimExportParserHelper.getNonLocalizedProductFeatures());
        importContext.setCategoryLevels(pimExportParserHelper.getCategoryLevels());
        importContext.setWhitelistedCategories(pimExportParserHelper.getWhitelistedCategories());

        // Set the sorting index increment, default value is 10.
        importContext.setSortingIndexIncrement(configuration.getInt(DistConstants.PropKey.Import.IMPORT_PIM_TAXONOMY_CATEGORY_SORTING_INDEX_INC, 10));

        return importContext;
    }

    protected PimImportElementHandler getHandler(final String id, final T importContext) {
        final PimImportElementHandler handler = pimImportElementHandlerFactory.createPimImportElementHandler(id);
        handler.setImportContext(importContext);
        return handler;
    }

    protected CatalogVersionModel getProductCatalogVersion() {
        final Configuration configuration = configurationService.getConfiguration();
        return catalogVersionService.getCatalogVersion(configuration.getString(DistConstants.PropKey.Import.PRODUCT_CATALOG_ID),
                configuration.getString(DistConstants.PropKey.Import.PRODUCT_CATALOG_VERSION));
    }

    private ClassificationSystemVersionModel getClassificationSystemVersion() {
        final Configuration configuration = configurationService.getConfiguration();
        return classificationSystemService.getSystemVersion(configuration.getString(DistConstants.PropKey.Import.CLASSIFICATION_SYSTEM_ID),
                configuration.getString(DistConstants.PropKey.Import.CLASSIFICATION_SYSTEM_VERSION));
    }

    private ClassificationClassModel getRootClassificationClass(final ClassificationSystemVersionModel classificationSystemVersion) {
        ClassificationClassModel rootClassificationClass;

        final String code = configurationService.getConfiguration().getString(DistConstants.PropKey.Import.ROOT_CLASSIFICATION_CLASS_CODE);
        try {
            rootClassificationClass = classificationSystemService.getClassForCode(classificationSystemVersion, code);
        } catch (final UnknownIdentifierException e) {
            rootClassificationClass = modelService.create(ClassificationClassModel.class);
            rootClassificationClass.setCode(code);
            rootClassificationClass.setCatalogVersion(classificationSystemVersion);
            modelService.save(rootClassificationClass);
        }
        return rootClassificationClass;
    }
}
