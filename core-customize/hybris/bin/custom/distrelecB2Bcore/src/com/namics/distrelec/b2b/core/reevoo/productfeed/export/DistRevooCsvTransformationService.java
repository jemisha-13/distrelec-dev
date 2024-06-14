package com.namics.distrelec.b2b.core.reevoo.productfeed.export;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Charsets;
import com.namics.distrelec.b2b.core.inout.export.DistCsvTransformationService;
import com.namics.distrelec.b2b.core.inout.export.exception.DistCsvTransformationException;
import com.opencsv.CSVWriter;

import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.JaloSession;

/**
 * Default implementation of {@link DistCsvTransformationService}.
 * 
 * @author ceberle, Namics AG
 * @since Namics Distrelec 1.1
 */

public class DistRevooCsvTransformationService  {
	
	 private static final Logger LOG = LogManager.getLogger(DistRevooCsvTransformationService.class);

	    public static final String ENCODE_URL_SUFFIX = "_encodeurl";
	    public static final String ENCODE_FF_SUFFIX = "_encodeff";

	    private static final int BUFFER_SIZE = 2048;
	    private static final char SEPARATOR = ',';

	    public InputStream transform(final String[] header, final List<String[]> arrayList) {
	        try {
	            final PipedInputStream input = new PipedInputStream(BUFFER_SIZE);
	            final PipedOutputStream output = new PipedOutputStream(input);

	            final ExecutorService executor = Executors.newSingleThreadExecutor();
	            final Runnable exporter = new ArrayCsvTransformer(header, arrayList, output);
	            executor.submit(exporter);
	            executor.shutdown();
	            return input;
	        } catch (final IOException e) {
	            throw new DistCsvTransformationException("Could not create piped stream", e);
	        }
	    }
	    
	    protected class ArrayCsvTransformer implements Runnable {

	        private final List<String[]> arrayList;
	        private final String[] header;
	        private final PipedOutputStream output;

	        public ArrayCsvTransformer(final String[] header, final List<String[]> arrayList, final PipedOutputStream output) {
	            this.arrayList = arrayList;
	            this.output = output;
	            this.header = header;
	        }

	        @Override
	        public void run() {
	            Registry.activateMasterTenant();
	            JaloSession.getCurrentSession().activate();

	            CSVWriter writer = null;
	            try {
	                writer = new CSVWriter(new OutputStreamWriter(output, Charsets.UTF_8.name()), SEPARATOR);

	                writer.writeNext(header);

	                writer.writeAll(arrayList);
	            } catch (final UnsupportedEncodingException e) {
	                LOG.error("Could not transform ResultSet to CSV stream", e);
	                throw new DistCsvTransformationException("Could not transform ResultSet to CSV stream", e);
	            } finally {
	                IOUtils.closeQuietly(writer);
	                IOUtils.closeQuietly(output);
	            }
	        }
	    }


}
