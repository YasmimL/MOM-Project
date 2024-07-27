package br.com.ifce.network.activemq;

public record Message(String topic, String text) {
    @Override
    public String toString() {
        return topic + ": " + text;
    }
}
