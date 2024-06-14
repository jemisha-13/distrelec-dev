package com.namics.distrelec.b2b.core.service.unit;

import java.math.BigDecimal;
import java.util.Optional;

import com.distrelec.b2b.core.search.data.Unit;

public interface UnitConversionService {

    String convertUnitsInText(String searchText);

    Optional<Unit> getUnitBySymbol(String symbol);

    String getBaseUnitSymbolForUnitType(String unitType);

    BigDecimal convertToBaseUnit(Unit unit, String quantity);
}
