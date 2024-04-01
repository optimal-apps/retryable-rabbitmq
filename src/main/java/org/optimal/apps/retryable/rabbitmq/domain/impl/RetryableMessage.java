package org.optimal.apps.retryable.rabbitmq.domain.impl;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

@Slf4j
public class RetryableMessage extends Message {

    private final Message message;

    public RetryableMessage(Message message) {
        super(message.getBody(), message.getMessageProperties());
        this.message = message;
    }

    public long getAttempt() {
        try {
            long attempt = Optional.ofNullable(this.message.getMessageProperties())
                .map(MessageProperties::getHeaders)
                .map(headers -> headers.get("x-retry-count"))
                .map(Long.class::cast)
                .orElse(0L);

            return ++attempt;
        } catch (ClassCastException e) {
            log.error(e.getMessage(), e);
            return 1;
        }
    }
}
