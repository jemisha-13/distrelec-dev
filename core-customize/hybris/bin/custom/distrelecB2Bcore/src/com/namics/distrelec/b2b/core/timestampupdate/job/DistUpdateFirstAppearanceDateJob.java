package com.namics.distrelec.b2b.core.timestampupdate.job;

import com.namics.distrelec.b2b.core.model.jobs.DistUpdateFirstAppearanceDateCronJobModel;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.timestampupdate.ImportException;
import com.namics.distrelec.b2b.core.timestampupdate.ImportException.ErrorSource;
import com.namics.distrelec.b2b.core.timestampupdate.service.DistTimeStampService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DistUpdateFirstAppearanceDateJob extends AbstractJobPerformable<DistUpdateFirstAppearanceDateCronJobModel> {

    private static final Logger LOG = Logger.getLogger(DistUpdateFirstAppearanceDateJob.class);

    private ModelService modelService;
    private DistTimeStampService distTimeStampService;
    private DistProductService productService;

    // The first 3 columns of the first sheet of the file have to be the following:
    // External PIM ID | PIM Category Type | Ok-to-Web |
    private static int defaultExcelSheet;
    private static int defaultExternalPIMIDPosition = 0;
    private static int defaultFirstAppearanceDatePosition = 1;

    // TODO complete implementation

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.CronJobModel)
     */
    @Override
    public PerformResult perform(final DistUpdateFirstAppearanceDateCronJobModel cronJob) {
        final long startTime = System.nanoTime();
        LOG.info("Starting DistUpdateFirstAppearanceDateCronJob at " + new Date());
        boolean success = true;

        try {
            performJob(cronJob.isImportfromfile(), cronJob.getFirstAppearanceDatefile(), cronJob.isIntialRun());

        } catch (final Exception exp) {
            LOG.error("An error occurs while importing date from file the   " + exp.toString());
            success = false;
        }
        LOG.info("Finished DistUpdateFirstAppearanceDateCronJob in " + (int) ((System.nanoTime() - startTime) / 1e9) + " seconds.");
        return new PerformResult(success ? CronJobResult.SUCCESS : CronJobResult.ERROR, CronJobStatus.FINISHED);
    }

    protected void performJob(final boolean importflag, final String filepath, final boolean initialRun) throws InterruptedException {
        try {
            if (importflag) {
                final long startTime = System.nanoTime();
                LOG.info("Starting Importing Data from Excel File at " + new Date());
                String extension = null;
                List<String[]> importResult = null;
                boolean ignoreFirstRow = false;

                final int position = filepath.lastIndexOf('.');
                if (position > 0) {
                    extension = filepath.substring(position + 1);
                }
                if ("xls".equals(extension) || "xlsx".equals(extension)) {
                    importResult = getLinesFromFile(filepath);
                }
                if (importResult != null && !importResult.isEmpty()) {
                    if (ignoreFirstRow) {
                        importResult.remove(0);
                    }
                    if (!importResult.isEmpty()) {
                        searchProducts(importResult, defaultExternalPIMIDPosition, defaultFirstAppearanceDatePosition);
                    }
                }
                LOG.info("Finished Import Process from file in " + (int) ((System.nanoTime() - startTime) / 1e9) + " seconds.");
            } else if (initialRun) {
                getProductService().initialUpdateDistSalesOrgProductNewLabel();
            } else {
                // Incremental Process Started
                final long startTime = System.nanoTime();
                LOG.info("Starting Incremental First Appearance Date Update process  " + new Date());
                distTimeStampService.updateProductFirstAppearanceDate();
                LOG.info("Finished Incremental First Appearance Date Update in " + (int) ((System.nanoTime() - startTime) / 1e9) + " seconds.");
            }

        } catch (final Exception exp) {
            LOG.error("An error occured while updating the : " + exp.toString());
        }
    }

    public List<String[]> getLinesFromFile(final String importFileName) {
        List<String[]> importRows = new ArrayList<String[]>();
        String extension = null;
        try {
            final int position = importFileName.lastIndexOf('.');
            if (position > 0) {
                extension = importFileName.substring(position + 1);
            }
            if ("xls".equals(extension)) {
                importRows = createArrayFromXLS(importFileName, defaultExcelSheet);
            } else if ("xlsx".equals(extension)) {
                importRows = createArrayFromXLSX(importFileName, defaultExcelSheet);
            } else {
                importRows = Collections.EMPTY_LIST;
            }

            if (importRows != null && !importRows.isEmpty() && importRows.size() > 1) {
                importRows.remove(0);
            }
        } catch (Exception e) {
            if (e instanceof NullPointerException || e instanceof IllegalStateException) {
                LOG.error("An error occurs while fetching data from file :" + e.toString());
            }
        }
        return importRows;
    }

    protected boolean searchProducts(final List<String[]> importRows, final int externalpimid, final int firstappearancedate) {
        // final List<ProductModel> updateEntries = new ArrayList<ProductModel>();
        Date javaDate = null;
        String productpimid = null;
        ProductModel product = null;
        boolean okflag = true;
        int counter = 0;
        // Search the product one by one
        try {
            for (final String[] productInformations : importRows) {
                // String[] product = {External PIM ID | PIM Category Type | Ok-to-Web}
                if (productInformations.length < 2) {
                    continue;
                }
                productpimid = productInformations[externalpimid];
                if (productpimid.contains("-")) {
                    productpimid = productpimid.replace("-", "");
                }

                String firstappearancedatevalue = productInformations[firstappearancedate];
                if (!"".equals(firstappearancedatevalue) && !"".equals(productpimid)) {
                    javaDate = DateUtil.getJavaDate(Double.parseDouble(firstappearancedatevalue));
                    product = getDistTimeStampService().getProductInfoPimId(productpimid);
                    if (null != product && null != javaDate) {
                        product.setFirstAppearanceDate(javaDate);
                        getModelService().save(product);
                        counter++;
                    }
                }
                productpimid = null;
                javaDate = null;
            }

            LOG.info("successfully updated " + counter + " products");
        } catch (final Exception e) {
            LOG.error("Exception occurred during searching products", e);
            okflag = false;
            productpimid = null;
            javaDate = null;
        }
        return okflag;
    }

    protected List createArrayFromXLS(final String orderFileName, final int sheetNumber) throws ImportException {
        try {
            return createArrayFromWorkbook(new HSSFWorkbook(new FileInputStream(orderFileName)), sheetNumber);
        } catch (final IOException e) {
            throw new ImportException(e, ErrorSource.IO_EXCEPTION);
        }
    }

    protected List<String[]> createArrayFromXLSX(final String orderFileName, final int sheetNumber) throws ImportException {
        try {
            return createArrayFromWorkbook(new XSSFWorkbook(new FileInputStream(orderFileName)), sheetNumber);
        } catch (final IOException e) {
            throw new ImportException(e, ErrorSource.IO_EXCEPTION);
        }
    }

    protected List createArrayFromWorkbook(final Workbook workbook, final int sheetNumber) throws ImportException {
        final List<String[]> importRows = new ArrayList<String[]>();

        // Get first sheet from the workbook
        final Sheet sheet = workbook.getSheetAt(sheetNumber);

        // Read the header
        if (!sheet.iterator().hasNext()) {
            throw new ImportException("File is empty!", ErrorSource.FILE_EMPTY);
        }

        final Iterator<Row> rowIterator = sheet.iterator();
        int count = 0;
        while (rowIterator.hasNext()) {
            final Row row = rowIterator.next();
            final String[] data = new String[row.getLastCellNum()];
            final Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                final Cell cell = cellIterator.next();
                if (cell != null) {
                    if (cell.getCellType() == CellType.NUMERIC) {
                        data[cell.getColumnIndex()] = String.valueOf((int) cell.getNumericCellValue());
                    } else {
                        data[cell.getColumnIndex()] = cell.getStringCellValue();
                    }
                }
            }
            count++;
            importRows.add(data);
        }
        LOG.info("Number of records in File: " + count);
        return importRows;
    }

    // Getters and Setters.
    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public DistTimeStampService getDistTimeStampService() {
        return distTimeStampService;
    }

    public void setDistTimeStampService(DistTimeStampService distTimeStampService) {
        this.distTimeStampService = distTimeStampService;
    }

    public DistProductService getProductService() {
        return productService;
    }

    public void setProductService(DistProductService productService) {
        this.productService = productService;
    }

}
