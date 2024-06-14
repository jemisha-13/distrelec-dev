package com.namics.distrelec.b2b.core.service.unit.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.distrelec.b2b.core.search.data.Unit;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.namics.distrelec.b2b.core.service.classification.dao.DistClassificationDao;
import com.namics.distrelec.b2b.core.service.unit.UnitConversionService;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.fest.util.Collections.set;

public class DistUnitConversionService implements UnitConversionService, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(DistUnitConversionService.class);

    private static final String UNITCONVERSION_CACHEDDURATION_MINUTES = "unitconversion.cacheduration.minutes";

    private static final String UNITCONVERSION_REGEX_BEFORE_UNIT = "unitconversion.regex.before.unit";

    private static final String UNITCONVERSION_REGEX_AFTER_UNIT = "unitconversion.regex.after.unit";

    private static final String UNITCONVERSION_REGEX_STANDARDCASE = "unitconversion.regex.standardcase";

    private static final String UNITCONVERSION_REGEX_SPECIALCASE = "unitconversion.regex.specialcase";

    private static final String REGEX_OR = "|";

    public static final int BIG_DECIMAL_SCALE = 12;

    private DistClassificationDao distClassificationDao;

    private ConfigurationService configurationService;

    public static String beforeUnit;

    public static String afterUnit;

    private static String regexUnitMatch;

    private static String caseSensitiveUnitPrefixRegex;

    //TODO have this as a map on the base store
    private static final HashMap<String, String> specialRegexBeforeConversion = new HashMap<>();

    //add other special cases here
    private static final Set<String> specialCaseUnits = set("mohm", "mw");

    private Supplier<List<Unit>> cachedUnits;

    private static Supplier<Map<String, String>> cachedUnitCodeToSymbolLookup;

    @Override
    public void afterPropertiesSet() throws Exception {
        beforeUnit = configurationService.getConfiguration().getString(UNITCONVERSION_REGEX_BEFORE_UNIT);
        afterUnit = configurationService.getConfiguration().getString(UNITCONVERSION_REGEX_AFTER_UNIT);

        String standardUnitRegex = configurationService.getConfiguration().getString(UNITCONVERSION_REGEX_STANDARDCASE);
        regexUnitMatch = beforeUnit + standardUnitRegex + afterUnit;

        String specialCaseRegex = configurationService.getConfiguration().getString(UNITCONVERSION_REGEX_SPECIALCASE);
        caseSensitiveUnitPrefixRegex = beforeUnit + specialCaseRegex + afterUnit;

        int cacheDuration = configurationService.getConfiguration().getInt(UNITCONVERSION_CACHEDDURATION_MINUTES);

        Supplier<List<Unit>> unitSupplier = () -> distClassificationDao.findAttributeUnitsWithUnitGroup();

        cachedUnits = Suppliers.memoizeWithExpiration(unitSupplier, cacheDuration, TimeUnit.MINUTES);
        cachedUnitCodeToSymbolLookup = Suppliers.memoizeWithExpiration(this::getUnitCodeToSymbolMap, cacheDuration, TimeUnit.MINUTES);

        String regexForKiloOhmSpecialCase = beforeUnit + "([0-9]+)[k,K]([0-9])" + afterUnit;
        String stringPatternForOhmSpecialCase = "%s%s00 Ohm";
        specialRegexBeforeConversion.put(regexForKiloOhmSpecialCase, stringPatternForOhmSpecialCase);
    }

    @Override
    public String convertUnitsInText(String text) {
        long t1 = System.currentTimeMillis();

        List<Unit> selectedAttributeUnits = preselectRegexesToUse(text, cachedUnits.get());

        String convertedText = convertTextRecursively(text, selectedAttributeUnits);

        LOG.debug("Conversion took:{} ms", (System.currentTimeMillis() - t1));
        return convertedText;
    }

    @Override
    public Optional<Unit> getUnitBySymbol(String symbol) {
        return cachedUnits.get()
                          .stream()
                          .filter(unit -> unit.getSymbol().equals(symbol))
                          .findAny();
    }

    @Override
    public String getBaseUnitSymbolForUnitType(String unitType) {
        return cachedUnitCodeToSymbolLookup.get().get(unitType);
    }

    private String convertTextRecursively(final String searchText, List<Unit> attributeUnits) {
        if (isBlank(searchText)) {
            return searchText;
        }

        Optional<String> textAfterSpecialCases = checkForSpecialCases(searchText, attributeUnits);

        if (textAfterSpecialCases.isPresent()) {
            return textAfterSpecialCases.get();
        }

        for (final Unit unit : attributeUnits) {
            if (unit.getConversionFactor() == null || unit.getConversionFactor() == 0) {
                continue;
            }

            String symbol = unit.getSymbol();

            String preprocessedCode = unitCodePreprocessing(unit);

            String regexForThisUnit = createRegex(symbol, preprocessedCode);

            Pattern pattern = Pattern.compile(regexForThisUnit);
            Matcher matcher = pattern.matcher(searchText);

            if (matcher.matches()) {
                LOG.debug("Matching unit is:{} {}", unit.getCode(), unit.getSymbol());

                String textBefore = matcher.group(1);
                String quantity = matcher.group(2);
                String textAfter = matcher.group(4);

                String baseUnit = cachedUnitCodeToSymbolLookup.get().get(unit.getUnitType());

                if (isNotBlank(quantity) && baseUnit != null) {
                    BigDecimal quantityNumber = convertToBaseUnit(unit, quantity);

                    String textAfterConverted = convertTextRecursively(textAfter, attributeUnits);
                    String textBeforeConverted = convertTextRecursively(textBefore, attributeUnits);
                    return textBeforeConverted + quantityNumber.toPlainString() + " " + baseUnit + textAfterConverted;
                }
            }
        }
        return searchText;
    }

    @Override
    public BigDecimal convertToBaseUnit( Unit unit,  String quantity) {
        if (isInvalidInputForConversion(unit, quantity)) {
            return BigDecimal.ZERO;
        }
        BigDecimal quantityNumber = new BigDecimal(quantity)
                                      .multiply(BigDecimal.valueOf(unit.getConversionFactor())).setScale(BIG_DECIMAL_SCALE, RoundingMode.HALF_UP)
                                      .stripTrailingZeros();
        return quantityNumber;
    }

    private boolean isInvalidInputForConversion(Unit unit,  String quantity) {
        return !(isValidUnitForConversion(unit) && isValidQuantityForConversion(quantity));
    }

    private boolean isValidQuantityForConversion(String quantity) {
        return NumberUtils.isCreatable(quantity);
    }

    private boolean isValidUnitForConversion(Unit unit) {
        return unit != null && unit.getConversionFactor() != null;
    }

    private Optional<String> checkForSpecialCases(final String searchText, final List<Unit> attributeUnits) {
        for (final Map.Entry<String, String> regexWithPattern : specialRegexBeforeConversion.entrySet()) {
            Pattern pattern = Pattern.compile(regexWithPattern.getKey());
            Matcher matcher = pattern.matcher(searchText);

            if (matcher.matches()) {
                String textBefore = matcher.group(1);
                String group2 = matcher.group(2);
                String group3 = matcher.group(3);
                String textAfter = matcher.group(matcher.groupCount());

                String value = regexWithPattern.getValue();

                group3 = isBlank(group3) ? group2 : group3;

                return Optional.of(convertTextRecursively(textBefore, attributeUnits) + format(value, group2, group3)
                                   + convertTextRecursively(textAfter, attributeUnits));
            }
        }
        return Optional.empty();
    }

    private List<Unit> preselectRegexesToUse(final String searchText, List<Unit> attributeUnits) {
        return attributeUnits.stream().filter(unit -> {
            String lowerCaseText = searchText.toLowerCase();
            return lowerCaseText.contains(unitCodePreprocessing(unit).toLowerCase()) || lowerCaseText.contains(unit.getSymbol().toLowerCase());
        }).collect(toList());
    }

    private String createRegex(final String symbol, final String preprocessedCode) {
        if (specialCaseUnits.contains(symbol.toLowerCase())) {
            String firstCharacter = substring(symbol, 0, 1);
            String codeAndSymbol = preprocessedCode + REGEX_OR + StringUtils.substring(symbol, 1);
            return format(caseSensitiveUnitPrefixRegex, firstCharacter, codeAndSymbol);
        } else {
            String codeAndSymbol = preprocessedCode + REGEX_OR + symbol;
            return format(regexUnitMatch, codeAndSymbol);
        }
    }

    private String unitCodePreprocessing(final Unit unit) {
        return unit.getCode().replace("_", "");
    }

    private Map<String, String> getUnitCodeToSymbolMap() {
        Map<String, String> unitLookup = new HashMap<>();
        for (final Unit classificationAttributeUnitModel : cachedUnits.get()) {
            unitLookup.put(classificationAttributeUnitModel.getCode(), classificationAttributeUnitModel.getSymbol());
        }
        return unitLookup;
    }

    public DistClassificationDao getDistClassificationDao() {
        return distClassificationDao;
    }

    public void setDistClassificationDao(final DistClassificationDao distClassificationDao) {
        this.distClassificationDao = distClassificationDao;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
