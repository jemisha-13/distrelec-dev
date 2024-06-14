SELECT * FROM {B2BUnit AS unit
    JOIN CustomerType AS ct ON {unit.customerType}={ct.pk}
    JOIN DistSalesOrg AS so ON {unit.salesOrg}={so.pk}
    JOIN Country AS c ON {unit.country}={c.pk}}

WHERE {ct.code}='B2B'
AND {so.code}='7801'
AND {c.isocode}='FR'