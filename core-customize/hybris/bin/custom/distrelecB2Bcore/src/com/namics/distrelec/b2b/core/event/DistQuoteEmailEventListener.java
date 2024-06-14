package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.inout.export.DistCsvTransformationService;
import com.namics.distrelec.b2b.core.model.process.DistQuoteEmailProcessModel;
import com.namics.hybris.toolbox.spring.SpringUtil;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.media.MediaIOException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.Quote.ATTRIBUTE_QUOTATION_CSV_HEADER;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Punctuation.COMMA;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Punctuation.EMPTY;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Punctuation.DOUBLE_QUOTES;

public class DistQuoteEmailEventListener extends AbstractEventListener<DistQuoteEmailEvent>{
    protected static final Logger LOG = LogManager.getLogger(DistQuoteEmailEventListener.class);
    private static final String HYBRIS_TEMP_DIRECTORY = "HYBRIS_TEMP_DIR";
    private static final int NUMBER_OF_COLUMNS_FROM_QUOTATION_FORM = 5;

    @Override
    protected void onEvent(DistQuoteEmailEvent event) {
        final long timeStamp = System.currentTimeMillis();

        final String processName = "quoteEmail" + timeStamp;
        final String customerId = event.getCustomer().getErpContactID();
        final String tempFileName = "products_"+customerId+"_"+timeStamp+".csv";

        final DistQuoteEmailProcessModel quoteEmailProcess = getDistQuoteEmailProcessModel(event, processName);
        final String csvHeader = getConfigurationService().getConfiguration().getString(ATTRIBUTE_QUOTATION_CSV_HEADER);
        final String [] csvHeaders = Arrays.stream(csvHeader.split(COMMA)).toArray(String[]::new);

        if(event.getProductRow() != null && event.getProductRow().size() > 0){

            final String pathname = getConfigurationService().getConfiguration().getString(HYBRIS_TEMP_DIRECTORY) + File.pathSeparator + tempFileName;
            final File file = new File(pathname);
            try(FileWriter writer = new FileWriter(pathname)){
                writer.write(String.join(COMMA, csvHeaders) + "\n");

                for (String[] row : getProductEntries(event.getProductRow())) {
                    writer.write(String.join(COMMA, row) + "\n");
                }
            } catch (IOException e1) {
                LOG.error("Error in adding attachment");
            }
            quoteEmailProcess.setAttachment(createEmailAttachmentModel(file,"products.csv"));

            file.delete();
        }
        // Save and start the process
        getModelServiceViaLookup().save(quoteEmailProcess);
        getBusinessProcessService().startProcess(quoteEmailProcess);
    }

    private DistQuoteEmailProcessModel getDistQuoteEmailProcessModel(final DistQuoteEmailEvent event, final String processName) {
        final DistQuoteEmailProcessModel quoteEmailProcess = getBusinessProcessService().createProcess(processName, "quoteEmailProcess");
        quoteEmailProcess.setSite(event.getSite());
        quoteEmailProcess.setStore(event.getBaseStore());
        quoteEmailProcess.setCustomer(event.getCustomer());
        quoteEmailProcess.setCompany(event.getCompany());
        quoteEmailProcess.setFirstName(event.getFirstName());
        quoteEmailProcess.setLastName(event.getLastName());
        quoteEmailProcess.setCompany(event.getCompany());
        quoteEmailProcess.setCustomerEmail(event.getCustomerEmail());
        quoteEmailProcess.setPhoneNumber(event.getPhone());
        quoteEmailProcess.setComment(event.getComment());
        quoteEmailProcess.setReference(event.getReference());
        quoteEmailProcess.setTenderProcess(event.getIsTenderProcess());
        return quoteEmailProcess;
    }

    private List<String[]> getProductEntries(final List<DistQuoteEmailProduct> entries) {

        List<String []> csvContent = new ArrayList<>();
        entries.forEach(e -> {
            String[] csvRow = new String [NUMBER_OF_COLUMNS_FROM_QUOTATION_FORM];

            csvRow[0] = String.valueOf(e.getQuantity());
            csvRow[1] = e.getArticleNumber();
            csvRow[2] = e.getMpn();
            csvRow[3] = e.getNote();
            csvRow[4] = e.getPriceData() != null ? e.getPriceData().getFormattedValue() :
                    e.getPrice() != null ? e.getPrice() : EMPTY;

            for(int current = 0; current < csvRow.length; current++){
                if(StringUtils.containsAny(csvRow[current], new char[] {',','.'})){
                    csvRow[current] = DOUBLE_QUOTES + csvRow[current] + DOUBLE_QUOTES;
                }
            }
            csvContent.add(csvRow);
        });

        return csvContent;
    }

    protected EmailAttachmentModel createEmailAttachmentModel(final File file, final String attachmentFileName) {
        final EmailAttachmentModel attachmentModel = getModelServiceViaLookup().create(EmailAttachmentModel.class);
        attachmentModel.setCode(file.getName());

        // store attachment model to default catalog
        final CatalogModel catalogModel = new CatalogModel();
        catalogModel.setId("Default");
        final CatalogModel defaultCatalogModel = getFlexibleSearchService().getModelByExample(catalogModel);
        attachmentModel.setCatalogVersion(defaultCatalogModel.getActiveCatalogVersion());

        getModelServiceViaLookup().save(attachmentModel);
        try {
            final byte[] data = FileUtils.readFileToByteArray(file);
            getMediaService().setDataForMedia(attachmentModel, data);
        } catch (final MediaIOException | IllegalArgumentException e) {
            LOG.error("Could not create MediaModel from file: " + file.getName(), e);
        } catch (final IOException e) {
            LOG.error("Error while reading file: " + file.getName(), e);
        }
        attachmentModel.setRealFileName(attachmentFileName);
        attachmentModel.setAltText(file.getName());
        getModelServiceViaLookup().save(attachmentModel);
        return attachmentModel;
    }

    public MediaService getMediaService() {
        return SpringUtil.getBean("mediaService", MediaService.class);
    }

    public BusinessProcessService getBusinessProcessService() {
        return (BusinessProcessService) Registry.getApplicationContext().getBean("businessProcessService");
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return SpringUtil.getBean("flexibleSearchService", FlexibleSearchService.class);
    }

    public ConfigurationService getConfigurationService() {
        return SpringUtil.getBean("configurationService", ConfigurationService.class);
    }

    public DistCsvTransformationService getDistCsvTransformationService() {
        return SpringUtil.getBean("core.distDefaultCsvTransformationService", DistCsvTransformationService.class);
    }

    public ModelService getModelServiceViaLookup() {
        throw new UnsupportedOperationException("Please define in the spring configuration a <lookup-method> for getModelServiceViaLookup().");
    }
}
