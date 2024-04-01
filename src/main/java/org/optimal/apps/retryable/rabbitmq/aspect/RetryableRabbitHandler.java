package org.optimal.apps.retryable.rabbitmq.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RabbitHandler
public @interface RetryableRabbitHandler {

    /**
     * Maximum number of attempts to retry the message before moving the message to dlq. Defaults to 3.
     *
     * @return maximum number of attempts
     */
    int maxAttempts() default 3;

    /**
     * Exception types that are retryable. Defaults to empty (and, if noRetryFor is also empty, all exceptions are retried).
     *
     * @return exception types to retry
     */
    Class<? extends Throwable>[] retryFor() default {};

    /**
     * Exception types that are not retryable. Defaults to empty (and, if retryFor is also empty, all exceptions are retried). If retryFor is empty
     * but noRetryFor is not, all other exceptions are retried
     *
     * @return exception types not to retry
     */
    Class<? extends Throwable>[] noRetryFor() default {};
}
