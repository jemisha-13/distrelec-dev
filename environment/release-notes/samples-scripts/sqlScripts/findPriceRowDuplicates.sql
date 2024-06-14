/*
List the number of ambiguous price definition.
*/
SELECT
  count(*)
FROM
({{
  SELECT
    count(*),
    {upg.code},
    {p.code},
    {c.isocode},
    {dpr.minqtd},
    {dpr.specialPrice}
  FROM
    {DistPriceRow AS dpr
    JOIN UserPriceGroup AS upg ON {upg.pk}={dpr.ug}
    JOIN Product AS p ON {p.pk}={dpr.product}
    JOIN Currency AS c ON {c.pk}={dpr.currency}}
  WHERE
    {upg.code} = 'SalesOrg_UPG_7710_01' AND
    sysdate BETWEEN {dpr.startTime} AND {dpr.endTime}
  GROUP BY
    {upg.code},
    {p.code},
    {c.isocode},
    {dpr.minqtd},
    {dpr.specialPrice}
  HAVING COUNT(*) > 1
  ORDER BY {p.code}
}})


/*
List the number of ambiguous price definition grouped by pricelist.
*/
SELECT
  count(*),
  g.pricelist
FROM
({{
  SELECT
    count({dpr.pk}),
    {upg.code} AS pricelist,
    {p.code},
    {c.isocode},
    {dpr.minqtd},
    {dpr.specialPrice}
  FROM
    {DistPriceRow AS dpr
    JOIN UserPriceGroup AS upg ON {upg.pk}={dpr.ug}
    JOIN Product AS p ON {p.pk}={dpr.product}
    JOIN Currency AS c ON {c.pk}={dpr.currency}}
  WHERE
    sysdate BETWEEN {dpr.startTime} AND {dpr.endTime}
  GROUP BY
    {upg.code},
    {p.code},
    {c.isocode},
    {dpr.minqtd},
    {dpr.specialPrice}
  HAVING COUNT(*) > 1
  ORDER BY {p.code}
}}) g
GROUP BY
  g.pricelist


/*
List all DistPriceRows that are part of a ambiguous price definition.
Mutliple DistPriceRows are currently (time) valid for the same combination of product, pricelist, scale and currency.
*/
SELECT
  {outer_dpr.pk}
FROM
  {DistPriceRow AS outer_dpr}
WHERE EXISTS
({{
  SELECT
    count({dpr.pk}),
    {dpr.ug},
    {dpr.product},
    {dpr.currency},
    {dpr.minqtd},
    {dpr.specialPrice}
  FROM
    {DistPriceRow AS dpr}
  WHERE
    sysdate BETWEEN {dpr.startTime} AND {dpr.endTime} AND
    {dpr.ug}={outer_dpr.ug} AND
    {dpr.product}={outer_dpr.product} AND
    {dpr.currency}={outer_dpr.currency} AND
    {dpr.minqtd}={outer_dpr.minqtd} AND
    {dpr.specialPrice}={outer_dpr.specialPrice}
  GROUP BY
    {dpr.ug},
    {dpr.product},
    {dpr.currency},
    {dpr.minqtd},
    {dpr.specialPrice}
  HAVING COUNT({dpr.pk}) > 1
}})


/*
Reset lastModifiedErp of all entries of a price list (UserPriceGroup)
*/
UPDATE
	PriceRows
SET
	p_lastmodifiederp = NULL
WHERE
	p_ug IN (SELECT pk FROM enumerationvalues WHERE Code = 'SalesOrg_UPG_7710_01') 


/*
Number of PriceRows not being updated so far.
*/
SELECT
	count(*)
FROM
	PriceRows
WHERE
	p_ug IN (SELECT pk FROM enumerationvalues WHERE Code = 'SalesOrg_UPG_7710_01') AND
	p_lastmodifiederp IS NULL


/*
Delete PriceRows not being updated so far.
==> Better to delete items in hMC to ensure cache is cleared as well and dependent objects are deleted, too.
*/
DELETE FROM
	PriceRows
WHERE
	p_ug IN (SELECT pk FROM enumerationvalues WHERE Code = 'SalesOrg_UPG_7710_01') AND
	p_lastmodifiederp IS NULL

	
/*
Report number of PriceRows per SalesOrg and Currency.
*/
SELECT
	{upg.code},
	{c.isocode},
	count({dpr.pk})
FROM
	{DistPriceRow AS dpr
	JOIN UserPriceGroup AS upg ON {upg.pk}={dpr.ug}
	JOIN Currency AS c ON {c.pk}={dpr.currency}}
GROUP BY
	{upg.code},
	{c.isocode}
