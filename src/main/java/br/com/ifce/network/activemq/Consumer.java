package br.com.ifce.network.activemq;

import javax.jms.Message;
import javax.jms.*;
import java.util.List;

public class Consumer {

    private List<MessageListenerImpl> brokerListeners;

    private Listener listenerComponent;

    private Consumer() {
    }

    public static String newInstance(List<String> topicNames) {
        var consumer = new Consumer();
        consumer.brokerListeners = topicNames.stream().map(topic -> MessageListenerImpl.newInstance(consumer, topic)).toList();

        return BrokerMediator.getInstance().addConsumer(consumer);
    }

    public void onMessage(br.com.ifce.network.activemq.Message message) {
        listenerComponent.onMessage(message);
    }

    public void close() {
        brokerListeners.forEach(MessageListenerImpl::close);
    }

    public void setListenerComponent(Listener listenerComponent) {
        this.listenerComponent = listenerComponent;
    }

    public static class MessageListenerImpl implements MessageListener {
        private final Consumer parentConsumer;

        private final Session session;

        private final MessageConsumer messageConsumer;

        private final String topicName;

        private MessageListenerImpl(Consumer parentConsumer, Session session, MessageConsumer messageConsumer, String topicName) {
            this.parentConsumer = parentConsumer;
            this.session = session;
            this.messageConsumer = messageConsumer;
            this.topicName = topicName;
        }

        public static MessageListenerImpl newInstance(Consumer parentConsumer, String topicName) {
            try {
                Connection connection = BrokerMediator.getInstance().getConnection();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createTopic(topicName);
                MessageConsumer subscriber = session.createConsumer(destination);
                MessageListenerImpl messageListener = new MessageListenerImpl(parentConsumer, session, subscriber, topicName);
                subscriber.setMessageListener(messageListener);

                return messageListener;
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onMessage(Message message) {
            try {
                var textMessage = new br.com.ifce.network.activemq.Message(topicName, ((TextMessage) message).getText());
                parentConsumer.onMessage(textMessage);
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }

        public void close() {
            try {
                messageConsumer.close();
                session.close();
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
