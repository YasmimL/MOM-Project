package br.com.ifce.network.activemq;

import javax.jms.*;

public final class Producer {
    private final Session session;

    private final MessageProducer publisher;

    private Producer(Session session, MessageProducer publisher) {
        this.session = session;
        this.publisher = publisher;
    }

    public static Producer newInstance(String topicName) {
        try {
            BrokerMediator mediator = BrokerMediator.getInstance();
            Connection connection = mediator.getConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(topicName);
            MessageProducer publisher = session.createProducer(destination);
            Producer producer = new Producer(session, publisher);
            mediator.addProducer(topicName, producer);

            return producer;
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String text) {
        try {
            TextMessage message = session.createTextMessage();
            message.setText(text);
            publisher.send(message);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            publisher.close();
            session.close();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
