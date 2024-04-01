package org.optimal.apps.retryable.rabbitmq.config;

import java.util.function.Supplier;
import org.optimal.apps.retryable.rabbitmq.domain.RabbitQueueConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Order
@Configuration
public class RetryableRabbitMQRegisterer {

    private final RetryableRabbitmqProperties retryableRabbitMQProperties;

    private final ConfigurableApplicationContext applicationContext;

    public RetryableRabbitMQRegisterer(RetryableRabbitmqProperties retryableRabbitMQProperties,
        ConfigurableApplicationContext applicationContext) {
        this.retryableRabbitMQProperties = retryableRabbitMQProperties;
        this.applicationContext = applicationContext;

        this.init();
    }

    protected void init() {
        retryableRabbitMQProperties.getRabbitmq().forEach((key, queueConfig) -> {
            DirectExchange exchange = this.createExchange(queueConfig.getExchangeName());

            Queue queue = this.createQueue(queueConfig);
            Queue retryQueue = this.createRetryQueue(queueConfig);
            Queue dlqQueue = this.createDeadLetterQueue(queueConfig);

            this.registerBeans(exchange, queue, retryQueue, dlqQueue, queueConfig.getRoutingKey());
        });
    }

    public DirectExchange createExchange(String exchangeName) {
        return new DirectExchange(exchangeName);
    }

    public Queue createQueue(RabbitQueueConfig queueConfig) {
        QueueBuilder queueBuilder = QueueBuilder.durable(queueConfig.getQueueName());
        queueConfig.getArguments().forEach(queueBuilder::withArgument);

        return queueBuilder.build();
    }

    public Queue createRetryQueue(RabbitQueueConfig queueConfig) {
        QueueBuilder queueBuilder = QueueBuilder.durable(queueConfig.getRetryQueueName());
        queueConfig.getRetryQueueArguments().ifPresent(queueBuilder::withArguments);

        return queueBuilder.build();
    }

    public Queue createDeadLetterQueue(RabbitQueueConfig queueConfig) {
        QueueBuilder queueBuilder = QueueBuilder.durable(queueConfig.getDeadLetterQueueName());
        queueConfig.getDeadLetterQueueArguments().ifPresent(queueBuilder::withArguments);

        return queueBuilder.build();
    }

    private void registerBeans(DirectExchange exchange, Queue queue, Queue retryQueue, Queue dlqQueue, String routingKey) {
        BeanDefinition exchangeDefinition = BeanDefinitionBuilder.genericBeanDefinition(DirectExchange.class, () -> exchange).getBeanDefinition();

        BeanDefinition queueDefinition = BeanDefinitionBuilder.genericBeanDefinition(Queue.class, () -> queue).getBeanDefinition();

        BeanDefinition retryQueueDefinition = BeanDefinitionBuilder.genericBeanDefinition(Queue.class, () -> retryQueue).getBeanDefinition();

        BeanDefinition dlqQueueDefinition = BeanDefinitionBuilder.genericBeanDefinition(Queue.class, () -> dlqQueue).getBeanDefinition();

        BeanDefinitionBuilder queueBinder = BeanDefinitionBuilder.genericBeanDefinition(Binding.class, createBinding(queue, exchange, routingKey));

        BeanDefinitionBuilder retryQueueBinder = BeanDefinitionBuilder.genericBeanDefinition(Binding.class,
            createBinding(retryQueue, exchange, routingKey + ".retry"));

        BeanDefinitionBuilder dlqQueueBinder = BeanDefinitionBuilder.genericBeanDefinition(Binding.class,
            createBinding(dlqQueue, exchange, routingKey + ".dlq"));

        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        beanDefinitionRegistry.registerBeanDefinition(exchange.getName(), exchangeDefinition);

        beanDefinitionRegistry.registerBeanDefinition(queue.getName(), queueDefinition);
        beanDefinitionRegistry.registerBeanDefinition(retryQueue.getName(), retryQueueDefinition);
        beanDefinitionRegistry.registerBeanDefinition(dlqQueue.getName(), dlqQueueDefinition);

        beanDefinitionRegistry.registerBeanDefinition(queue.getName() + "Binding", queueBinder.getBeanDefinition());
        beanDefinitionRegistry.registerBeanDefinition(retryQueue.getName() + "Binding", retryQueueBinder.getBeanDefinition());
        beanDefinitionRegistry.registerBeanDefinition(dlqQueue.getName() + "Binding", dlqQueueBinder.getBeanDefinition());
    }

    public static Supplier<Binding> createBinding(Queue queue, Exchange exchange, String routingKey) {
        return () -> BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
    }
}
