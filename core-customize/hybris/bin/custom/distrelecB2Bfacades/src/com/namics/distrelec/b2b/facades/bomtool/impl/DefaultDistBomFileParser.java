package com.namics.distrelec.b2b.facades.bomtool.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.namics.distrelec.b2b.facades.bomtool.DistBomFileParser;
import com.namics.distrelec.b2b.facades.bomtool.exception.BomToolFacadeException;
import com.namics.distrelec.b2b.facades.bomtool.exception.BomToolFacadeException.ErrorSource;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultDistBomFileParser implements DistBomFileParser {

	@Autowired
	private MediaService mediaService;

	// https://wiki.namics.com/display/distrelint/C300-BOMDataImport :
	// Limit number of rows : max 100 rows (server side validation)
	private static final int ROWS_LIMIT = 300;

	// https://wiki.namics.com/display/distrelint/C300-BOMDataImport :
	// Copy&Paste of comma-separated-values / tab-separated-values
	private static String[] csvSeparators = { ";", "\t", "," };

	@Override
	public List<String[]> createArrayFromCsv(final MediaModel file, final Integer referencePosition) throws BomToolFacadeException {
		LineNumberReader lineNumberReader = null;

		try {
			final List<String[]> importRows = new ArrayList<String[]>();

			File csvFile = new File(file.getCode());
			FileUtils.copyInputStreamToFile(mediaService.getStreamFromMedia(file), csvFile);
			final FileReader fileReader = new FileReader(csvFile);
			final int rowCount = count(csvFile);
			if (rowCount > ROWS_LIMIT) {
				// https://wiki.namics.com/display/distrelint/C300-BOMDataImport
				// :
				// Limit number of rows : max 100 rows (server side validation)
				throw new BomToolFacadeException("Too many lines! " + ROWS_LIMIT + " are allowed, but got" + rowCount,
						ErrorSource.TOO_MANY_LINES);
			}

			// Read the header and determine the separator
			lineNumberReader = new LineNumberReader(fileReader);
			String csvSeparator = null;
			String line = lineNumberReader.readLine();
			if (line != null) {
				for (int i = 0; i < csvSeparators.length; i++) {
					if (line.split(csvSeparators[i]).length > 1) {
						csvSeparator = csvSeparators[i];
						break;
					}
				}
			}
			if (csvSeparator == null) {
				throw new BomToolFacadeException("Separator not found!", ErrorSource.FIELD_SEPARATOR_NOT_FOUND);
			} else {
				// DISTRELEC-10762 we need to add also the first line to the list.
				importRows.add(line.split(csvSeparator));
			}

			// https://wiki.namics.com/display/distrelint/C300-BOMDataImport
			while ((line = lineNumberReader.readLine()) != null) {
				final String[] data = line.split(csvSeparator);

				if (data.length > 2 && referencePosition != null && data[referencePosition] != null && data[referencePosition].length() > 35) {
					throw new BomToolFacadeException("Customer reference is longer than 35 chars!",
							lineNumberReader.getLineNumber(), ErrorSource.CUSTOMER_REFERENCE_FIELD);
				}

				importRows.add(data);
			}

			if (importRows.isEmpty()) {
				throw new BomToolFacadeException("No data found!", ErrorSource.NO_DATA);
			}

			return importRows;
		} catch (final IOException e) {
			throw new BomToolFacadeException(e, ErrorSource.IO_EXCEPTION);
		} finally {
			IOUtils.closeQuietly(lineNumberReader);
		}
	}

	@Override
	public List<String[]> createArrayFromXLSX(final MediaModel file, final int sheetNumber,
			final Integer referencePosition) throws BomToolFacadeException {
		try {
			final FileInputStream inputStream = (FileInputStream) mediaService.getStreamFromMedia(file);
			return createArrayFromWorkbook(new XSSFWorkbook(inputStream), sheetNumber,
					referencePosition);
		} catch (final IOException e) {
			throw new BomToolFacadeException(e, ErrorSource.IO_EXCEPTION);
		}
	}

	@Override
	public List<String[]> createArrayFromXLS(final MediaModel file, final int sheetNumber, final Integer referencePosition)
			throws BomToolFacadeException {
		try {
			final FileInputStream inputStream = (FileInputStream) mediaService.getStreamFromMedia(file);
			return createArrayFromWorkbook(new HSSFWorkbook(inputStream), sheetNumber,
					referencePosition);
		} catch (final IOException e) {
			throw new BomToolFacadeException(e, ErrorSource.IO_EXCEPTION);
		}
	}

	protected List<String[]> createArrayFromWorkbook(final Workbook workbook, final int sheetNumber, final Integer referencePosition)
			throws BomToolFacadeException {
		final List<String[]> importRows = new ArrayList<>();

		// Get the workbook instance for XLS file

		// Get first sheet from the workbook
		final Sheet sheet = workbook.getSheetAt(sheetNumber);

		// Read the header
		if (!sheet.iterator().hasNext()) {
			throw new BomToolFacadeException("File is empty!", ErrorSource.FILE_EMPTY);
		} else if (sheet.getLastRowNum() >= ROWS_LIMIT) {
			// https://wiki.namics.com/display/distrelint/C300-BOMDataImport :
			// Limit number of rows : max 100 rows (server side validation)
			throw new BomToolFacadeException("Too many lines! " + ROWS_LIMIT + " are allowed, but got" + sheet.getLastRowNum(),
					ErrorSource.TOO_MANY_LINES);
		}

		final Iterator<Row> rowIterator = sheet.iterator();
		int rowCounter = 0;
		while (rowIterator.hasNext()) {
			final Row row = rowIterator.next();
			rowCounter++;
			final String[] data = new String[row.getLastCellNum()];
			final Iterator<Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				final Cell cell = cellIterator.next();
				if (cell != null) {
					if (cell.getCellType() == CellType.NUMERIC) {
						data[cell.getColumnIndex()] = String.valueOf((long) cell.getNumericCellValue());
					} else {
						data[cell.getColumnIndex()] = cell.getStringCellValue();
					}
				}
			}

			if (referencePosition != null &&
					referencePosition != Integer.MIN_VALUE &&
					data.length > 2 &&
					referencePosition <= data.length &&
					data[referencePosition] != null &&
					data[referencePosition].length() > 35) {
				throw new BomToolFacadeException("Customer reference is longer than 35 chars!", rowCounter,
						ErrorSource.CUSTOMER_REFERENCE_FIELD);
			}

			importRows.add(data);
		}
		return importRows;
	}

	private int count(final File file) throws BomToolFacadeException {
		LineNumberReader lnr = null;
		try {
			lnr = new LineNumberReader(new FileReader(file));
			lnr.skip(Long.MAX_VALUE);
			return lnr.getLineNumber();
		} catch (final IOException e) {
			throw new BomToolFacadeException(e, ErrorSource.IO_EXCEPTION);
		} finally {
			IOUtils.closeQuietly(lnr);
		}
	}

}
