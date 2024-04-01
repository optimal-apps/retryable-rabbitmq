package org.optimal.apps.retryable.rabbitmq.domain;

import java.util.Map;
import java.util.Optional;

public interface RabbitQueueConfig {

    String getExchangeName();

    String getQueueName();

    String getRetryQueueName();

    String getDeadLetterQueueName();

    String getRoutingKey();

    Map<String, Object> getArguments();

    Optional<Map<String, Object>> getRetryQueueArguments();

    Optional<Map<String, Object>> getDeadLetterQueueArguments();
}