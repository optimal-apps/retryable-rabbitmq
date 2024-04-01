package org.optimal.apps.retryable.rabbitmq.policy.impl;

import org.optimal.apps.retryable.rabbitmq.aspect.RetryableRabbitHandler;
import org.optimal.apps.retryable.rabbitmq.policy.RetryPolicy;
import java.util.Arrays;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

@Component
public class RetryForExceptionRetryPolicy implements RetryPolicy {

    @Override
    public boolean shouldRetry(RetryableRabbitHandler annotation, Message message, Throwable exception) {
        if (annotation.retryFor().length == 0) {
            return true;
        }

        return Arrays.stream(annotation.retryFor())
            .anyMatch(exceptionType -> exceptionType.isInstance(exception));
    }
}
