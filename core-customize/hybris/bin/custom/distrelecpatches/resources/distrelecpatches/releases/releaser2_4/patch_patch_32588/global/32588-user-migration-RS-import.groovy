package distrelecpatches.releases.releaser2_4.patch_patch_32588.global

import com.namics.distrelec.b2b.facades.customer.impl.DistCustomerRegistrationFacade
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel
import de.hybris.platform.commercefacades.user.data.RegisterData
import de.hybris.platform.commerceservices.enums.CustomerType
import de.hybris.platform.core.model.media.MediaModel
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException
import de.hybris.platform.servicelayer.interceptor.impl.InterceptorExecutionPolicy
import de.hybris.platform.servicelayer.session.SessionExecutionBody
import de.hybris.platform.servicelayer.session.SessionService
import de.hybris.platform.site.BaseSiteService
import de.hybris.platform.tx.Transaction
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.StringUtils
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.security.SecureRandom

import static distrelecpatches.releases.releaser2_4.patch_patch_32588.global.Constants.*

DistCustomerRegistrationFacade distCustomerRegistrationFacade = spring.getBean('distCustomerRegistrationFacade')
SessionService sessionService = spring.getBean(SessionService.class)
BaseSiteService baseSiteService = spring.getBean('defaultBaseSiteService')
final Map<String, Object> params = Map.of(InterceptorExecutionPolicy.DISABLED_INTERCEPTOR_TYPES, Set.of(InterceptorExecutionPolicy.InterceptorType.VALIDATE))

class Constants {
    static final int TITLE_INDEX = 0
    static final int FIRST_NAME_INDEX = 1
    static final int LAST_NAME_INDEX = 2
    static final int PHONE_NUMBER_INDEX = 3
    static final int MOBILE_NUMBER_INDEX = 4
    static final int COUNTRY_INDEX = 5
    static final int LANGUAGE_INDEX = 6
    static final int CURRENCY_INDEX = 7
    static final int UID_INDEX = 8
    static final int RS_CONTACT_NUMBER_INDEX = 9
    static final int DISTRELEC_CONTACT_NUMBER_INDEX = 10
    static final int DISTRELEC_CUSTOMER_NUMBER_INDEX = 11
    static final int RS_CUSTOMER_NUMBER_INDEX = 12
    static final int COMPANY_NAME_INDEX = 13
    static final int COMPANY_NAME_2_INDEX = 14
    static final int RESULT_INDEX = 15
    static final int SHEET_INDEX = 0
    static final String CHARACTERS_USED_IN_PASSWORDS = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
    static final int GENERATED_PASSWORD_LENGTH = 8
    static final String IMPORT_FILE_NAME = 'customer-data'
    static final String IMPORT_RESULT_FILE_NAME = 'customer-data-result'
    static boolean SKIP_HEADER = true
    static final Logger LOG = LoggerFactory.getLogger('RS_MIGRATION_SCRIPT')
}

MediaModel importMedia = mediaService.getMedia(IMPORT_FILE_NAME)
InputStream fileInputStream = mediaService.getStreamFromMedia(importMedia)
Workbook workbook = getExcelWorkbook(fileInputStream, importMedia)

final Iterator<Row> rowIterator = workbook.getSheetAt(SHEET_INDEX).iterator()
while (rowIterator.hasNext()) {
    final Transaction tx = Transaction.current()
    tx.begin()

    try {
        Row row = rowIterator.next()
        if (row.getRowNum() == 0 && SKIP_HEADER || isAlreadyProcessed(row)) {
            continue
        }
        String[] data = new String[RESULT_INDEX]
        populateData(row.cellIterator(), data)
        prepareSessionAndCreateCustomer(sessionService, params, baseSiteService, data, distCustomerRegistrationFacade, row, workbook, importMedia)
    } finally {
        tx.commit()
    }
}
workbook.close()


private boolean isAlreadyProcessed(Row row) {
    return row.getCell(RESULT_INDEX) != null && !StringUtils.equals(row.getCell(RESULT_INDEX).getStringCellValue(), StringUtils.EMPTY);
}

private void prepareSessionAndCreateCustomer(SessionService sessionService, Map<String, Object> params, baseSiteService, String[] data, DistCustomerRegistrationFacade distCustomerRegistrationFacade, Row row, Workbook workbook, MediaModel importMedia) {
    sessionService.executeInLocalViewWithParams(params, new SessionExecutionBody()
    {
        @Override
        void executeWithoutResult() {
            baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID('distrelec_CH'), false)
            try {
                String lastName = data[LAST_NAME_INDEX]
                String email = data[UID_INDEX]
                String companyName = getCompanyName(data)
                String customerId = data[DISTRELEC_CUSTOMER_NUMBER_INDEX]

                if (isMandatoryDataPopulated(lastName, email, companyName, customerId)) {
                    String password = generateRandomString()

                    RegisterData registerData = new RegisterData()
                    registerData.setTitleCode(getTitle(data))
                    registerData.setFirstName(getFirstName(data))
                    registerData.setLastName(lastName)
                    registerData.setPhoneNumber(getPhoneNumber(data))
                    registerData.setLogin(email)
                    registerData.setPassword(password)
                    registerData.setCheckPwd(password)
                    registerData.setCompany(companyName)
                    registerData.setCountryCode('CH')
                    registerData.setCustomerId(customerId)
                    registerData.setCustomerSurveysConsent(false)
                    registerData.setKnowHowConsent(false)
                    registerData.setIsMarketingCookieEnabled(false)
                    registerData.setNewsLetterConsent(false)
                    registerData.setNpsConsent(false)
                    registerData.setObsolescenceConsent(false)
                    registerData.setPersonalisationConsent(false)
                    registerData.setPersonalisedRecommendationConsent(false)
                    registerData.setPhoneConsent(false)
                    registerData.setPostConsent(false)
                    registerData.setProfilingConsent(false)
                    registerData.setSaleAndClearanceConsent(false)
                    registerData.setSelectAllemailConsents(false)
                    registerData.setMarketingConsent(false)
                    registerData.setSmsConsent(false)
                    registerData.setTermsAndConditionsConsent(false)
                    registerData.setTermsOfUseOption(true)
                    registerData.setCustomerType('B2B')
                    registerData.setRegistrationType('STANDALONE')
                    registerData.setExistingCustomer(true)
                    registerData.setInvoiceEmail(email)

                    distCustomerRegistrationFacade.activate(registerData, CustomerType.B2B)
                    appendResultCell(row, isDataModified(data) ? 'success_with_modification' : 'success')
                    LOG.info('Successfully imported customer: {}', email)
                } else {
                    appendResultCell(row, 'mandatory_required')
                }
            } catch (Exception ex) {
                appendResultCell(row, 'error')
            }
            createResultMedia(workbook, importMedia)
        }
    })
}

