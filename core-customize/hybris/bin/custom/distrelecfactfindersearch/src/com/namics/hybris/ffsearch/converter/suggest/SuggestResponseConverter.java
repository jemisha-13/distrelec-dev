/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.suggest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Charsets;
import com.namics.hybris.ffsearch.data.suggest.AutocompleteSuggestion;
import com.namics.hybris.ffsearch.data.suggest.CategorySuggestion;
import com.namics.hybris.ffsearch.data.suggest.ManufacturerSuggestion;
import com.namics.hybris.ffsearch.data.suggest.ProductSuggestion;
import com.namics.hybris.ffsearch.data.suggest.SuggestRequest;
import com.namics.hybris.ffsearch.data.suggest.SuggestResponse;
import com.namics.hybris.ffsearch.data.suggest.TermSuggestion;

import de.factfinder.webservice.ws71.FFsearch.ResultSuggestion;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Converter for building up a {@link AutocompleteSuggestion} from a {@link SuggestResponse}.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class SuggestResponseConverter implements Converter<SuggestResponse, AutocompleteSuggestion> {

    private static final Logger LOG = LoggerFactory.getLogger(SuggestResponseConverter.class);

    private Converter<ResultSuggestion, ProductSuggestion> productSuggestionConverter;
    private Converter<ResultSuggestion, ManufacturerSuggestion> manufacturerSuggestionConverter;
    private Converter<ResultSuggestion, CategorySuggestion> categorySuggestionConverter;
    private Converter<ResultSuggestion, TermSuggestion> termSuggestionConverter;

    @Override
    public AutocompleteSuggestion convert(final SuggestResponse source, final AutocompleteSuggestion target) {
        if (responseIsEmpty(source)) {
            return target;
        }
        for (final ResultSuggestion suggestion : source.getSuggestResults().getResultSuggestion()) {
            convertSuggestion(suggestion, source.getSuggestRequest(), target);
        }
        return target;
    }

    protected void convertSuggestion(final ResultSuggestion suggestion, final SuggestRequest request, final AutocompleteSuggestion target) {

        // Term
        target.setTerm(getSuggestTerm(request));

        // Results for Blocks
        switch (SuggestType.get(suggestion.getType())) {
        case PRODUCT:
            target.getRes().getProds().getList().add(productSuggestionConverter.convert(suggestion));
            break;
        case MANUFACTURER:
            target.getRes().getMans().getList().add(manufacturerSuggestionConverter.convert(suggestion));
            break;
        case CATEGORY:
            target.getRes().getCats().getList().add(categorySuggestionConverter.convert(suggestion));
            break;
        case TERM:
            target.getRes().getTerms().getList().add(termSuggestionConverter.convert(suggestion));
            break;
        default:
            LOG.debug("Ignore converting suggestion of type {}", suggestion.getType());
            break;
        }
    }

    /**
     * Returns the URLencoded suggest term. Empty string value <code>""</code> if an error occured.
     */
    protected String getSuggestTerm(final SuggestRequest request) {
        final String term = request.getSearchParams().getQuery();
        if (StringUtils.isEmpty(term)) {
            return StringUtils.EMPTY;
        }
        try {
            return URLEncoder.encode(term, Charsets.UTF_8.name());
        } catch (final UnsupportedEncodingException e) {
            LOG.error("Unable to decode suggest term with value {}.", term);
        }
        return StringUtils.EMPTY;
    }

    @Override
    public AutocompleteSuggestion convert(final SuggestResponse source) {
        return convert(source, createTarget());
    }

    private boolean responseIsEmpty(final SuggestResponse source) {
        return source.getSuggestResults() == null || source.getSuggestResults().getResultSuggestion().isEmpty();
    }

    protected AutocompleteSuggestion createTarget() {
        return new AutocompleteSuggestion();
    }

    // BEGIN GENERATED CODE

    protected Converter<ResultSuggestion, ProductSuggestion> getProductSuggestionConverter() {
        return productSuggestionConverter;
    }

    @Required
    public void setProductSuggestionConverter(final Converter<ResultSuggestion, ProductSuggestion> productSuggestionConverter) {
        this.productSuggestionConverter = productSuggestionConverter;
    }

    protected Converter<ResultSuggestion, ManufacturerSuggestion> getManufacturerSuggestionConverter() {
        return manufacturerSuggestionConverter;
    }

    @Required
    public void setManufacturerSuggestionConverter(final Converter<ResultSuggestion, ManufacturerSuggestion> manufacturerSuggestionConverter) {
        this.manufacturerSuggestionConverter = manufacturerSuggestionConverter;
    }

    protected Converter<ResultSuggestion, CategorySuggestion> getCategorySuggestionConverter() {
        return categorySuggestionConverter;
    }

    @Required
    public void setCategorySuggestionConverter(final Converter<ResultSuggestion, CategorySuggestion> categorySuggestionConverter) {
        this.categorySuggestionConverter = categorySuggestionConverter;
    }

    public Converter<ResultSuggestion, TermSuggestion> getTermSuggestionConverter() {
        return termSuggestionConverter;
    }

    public void setTermSuggestionConverter(final Converter<ResultSuggestion, TermSuggestion> termSuggestionConverter) {
        this.termSuggestionConverter = termSuggestionConverter;
    }
}
