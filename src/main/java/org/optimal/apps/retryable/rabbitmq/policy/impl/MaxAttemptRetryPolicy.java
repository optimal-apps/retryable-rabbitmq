package org.optimal.apps.retryable.rabbitmq.policy.impl;

import org.optimal.apps.retryable.rabbitmq.aspect.RetryableRabbitHandler;
import org.optimal.apps.retryable.rabbitmq.domain.impl.RetryableMessage;
import org.optimal.apps.retryable.rabbitmq.policy.RetryPolicy;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Component
public class MaxAttemptRetryPolicy implements RetryPolicy {

    @Override
    public boolean shouldRetry(RetryableRabbitHandler annotation, Message message, Throwable exception) {
        return ((RetryableMessage) message).getAttempt() <= annotation.maxAttempts();
    }
}