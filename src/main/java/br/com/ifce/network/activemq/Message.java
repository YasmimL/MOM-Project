package br.com.ifce.network.activemq;

public record Message(String topic, String text) {
    @Override
    public String toString() {
        if (topic == null) return text;
        return topic + ": " + text;
    }
}
