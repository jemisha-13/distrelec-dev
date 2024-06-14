/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.export;

import java.io.InputStream;

/**
 * Interface specifying methods to deal with zipped data.
 * 
 * @author ceberle, Namics AG
 * @since Namics Distrelec 1.1
 */
public interface DistZipService {

    byte[] zip(InputStream data, String zipArchiveEntryName);

}
