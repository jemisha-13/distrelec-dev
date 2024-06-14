import org.springframework.integration.support.MessageBuilder;

controlBusChannel = ctx.getBean("operationChannel");


// --- stop polling PIM files ---
// controlBusChannel.send(MessageBuilder.withPayload("@'pimXmlFiles.adapter'.stop()").build())

// --- start polling PIM files ---
// controlBusChannel.send(MessageBuilder.withPayload("@'pimXmlFiles.adapter'.start()").build())


// --- stop polling Movex ERP files ---
// controlBusChannel.send(MessageBuilder.withPayload("@'movexAsciiBatchFiles.adapter'.stop()").build())

// --- start polling Movex ERP files ---
// controlBusChannel.send(MessageBuilder.withPayload("@'movexAsciiBatchFiles.adapter'.start()").build())


// --- stop polling SAP ERP files ---
// controlBusChannel.send(MessageBuilder.withPayload("@'erpBatchFiles.adapter'.stop()").build())

// --- start polling SAP ERP files ---
// controlBusChannel.send(MessageBuilder.withPayload("@'erpBatchFiles.adapter'.start()").build())
