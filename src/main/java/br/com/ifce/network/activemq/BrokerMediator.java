package br.com.ifce.network.activemq;

import br.com.ifce.model.Parameter;
import br.com.ifce.model.Sensor;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.activemq.ActiveMQConnection.*;

public final class BrokerMediator {
    private static final BrokerMediator INSTANCE = new BrokerMediator();

    private final Connection connection;

    private final Map<String, Producer> producers = new HashMap<>();

    private final Map<String, Consumer> consumers = new HashMap<>();

    private final List<Sensor> sensors = new ArrayList<>();

    public static BrokerMediator getInstance() {
        return INSTANCE;
    }

    private BrokerMediator() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(DEFAULT_BROKER_URL);
            this.connection = connectionFactory.createConnection(DEFAULT_USER, DEFAULT_PASSWORD);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        try {
            connection.start();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            producers.values().forEach(Producer::close);
            consumers.values().forEach(Consumer::close);
            connection.close();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void addProducer(String topicName, Producer producer) {
        producers.put(topicName, producer);
    }

    public String addConsumer(Consumer consumer) {
        var consumerId = "Consumer " + (consumers.size() + 1);
        consumers.put(consumerId, consumer);

        return consumerId;
    }

    public Consumer getConsumer(String consumerId) {
        return consumers.get(consumerId);
    }

    public void closeConsumer(String consumerId) {
        if (consumers.containsKey(consumerId)) consumers.get(consumerId).close();
    }

    public List<String> getTopics() {
        return producers.keySet().stream().toList();
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void addSensor(Parameter parameter, Double minLimit, Double maxLimit, Double currentValue) {
        String topicName = this.generateTopicName(parameter);
        Sensor sensor = new Sensor(topicName, parameter, minLimit, maxLimit, currentValue);
        this.sensors.add(sensor);
        this.producers.put(topicName, Producer.newInstance(topicName));
        this.verifySensor(sensor);
    }

    public void replaceSensor(int index, Sensor sensor) {
        if (!this.producers.containsKey(sensor.topicName())) return;
        this.sensors.set(index, sensor);
        this.verifySensor(sensor);
    }

    private void verifySensor(Sensor sensor) {
        if (sensor.isMinLimitExceeded()) {
            this.producers.get(sensor.topicName()).sendMessage("minimum limit has been exceeded");
        }
        if (sensor.isMaxLimitExceeded()) {
            this.producers.get(sensor.topicName()).sendMessage("maximum limit has been exceeded");
        }
    }

    public String generateTopicName(Parameter parameter) {
        var foundSensors = this.sensors.stream().filter(it -> it.parameter() == parameter).toList();
        return "SENSOR-" + parameter.getText().toUpperCase() + "-" + (foundSensors.size() + 1);
    }
}
