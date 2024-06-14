/*
List of all attributes assigned to the class-root classification.
Number of values assigned to a product is calculated, too. Localized attribute count multiple times.
ATTENTION: Execution time might be up to 30min!
*/
SELECT
    u.Classification,
    u.AttributeCode,
    SUM(u.NumOfAttributes)
FROM
({{
    SELECT
        {cc.code} AS Classification,
        {ca.code} AS AttributeCode,
        0 AS NumOfAttributes
    FROM
        {
        ClassificationAttribute AS ca
        JOIN ClassAttributeAssignment AS caa ON {caa.classificationAttribute}={ca.pk}
        JOIN ClassificationClass AS cc ON {cc.pk}={caa.classificationClass}
        }
    WHERE
        {cc.code}='class-root'
}}
UNION ALL
{{
    SELECT
        {cc.code} AS Classification,
        {ca.code} AS AttributeCode,
        COUNT(*) AS NumOfAttributes
    FROM
        {
        ClassificationAttribute AS ca
        JOIN ClassAttributeAssignment AS caa ON {caa.classificationAttribute}={ca.pk}
        JOIN ClassificationClass AS cc ON {cc.pk}={caa.classificationClass}
        JOIN ProductFeature AS pf ON {pf.classificationAttributeAssignment}={caa.pk}
        }
    WHERE
        {cc.code}='class-root'
    GROUP BY
        {cc.code},
        {ca.code}
}}) u
WHERE
    u.Classification='class-root'
GROUP BY
    u.Classification,
    u.AttributeCode
ORDER BY
    u.Classification,
    u.AttributeCode



/*
Attributes that are assigned to the class-root classification only (and not part of any other classification class).
*/
SELECT
    u.Classification,
    u.AttributeCode,
    SUM(u.NumOfAttributes)
FROM
({{
    SELECT
        {cc.code} AS Classification,
        {ca.code} AS AttributeCode,
        0 AS NumOfAttributes
    FROM
        {
        ClassificationAttribute AS ca
        JOIN ClassAttributeAssignment AS caa ON {caa.classificationAttribute}={ca.pk}
        JOIN ClassificationClass AS cc ON {cc.pk}={caa.classificationClass}
        }
    WHERE
        {cc.code}='class-root'
}}
UNION ALL
{{
    SELECT
        {cc.code} AS Classification,
        {ca.code} AS AttributeCode,
        COUNT(*) AS NumOfAttributes
    FROM
        {
        ClassificationAttribute AS ca
        JOIN ClassAttributeAssignment AS caa ON {caa.classificationAttribute}={ca.pk}
        JOIN ClassificationClass AS cc ON {cc.pk}={caa.classificationClass}
        JOIN ProductFeature AS pf ON {pf.classificationAttributeAssignment}={caa.pk}
        }
    WHERE
        {cc.code}='class-root'
    GROUP BY
        {cc.code},
        {ca.code}
}}) u
WHERE
    u.Classification='class-root' AND
	u.AttributeCode NOT IN ({{
		SELECT
			{ca.code} AS AttributeCode
		FROM
			{
			ClassificationAttribute AS ca
			JOIN ClassAttributeAssignment AS caa ON {caa.classificationAttribute}={ca.pk}
			JOIN ClassificationClass AS cc ON {cc.pk}={caa.classificationClass}
			}
		GROUP BY
			{ca.code}
		HAVING
			count(*) > 1
	}})
GROUP BY
    u.Classification,
    u.AttributeCode
ORDER BY
    u.Classification,
    u.AttributeCode


/*
Number of attribute values for a given attribute. (attribute can be part of multiple classification classes)
*/
SELECT
	count(*)
FROM
	{
	ClassificationAttribute AS ca
	JOIN ClassAttributeAssignment AS caa ON{caa.classificationAttribute}={ca.pk}
	JOIN ClassificationClass AS cc ON {cc.pk}={caa.classificationClass}
	JOIN ProductFeature AS pf ON {pf.classificationAttributeAssignment}={caa.pk}
	}
WHERE
	{ca.code} = 'B_mm'


/*
Number of ProductFeatures per language
*/
SELECT
	COUNT({pf.pk}),
	{l.isocode}
FROM
	{
	ProductFeature AS pf
	LEFT JOIN Language AS l ON {l.pk}={pf.language}
	}
GROUP BY
	{l.isocode}
	