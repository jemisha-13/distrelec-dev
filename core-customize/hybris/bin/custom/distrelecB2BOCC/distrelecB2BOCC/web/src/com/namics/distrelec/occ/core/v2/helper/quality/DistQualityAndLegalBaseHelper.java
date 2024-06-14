package com.namics.distrelec.occ.core.v2.helper.quality;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;

import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.CustomerData;

@Component
public class DistQualityAndLegalBaseHelper {

    @Autowired
    @Qualifier("distrelecProductFacade")
    private DistrelecProductFacade productFacade;

    public List<ProductData> getProductsForReport(List<String> productCodes) {
        final List<ProductOption> productOptions = Arrays.asList(ProductOption.BASIC,
                                                                 ProductOption.MIN_BASIC,
                                                                 ProductOption.DESCRIPTION,
                                                                 ProductOption.DIST_MANUFACTURER,
                                                                 ProductOption.CLASSIFICATION_AND_PRODUCT_INFORMATION);
        return productFacade.getProductListForCodesAndOptions(productCodes, productOptions);
    }

    protected String getCustomerName(CustomerData b2bCustomer, String customCustomerName) {
        if (StringUtils.isNotBlank(customCustomerName)) {
            return customCustomerName;
        }

        return b2bCustomer.getFirstName() + " " + b2bCustomer.getLastName();
    }

    protected void autoSizeColumns(Workbook workbook) {
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet.getPhysicalNumberOfRows() > 0) {
                for (int j = sheet.getFirstRowNum(); j <= sheet.getLastRowNum(); j++) {
                    Row row = sheet.getRow(j);
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        int columnIndex = cell.getColumnIndex();
                        sheet.autoSizeColumn(columnIndex);
                    }
                }
            }
        }
    }

}
