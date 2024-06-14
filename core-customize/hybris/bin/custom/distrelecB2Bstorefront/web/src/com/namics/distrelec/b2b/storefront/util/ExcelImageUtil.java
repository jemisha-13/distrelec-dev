package com.namics.distrelec.b2b.storefront.util;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.Units;

import static java.lang.Math.round;
import static org.apache.poi.util.IOUtils.toByteArray;

/**
 * This code was originally taken from https://svn.apache.org/repos/asf/poi/trunk/poi-examples/src/main/java/org/apache/poi/examples/ss/AddDimensionedImage.java
 * More info: https://poi.apache.org/components/spreadsheet/examples.html
 * It's modified to our needs.
 * <p>
 * Other resources:
 * - https://stackoverflow.com/a/60613952
 * - https://stackoverflow.com/a/48607117
 * - https://stackoverflow.com/a/61907219
 * - https://roytuts.com/add-images-to-excel-file-using-apache-poi-in-java/
 * - https://poi.apache.org/apidocs/4.1/
 * <p>
 * This was a hell to work on.
 */
public class ExcelImageUtil {

    public static final int EXPAND_ROW = 1;

    public static final int EXPAND_COLUMN = 2;

    public static final int EXPAND_ROW_AND_COLUMN = 3;

    public static final int OVERLAY_ROW_AND_COLUMN = 7;

    private static final int EMU_PER_MM = 36000;

    public static void addImageToSheet(ImageCreationDetails creation) throws IOException, IllegalArgumentException {
        if (creation == null) {
            return;
        }

        ClientAnchor anchor;
        ClientAnchorDetail rowClientAnchorDetail;
        ClientAnchorDetail colClientAnchorDetail;

        if ((creation.getResizeBehaviour() != EXPAND_COLUMN) &&
            (creation.getResizeBehaviour() != EXPAND_ROW) &&
            (creation.getResizeBehaviour() != EXPAND_ROW_AND_COLUMN) &&
            (creation.getResizeBehaviour() != OVERLAY_ROW_AND_COLUMN)) {
            throw new IllegalArgumentException("Invalid value passed to the resizeBehaviour parameter of AddDimensionedImage.addImageToSheet()");
        }

        colClientAnchorDetail = fitImageToColumns(creation.getSheet(), creation.getColNumber(), creation.getReqImageWidthMM(), creation.getResizeBehaviour());
        rowClientAnchorDetail = fitImageToRows(creation.getSheet(), creation.getRowNumber(), creation.getReqImageHeightMM(), creation.getResizeBehaviour());

        anchor = creation.getSheet().getWorkbook().getCreationHelper().createClientAnchor();

        anchor.setDx1(0);
        anchor.setDy1(0);
        if (colClientAnchorDetail != null) {
            anchor.setDx2(colClientAnchorDetail.getInset());
            anchor.setCol1(colClientAnchorDetail.getFromIndex());
            anchor.setCol2(colClientAnchorDetail.getToIndex());
        }

        if (rowClientAnchorDetail != null) {
            anchor.setDy2(rowClientAnchorDetail.getInset());
            anchor.setRow1(rowClientAnchorDetail.getFromIndex());
            anchor.setRow2(rowClientAnchorDetail.getToIndex());
        }

        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
        positionImageAnchor(anchor, creation, rowClientAnchorDetail, colClientAnchorDetail);

        int index = creation.getSheet().getWorkbook().addPicture(toByteArray(creation.getImageFile().openStream()), getImageType(creation.getImageFile()));
        creation.getSheet().createDrawingPatriarch().createPicture(anchor, index);
    }

    private static void positionImageAnchor(ClientAnchor anchor, ImageCreationDetails creation, ClientAnchorDetail rowClientAnchorDetail, ClientAnchorDetail colClientAnchorDetail) {
        int yInset = rowClientAnchorDetail != null ? rowClientAnchorDetail.getInset() : 0;
        int xInset = colClientAnchorDetail != null ? colClientAnchorDetail.getInset() : 0;
        anchor.setDy1(creation.getDy1() * Units.EMU_PER_POINT);
        anchor.setDx1(round(creation.getDx1() * Units.EMU_PER_PIXEL * Units.DEFAULT_CHARACTER_WIDTH / 256f));
        anchor.setDy2(yInset + (creation.getDy2() * Units.EMU_PER_POINT));
        anchor.setDx2(xInset + (round(creation.getDx2() * Units.EMU_PER_PIXEL * Units.DEFAULT_CHARACTER_WIDTH / 256f)));
    }

    private static int getImageType(URL imageFileUrl) {
        String sURL = imageFileUrl.toString().toLowerCase(Locale.ROOT);
        if (sURL.endsWith(".png")) {
            return Workbook.PICTURE_TYPE_PNG;
        } else if (sURL.endsWith(".jpg") || sURL.endsWith(".jpeg")) {
            return Workbook.PICTURE_TYPE_JPEG;
        } else {
            throw new IllegalArgumentException("Invalid Image file : " + sURL);
        }
    }

