import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.SensorData;
import services.SensorDataProducer;
import services.SensorReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SayTestHello {
    public static void main(String[] args) throws InterruptedException, IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        SensorReceiver receiver = new SensorReceiver("localhost:9092", "aggregator", "test");
        executorService.execute(receiver);
        SensorDataProducer sensorDataProducer = new SensorDataProducer("localhost:9092");
        while (true) {
            List<String> data = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            for (int i=0; i<10; i++) {
                data.add(objectMapper.writeValueAsString(new SensorData(random_float(28, 32), random_float(60, 70), random_float(9, 10))));
            }
            sensorDataProducer.send("test", data);
            Thread.sleep(1000);
        }
    }

    private static float random_float(float min, float max) {
        return min + (float) Math.random() * (max - min);
    }
}
