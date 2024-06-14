package com.namics.distrelec.b2b.core.dataimport.batch.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.impex.jalo.translators.SingleValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

public class SapWeighUnitValueTranslator extends SingleValueTranslator {

    // GR in SAP stands for dummy weight
    public static final String DUMMY_WEIGH = "GR";

    @Override
    protected Object convertToJalo(String code, Item item) {
        if (DUMMY_WEIGH.equals(code)) {
            return null;
        } else {
            return findUnitByCode(code);
        }
    }

    @Override
    protected String convertToString(Object o) {
        return ((Unit) o).getCode();
    }

    protected Unit findUnitByCode(String code) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(MediaModel.CODE, code);

        final String query = "SELECT {" + Item.PK + "} FROM {" + UnitModel._TYPECODE + "} " + "WHERE {" + UnitModel.CODE + "} = ?" + UnitModel.CODE;

        List<Unit> units = FlexibleSearch.getInstance().search(query, parameters, Unit.class).getResult();

        if (units.isEmpty()) {
            throw new ModelNotFoundException(String.format("Unit %s not found", code));
        } else if (units.size() > 1) {
            throw new AmbiguousIdentifierException(String.format("Found %s units with the same code %s", units.size(), code));
        } else {
            Unit unit = units.get(0);
            if (unit == null) {
                throw new ModelNotFoundException("No result for the given query");
            } else {
                return unit;
            }
        }
    }
}
