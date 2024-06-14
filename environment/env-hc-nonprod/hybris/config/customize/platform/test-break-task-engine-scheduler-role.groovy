import de.hybris.platform.core.Registry;
import de.hybris.platform.core.TenantAwareThreadFactory;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.task.impl.AuxiliaryTablesGatewayFactory;
import de.hybris.platform.task.impl.gateways.SchedulerState;
import de.hybris.platform.task.impl.gateways.SchedulerStateGateway;
import de.hybris.platform.task.impl.gateways.TasksQueueGateway;
import de.hybris.platform.util.MessageFormatUtils;

import java.time.Instant;
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

int numOfTasks = 10000;
int numOfThreads = 10;


def tenant = Registry.getCurrentTenant()
def jaloSession = JaloSession.getCurrentSession()
def threadFactory = new TenantAwareThreadFactory(tenant, jaloSession)

List<Callable> tasks = new ArrayList<>(numOfTasks);

AuxiliaryTablesGatewayFactory gatewayFactory = spring.getBean("auxiliaryTablesGatewayFactory");
SchedulerStateGateway schedulerStateGateway = gatewayFactory.getSchedulerStateGateway();
TasksQueueGateway tasksQueueGateway = gatewayFactory.getTasksQueueGateway();
TypeService typeService = spring.getBean(TypeService.class);

for (int taskId = 0 ; taskId < numOfTasks ; taskId++) {
    Callable callable = new Callable() {
        @Override
        Object call() throws Exception {
            Optional<SchedulerState> result = schedulerStateGateway.getSchedulerTimestamp();
            if (result.isPresent()) {
                SchedulerState schedulerState = result.get();
                Instant schedulerTimestamp = schedulerState.getLastActiveTs();
                Instant dbNow = schedulerState.getDbNow();
                boolean lockAcquired = schedulerStateGateway.updateSchedulerRow(dbNow, schedulerTimestamp);
                println("Lock aquired: " + schedulerTimestamp + " - " + dbNow + " - " + lockAcquired);
                if (lockAcquired) {
                    int rangeStart = 0;
                    int rangeEnd = 1000;
                    String tasksQuery = getTasksQuery(rangeStart, rangeEnd);
                    String expiredTasksQuery = this.getExpiredTasksQuery(rangeStart, rangeEnd);
                    Instant now = Instant.now();
                    tasksQueueGateway.addTasks(tasksQuery, expiredTasksQuery, now, rangeStart, rangeEnd);
                }
            }
            return null;
        }
    }
    tasks.add(callable);
}

ExecutorService threadPool = Executors.newFixedThreadPool(numOfThreads, threadFactory);
threadPool.invokeAll(tasks);

protected String getExpiredTasksQuery(int rangeStart, int rangeEnd) {
    String randomRangeSQLExpression = tasksQueueGateway.getRangeSQLExpression(rangeStart, rangeEnd);
    String nodeGroupExpression = tasksQueueGateway.defaultIfNull("p_nodeGroup", tasksQueueGateway.getEmptyGroupValue());
    String nodeIdExpression = tasksQueueGateway.defaultIfNull("p_nodeId", -1);
    return MessageFormatUtils.format("SELECT PK, {0} AS rangeCol, {5} AS nodeIdCol, {6} AS nodeGroupCol, {1}/1000/60 AS execTimeCol FROM {2} WHERE {3} = 0 AND {1} <= ? AND {4} = -1", randomRangeSQLExpression, this.getColumnName("Task", "expirationTimeMillis"), this.getTableName("Task"), this.getColumnName("Task", "failed"), this.getColumnName("Task", "runningOnClusterNode"), nodeIdExpression, nodeGroupExpression);
}

protected String getTasksQuery(int rangeStart, int rangeEnd) {
    String randomRangeSQLExpression = tasksQueueGateway.getRangeSQLExpression(rangeStart, rangeEnd);
    String nodeGroupExpression = tasksQueueGateway.defaultIfNull("p_nodeGroup", tasksQueueGateway.getEmptyGroupValue());
    String nodeIdExpression = tasksQueueGateway.defaultIfNull("p_nodeId", -1);
    String conditionSubQuery = MessageFormatUtils.format("SELECT p_task FROM {0} WHERE {1} =t.PK AND {2} = 0", this.getTableName("TaskCondition"), this.getColumnName("TaskCondition", "task"), this.getColumnName("TaskCondition", "fulfilled"));
    return MessageFormatUtils.format("SELECT PK, {0} AS rangeCol, {6} AS nodeIdCol, {7} AS nodeGroupCol, {1}/1000/60 AS execTimeCol FROM {2} t WHERE {3} = 0  AND {1} <= ? AND {4} = -1 AND NOT EXISTS ({5})", randomRangeSQLExpression, this.getColumnName("Task", "executionTimeMillis"), this.getTableName("Task"), this.getColumnName("Task", "failed"), this.getColumnName("Task", "runningOnClusterNode"), conditionSubQuery, nodeIdExpression, nodeGroupExpression);
}

private String getTableName(String code) {
    ComposedTypeModel type = typeService.getComposedTypeForCode(code);
    return type.getTable();
}

private String getColumnName(String code, String column) {
    ComposedTypeModel type = typeService.getComposedTypeForCode(code);
    for (AttributeDescriptorModel attributeDescriptorModel : type.getDeclaredattributedescriptors()) {
        if (attributeDescriptorModel.getQualifier().equalsIgnoreCase(column)) {
            return attributeDescriptorModel.getDatabaseColumn();
        }
    }
    throw new IllegalArgumentException("type " + code + " is not recognizable");
}