package com.namics.distrelec.b2b.core.cms.job;

import com.namics.distrelec.b2b.core.event.DistInternalLinkEvent;
import com.namics.distrelec.b2b.core.message.queue.model.RowType;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.event.EventService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.InternalLink.INTERNAL_LINK_FORCE_RECALCULATION;
import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.Taxonomy.CATEGORY_MAX_LEVEL_KEY;
import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.Taxonomy.ROOT_CATEGORY_CODE_KEY;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public class RecalculateMeshLinksJob extends AbstractJobPerformable<CronJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(RecalculateMeshLinksJob.class);

    private static final String INTERNATIONAL_SITE_UID = "distrelec";

    @Autowired
    private CMSSiteService cmsSiteService;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private DistCategoryService categoryService;
    @Autowired
    private EventService eventService;

    @Override
    public PerformResult perform(CronJobModel cronJobModel) {
        boolean error = false;
        try {
            processCategories();

        } catch (final Exception e) {
        error = true;
        LOG.error("Error occur while placing mesh links recalculation tasks", e);
    }

        return new PerformResult(error ? CronJobResult.FAILURE : CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    private void processCategories() {
        // Fetch the root L0 category and its sub-categories
        // The ROOT Category Navigation Node should also have an entry pointing to the ROOT L0 Category.
        final String root_category_code = configurationService.getConfiguration().getString(ROOT_CATEGORY_CODE_KEY, "");
        if (StringUtils.isBlank(root_category_code)) {
            throw new IllegalStateException("No Root Category Code defined");
        }

        final CategoryModel root = categoryService.getCategoryForCode(root_category_code);

        if (root == null || CollectionUtils.isEmpty(root.getCategories())) {
            throw new RuntimeException("No category found");
        }

        // Fetch all CMS sites to update.
        final Collection<CMSSiteModel> cmsSites = cmsSiteService.getSites();
        List<CMSSiteModel> filteredCMSSites = filterCMSSites(cmsSites);
        if (CollectionUtils.isEmpty(filteredCMSSites)) {
            throw new RuntimeException("No CMSSite found");
        }

        processCategories(root.getCategories(), filteredCMSSites);
    }

    protected List<CMSSiteModel> filterCMSSites(Collection<CMSSiteModel> cmsSites) {
        return cmsSites.stream()
                .filter(cmsSite -> isNotEmpty(cmsSite.getStores()))
                .filter(cmsSite -> !cmsSite.getUid().equals(INTERNATIONAL_SITE_UID))
                .collect(Collectors.toList());
    }

    protected void processCategories(List<CategoryModel> categories, List<CMSSiteModel> cmsSites) {
        for (CategoryModel category : categories) {
            if (category.getLevel() == null || category.getLevel().intValue() > getMaxCategoryLevel()) {
                break;
            }

            for (CMSSiteModel cmsSite : cmsSites) {
                if (!isEmptyCategory(category, cmsSite)) {
                    DistInternalLinkEvent ilEvent = new DistInternalLinkEvent(category.getCode(), RowType.CATEGORY,
                            cmsSite.getUid(), null, isRecalculationForced());
                    eventService.publishEvent(ilEvent);
                }
            }

            processCategories(category.getCategories(), cmsSites);
        }
    }

    protected boolean isEmptyCategory(final CategoryModel category, final CMSSiteModel cmsSite) {
        return category == null || categoryService.isCategoryEmptyForCMSSite(category, cmsSite) ||
                categoryService.hasSuccessor(category.getCode());
    }

    protected int getMaxCategoryLevel() {
        return configurationService.getConfiguration().getInt(CATEGORY_MAX_LEVEL_KEY, 2);
    }

    protected boolean isRecalculationForced() {
        return configurationService.getConfiguration().getBoolean(INTERNAL_LINK_FORCE_RECALCULATION, false);
    }
}
