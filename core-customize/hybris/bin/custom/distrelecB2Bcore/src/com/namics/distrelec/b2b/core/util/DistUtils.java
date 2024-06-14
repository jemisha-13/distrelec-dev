/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.util.PropertyPlaceholderHelper;

import com.google.common.base.Charsets;
import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import com.namics.distrelec.b2b.core.service.url.UrlResolverUtils;

import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.util.AbstractComparator;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.jalo.order.payment.PaymentInfo;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.i18n.L10NService;

/**
 * Different util methods which could be used for the Distrelec project.
 *
 * @author pbueschi, Namics AG
 */
public class DistUtils {

    private static final Logger LOG = Logger.getLogger(DistUtils.class);

    public static final String ADMIN_PROFILE_NAME = "adm";

    private static final Map<String, String> convertedCatalogIds = new HashMap<>();

    private static final Map<String, String> convertedSiteUids = new HashMap<>();

    private static final Pattern STARTS_WITH_HTTP_HTTPS_PATTERN = Pattern.compile("^(?i)https?://.*");

    /**
     * Truncates
     *
     * @param characterLimit
     * @param name
     * @return Empty String if name is null otherwise a truncated String up to the character limit
     */
    public static String truncateProductName(final int characterLimit, final String name) {
        return StringUtils.isBlank(name) ? StringUtils.EMPTY : StringUtils.left(name, characterLimit);
    }

    /**
     * Gets smallest unit of currency. (for example: 16.65 -> 1665)
     *
     * @param currency
     * @param amount
     * @return smallest unit of currency
     */
    public static String getSmallestUnitOfCurrency(final CurrencyModel currency, final Double amount) {
        if (currency != null && amount != null) {
            final Integer digits = currency.getDigits();
            final Integer multipl = Integer.valueOf(StringUtils.rightPad("1", digits.intValue() + 1, "0"));

            final Double smallestUnitOfCurrency = Double.valueOf(amount.doubleValue() * multipl.intValue());
            final Double roundedValue = Double.valueOf(Math.round(smallestUnitOfCurrency));
            return String.valueOf(roundedValue.intValue());
        }
        return "";
    }

    /**
     * Gets biggest unit of currency. (for example: 1665 -> 16.65)
     *
     * @param currency
     * @param amount
     * @return biggest unit of currency
     */
    public static BigDecimal getBiggestUnitOfCurrency(final CurrencyModel currency, final BigDecimal amount) {
        if (currency != null && amount != null) {
            final Integer digits = currency.getDigits();
            final Integer multipl = Integer.valueOf(StringUtils.rightPad("1", digits.intValue() + 1, "0"));
            return BigDecimal.valueOf(amount.doubleValue() / multipl.intValue());
        }
        return BigDecimal.ZERO;
    }

    /**
     * Gets map out of a string based on delivered parameter -and keyValue separator.
     *
     * @param paymentParamsString
     * @param paramSeparator
     * @param keyValueSeparator
     * @return Map<String, String>
     */
    public static Map<String, String> getMapFromString(final String paymentParamsString, final String paramSeparator, final String keyValueSeparator) {
        final Map<String, String> paymentParamsMap = new CaseInsensitiveMap();
        if (StringUtils.isNotBlank(paymentParamsString)) {
            final String[] requestParams = paymentParamsString.split(paramSeparator);
            for (int i = 0; i < requestParams.length; i++) {
                final String[] keyValue = requestParams[i].split(keyValueSeparator);
                paymentParamsMap.put(keyValue[0], keyValue[1]);
            }
        }
        return paymentParamsMap;
    }

    /**
     * Formats a map to a string.
     *
     * @param map
     *            source map
     * @param entrySeparator
     *            separator for each entry
     * @param valueSeparator
     *            separator for key / value
     * @return map as string
     */
    public static String getStringFromMap(final Map<String, String> map, final String entrySeparator, final String valueSeparator) {
        final StringBuilder returnValue = new StringBuilder();
        for (final Entry<String, String> entry : map.entrySet()) {
            returnValue.append(entry.getKey()).append(valueSeparator).append(entry.getValue()).append(entrySeparator);
        }
        return returnValue.toString();
    }

