package com.namics.hybris.ffsearch.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;

import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.CATEGORY_CODE_PREFIX;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.CATEGORY_CODE_SUFFIX;

public class DistFactFinderUtils {

    public static Collection<DistFactFinderExportChannelModel> getActiveChannelsForCMSSite(final CMSSiteModel cmsSite) {
        Map<Long, DistFactFinderExportChannelModel> channelModelMap = new HashMap<>();

        for (DistFactFinderExportChannelModel channelModel : cmsSite.getFfExportChannels()) {
            if (channelModel.getActive()) {
                channelModelMap.put(channelModel.getPk().getLong(), channelModel);
            }
        }

        return channelModelMap.values();
    }

    public static Set<String> getLanguagesForCMSSite(final CMSSiteModel cmsSite) {
        Set<String> languages = new HashSet<>();
        for (DistFactFinderExportChannelModel channelModel : cmsSite.getFfExportChannels()) {
            if (channelModel.getActive()) {
                LanguageModel languageModel = channelModel.getLanguage();
                languages.add(languageModel.getIsocode());
            }
        }
        return languages;
    }

    public static Set<String> getSalesOrgLanguages(final Collection<CMSSiteModel> cmsSites, final DistSalesOrgModel salesOrgModel) {
        Set<String> languages = new HashSet<>();

        for (CMSSiteModel cmsSiteModel : cmsSites) {
            if (cmsSiteModel.getSalesOrg().getCode().equals(salesOrgModel.getCode())) {
                for (DistFactFinderExportChannelModel channelModel : cmsSiteModel.getFfExportChannels()) {
                    if (channelModel.getActive()) {
                        LanguageModel languageModel = channelModel.getLanguage();
                        languages.add(languageModel.getIsocode());
                    }
                }
            }
        }

        return languages;
    }

    public static String getCategoryCodeFFAttribute(int categoryLevel) {
        return CATEGORY_CODE_PREFIX + categoryLevel + CATEGORY_CODE_SUFFIX;
    }

    private DistFactFinderUtils() {
        // private constructor protects from instancing
    }
}
