package org.optimal.apps.retryable.rabbitmq.config;

import java.util.Map;
import org.optimal.apps.retryable.rabbitmq.domain.RabbitQueueConfig;
import org.optimal.apps.retryable.rabbitmq.domain.impl.RabbitQueueConfigImpl;

public interface RetryableRabbitmqProperties {

    Map<String, RabbitQueueConfig> getRabbitmq();

    void setRabbitmq(Map<String, RabbitQueueConfigImpl> rabbitmqImpl);
}
