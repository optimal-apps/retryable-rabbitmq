package org.optimal.apps.retryable.rabbitmq.aspect.impl;

import org.optimal.apps.retryable.rabbitmq.aspect.RetryableRabbitHandler;
import org.optimal.apps.retryable.rabbitmq.domain.impl.RetryableMessage;
import org.optimal.apps.retryable.rabbitmq.policy.RetryPolicy;
import java.lang.reflect.Method;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RetryableRabbitHandlerAspect {

    private final RabbitTemplate rabbitTemplate;

    private final List<RetryPolicy> retryPolicies;

    @Around("@annotation(org.optimal.apps.retryable.rabbitmq.aspect.RetryableRabbitHandler)")
    public Object handleRabbitMessage(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            RetryableMessage message = this.extractMessage(joinPoint.getArgs());
            if (message == null) {
                return null;
            }

            RetryableRabbitHandler annotation = this.getRetryableAnnotation(joinPoint);

            boolean shouldRetry = retryPolicies.stream().allMatch(policy -> policy.shouldRetry(annotation, message, e));
            if (shouldRetry) {
                long retryCount = message.getAttempt();
                message.getMessageProperties().getHeaders().put("x-retry-count", retryCount);
                this.moveMessageToRetryQueue(message);
            } else {
                this.moveMessageToDLQ(message);
            }

            return null;
        }
    }

    private RetryableMessage extractMessage(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Message message) {
                return new RetryableMessage(message);
            }
        }

        return null;
    }

    private RetryableRabbitHandler getRetryableAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return method.getAnnotation(RetryableRabbitHandler.class);
    }

    private void moveMessageToRetryQueue(Message message) {
        MessageProperties messageProperties = message.getMessageProperties();
        rabbitTemplate.convertAndSend(messageProperties.getReceivedExchange(), messageProperties.getReceivedRoutingKey() + ".retry", message);
    }

    private void moveMessageToDLQ(Message message) {
        MessageProperties messageProperties = message.getMessageProperties();
        rabbitTemplate.convertAndSend(messageProperties.getReceivedExchange(), messageProperties.getReceivedRoutingKey() + ".dlq", message);
    }
}