    /**
     * Counts Length of UTF-8 byte array.
     *
     * @param str
     * @return length of UTF-8 byte array
     */
    public static int countUTF8Length(final String str) {
        try {
            return str.getBytes("UTF-8").length;
        } catch (final UnsupportedEncodingException e) {
            LOG.error("Cound UTF-8 length of byte array from String '" + str + "' failed", e);
            return str.length();
        }
    }

    /**
     * Truncates an input String considering a given Charset.
     *
     * @param input
     *            the String to truncate
     * @param length
     *            the length
     * @param charset
     *            the Charset
     * @return the truncated String
     */
    public static String truncateWithCharset(final String input, final int length, final Charset charset) {
        final byte[] inputBytes = input.getBytes(charset);

        final ByteBuffer byteBuffer = ByteBuffer.wrap(inputBytes, 0, length);
        final CharBuffer charBuffer = CharBuffer.allocate(length);

        final CharsetDecoder charsetDecoder = charset.newDecoder();
        // Ignore an incomplete character
        charsetDecoder.onMalformedInput(CodingErrorAction.IGNORE);
        charsetDecoder.decode(byteBuffer, charBuffer, true);
        charsetDecoder.flush(charBuffer);

        return new String(charBuffer.array(), 0, charBuffer.position());
    }

    /**
     * Gets current date time in ISO8601 format.
     *
     * @return YYYY-MM-DDTHH:mm:ss+UTC
     */
    public static String getISO8601DateTime() {
        final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();
        return dateTimeFormatter.print(new DateTime());
    }

    /**
     * Masks the creditcard number so just the last 3 numbers are visible. The leading numbers will be replaced with 0. The code is from
     * {PaymentInfo.maskCreditCardNumber}, which we couldn't use because it shows the last 4 numbers. {@link PaymentInfo}
     *
     * @param cn
     *            the credtcard number
     * @return masked number
     */
    public static String maskCreditCardNumber(String cn) {
        if (cn == null) {
            return cn;
        }

        Pattern pattern = Pattern.compile("[^0-9]", 2);
        Matcher matcher = pattern.matcher(cn);
        cn = matcher.replaceAll("");
        final int len = cn.length();

        if (len < 13) {
            LOG.error("Invalid length ( shorter than 13 characters) of the submitted credit card number!");
        }

        pattern = Pattern.compile("[0-9]", 2);
        matcher = pattern.matcher(cn.substring(0, len - 3));

        final String _cn = matcher.replaceAll("*");
        return _cn + cn.substring(len - 3, len);
    }

    /**
     * Sorts the payment modes.
     *
     * @param paymentModes
     * @return sorted payment modes.
     */
    public static List<AbstractDistPaymentModeModel> getSortedPaymentModes(final List<AbstractDistPaymentModeModel> paymentModes) {
        AbstractDistPaymentModeModel creditCardPaymentMode = null;
        AbstractDistPaymentModeModel anotherPaymentMode = null;
        AbstractDistPaymentModeModel payPalPaymentMode = null;
        final List<AbstractDistPaymentModeModel> sortedPaymentModes = new ArrayList<>();
        for (final AbstractDistPaymentModeModel paymentMode : paymentModes) {
            if (isInvoicePaymentMode(paymentMode)) {
                sortedPaymentModes.add(paymentMode);
            } else if (paymentMode.getPaymentInfoType().getCode().equals(CreditCardPaymentInfoModel._TYPECODE)) {
                creditCardPaymentMode = paymentMode;
            } else if (("PayPal").equals(paymentMode.getCode())) {
                payPalPaymentMode = paymentMode;
            } else {
                anotherPaymentMode = paymentMode;
            }
        }
        if (null != creditCardPaymentMode) {
            sortedPaymentModes.add(creditCardPaymentMode);
        }
        if (null != payPalPaymentMode) {
            sortedPaymentModes.add(payPalPaymentMode);
        }
        if (null != anotherPaymentMode) {
            sortedPaymentModes.add(anotherPaymentMode);
        }
        return sortedPaymentModes;
    }

    public static boolean isInvoicePaymentMode(PaymentModeModel paymentMode) {
        return paymentMode.getPaymentInfoType().getCode().equals(InvoicePaymentInfoModel._TYPECODE);
    }

