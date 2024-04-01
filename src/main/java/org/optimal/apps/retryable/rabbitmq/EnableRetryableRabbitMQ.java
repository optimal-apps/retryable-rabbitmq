package org.optimal.apps.retryable.rabbitmq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(RetryableRabbitMQAutoConfiguration.class)
public @interface EnableRetryableRabbitMQ {

}
