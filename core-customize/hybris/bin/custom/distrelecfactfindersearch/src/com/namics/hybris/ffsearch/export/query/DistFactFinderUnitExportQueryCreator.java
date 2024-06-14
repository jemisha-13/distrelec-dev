/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export.query;

import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchQueryCreator;

import com.namics.distrelec.b2b.core.util.DistSqlUtils;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import org.springframework.beans.factory.annotation.Autowired;

import static com.namics.distrelec.b2b.core.constants.DistConstants.FactFinder.FACTFINDER_UNIT_PREFIX;
import static com.namics.distrelec.b2b.core.inout.export.impl.DistDefaultCsvTransformationService.ENCODE_FF_SUFFIX;

/**
 * Creates a FlexibleSearch query to export units.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistFactFinderUnitExportQueryCreator implements DistFlexibleSearchQueryCreator {

    //@formatter:off
    /*
        SELECT {ca.name:o} || '~~' || replace({cauAll.symbol}, 'Ω', 'Ohm') AS "AttributeWithUnit_encodeff", {cauAll.conversionFactor} AS "ConversionFactor",
            {cauAll.unitType} AS "UnitType"
        FROM {ClassAttributeAssignment AS caa
                JOIN ClassificationAttribute AS ca ON {caa.classificationAttribute} = {ca.pk}
            JOIN ClassificationAttributeUnit AS cau ON {pf.unit} = {cau.pk}
                JOIN ClassificationAttributeUnit AS cauAll ON {cau.unitType} = {cauAll.unitType}
        }
        GROUP BY {ca.name:o} || '~~' || replace({cauAll.symbol}, 'Ω', 'Ohm'), {cauAll.conversionFactor}, {cauAll.unitType}
        ORDER BY {ca.name:o} || '~~' || replace({cauAll.symbol}, 'Ω', 'Ohm'), {cauAll.conversionFactor}, {cauAll.unitType}
    */

    @Autowired
    private DistSqlUtils distSqlUtils;

    @Override
    public String createQuery() {
        final StringBuilder query = new StringBuilder();

        query.append("SELECT ").append(distSqlUtils.concat("{ca.".concat(ClassificationAttributeModel.NAME).concat(":o}"), "'".concat(FACTFINDER_UNIT_PREFIX).concat("'"),
                "replace({cauAll.".concat(ClassificationAttributeUnitModel.SYMBOL).concat("}, ").concat(utf8("'Ω'")).concat(", 'Ohm')"))).append(" AS \"AttributeWithUnit").append(ENCODE_FF_SUFFIX).append("\", ");
        query.append("  {cauAll.").append(ClassificationAttributeUnitModel.CONVERSIONFACTOR).append("} AS \"ConversionFactor\", ");
        query.append("  {cauAll.").append(ClassificationAttributeUnitModel.UNITTYPE).append("} AS \"UnitType\" ");

        query.append("FROM {").append(ClassAttributeAssignmentModel._TYPECODE).append(" AS caa ");
        query.append("    JOIN ").append(ClassificationAttributeModel._TYPECODE).append(" AS ca ");
        query.append("      ON {caa.").append(ClassAttributeAssignmentModel.CLASSIFICATIONATTRIBUTE).append("} = {ca.").append(ClassificationAttributeModel.PK).append("} ");
        query.append("  JOIN ").append(ClassificationAttributeUnitModel._TYPECODE).append(" AS cau ");
        query.append("    ON {caa.").append(ClassAttributeAssignmentModel.UNIT).append("} = {cau.").append(ClassificationAttributeUnitModel.PK).append("} ");
        query.append("    JOIN ").append(ClassificationAttributeUnitModel._TYPECODE).append(" AS cauAll ");
        query.append("      ON {cau.").append(ClassificationAttributeUnitModel.UNITTYPE).append("} = {cauAll.").append(ClassificationAttributeUnitModel.UNITTYPE).append("} ");
        query.append("} ");

        query.append("GROUP BY ").append(distSqlUtils.concat("{ca.".concat(ClassificationAttributeModel.NAME).concat(":o}"), "'".concat(FACTFINDER_UNIT_PREFIX).concat("'"),
            "replace({cauAll.".concat(ClassificationAttributeUnitModel.SYMBOL).concat("}, ").concat(utf8("'Ω'")).concat(", 'Ohm')"))).append(", ");
        query.append("  {cauAll.").append(ClassificationAttributeUnitModel.CONVERSIONFACTOR).append("}, ");
        query.append("  {cauAll.").append(ClassificationAttributeUnitModel.UNITTYPE).append("} ");
        query.append("ORDER BY ").append(distSqlUtils.concat("{ca.".concat(ClassificationAttributeModel.NAME).concat(":o}"), "'".concat(FACTFINDER_UNIT_PREFIX).concat("'"),
                "replace({cauAll.".concat(ClassificationAttributeUnitModel.SYMBOL).concat("}, ").concat(utf8("'Ω'")).concat(", 'Ohm')"))).append(", ");
        query.append("  {cauAll.").append(ClassificationAttributeUnitModel.CONVERSIONFACTOR).append("}, ");
        query.append("  {cauAll.").append(ClassificationAttributeUnitModel.UNITTYPE).append("} ");

        return query.toString();
    } 
    //@formatter:on

    protected String utf8(String stringExpression) {
        return distSqlUtils.utf8(stringExpression);
    }
}
