/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.spring.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.util.Assert;

/**
 * DistCompositeFileListFilter.
 * 
 * @param <F>
 * 
 * @author pnueesch, Namics AG
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DistCompositeFileListFilter<F> extends CompositeFileListFilter<F> {

    private final Set<FileListFilter<F>> fileFilters;
    private boolean acceptOnce = true;

    public DistCompositeFileListFilter(final Collection<? extends FileListFilter<F>> fileFilters) {
        this(fileFilters, true);
    }

    public DistCompositeFileListFilter(final Collection<? extends FileListFilter<F>> fileFilters, final boolean acceptOnce) {
        this.fileFilters = new LinkedHashSet<FileListFilter<F>>(fileFilters);
        this.acceptOnce = acceptOnce;
    }

    @Override
    public List<F> filterFiles(final F[] files) {
        Assert.notNull(files, "'files' should not be null");
        final List<F> results = new ArrayList<F>(Arrays.asList(files));

        this.fileFilters.stream().filter(fileFilter -> (!acceptOnce ? !(fileFilter instanceof AcceptOnceFileListFilter) : true))
                .forEach(fileFilter -> results.retainAll(fileFilter.filterFiles((F[]) results.toArray())));

        return results;
    }
}
