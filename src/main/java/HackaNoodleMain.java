import config.ServiceConfig;
import controllers.HelloWorldResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class HackaNoodleMain extends Application<ServiceConfig> {

    public static void main(String[] args) throws Exception {
        new HackaNoodleMain().run(args);
    }

    public void run(ServiceConfig serviceConfig, Environment environment) throws Exception {
        System.out.println("Starting application");
        environment.jersey().register(new HelloWorldResource(serviceConfig.getMessage()));
    }
}