    /**
     * Tells if the given customer is a B2B customer.
     *
     * @param customer
     * @return true if B2B else false
     */
    public static boolean isB2BCustomer(final CustomerModel customer) {
        return CustomerType.B2B.equals(customer.getCustomerType()) || CustomerType.B2B_KEY_ACCOUNT.equals(customer.getCustomerType());
    }

    /**
     * Encode the string and raise a runtime exception in case of problem
     *
     * @param str
     * @return
     */
    public static String encodeString(final String str) {
        try {
            return URLEncoder.encode(str.toLowerCase(), Charsets.UTF_8.name());
        } catch (final UnsupportedEncodingException e) {
            throw new SystemException("Error during encoding string: " + str);
        }
    }

    /**
     * Copied from AbstractUrlResolver
     */
    public static String urlEncode(final String source) {
        try {
            return URLEncoder.encode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("Unsupported encoding (UTF-8). Return input parameter as fallback.", e);
            return source;
        }
    }

    public static String encodeFfSpecialChars(String source) {
        // See DISTRELEC-1603 for more information
        if (StringUtils.isNotBlank(source)) {
            return source.replace("|", "%7C")
                         .replace("#", "%23")
                         .replace("’", "%27")
                         .replace("≤", "&le;")
                         .replace("≥", "&ge;")
                         .replace("±", "&plusmn;")
                         .replace("½", "&frac12;")
                         .replace("⅓", "&frac13;")
                         .replace("¼", "&frac14;")
                         .replace("¾", "&frac34;")
                         .replace("=", "%3D");
        }
        return StringUtils.EMPTY;
    }

    public static String decodeFfSpecialChars(String source) {
        if (StringUtils.isNotBlank(source)) {
            return source.replace("%23", "#");
        }
        return StringUtils.EMPTY;
    }

    /**
     * Copied from AbstractUrlResolver with addition of removing "+" character.
     */
    public static String urlSafe(final String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        String encodedText;
        try {
            encodedText = URLEncoder.encode(text, "utf-8");
        } catch (final UnsupportedEncodingException encodingException) {
            encodedText = text;
            LOG.debug(encodingException.getMessage(), encodingException);
        }

        // Cleanup the text
        String cleanedText = encodedText;
        cleanedText = cleanedText.replaceAll("%2F", "/");
        cleanedText = cleanedText.replaceAll("%20", "+");
        cleanedText = cleanedText.replaceAll("[^%A-Za-z0-9\\-]+", "-");
        return cleanedText;
    }

    /**
     * Calculate the canonical url for a manufacturer for the given name and code
     *
     * @param request
     * @param code
     * @param name
     * @return
     */
    public static String getManufacturerCanonicalUrl(final HttpServletRequest request, final String code, final String name) {
        final StringBuilder resultUrl = new StringBuilder();
        URL currentUrl = null;
        try {
            currentUrl = new URL(request.getRequestURL().toString());
        } catch (MalformedURLException e) {
            LOG.error(e.getMessage(), e);
        }
        final String path = currentUrl.getPath();
        final String[] parts = path.split("/");
        final String language = parts[1].length() == 2 ? parts[1] : "";
        // resultUrl = "/manufacturer/" + DistUtils.encodeString(name.toLowerCase()) + "/" + code;
        if (StringUtils.isBlank(language)) {
            resultUrl.append("/manufacturer/").append(DistUtils.encodeString(name.toLowerCase())).append("/").append(code);
        } else {
            resultUrl.append("/").append(language).append("/manufacturer/").append(DistUtils.encodeString(name.toLowerCase())).append("/").append(code);
        }

        return UrlResolverUtils.normalize(resultUrl.toString());
    }

    public static boolean containsUnderscore(String siteUid) {
        return siteUid.contains("_");
    }

    public static boolean containsMinus(String siteUid) {
        return siteUid.contains("-");
    }

    public static String convertCatalogIdUnderscoreToMinus(String catalogId) {
        return convertUnderscoreToMinus(catalogId, convertedCatalogIds);
    }

    public static String revertCatalogIdMinusToUnderscore(String catalogId) {
        return revertMinusToUnderscore(catalogId, convertedCatalogIds);
    }

