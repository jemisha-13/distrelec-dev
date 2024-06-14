/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.task;

/**
 * BeforeEachPunchOutFilterManufacturer.
 * 
 * @author dathusir, Distrelec
 * 
 */

public interface BeforePunchoutFilterImport {

    void deletePunchoutFilter(String filename);

}
