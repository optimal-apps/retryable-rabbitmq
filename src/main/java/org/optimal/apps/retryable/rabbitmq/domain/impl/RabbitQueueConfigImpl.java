package org.optimal.apps.retryable.rabbitmq.domain.impl;

import org.optimal.apps.retryable.rabbitmq.domain.RabbitQueueConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RabbitQueueConfigImpl implements RabbitQueueConfig {

    private String queueName;
    private String exchangeName;
    private String routingKey;

    private Map<String, Object> arguments = new HashMap<>();

    private Map<String, Object> retryQueueArguments = new HashMap<>();
    private Map<String, Object> deadLetterQueueArguments = new HashMap<>();

    public String getRetryQueueName() {
        return this.queueName + ".retry";
    }

    public String getDeadLetterQueueName() {
        return this.queueName + ".dlq";
    }

    @Override
    public String getRoutingKey() {
        return Optional.ofNullable(this.routingKey).orElse("");
    }

    public Optional<Map<String, Object>> getRetryQueueArguments() {
        return Optional.ofNullable(retryQueueArguments);
    }

    public Optional<Map<String, Object>> getDeadLetterQueueArguments() {
        return Optional.ofNullable(deadLetterQueueArguments);
    }

    public void setQueueName(String queueName) {
        Assert.hasText(queueName, "Queue name must not be empty");
        this.queueName = queueName;
    }

    public void setExchangeName(String exchangeName) {
        Assert.hasText(exchangeName, "Exchange name must not be empty");
        this.exchangeName = exchangeName;
    }
}
