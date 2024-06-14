/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export.query;

import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchQueryCreator;
import com.namics.distrelec.b2b.core.model.DistPimCategoryTypeModel;

import com.namics.distrelec.b2b.core.util.DistSqlUtils;
import de.hybris.platform.catalog.enums.ClassificationAttributeVisibilityEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.link.LinkModel;
import org.springframework.beans.factory.annotation.Autowired;

import static com.namics.distrelec.b2b.core.inout.export.impl.DistDefaultCsvTransformationService.ENCODE_URL_SUFFIX;

/**
 * Query builder implementation to create Attribute export query.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistFactFinderAttributeExportQueryCreator implements DistFlexibleSearchQueryCreator {

    // @formatter:off
    /*
        SELECT subselect.CategoryPath AS "CategoryPath_encodeurl", STRING_AGG(REPLACE(REPLACE(REPLACE(subselect.AttributeName, '|', '%7C'), '#', '%23'), '=', '%3D'), '|') WITHIN GROUP (ORDER BY subselect.Position) AS "Attributes"
        FROM (
            SELECT u.CategoryPath, u.AttributeName, min(u.Position) Position
            FROM (
                {{
                    SELECT CONCAT({c1.name:o} , '/'
                            , {c2.name:o} , '/'
                            , {c3.name:o} , '/'
                            , {c4.name:o}) CategoryPath,
                        {ca.name} AttributeName, {caa.position} Position
                    FROM {Category! AS c1
                        JOIN CategoryCategoryRelation AS ccr12 ON {c1.pk} = {ccr12.source}
                        JOIN Category! AS c2 ON {ccr12.target} = {c2.pk}
                            JOIN CategoryCategoryRelation AS ccr23 ON {c2.pk} = {ccr23.source}
                            JOIN Category! AS c3 ON {ccr23.target} = {c3.pk}
                                JOIN CategoryCategoryRelation AS ccr34 ON {c3.pk} = {ccr34.source}
                                JOIN Category! AS c4 ON {ccr34.target} = {c4.pk}
                                    JOIN DistPimCategoryType AS dpct4 ON {c4.pimCategoryType} = {dpct4.pk}
                                    JOIN CategoryCategoryRelation AS ccrCla ON {c4.pk} = {ccrCla.target}
                                        JOIN ClassificationClass AS cc ON {ccrCla.source} = {cc.pk}
                                            JOIN ClassAttributeAssignment AS caa ON {cc.pk} = {caa.classificationClass}
                                                JOIN ClassificationAttribute AS ca ON {caa.classificationAttribute} = {ca.pk}
                                                JOIN ClassificationAttributeVisibilityEnum AS cave ON {caa.visibility} = {cave.pk}
                    }
                    WHERE {dpct4.visible} = 1
                        AND {cave.code} in ('a_visibility', 'b_visibility', 'c_visibility')
                        AND {c1.level} = 1
                }}
                UNION
                {{
                    SELECT CONCAT({c1.name:o} , '/'
                            , {c2.name:o} , '/'
                            , {c3.name:o} , '/'
                            , {c4.name:o} , '/'
                            , {c5.name:o}) CategoryPath,
                        {ca.name:o} AttributeName, {caa.position} Position
                    FROM {Category! AS c1
                        JOIN CategoryCategoryRelation AS ccr12 ON {c1.pk} = {ccr12.source}
                        JOIN Category! AS c2 ON {ccr12.target} = {c2.pk}
                            JOIN CategoryCategoryRelation AS ccr23 ON {c2.pk} = {ccr23.source}
                            JOIN Category! AS c3 ON {ccr23.target} = {c3.pk}
                                JOIN CategoryCategoryRelation AS ccr34 ON {c3.pk} = {ccr34.source}
                                JOIN Category! AS c4 ON {ccr34.target} = {c4.pk}
                                    JOIN CategoryCategoryRelation AS ccr45 ON {c4.pk} = {ccr45.source}
                                    JOIN Category! AS c5 ON {ccr45.target} = {c5.pk}
                                        JOIN DistPimCategoryType AS dpct5 ON {c5.pimCategoryType} = {dpct5.pk}
                                        JOIN CategoryCategoryRelation AS ccrCla ON {c5.pk} = {ccrCla.target}
                                            JOIN ClassificationClass AS cc ON {ccrCla.source} = {cc.pk}
                                                JOIN ClassAttributeAssignment AS caa ON {cc.pk} = {caa.classificationClass}
                                                    JOIN ClassificationAttribute AS ca ON {caa.classificationAttribute} = {ca.pk}
                                                    JOIN ClassificationAttributeVisibilityEnum AS cave ON {caa.visibility} = {cave.pk}
                    }
                    WHERE {dpct5.visible} = 1
                        AND {cave.code} in ('a_visibility', 'b_visibility', 'c_visibility')
                        AND {c1.level} = 1
                }}
            ) u
            GROUP BY u.CategoryPath, u.AttributeName
        ) subselect
        GROUP BY subselect.CategoryPath
    */
    // @formatter:on

    @Autowired
    private DistSqlUtils distSqlUtils;

    @Override
    public String createQuery() {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT subselect.CategoryPath AS \"CategoryPath").append(ENCODE_URL_SUFFIX).append("\", ").append(stringAgg("REPLACE(REPLACE(REPLACE(subselect.AttributeName, '|', '%7C'), '#', '%23'), '=', '%3D')", "|", "subselect.Position")).append(" AS \"Attributes\" ");
        query.append("FROM ( ");
        query.append("  SELECT u.CategoryPath, u.AttributeName, min(u.Position) Position ");
        query.append("  FROM ( ");
        query.append("    {{ ");
        appendSubSelectCategoryLevel4(query);
        query.append("    }} ");
        query.append("    UNION ");
        query.append("    {{ ");
        appendSubSelectCategoryLevel5(query);
        query.append("    }} ");
        query.append("  ) u ");
        query.append("  GROUP BY u.CategoryPath, u.AttributeName ");
        query.append(") subselect ");
        query.append("GROUP BY subselect.CategoryPath ");
        return query.toString();
    }

    private void appendSubSelectCategoryLevel4(final StringBuilder query) {
        query.append("SELECT ").append(distSqlUtils.concat("{c1.".concat(CategoryModel.NAME).concat(":o}"), "'/'",
                "{c2.".concat(CategoryModel.NAME).concat(":o}"), "'/'",
                "{c3.".concat(CategoryModel.NAME).concat(":o}"), "'/'",
                "{c4.".concat(CategoryModel.NAME).concat(":o}"))).append("CategoryPath, ");
        query.append("  {ca.").append(ClassificationAttributeModel.NAME).append(":o} AttributeName, {caa.").append(ClassAttributeAssignmentModel.POSITION)
                .append("} Position ");
        query.append("FROM {").append(CategoryModel._TYPECODE).append("! AS c1 ");
        joinCategory(query, 1, 2);
        joinCategory(query, 2, 3);
        joinCategory(query, 3, 4);
        query.append("  JOIN ").append(DistPimCategoryTypeModel._TYPECODE).append(" AS dpct4 ON {c4.").append(CategoryModel.PIMCATEGORYTYPE)
                .append("} = {dpct4.pk} ");
        joinClassificationClass(query, 4);
        query.append("} ");
        query.append("WHERE {dpct4.").append(DistPimCategoryTypeModel.VISIBLE).append("} = 1 ");
        query.append("  AND {cave.code} in ('").append(ClassificationAttributeVisibilityEnum.A_VISIBILITY.getCode()).append("', ");
        query.append("    '").append(ClassificationAttributeVisibilityEnum.B_VISIBILITY.getCode()).append("', ");
        query.append("    '").append(ClassificationAttributeVisibilityEnum.C_VISIBILITY.getCode()).append("') ");
        query.append("  AND {c1.").append(CategoryModel.LEVEL).append("} = 1 ");
    }

    private void appendSubSelectCategoryLevel5(final StringBuilder query) {
        query.append("      SELECT ").append(distSqlUtils.concat("{c1.".concat(CategoryModel.NAME).concat(":o}"), "'/'",
                "{c2.".concat(CategoryModel.NAME).concat(":o}"), "'/'",
                "{c3.".concat(CategoryModel.NAME).concat(":o}"), "'/'",
                "{c4.".concat(CategoryModel.NAME).concat(":o}"), "'/'",
                "{c5.".concat(CategoryModel.NAME).concat(":o}"))).append(" CategoryPath, ");
        query.append("        {ca.").append(ClassificationAttributeModel.NAME).append(":o} AttributeName, {caa.")
                .append(ClassAttributeAssignmentModel.POSITION).append("} Position ");
        query.append("      FROM {").append(CategoryModel._TYPECODE).append("! AS c1 ");
        joinCategory(query, 1, 2);
        joinCategory(query, 2, 3);
        joinCategory(query, 3, 4);
        joinCategory(query, 4, 5);
        query.append("        JOIN ").append(DistPimCategoryTypeModel._TYPECODE).append(" AS dpct5 ON {c5.").append(CategoryModel.PIMCATEGORYTYPE)
                .append("} = {dpct5.pk} ");
        joinClassificationClass(query, 5);
        query.append("      } ");
        query.append("      WHERE {dpct5.").append(DistPimCategoryTypeModel.VISIBLE).append("} = 1 ");
        query.append("        AND {cave.code} in ('").append(ClassificationAttributeVisibilityEnum.A_VISIBILITY.getCode()).append("', ");
        query.append("          '").append(ClassificationAttributeVisibilityEnum.B_VISIBILITY.getCode()).append("', ");
        query.append("          '").append(ClassificationAttributeVisibilityEnum.C_VISIBILITY.getCode()).append("') ");
        query.append("        AND {c1.").append(CategoryModel.LEVEL).append("} = 1 ");
    }

    private StringBuilder joinCategory(final StringBuilder query, final int fromLevel, final int toLevel) {
        final String fromTo = Integer.toString(fromLevel) + Integer.toString(toLevel);
        query.append("JOIN ").append(CategoryModel._CATEGORYCATEGORYRELATION).append(" AS ccr" + fromTo + " ");
        query.append("  ON {c" + fromLevel + ".").append(CategoryModel.PK).append("} = {ccr" + fromTo + ".").append(LinkModel.SOURCE).append("} ");
        query.append("JOIN ").append(CategoryModel._TYPECODE).append("! AS c" + toLevel + " ");
        query.append("  ON {ccr" + fromTo + ".").append(LinkModel.TARGET).append("} = {c" + toLevel + ".").append(CategoryModel.PK).append("} ");
        return query;
    }

    private StringBuilder joinClassificationClass(final StringBuilder query, final int fromLevel) {
        query.append("JOIN ").append(CategoryModel._CATEGORYCATEGORYRELATION).append(" AS ccrCla ");
        query.append("  ON {c" + fromLevel + ".").append(CategoryModel.PK).append("} = {ccrCla.").append(LinkModel.TARGET).append("} ");
        query.append("  JOIN ").append(ClassificationClassModel._TYPECODE).append(" AS cc ");
        query.append("    ON {ccrCla.").append(LinkModel.SOURCE).append("} = {cc.").append(ClassificationClassModel.PK).append("} ");
        query.append("    JOIN ").append(ClassAttributeAssignmentModel._TYPECODE).append(" AS caa ");
        query.append("      ON {cc.").append(ClassificationClassModel.PK).append("} = {caa.").append(ClassAttributeAssignmentModel.CLASSIFICATIONCLASS)
                .append("} ");
        query.append("      JOIN ").append(ClassificationAttributeModel._TYPECODE).append(" AS ca ");
        query.append("        ON {caa.").append(ClassAttributeAssignmentModel.CLASSIFICATIONATTRIBUTE).append("} = {ca.")
                .append(ClassificationAttributeModel.PK).append("} ");
        query.append("      JOIN ").append(ClassificationAttributeVisibilityEnum._TYPECODE).append(" AS cave ");
        query.append("        ON {caa.").append(ClassAttributeAssignmentModel.VISIBILITY).append("} = {cave.pk} ");
        return query;
    }

    protected String stringAgg(String expression, String connector, String orderBy) {
        return distSqlUtils.stringAgg(expression, connector, orderBy);
    }
}
