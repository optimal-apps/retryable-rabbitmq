package org.optimal.apps.retryable.rabbitmq;

import org.optimal.apps.retryable.rabbitmq.aspect.RetryableRabbitHandler;
import org.optimal.apps.retryable.rabbitmq.aspect.impl.RetryableRabbitHandlerAspect;
import org.optimal.apps.retryable.rabbitmq.config.RetryableRabbitMQRegisterer;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackageClasses = {
    RetryableRabbitMQRegisterer.class,
    RetryableRabbitHandler.class,
    RetryableRabbitHandlerAspect.class,
})
public class RetryableRabbitMQAutoConfiguration {

}