    public static String convertSiteUidUnderscoreToMinus(String siteUid) {
        return convertUnderscoreToMinus(siteUid, convertedSiteUids);
    }

    public static String revertSiteUidMinusToUnderscore(String siteUid) {
        return revertMinusToUnderscore(siteUid, convertedSiteUids);
    }

    public static String stripUnderscores(String value) {
        return value.replaceAll("_", "");
    }

    /**
     * Sort Countries
     *
     * @param countries
     * @return List<CountryModel>
     */
    public static List<CountryModel> sortCountries(final Collection<CountryModel> countries) {
        final List<CountryModel> result = new ArrayList<>(countries);
        Collections.sort(result, CountryComparator.INSTANCE);
        return result;
    }

    /**
     * Country Comparator
     */
    public static class CountryComparator extends AbstractComparator<CountryModel> {
        public static final CountryComparator INSTANCE = new CountryComparator();

        @Override
        protected int compareInstances(final CountryModel country1, final CountryModel country2) {
            int result = (country1.getName() != null && country2.getName() != null) ? country1.getName().compareToIgnoreCase(country2.getName()) : BEFORE;
            if (EQUAL == result) {
                result = country1.getIsocode().compareToIgnoreCase(country2.getIsocode());
            }
            return result;
        }
    }

    private static String convertUnderscoreToMinus(String value, Map<String, String> convertedValues) {
        if (containsUnderscore(value) || containsMinus(value)) {
            String convertedValue = value.replaceAll("_", "-");
            convertedValues.put(convertedValue, value);
            return convertedValue;
        } else {
            return value;
        }
    }

    private static String revertMinusToUnderscore(String value, Map<String, String> convertedValues) {
        if (containsMinus(value)) {
            if (convertedValues.containsKey(value)) {
                return convertedValues.get(value);
            } else {
                throw new IllegalStateException(String.format("Value %s must be converted prior of reverted",
                                                              value));
            }
        } else {
            return value;
        }
    }

    public static boolean isAdminNode() {
        return Arrays.asList(Registry.getApplicationContext().getEnvironment().getActiveProfiles()).stream()
                     .anyMatch(profileName -> profileName.equals(ADMIN_PROFILE_NAME));
    }

    public static <T, U> Map<T, U> combineMaps(Map<T, U> first, Map<T, U> second) {
        Map<T, U> combinedMap = new HashMap<>();
        combinedMap.putAll(first);
        combinedMap.putAll(second);
        return combinedMap;
    }

    public static String getCurrentLanguageIsocode() {
        I18NService i18NService = Registry.getApplicationContext().getBean(I18NService.class);
        if (i18NService != null && i18NService.getCurrentLocale() != null) {
            return i18NService.getCurrentLocale().getLanguage();
        }
        LOG.warn("no current language available");
        return null;
    }

    public static String localizeText(String text) {
        return localizeText(text, "${", "}");
    }

    public static String localizeText(String text, String prefix, String suffix) {
        PropertyPlaceholderHelper placeholderHelper = new PropertyPlaceholderHelper(prefix, suffix);
        return placeholderHelper.replacePlaceholders(text, s -> Registry.getApplicationContext().getBean(L10NService.class).getLocalizedString(s));
    }

    /**
     * Checks if current language is present in collection of Language
     *
     * @param languageModels
     *            the String to truncate
     * @return boolean
     */
    public static boolean checkLanguage(final Collection<LanguageModel> languageModels) {
        String currentLangIsocode = getCurrentLanguageIsocode();
        if (LOG.isDebugEnabled()) {
            LOG.debug("currentLangIsocode" + currentLangIsocode);
        }
        if (languageModels.isEmpty()) {
            // all valid
            if (LOG.isDebugEnabled()) {
                LOG.debug("Lanugage is not set for Media Model");
            }
            return true;
        }
        return languageModels.stream()
                             .map(LanguageModel::getIsocode)
                             .filter(isocode -> isocode.equals(currentLangIsocode))
                             .findAny()
                             .isPresent();
    }

    /**
     * Returns true if url start with either http:// or https://
     */
    public static boolean startsWithHttpHttps(String url) {
        return STARTS_WITH_HTTP_HTTPS_PATTERN.matcher(url).matches();
    }
}