    private static ClientAnchorDetail fitImageToColumns(Sheet sheet, int colNumber, double reqImageWidthMM, int resizeBehaviour) {
        double colWidthMM;
        int pictureWidthCoordinates;
        ClientAnchorDetail colClientAnchorDetail = null;

        colWidthMM = ConvertImageUnits.widthUnits2Millimetres((short) sheet.getColumnWidth(colNumber));

        if (colWidthMM < reqImageWidthMM) {
            if ((resizeBehaviour == EXPAND_COLUMN) || (resizeBehaviour == EXPAND_ROW_AND_COLUMN)) {
                sheet.setColumnWidth(colNumber, ConvertImageUnits.millimetres2WidthUnits(reqImageWidthMM));
                pictureWidthCoordinates = (int) reqImageWidthMM * EMU_PER_MM;
                colClientAnchorDetail = new ClientAnchorDetail(colNumber, colNumber, pictureWidthCoordinates);
            } else if ((resizeBehaviour == OVERLAY_ROW_AND_COLUMN) || (resizeBehaviour == EXPAND_ROW)) {
                colClientAnchorDetail = calculateColumnLocation(sheet, colNumber, reqImageWidthMM);
            }
        } else {
            pictureWidthCoordinates = (int) reqImageWidthMM * EMU_PER_MM;
            colClientAnchorDetail = new ClientAnchorDetail(colNumber, colNumber, pictureWidthCoordinates);
        }

        return colClientAnchorDetail;
    }

    private static ClientAnchorDetail fitImageToRows(Sheet sheet, int rowNumber, double reqImageHeightMM, int resizeBehaviour) {
        Row row;
        double rowHeightMM;
        int pictureHeightCoordinates;
        ClientAnchorDetail rowClientAnchorDetail = null;

        row = sheet.getRow(rowNumber);
        if (row == null) {
            row = sheet.createRow(rowNumber);
        }

        rowHeightMM = row.getHeightInPoints() / ConvertImageUnits.POINTS_PER_MILLIMETRE;

        if (rowHeightMM < reqImageHeightMM) {
            if ((resizeBehaviour == EXPAND_ROW) || (resizeBehaviour == EXPAND_ROW_AND_COLUMN)) {
                row.setHeightInPoints((float) (reqImageHeightMM * ConvertImageUnits.POINTS_PER_MILLIMETRE));
                pictureHeightCoordinates = (int) (reqImageHeightMM * EMU_PER_MM);
                rowClientAnchorDetail = new ClientAnchorDetail(rowNumber, rowNumber, pictureHeightCoordinates);
            } else if ((resizeBehaviour == OVERLAY_ROW_AND_COLUMN) || (resizeBehaviour == EXPAND_COLUMN)) {
                rowClientAnchorDetail = calculateRowLocation(sheet, rowNumber, reqImageHeightMM);
            }
        } else {
            pictureHeightCoordinates = (int) (reqImageHeightMM * EMU_PER_MM);
            rowClientAnchorDetail = new ClientAnchorDetail(rowNumber, rowNumber, pictureHeightCoordinates);
        }

        return rowClientAnchorDetail;
    }

    private static ClientAnchorDetail calculateRowLocation(Sheet sheet, int startingRow, double reqImageHeightMM) {
        Row row;
        double rowHeightMM = 0.0D;
        double totalRowHeightMM = 0.0D;
        int toRow = startingRow;

        while (totalRowHeightMM < reqImageHeightMM) {
            row = sheet.getRow(toRow);
            if (row == null) {
                row = sheet.createRow(toRow);
            }
            rowHeightMM = row.getHeightInPoints() / ConvertImageUnits.POINTS_PER_MILLIMETRE;
            totalRowHeightMM += rowHeightMM;
            toRow++;
        }
        return getClientAnchorDetail(startingRow, reqImageHeightMM, totalRowHeightMM, rowHeightMM, toRow);
    }

    private static ClientAnchorDetail calculateColumnLocation(Sheet sheet, int startingColumn, double reqImageWidthMM) {
        double totalWidthMM = 0.0D;
        double colWidthMM = 0.0D;
        int toColumn = startingColumn;

        while (totalWidthMM < reqImageWidthMM) {
            colWidthMM = ConvertImageUnits.widthUnits2Millimetres((short) (sheet.getColumnWidth(toColumn)));
            totalWidthMM += (colWidthMM + ConvertImageUnits.CELL_BORDER_WIDTH_MILLIMETRES);
            toColumn++;
        }

        return getClientAnchorDetail(startingColumn, reqImageWidthMM, totalWidthMM, colWidthMM, toColumn);
    }

