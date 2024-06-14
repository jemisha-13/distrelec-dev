package com.namics.hybris.toolbox.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.namics.hybris.toolbox.spring.SpringUtil;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Util class to transform a Map with ISO Codes to a map with hybris <code>Language</code> Objects.
 * 
 * @see Language
 * @author jweiss, namics ag
 * @since Athleticum 1.0
 * 
 */
public class HybrisItemsUtils {
    private final static ModelService modelService = SpringUtil.getBean("modelService", ModelService.class);
    private final static CommonI18NService commonI18NService = SpringUtil.getBean("commonI18NService", CommonI18NService.class);

    /**
     * Transform a Map with ISO-codes and values to a Map with the appropriate hybris <code>Language</code> Object as keys.
     * 
     * @param languageNames
     *            The naming of an Object in different languages.<br>
     *            Example: Map{de:Währung, fr:Monnaie, en:Concurrency}<br>
     *            The key is the ISO code of the language.<br>
     *            The value is the name of an object in the language of the ISO code.
     * @return The naming of an Object in different languages.<br>
     *         Example: Map{<code>de.hybris.platform.jalo.c2l.Language("de")</code>:Währung, <br>
     *         <code>de.hybris.platform.jalo.c2l.Language("fr")</code>:Monnaie, <br>
     *         <code>de.hybris.platform.jalo.c2l.Language("en")</code>:Concurrency}<br>
     *         The key is a reference to the hybris jalo language object with this iso code.<br>
     *         The value is the name of an object in the language of the ISO code.
     */
    public static Map<Language, Object> transformToHybrisAttributesMap(final Map<String, String> languageNames) {
        final Map<Language, Object> hybrisAttributesMap = new HashMap<Language, Object>();

        for (final String isoCode : languageNames.keySet()) {
            final String localizedName = languageNames.get(isoCode);
            final Language language = modelService.getSource(commonI18NService.getLanguage(isoCode));
            hybrisAttributesMap.put(language, localizedName);
        }
        return hybrisAttributesMap;
    }

    /**
     * Creates a Map, that contains a key for each entry in <code>supportedIsoLanguageCodes</code>. The value is always
     * <code>internationalName</code>.
     * 
     * <pre>
     *  Map{de:"InternationalName", fr:"InternationalName"}
     * </pre>
     * 
     * @param supportedIsoLanguageCodes
     *            List of strings of supported iso codes
     * @param internationalName
     *            String of international name
     * @return a Map, that contains a key for each entry in <code>supportedIsoLanguageCodes</code>. The value is always
     *         <code>internationalName</code>.
     */
    public static Map<String, String> createMapWithLocalizedNamesOfInternationalName(final List<String> supportedIsoLanguageCodes,
            final String internationalName) {
        final Map<String, String> resultMap = new HashMap<String, String>();
        for (final String isoCode : supportedIsoLanguageCodes) {
            resultMap.put(isoCode, internationalName);
        }
        return resultMap;
    }

    /**
     * Creates a Map, that contains a key for each available isoCode in hybris. The value is always <code>internationalName</code>.
     * 
     * <pre>
     *  Map{de:"InternationalName", fr:"InternationalName"}
     * </pre>
     * 
     * @param internationalName
     *            String of international name
     * @return a Map, that contains a key for each available isoCode in hybris. The value is always <code>internationalName</code>.
     */
    public static Map<String, String> createMapWithLocalizedNamesOfInternationalName(final String internationalName) {
        final Map<String, String> resultMap = new HashMap<String, String>();

        for (final LanguageModel lang : commonI18NService.getAllLanguages()) {
            final String isoCode = lang.getIsocode();
            resultMap.put(isoCode, internationalName);
        }
        return resultMap;
    }

    /**
     * Creates a Map, that contains a key for each available isoCode in hybris. The value is always <code>internationalName</code>.
     * 
     * <pre>
     *  Map{de:"InternationalName", fr:"InternationalName"}
     * </pre>
     * 
     * @param internationalName
     *            String of international name
     * @return a Map, that contains a key for each available isoCode in hybris. The value is always <code>internationalName</code>.
     */
    public static Map<Language, String> createLanguageMapWithInternationalName(final String internationalName) {
        final Map<Language, String> resultMap = new HashMap<Language, String>();

        for (final LanguageModel lang : commonI18NService.getAllLanguages()) {
            resultMap.put((Language) modelService.getSource(lang), internationalName);
        }
        return resultMap;
    }

