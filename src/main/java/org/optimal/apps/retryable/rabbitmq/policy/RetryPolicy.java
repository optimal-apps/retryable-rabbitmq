package org.optimal.apps.retryable.rabbitmq.policy;

import org.optimal.apps.retryable.rabbitmq.aspect.RetryableRabbitHandler;
import org.springframework.amqp.core.Message;

public interface RetryPolicy {

    boolean shouldRetry(RetryableRabbitHandler annotation, Message message, Throwable exception);
}
