package models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import constants.SensorType;
import dao.RedisDaoImpl;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import sensorRepo.SensorRepo;
import services.RedisDataService;
import services.RedisDataServiceImpl;

public class FieldSensor {
    private String id;
    private SensorData sensorData;
    private ExecutorService executorService = Executors.newFixedThreadPool(6);
    private boolean isRunning = Boolean.TRUE;
    private boolean isSprinklerRunning = Boolean.FALSE;
    private RedisDataService redisDataService;

    @JsonCreator
    public FieldSensor(@JsonProperty("id") String id, @JsonProperty("moisture") float moisture, @JsonProperty("temperature") float temperature, @JsonProperty("ph") float ph) {
        this.id = id;
        this.sensorData = new SensorData(temperature, moisture, ph);
        RedisDaoImpl redisDao = new RedisDaoImpl();
        SensorRepo sensorRepo = new SensorRepo();
        redisDataService = new RedisDataServiceImpl(redisDao, sensorRepo);
        executorService.execute(new TempChanger());
        executorService.execute(new MoistureChanger());
        executorService.execute(new PhChanger());
        executorService.execute(new Sender());
        executorService.execute(new Sprinkler());
        executorService.execute(new SensorReceiver("localhost:9092", "aggre", "test"));
    }

    public String getId() {
        return id;
    }

    public void stop() {
        isRunning = Boolean.FALSE;
    }

    public void setSprinklerRunning(Boolean sprinklerRunning) {
        isSprinklerRunning = sprinklerRunning;
    }

    private class TempChanger implements Runnable {
        @Override
        public void run() {
            while (isRunning) {
                float delta = (float) Math.random();
                if (Math.random() > 0.5) {
                    delta = delta * -1;
                }
                sensorData.setTemparature(sensorData.getTemparature() + delta);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MoistureChanger implements Runnable {
        @Override
        public void run() {
            while (isRunning) {
                if (sensorData.getTemparature() > 25 && sensorData.getMoisture() > 0) {
                    float delta = (float) (sensorData.getTemparature() - 25.0);
                    sensorData.setMoisture((float) (sensorData.getMoisture() - delta*0.01));
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class PhChanger implements Runnable {
        @Override
        public void run() {
            while (isRunning) {
                float delta = (float) Math.random();
                if (Math.random() > 0.5) {
                    delta = delta * -1;
                }
                delta = (float) (delta*0.1);
                sensorData.setPh(sensorData.getPh() + delta);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class Sender implements Runnable {
        private KafkaProducer kafkaProducer;
        private ObjectMapper objectMapper;

        public Sender() {
            Properties props = new Properties();
            props.put("bootstrap.servers", "localhost:9092");
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 33554432);
            props.put("key.serializer", StringSerializer.class.getName());
            props.put("value.serializer", StringSerializer.class.getName());
            kafkaProducer = new KafkaProducer(props);
            objectMapper = new ObjectMapper();
        }

        @Override
        public void run() {
            while (isRunning) {
                try {
                    kafkaProducer.send(new ProducerRecord("test", objectMapper.writeValueAsString(sensorData)));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class SensorReceiver implements Runnable{
        private KafkaConsumer kafkaConsumer;

        public SensorReceiver(String bootstrap_servers, String group_id, String topic) {
            Properties props = new Properties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers);
            props.put(ConsumerConfig.GROUP_ID_CONFIG, group_id);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024*1024*1024);
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
            KafkaConsumer<Long, String> consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Collections.singletonList(topic));
            kafkaConsumer = consumer;
        }

        public void run() {
            System.out.println("Starting receiver");
            while (isRunning) {
                List<SensorData> sensorDataList = new ArrayList<>();
                ObjectMapper objectMapper = new ObjectMapper();
                ConsumerRecords<Long, String> consumerRecords = kafkaConsumer.poll(10000);
                for (ConsumerRecord<Long, String> consumerRecord : consumerRecords) {
                    try {
                        sensorDataList.add(objectMapper.readValue(consumerRecord.value(), SensorData.class));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (sensorDataList.size() > 0) {
                        SensorData sensorData = new SensorData(get_avg_temp(sensorDataList), get_avg_moisture(sensorDataList), get_avg_ph(sensorDataList));
                        System.out.println(objectMapper.writeValueAsString(sensorData));
                        redisDataService.dumpData(Integer.parseInt(id), SensorType.MOISTURE, sensorData.getMoisture());
                        redisDataService.dumpData(Integer.parseInt(id), SensorType.TEMPRATURE, sensorData.getTemparature());
                        redisDataService.dumpData(Integer.parseInt(id), SensorType.PH, sensorData.getPh());
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                kafkaConsumer.commitSync();
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

    public class Sprinkler implements Runnable {

        @Override
        public void run() {
            while (true) {
                if (isSprinklerRunning && sensorData.getMoisture() < 100.0) {
                    sensorData.setMoisture((float) (sensorData.getMoisture() + 0.5));
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