private boolean isMandatoryDataPopulated(String lastName, String email, String companyName, String customerId) {
    return StringUtils.isNotBlank(lastName) &&
            StringUtils.isNotBlank(email) &&
            StringUtils.isNotBlank(companyName) &&
            StringUtils.isNotBlank(customerId)
}

private void appendResultCell(Row row, String result) {
    Cell newCell = row.createCell(RESULT_INDEX)
    newCell.setCellValue(result)
}

private Workbook getExcelWorkbook(InputStream inputStream, MediaModel importMedia) {
    if (StringUtils.equalsIgnoreCase(FilenameUtils.getExtension(importMedia.getRealFileName()), 'xlsx')) {
        return new XSSFWorkbook(inputStream)
    } else {
        return new HSSFWorkbook(inputStream)
    }
}

private void populateData(Iterator<Cell> cellIterator, String[] data) {
    while (cellIterator.hasNext()) {
        final Cell cell = cellIterator.next()
        if (cell != null) {
            if (cell.getCellType() == CellType.NUMERIC) {
                data[cell.getColumnIndex()] = String.valueOf((long) cell.getNumericCellValue())
            } else {
                data[cell.getColumnIndex()] = cell.getStringCellValue()
            }
        }
    }
}

private boolean isDataModified(String[] data) {
    return StringUtils.isBlank(data[FIRST_NAME_INDEX])
}

private String getTitle(String[] data) {
    String title = data[TITLE_INDEX]
    if (StringUtils.containsIgnoreCase(title, 'Ms.')) {
        return 'ms'
    }
    if (StringUtils.containsIgnoreCase(title, 'Mr.')) {
        return 'mr'
    }
    return 'mr'
}

private String getCompanyName(String[] data) {
    String companyName2 = data[COMPANY_NAME_2_INDEX]
    if (StringUtils.isNotBlank(companyName2)) {
        return data[COMPANY_NAME_INDEX] + StringUtils.SPACE + companyName2
    }
    return data[COMPANY_NAME_INDEX]
}

private String getFirstName(String[] data) {
    String firstName = data[FIRST_NAME_INDEX]

    if (StringUtils.isNotBlank(firstName)) {
        return firstName
    } else {
        return ' '
    }
}

private String getPhoneNumber(String[] data) {
    String mobileNumber = data[MOBILE_NUMBER_INDEX]

    if (StringUtils.isNotBlank(mobileNumber)) {
        return mobileNumber
    } else {
        return data[PHONE_NUMBER_INDEX]
    }
}

private String generateRandomString() {
    final SecureRandom random = new SecureRandom()
    StringBuilder stringBuilder = new StringBuilder(GENERATED_PASSWORD_LENGTH)

    for (int i = 0; i < GENERATED_PASSWORD_LENGTH; i++) {
        int randomIndex = random.nextInt(CHARACTERS_USED_IN_PASSWORDS.length())
        char randomChar = CHARACTERS_USED_IN_PASSWORDS.charAt(randomIndex)
        stringBuilder.append(randomChar)
    }

    return stringBuilder.toString()
}


private void createResultMedia(Workbook workbook, MediaModel importMedia) {
    try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
        workbook.write(stream)

        MediaModel resultMedia = getExistingMediaOrCreateNew(importMedia)
        try (InputStream inputStream = new ByteArrayInputStream(stream.toByteArray())) {
            mediaService.setStreamForMedia(resultMedia, inputStream)
            resultMedia.setRealFileName(getImportResultFileName(importMedia))
            modelService.save(resultMedia)
        }
    }
}

private MediaModel getExistingMediaOrCreateNew(MediaModel importMedia) {
    try {
        return mediaService.getMedia(IMPORT_RESULT_FILE_NAME)
    } catch (final UnknownIdentifierException e) {
        MediaModel media = modelService.create(CatalogUnawareMediaModel.class)
        media.setCode(IMPORT_RESULT_FILE_NAME)
        media.setRealFileName(getImportResultFileName(importMedia))
        modelService.save(media)
        return media
    }
}

private String getImportResultFileName(MediaModel importMedia) {
    IMPORT_RESULT_FILE_NAME + System.currentTimeMillis() + '.' + FilenameUtils.getExtension(importMedia.getRealFileName())
}
