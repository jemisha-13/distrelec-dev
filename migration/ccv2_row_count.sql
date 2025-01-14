SELECT 
[Tables].name AS [TableName],
SUM([Partitions].[rows]) AS [TotalRowCount]
FROM sys.tables AS [Tables]
JOIN sys.partitions AS [Partitions]
ON [Tables].[object_id] = [Partitions].[object_id]
AND [Partitions].index_id IN ( 0, 1 )
WHERE SCHEMA_NAME(schema_id) = 'dbo'
GROUP BY SCHEMA_NAME(schema_id), [Tables].name
ORDER BY [Tables].name ASC;