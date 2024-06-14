package com.distrelec.solrfacetsearch.provider.product.impl;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.substring;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistSapPlantProfilesValueResolver extends AbstractDistProductValueResolver {

    private static final String PLANT_SWITZERLAND = "7374&((\\w|\\+)*)";

    private static final String PLANT_INTERNATIONAL = "7371&((\\w|\\+)*)";

    private static final String CH_SUFFIX = "_CH";

    private static final String PROFILE_NEW = "NEW";

    private static final String PROFILE_MAN = "MAN";

    private static final String PROFILE_COMP = "COMP";

    private static final String PROFILE_DAVE = "DAVE";

    private static final String PROFILE_RSP = "RSP";

    private static final String SIMPLIFIED_PROFILE_4 = "4";

    private static final List<String> PROFILE_4_ADDITIONAL_VALUES = List.of(PROFILE_NEW, PROFILE_MAN, PROFILE_COMP, PROFILE_DAVE, PROFILE_RSP);

    protected DistSapPlantProfilesValueResolver(final DistCMSSiteDao distCMSSiteDao, final DistProductSearchExportDAO distProductSearchExportDAO,
                                                final EnumerationService enumerationService) {
        super(distCMSSiteDao, distProductSearchExportDAO, enumerationService);
    }

    @Override
    protected void addFieldValues(final InputDocument document, final IndexerBatchContext indexerBatchContext, final IndexedProperty indexedProperty,
                                  final ProductModel productModel,
                                  final ValueResolverContext<Object, Object> valueResolverContext) throws FieldValueProviderException {
        String plantProfiles = productModel.getSapPlantProfiles();
        if (isBlank(plantProfiles)) {
            return;
        }

        String internationalPlant = extractInternationalPlantProfile(plantProfiles);
        String swissPlant = extractSwissPlantProfile(plantProfiles);

        if (isNotBlank(internationalPlant)) {
            document.addField(indexedProperty, internationalPlant, valueResolverContext.getFieldQualifier());
        }
        if (isNotBlank(swissPlant)) {
            IndexedProperty newIndexedProperty = createNewIndexedProperty(indexedProperty, indexedProperty.getExportId() + CH_SUFFIX);
            document.addField(newIndexedProperty, swissPlant, valueResolverContext.getFieldQualifier());
        }
    }

    private String extractSwissPlantProfile(String plantProfiles) {
        String originalProfile = extractPlantProfiles(plantProfiles, PLANT_SWITZERLAND);
        return extractSimplifiedPlantCodeSwiss(originalProfile);
    }

    private String extractInternationalPlantProfile(String plantProfiles) {
        String originalProfile = extractPlantProfiles(plantProfiles, PLANT_INTERNATIONAL);
        return extractSimplifiedPlantCodeInternational(originalProfile);
    }

    private String extractPlantProfiles(String plantProfiles, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(plantProfiles);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractSimplifiedPlantCodeSwiss(String plantProfile) {
        if (isBlank(plantProfile)) {
            return plantProfile;
        }
        String simplifiedPlantCodeCommon = extractSimplifiedPlantCodeCommon(plantProfile);
        if (isNotBlank(simplifiedPlantCodeCommon)) {
            return simplifiedPlantCodeCommon;
        }
        if (plantProfile.startsWith("FB")) {
            return substring(plantProfile, 2).replaceAll("^0*", "");
        }
        return plantProfile;
    }

    private String extractSimplifiedPlantCodeInternational(String plantProfile) {
        if (isBlank(plantProfile)) {
            return plantProfile;
        }
        String simplifiedPlantCodeCommon = extractSimplifiedPlantCodeCommon(plantProfile);
        if (isNotBlank(simplifiedPlantCodeCommon)) {
            return simplifiedPlantCodeCommon;
        }
        if (plantProfile.startsWith("FA")) {
            return substring(plantProfile, 2).replaceAll("^0*", "");
        }
        return plantProfile;
    }

    private String extractSimplifiedPlantCodeCommon(String plantProfile) {
        if (isProfile4Value(plantProfile)) {
            return SIMPLIFIED_PROFILE_4;
        }
        if (plantProfile.startsWith("FP")) {
            return substring(plantProfile, 2, 3);
        }
        if (plantProfile.startsWith("T+M")) {
            return substring(plantProfile, 3, 4);
        }
        return null;
    }

    private boolean isProfile4Value(String plantProfile) {
        return PROFILE_4_ADDITIONAL_VALUES.contains(plantProfile);
    }

}
