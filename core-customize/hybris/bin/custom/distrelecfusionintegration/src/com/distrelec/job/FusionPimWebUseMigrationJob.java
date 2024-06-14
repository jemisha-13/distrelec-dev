package com.distrelec.job;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Fusion.ALLOWED_CHARACTERS_FIELDNAME;
import static de.hybris.platform.catalog.enums.ClassificationAttributeVisibilityEnum.A_VISIBILITY;
import static de.hybris.platform.catalog.enums.ClassificationAttributeVisibilityEnum.B_VISIBILITY;
import static de.hybris.platform.catalog.enums.ClassificationAttributeVisibilityEnum.C_VISIBILITY;
import static de.hybris.platform.catalog.enums.ClassificationAttributeVisibilityEnum.D_VISIBILITY;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.distrelec.b2b.core.search.data.PimWebUseField;
import com.distrelec.solrfacetsearch.model.jobs.FusionPimWebUseMigrationCronJobModel;
import com.google.gson.Gson;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;

/**
 * Job populates the newly introduced field product.pimWebUseJson.
 * It reads the classification of the products and creates a localized JSON which is saved on the product.
 */
public class FusionPimWebUseMigrationJob extends AbstractJobPerformable<FusionPimWebUseMigrationCronJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(FusionPimWebUseMigrationJob.class);

    private static final String PRODUCT_QUERY = "SELECT {pk} FROM {product}";

    private static final String DELTA_MIGRATION_CONDITION = " WHERE {pimWebUseJson[%s]} IS NULL AND {pimWebUse[%s]} IS NOT NULL";

    private static final int ABORT_JOB_CHECK_COUNT = 200;

    private static final int LOG_PROGRESS_CHECK_COUNT = 100;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###.########", new DecimalFormatSymbols(Locale.US));

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private Gson gson;

    @Override
    public PerformResult perform(FusionPimWebUseMigrationCronJobModel cronJob) {
        var t1 = System.currentTimeMillis();

        List<LanguageModel> languages = cronJob.getLanguages();
        if (isEmpty(languages)) {
            LOG.info("No languages configured for PimWebUseMigrationJob");
            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        }

        List<PerformResult> subResults = new ArrayList<>();
        for (LanguageModel language : languages) {
            try {
                PerformResult subResult = processLanguage(language, cronJob);
                subResults.add(subResult);
            } catch (CronJobAbortedException e) {
                LOG.info(e.getMessage());
                return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.ABORTED);
            }
        }

        PerformResult mainResult = subResults.stream()
                                             .filter(this::isNotFinished)
                                             .findFirst()
                                             .orElse(new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED));

        LOG.info("Total time for PimWebUseJson-migration was:{} s", ((System.currentTimeMillis() - t1) / 1000));
        LOG.info("Full job result is: {} and status is: {}", mainResult.getResult().getCode(), mainResult.getStatus().getCode());

        return mainResult;
    }

    private PerformResult processLanguage(LanguageModel language, FusionPimWebUseMigrationCronJobModel cronJob) {
        JaloSession currentSession = JaloSession.getCurrentSession();
        Tenant currentTenant = Registry.getCurrentTenant();
        return sessionService.executeInLocalView(new SessionExecutionBody() {

            @Override
            public PerformResult execute() {
                List<PK> products = getProductsForMigration(language, isTrue(cronJob.getFullMigration()));
                LOG.info("Start converting {} products for language {}", products.size(), language);
                processProducts(currentSession, currentTenant, products, cronJob, language);
                return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
            }
        });
    }

    private void processProducts(JaloSession currentSession, Tenant currentTenant, List<PK> products, FusionPimWebUseMigrationCronJobModel cronJob,
                                 LanguageModel language) {
        AtomicInteger index = new AtomicInteger();
        products.parallelStream()
                .forEach(productPk -> {
                    Registry.setCurrentTenant(currentTenant);
                    currentSession.activate();
                    ProductModel product = modelService.get(productPk);
                    processProduct(product, products.size(), index.incrementAndGet(), cronJob, language);
                });
    }

    private void processProduct(ProductModel product, int numberOfProduct, int i, FusionPimWebUseMigrationCronJobModel cronJob, LanguageModel language) {
        if (shouldAbort(i, cronJob)) {
            throw new CronJobAbortedException(String.format("CronJob is aborted externally on %s/%s products for language %s", i, numberOfProduct, language));
        }

        commonI18NService.setCurrentLanguage(language);
        createPimWebUseForLanguage(product, i, numberOfProduct, language);
    }

    private boolean isNotFinished(PerformResult subResult) {
        return !CronJobStatus.FINISHED.equals(subResult.getStatus());
    }

    private boolean shouldAbort(int i, FusionPimWebUseMigrationCronJobModel cronJob) {
        return (i % ABORT_JOB_CHECK_COUNT == 0)
                && clearAbortRequestedIfNeeded(cronJob);
    }

    private List<PK> getProductsForMigration(LanguageModel language, boolean fullMigration) {
        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(createProductQuery(language, fullMigration));
        flexibleSearchQuery.setResultClassList(Collections.singletonList(PK.class));

        return flexibleSearchService.<PK> search(flexibleSearchQuery).getResult();
    }

    private String createProductQuery(LanguageModel language, boolean fullMigration) {
        String query = PRODUCT_QUERY;
        if (!fullMigration) {
            query += String.format(DELTA_MIGRATION_CONDITION, language.getIsocode(), language.getIsocode());
        }
        return query;
    }

    private void createPimWebUseForLanguage(ProductModel product, int index, int totalProducts, LanguageModel language) {
        var millis = System.currentTimeMillis();

        FeatureList features = classificationService.getFeatures(product);
        List<PimWebUseField> fields = createPimWebUseFields(features);

        int numberProcesses = index + 1;
        if (isNotEmpty(fields)) {
            product.setPimWebUseJson(gson.toJson(fields), Locale.forLanguageTag(language.getIsocode()));
            modelService.save(product);
            LOG.info("PimWebUseJson updated for product: {}[{}], progress: {} / {}", product.getCode(), language.getIsocode(), numberProcesses, totalProducts);
            LOG.debug("Time take {} ms", System.currentTimeMillis() - millis);
        } else {
            LOG.debug("PimWebUseJson not updated for product: {}[{}], progress: {} / {}", product.getCode(), language.getIsocode(), numberProcesses,
                      totalProducts);
        }
        if (numberProcesses % LOG_PROGRESS_CHECK_COUNT == 0) {
            LOG.info("PimWebUseJson migration progress for lang[{}]: {} / {}", language.getIsocode(), numberProcesses, totalProducts);
        }
    }

    private List<PimWebUseField> createPimWebUseFields(FeatureList features) {
        List<PimWebUseField> fields = new ArrayList<>();

        for (Feature feature : features) {
            ClassAttributeAssignmentModel classAssign = feature.getClassAttributeAssignment();

            if (shouldSkipFeature(feature, classAssign)) {
                continue;
            }

            String code = classAssign.getClassificationAttribute().getCode();
            String name = classAssign.getClassificationAttribute().getName();

            List<PimWebUseField> valueFields = feature.getValues()
                                                      .stream()
                                                      .map(value -> createPimWebUseField(classAssign, code, name, value))
                                                      .collect(toList());
            fields.addAll(valueFields);
        }
        return fields;
    }

    private boolean shouldSkipFeature(Feature feature, ClassAttributeAssignmentModel classAssign) {
        return isEmpty(feature.getValues())
                || shouldSkipClassAttributeAssignment(classAssign);
    }

    private boolean shouldSkipClassAttributeAssignment(ClassAttributeAssignmentModel classAssign) {
        return classAssign == null
                || !isPimWebUseVisible(classAssign)
                || isPimWebUseDfeature(classAssign)
                || classAssign.getClassificationAttribute() == null;
    }

    private boolean isPimWebUseDfeature(ClassAttributeAssignmentModel assignment) {
        return assignment.getVisibility() == D_VISIBILITY;
    }

    private boolean isPimWebUseVisible(ClassAttributeAssignmentModel assignment) {
        return assignment.getVisibility() == A_VISIBILITY
                || assignment.getVisibility() == B_VISIBILITY
                || assignment.getVisibility() == C_VISIBILITY
                || assignment.getVisibility() == D_VISIBILITY;
    }

    private PimWebUseField createPimWebUseField(ClassAttributeAssignmentModel classAssign, String code, String name, FeatureValue value) {
        String unitSymbol = value.getUnit() != null ? value.getUnit().getSymbol() : "";
        PimWebUseField pimWebUse = new PimWebUseField();

        if (value.getValue() instanceof Number) {
            pimWebUse.setValue(DECIMAL_FORMAT.format(value.getValue()));
        } else {
            pimWebUse.setValue(value.getValue().toString());
        }

        pimWebUse.setCode(cleanPimWebUseCode(code));
        pimWebUse.setAttributeName(name);
        pimWebUse.setUnit(unitSymbol);

        String fieldType = ClassificationAttributeTypeEnum.NUMBER == classAssign.getAttributeType() ? SolrPropertiesTypes.DOUBLE.getCode()
                                                                                                    : SolrPropertiesTypes.STRING.getCode();
        pimWebUse.setFieldType(fieldType);
        return pimWebUse;
    }

    private String cleanPimWebUseCode(final String attributeName) {
        return attributeName != null ? attributeName.replaceAll(ALLOWED_CHARACTERS_FIELDNAME, "").toLowerCase() : null;
    }

    @Override
    public boolean isAbortable() {
        return true;
    }

    private static class CronJobAbortedException extends RuntimeException {
        public CronJobAbortedException(String message) {
            super(message);
        }
    }
}
