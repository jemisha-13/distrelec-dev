/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.task;

import com.namics.distrelec.b2b.core.dataimport.batch.DistBatchHeader;
import com.namics.distrelec.b2b.core.dataimport.batch.converter.DistImpexConverter;
import com.namics.distrelec.b2b.core.dataimport.batch.converter.DistPriceImpexConverter;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexConverter;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.ImpexTransformerTask;
import de.hybris.platform.util.CSVReader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Extends the ImpexTransformerTask to have 4 hooks: - before - beforEach -
 * afterEach - after
 * 
 * @author dathusir, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>,
 * Distrelec
 * @since Distrelec 1.0
 */
public class DistImpexTransformerTask extends ImpexTransformerTask {
	private static final Integer DELETE_POSITION = 21;
	private static final Integer DELETE_POSITION_10_SCALES = 29;
	private static final Integer UNIT_FACTOR_POSITION = 5;


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.acceleratorservices.dataimport.batch.task.
	 * ImpexTransformerTask#convertFile(de.hybris.platform.acceleratorservices.
	 * dataimport.batch.BatchHeader, java.io.File, java.io.File,
	 * de.hybris.platform.acceleratorservices.dataimport.batch.converter.
	 * ImpexConverter)
	 */
	@Override
	protected boolean convertFile(final BatchHeader header, final File file, final File impexFile,
			final ImpexConverter converter) throws UnsupportedEncodingException, FileNotFoundException {
		DistBatchHeader distBatchHeader = (DistBatchHeader) header;
		if (converter instanceof DistPriceImpexConverter) {
			final DistPriceImpexConverter distImpexConverter = (DistPriceImpexConverter) converter;
			final int POSITION = "10_SCALES".equals(distImpexConverter.getVersion()) ? DELETE_POSITION_10_SCALES
					: DELETE_POSITION;
			boolean result = false;
			CSVReader csvReader = null;
			PrintWriter writer = null;
			PrintWriter errorWriter = null;
			try {
				csvReader = createCsvReader(file);
				writer = new PrintWriter(
						new BufferedWriter(new OutputStreamWriter(new FileOutputStream(impexFile), getEncoding())));
				writer.println(getReplacedInsertUpdateHeader(distBatchHeader, distImpexConverter));
				final StringBuffer pricesToRemove = new StringBuffer();
				while (csvReader.readNextLine()) {
					final Map<Integer, String> row = csvReader.getLine();
					if (row.containsKey(UNIT_FACTOR_POSITION)) {
						final String value = row.get(UNIT_FACTOR_POSITION);
						if (StringUtils.isEmpty(value) || value.equals("0")) {
							row.put(UNIT_FACTOR_POSITION, "1");
						}
					}
					// filter
					if (distImpexConverter.filter(row)) {
						try {
							if (BooleanUtils.toBoolean(row.get(POSITION))) {
								pricesToRemove.append(distImpexConverter.convertRemove(row,
										distBatchHeader.getSequenceId(), distBatchHeader.getErpSequenceId()));
								pricesToRemove.append(System.getProperty("line.separator"));
							} else {
								final String impexRow = distImpexConverter.convertInsertUpdate(row,
										distBatchHeader.getSequenceId(), distBatchHeader.getErpSequenceId());
								if (StringUtils.isNotBlank(impexRow)) {
									writer.print(impexRow);
								}
							}
							result = true;
						} catch (final IllegalArgumentException exc) {
							errorWriter = writeErrorLine(file, csvReader, errorWriter, exc);
						}
					}
				}

				if (pricesToRemove.length() > 0) {
					writer.println(System.getProperty("line.separator"));
					writer.println(System.getProperty("line.separator"));
					writer.println(getReplacedRemoveHeader(distBatchHeader, distImpexConverter));
					writer.println(pricesToRemove.toString());
				}
							
			} finally {
				IOUtils.closeQuietly(writer);
				IOUtils.closeQuietly(errorWriter);
				closeQuietly(csvReader);
			}
			return result;
		} else if (converter instanceof DistImpexConverter) {
			final DistImpexConverter distImpexConverter = (DistImpexConverter) converter;
			boolean result = false;
			CSVReader csvReader = null;
			PrintWriter writer = null;
			PrintWriter errorWriter = null;
			try {
				csvReader = createCsvReader(file);
				writer = new PrintWriter(
						new BufferedWriter(new OutputStreamWriter(new FileOutputStream(impexFile), getEncoding())));
				writer.println(getReplacedHeader(distBatchHeader, distImpexConverter));

				// before
				distImpexConverter.before(distBatchHeader, csvReader);
				Map<Integer, String> lastRow = null;
				while (csvReader.readNextLine()) {
					final Map<Integer, String> row = csvReader.getLine();

					// filter
					if (distImpexConverter.filter(row)) {
						try {
							// before each
							distImpexConverter.beforeEach(row);
							writer.println(distImpexConverter.convert(row, distBatchHeader.getSequenceId(),
									distBatchHeader.getErpSequenceId()));
							result = true;
							lastRow = row;
						} catch (final IllegalArgumentException exc) {
							errorWriter = writeErrorLine(file, csvReader, errorWriter, exc);
						}
					}
				}
			} finally {
				IOUtils.closeQuietly(writer);
				IOUtils.closeQuietly(errorWriter);
				closeQuietly(csvReader);
			}
			return result;
		} else {
			return super.convertFile(distBatchHeader, file, impexFile, converter);
		}
	}

	protected String getReplacedInsertUpdateHeader(final BatchHeader header, final DistPriceImpexConverter converter) {
		final Map<String, String> symbols = new TreeMap<String, String>();
		buildReplacementSymbols(symbols, header, converter);
		return replaceSymbolsInText(converter.getInsertUpdateHeader(), symbols);
	}

	protected String getReplacedRemoveHeader(final BatchHeader header, final DistPriceImpexConverter converter) {
		final Map<String, String> symbols = new TreeMap<String, String>();
		buildReplacementSymbols(symbols, header, converter);
		return replaceSymbolsInText(converter.getRemoveHeader(), symbols);
	}
}
