package services;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.List;
import java.util.Properties;

public class SensorDataProducer {
    private KafkaProducer<String, String> kafkaProducer;

    public SensorDataProducer(String bootstrapServers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        kafkaProducer = new KafkaProducer(props);
    }

    public void send(String topicName, List<String> messageList) {
        for (String message: messageList) {
            kafkaProducer.send(new ProducerRecord(topicName, message));
        }
    }
}
