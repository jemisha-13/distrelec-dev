UPDATE [dbo].[cronjobs] SET [p_nodeid] = NULL, [p_nodegroup] = 'integration' WHERE [p_nodeid] IS NOT NULL OR [p_nodegroup]is NULL
UPDATE [dbo].[jobs] SET [p_nodeid] = NULL WHERE [p_nodeid] IS NOT NULL
UPDATE [dbo].[tasks] SET [p_nodeid] = NULL, [p_nodegroup] = 'integration' WHERE [TypePkString] = (SELECT PK FROM [dbo].[composedtypes] WHERE [InternalCode] = 'TriggerTask') AND [p_nodeid] IS NOT NULL OR [p_nodegroup] IS NULL;
delete from applicationresourcelock
TRUNCATE TABLE patchexecutionunits
TRUNCATE TABLE patchexecutions