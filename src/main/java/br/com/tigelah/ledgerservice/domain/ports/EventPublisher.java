package br.com.tigelah.ledgerservice.domain.ports;

public interface EventPublisher {
    void publish(String topic, String key, String payloadJson);
}
