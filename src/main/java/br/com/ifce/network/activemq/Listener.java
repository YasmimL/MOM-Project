package br.com.ifce.network.activemq;

public interface Listener {
    void onMessage(Message message);
}
