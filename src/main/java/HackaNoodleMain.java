import config.ServiceConfig;
import controllers.HelloWorldResource;
import dao.RedisDaoImpl;
import controllers.SensorController;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import sensorRepo.SensorRepo;
import services.RedisDataService;
import services.RedisDataServiceImpl;
import models.SensorData;

public class HackaNoodleMain extends Application<ServiceConfig> {

    public static void main(String[] args) throws Exception {
        new HackaNoodleMain().run(args);
    }

    public void run(ServiceConfig serviceConfig, Environment environment) throws Exception {
        System.out.println("Starting application");
        RedisDaoImpl redisDao = new RedisDaoImpl();
        SensorRepo sensorRepo = new SensorRepo();
        RedisDataService redisDataService = new RedisDataServiceImpl(redisDao, sensorRepo);
        environment.jersey().register(new HelloWorldResource("message", redisDataService));
        environment.jersey().register(new SensorController());
    }
}