    /**
     * Calculates the Anchor details for row and column location
     *
     * @param startingColumn Starting column
     * @param reqImageSizeMM Image size - in case of rows it's image height, in case of columns it's image width
     * @param totalSizeMM Total size - in case of rows it's total row height, in case of columns it's total column width
     * @param cellSizeMM Cell size - in case of rows it's row height, in case of columns it's column width
     * @param toElement To element - element can be row, or column to which the anchor stretches to
     *
     * @return Anchor detail for row or column span
     */
    private static ClientAnchorDetail getClientAnchorDetail(int startingColumn, double reqImageSizeMM, double totalSizeMM, double cellSizeMM, int toElement) {
        ClientAnchorDetail anchorDetail;
        double overlapMM;
        int inset;
        toElement--;

        if ((int) totalSizeMM == (int) reqImageSizeMM) {
            anchorDetail = new ClientAnchorDetail(startingColumn, toElement, (int) reqImageSizeMM * EMU_PER_MM);
        } else {
            overlapMM = reqImageSizeMM - (totalSizeMM - cellSizeMM);

            if (overlapMM < 0) {
                overlapMM = 0.0D;
            }

            inset = (int) overlapMM * EMU_PER_MM;
            anchorDetail = new ClientAnchorDetail(startingColumn, toElement, inset);
        }

        return anchorDetail;
    }

    private static class ClientAnchorDetail {

        private int fromIndex;

        private int toIndex;

        private int inset;

        public ClientAnchorDetail(int fromIndex, int toIndex, int inset) {
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.inset = inset;
        }

        public int getFromIndex() {
            return (this.fromIndex);
        }

        public int getToIndex() {
            return (this.toIndex);
        }

        public int getInset() {
            return (this.inset);
        }
    }

    private static class ConvertImageUnits {

        public static final double PIXELS_PER_MILLIMETRES = 3.78;

        public static final double POINTS_PER_MILLIMETRE = 2.83;

        public static final double CELL_BORDER_WIDTH_MILLIMETRES = 2.0D;

        public static final short EXCEL_COLUMN_WIDTH_FACTOR = 256;

        public static final int UNIT_OFFSET_LENGTH = 7;

        private static final int[] UNIT_OFFSET_MAP = {0, 36, 73, 109, 146, 182, 219};

        public static short pixel2WidthUnits(int pxs) {
            short widthUnits = (short) (EXCEL_COLUMN_WIDTH_FACTOR * (pxs / UNIT_OFFSET_LENGTH));
            widthUnits += UNIT_OFFSET_MAP[(pxs % UNIT_OFFSET_LENGTH)];
            return widthUnits;
        }

        public static int widthUnits2Pixel(short widthUnits) {
            int pixels = (widthUnits / EXCEL_COLUMN_WIDTH_FACTOR) * UNIT_OFFSET_LENGTH;
            int offsetWidthUnits = widthUnits % EXCEL_COLUMN_WIDTH_FACTOR;
            pixels += round(offsetWidthUnits / ((float) EXCEL_COLUMN_WIDTH_FACTOR / UNIT_OFFSET_LENGTH));
            return pixels;
        }

        public static double widthUnits2Millimetres(short widthUnits) {
            return (ConvertImageUnits.widthUnits2Pixel(widthUnits) / ConvertImageUnits.PIXELS_PER_MILLIMETRES);
        }

        public static int millimetres2WidthUnits(double millimetres) {
            return (ConvertImageUnits.pixel2WidthUnits((int) (millimetres * ConvertImageUnits.PIXELS_PER_MILLIMETRES)));
        }
    }

    public static class ImageCreationDetails {
        private int colNumber;

        private int rowNumber;

        private Sheet sheet;

        private URL imageFile;

        private double reqImageWidthMM;

        private double reqImageHeightMM;

        private int resizeBehaviour;

        int dx1;

        int dy1;

        int dx2;

        int dy2;

        public int getColNumber() {
            return colNumber;
        }

        public void setColNumber(int colNumber) {
            this.colNumber = colNumber;
        }

        public int getRowNumber() {
            return rowNumber;
        }

        public void setRowNumber(int rowNumber) {
            this.rowNumber = rowNumber;
        }

        public Sheet getSheet() {
            return sheet;
        }

        public void setSheet(Sheet sheet) {
            this.sheet = sheet;
        }

        public URL getImageFile() {
            return imageFile;
        }

        public void setImageFile(URL imageFile) {
            this.imageFile = imageFile;
        }

        public double getReqImageWidthMM() {
            return reqImageWidthMM;
        }

        public void setReqImageWidthMM(double reqImageWidthMM) {
            this.reqImageWidthMM = reqImageWidthMM;
        }

        public double getReqImageHeightMM() {
            return reqImageHeightMM;
        }

        public void setReqImageHeightMM(double reqImageHeightMM) {
            this.reqImageHeightMM = reqImageHeightMM;
        }

        public int getResizeBehaviour() {
            return resizeBehaviour;
        }

        public void setResizeBehaviour(int resizeBehaviour) {
            this.resizeBehaviour = resizeBehaviour;
        }

        public int getDx1() {
            return dx1;
        }

        public void setDx1(int dx1) {
            this.dx1 = dx1;
        }

        public int getDy1() {
            return dy1;
        }

        public void setDy1(int dy1) {
            this.dy1 = dy1;
        }

        public int getDx2() {
            return dx2;
        }

        public void setDx2(int dx2) {
            this.dx2 = dx2;
        }

        public int getDy2() {
            return dy2;
        }

        public void setDy2(int dy2) {
            this.dy2 = dy2;
        }
    }

}
