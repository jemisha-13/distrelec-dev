package com.namics.distrelec.b2b.facades.order;

import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;

public class OrderCsvUtil {
    private static final String I_SEP = ";";

    private static final String CRLF = System.getProperty("line.separator");

    private static final String HEADER_LINE = "Order Date;OrderNr;OrderReceivedVia;State;Reference number;Project number;Comment;Art-Nr;"
                                              + "Manufacturer;Type;Quantity;Name;Your Reference;My Single Price;My Subtotal;List Single Price;List Subtotal"
                                              + CRLF;

    public OrderCsvUtil() {
    }

    public static String toCSV(OrderData order, String dateFormatPattern) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern != null ? dateFormatPattern : "dd.MM.yyyy");

        final StringBuilder builder = new StringBuilder(HEADER_LINE);

        builder.append(order.getOrderDate() != null ? dateFormat.format(order.getOrderDate()) : "").append(I_SEP);
        builder.append(order.getCode()).append(I_SEP);
        builder.append(order.getSalesApplication()).append(I_SEP);
        builder.append(order.getStatus().getCode()).append(I_SEP);
        builder.append(order.getCostCenter() == null ? "" : order.getCostCenter().getCode().replaceAll("\\W", "_")).append(I_SEP);
        builder.append(order.getProjectNumber() == null ? "" : order.getProjectNumber()).append(I_SEP);
        builder.append(order.getNote() == null ? "" : order.getNote()).append(I_SEP);
        boolean first = true;

        for (final OrderEntryData entryData : order.getEntries()) {
            if (first) {
                first = false;
            } else {
                builder.append(";;;;;;;");
            }
            builder.append(orderEntryToCSV(entryData)).append(CRLF);
        }

        return builder.toString();
    }

   private static String orderEntryToCSV(OrderEntryData orderEntryData) {
        final StringBuilder builder = new StringBuilder();
        builder.append(orderEntryData.getProduct().getCode() == null ? StringUtils.EMPTY : orderEntryData.getProduct().getCode()).append(I_SEP);
        builder.append(orderEntryData.getProduct().getManufacturer() == null ? StringUtils.EMPTY : orderEntryData.getProduct().getManufacturer()).append(I_SEP);
        builder.append(orderEntryData.getProduct().getTypeName() == null ? StringUtils.EMPTY : orderEntryData.getProduct().getTypeName()).append(I_SEP);
        builder.append(orderEntryData.getQuantity()).append(I_SEP);
        builder.append(orderEntryData.getProduct().getName() == null ? StringUtils.EMPTY : orderEntryData.getProduct().getName()).append(I_SEP);
        builder.append(orderEntryData.getCustomerReference() == null ? StringUtils.EMPTY : orderEntryData.getCustomerReference()).append(I_SEP);
        builder.append(orderEntryData.getBasePrice() == null ? StringUtils.EMPTY : orderEntryData.getBasePrice().getFormattedValue()).append(I_SEP);
        builder.append(orderEntryData.getTotalPrice() == null ? StringUtils.EMPTY : orderEntryData.getTotalPrice().getFormattedValue()).append(I_SEP);
        builder.append(orderEntryData.getBaseListPrice() == null ? StringUtils.EMPTY : orderEntryData.getBaseListPrice().getFormattedValue()).append(I_SEP);
        builder.append(orderEntryData.getTotalListPrice() == null ? StringUtils.EMPTY : orderEntryData.getTotalListPrice().getFormattedValue());

        return builder.toString();
    }

}