    /**
     * Transform a list of language ISO Codes to a list of hybris <code>Language</code> instances.
     * 
     * @see Language
     * @param listOfIsoCodes
     *            A list of language ISO Codes like {"de", "fr", etc}
     * @return A list of hybris <code>Language</code> instances, corresponding with the passed ISO Codes.
     * @throws JaloItemNotFoundException
     *             If a language does not exist.
     */
    public static List<Language> transformIsoCodeListToHybrisLanguageList(final List<String> listOfIsoCodes) {
        Language language;
        final List<Language> allhybrisLanguages = new ArrayList<Language>();

        for (final String isoCode : listOfIsoCodes) {
            language = modelService.getSource(commonI18NService.getLanguage(isoCode));
            allhybrisLanguages.add(language);
        }
        return allhybrisLanguages;
    }

    /**
     * Returns the date of last touch, that is the creation date if no modification date is available (<code>null</code>) or the last
     * modification date.
     * 
     * @param item
     *            a hybris item with create-date and probably a modification-date.
     * @return The date of last modification, maybe the creation date. <code>null</code> is returned, when there is any date information.
     */
    public static Date getItemLastModification(final Item item) {
        if (item == null) {
            return null;
        }

        final Date creationDate = item.getCreationTime();
        final Date modificationDate = item.getModificationTime();

        if (creationDate == null && modificationDate == null) {
            return null;
        } else if (creationDate != null && modificationDate == null) {
            return creationDate;
        } else if (creationDate == null && modificationDate != null) {
            return modificationDate;
        } else {
            return modificationDate.after(creationDate) ? modificationDate : creationDate;
        }
    }

    /**
     * Returns the date of last touch, that is the creation date if no modification date is available (<code>null</code>) or the last
     * modification date.
     * 
     * @param item
     *            a hybris item with create-date and probably a modification-date.
     * @return The date of last modification, maybe the creation date. <code>null</code> is returned, when there is any date information.
     */
    public static Date getItemModelLastModification(final ItemModel item) {
        if (item == null) {
            return null;
        }

        final Date creationDate = item.getCreationtime();
        final Date modificationDate = item.getModifiedtime();

        if (creationDate == null && modificationDate == null) {
            return null;
        } else if (creationDate != null && modificationDate == null) {
            return creationDate;
        } else if (creationDate == null && modificationDate != null) {
            return modificationDate;
        } else {
            return modificationDate.after(creationDate) ? modificationDate : creationDate;
        }
    }

    /**
     * Returns the same item as give in, if the item will abide by all restrictions.
     * 
     * @param item
     *            The item
     * @return The same item as give in, if the item will abide by all restrictions.
     */
    public static ItemModel getItemBasedOnSearchIncludingAllRestrictions(final ItemModel item) {
        final FlexibleSearchService flexibleSearchService = SpringUtil.getBean("flexibleSearchService", FlexibleSearchService.class);
        final SearchResult<ItemModel> result = flexibleSearchService.search("SELECT {" + Item.PK + "} FROM {" + item.getItemtype() + "} WHERE {" + Item.PK
                + "}=?itemPK", Collections.singletonMap("itemPK", item.getPk()));
        if (result.getResult().size() > 0) {
            return result.getResult().get(0);
        } else {
            return null;
        }
    }

    /**
     * Returns the same item as give in, if the item will abide by all restrictions.
     * 
     * @param item
     *            The item
     * @return The same item as give in, if the item will abide by all restrictions.
     */
    public static Item getItemBasedOnSearchIncludingAllRestrictions(final Item item) {
        final ModelService modelService = SpringUtil.getBean("modelService", ModelService.class);
        if (getItemBasedOnSearchIncludingAllRestrictions((ItemModel) modelService.get(item)) != null) {
            return item;
        } else {
            return null;
        }
    }
}
