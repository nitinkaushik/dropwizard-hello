package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.SensorData;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class SensorReceiver implements Runnable{
    private KafkaConsumer kafkaConsumer;

    public SensorReceiver(String bootstrap_servers, String group_id, String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group_id);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 600);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        KafkaConsumer<Long, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
        kafkaConsumer = consumer;
    }

    public void run() {
        System.out.println("Strating receiver");
        while (true) {
            List<SensorData> sensorDataList = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            ConsumerRecords<Long, String> consumerRecords = kafkaConsumer.poll(1000);
            for (ConsumerRecord<Long, String> consumerRecord : consumerRecords) {
                try {
                    sensorDataList.add(objectMapper.readValue(consumerRecord.value(), SensorData.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                if (sensorDataList.size() > 0) {
                    System.out.println(objectMapper.writeValueAsString(new SensorData(get_avg_temp(sensorDataList), get_avg_moisture(sensorDataList), get_avg_ph(sensorDataList))));
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    private float get_avg_temp(List<SensorData> sensorDataList) {
        float sum = 0;
        for (SensorData sensorData : sensorDataList) {
            sum += sensorData.getTemparature();
        }
        return sum/sensorDataList.size();
    }
    private float get_avg_moisture(List<SensorData> sensorDataList) {
        float sum = 0;
        for (SensorData sensorData : sensorDataList) {
            sum += sensorData.getMoisture();
        }
        return sum/sensorDataList.size();
    }
    private float get_avg_ph(List<SensorData> sensorDataList) {
        float sum = 0;
        for (SensorData sensorData : sensorDataList) {
            sum += sensorData.getPh();
        }
        return sum/sensorDataList.size();
    }
}
