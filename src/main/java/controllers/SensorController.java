package controllers;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import constants.SensorType;
import dao.RedisDaoImpl;
import io.dropwizard.jersey.PATCH;
import models.FieldSensor;

import javax.ws.rs.*;
import java.util.HashMap;
import java.util.Map;
import sensorRepo.SensorRepo;
import services.RedisDataService;
import services.RedisDataServiceImpl;

@Path("/sensor")
public class SensorController {
    private Map<String, FieldSensor> sensorMap;
    RedisDataService redisDataService;

    public SensorController() {
        sensorMap = new HashMap<>();
        RedisDaoImpl redisDao = new RedisDaoImpl();
        SensorRepo sensorRepo = new SensorRepo();
        redisDataService = new RedisDataServiceImpl(redisDao, sensorRepo);
    }

    @POST
    public void onboardFieldSensor(FieldSensor fieldSensor) {
        sensorMap.put(fieldSensor.getId(), fieldSensor);
    }

    @GET
    public void init() {
        sensorMap.put("1", new FieldSensor("1", 35, 17, 5));
        sensorMap.put("2", new FieldSensor("2", 45, 17, 5));
        sensorMap.put("3", new FieldSensor("3", 65, 17, 5));
        sensorMap.put("4", new FieldSensor("4", 35, 27, 5));
        sensorMap.put("5", new FieldSensor("5", 45, 27, 6));
        sensorMap.put("6", new FieldSensor("6", 65, 27, 6));
        sensorMap.put("7", new FieldSensor("7", 35, 47, 6));
        sensorMap.put("8", new FieldSensor("8", 45, 47, 5));
        sensorMap.put("9", new FieldSensor("9", 65, 47, 5));
        sensorMap.put("10", new FieldSensor("10", 35, 27, 5));
        sensorMap.put("11", new FieldSensor("11", 45, 27, 7));
        sensorMap.put("12", new FieldSensor("12", 65, 27, 7));
        sensorMap.put("13", new FieldSensor("13", 35, 27, 5));
        sensorMap.put("14", new FieldSensor("14", 45, 27, 8));
        sensorMap.put("15", new FieldSensor("15", 65, 47, 8));
        sensorMap.put("16", new FieldSensor("16", 35, 17, 9));
    }

    @GET
    @Path("/{id}/sprinkler_on")
    public void turnOnSprinkler(@PathParam("id") String id) {
        if (sensorMap.get(id) != null) {
            sensorMap.get(id).setSprinklerRunning(Boolean.TRUE);
            redisDataService.dumpData(Integer.parseInt(id), SensorType.active, 1.0F);
        }
    }

    @GET
    @Path("/{id}/sprinkler_off")
    public void turnOffSprinkler(@PathParam("id") String id) {
        if (sensorMap.get(id) != null) {
            sensorMap.get(id).setSprinklerRunning(Boolean.FALSE);
            redisDataService.dumpData(Integer.parseInt(id), SensorType.active, 0.0F);
        }
    }

    @GET
    @Path("/{id}")
    public void undeploySensor(@PathParam("id") String id) {
        if (sensorMap.get(id) != null) {
            sensorMap.get(id).stop();
            sensorMap.remove(id);
        }
    }

    @GET
    @Path("/stress")
    public void stress() {
        try {
            HttpResponse<String> unirestCall = Unirest.post("https://www.fast2sms.com/dev/bulk?sender_id=FSTSMS&message=Your farm is under stress. Please visit cloudfarm.&language=english&route=p&numbers=9717721375")
                .header("authorization", "xLZ7Mhj0CQ3pnUVrq58d4K2R9fmNksJeXlD1BSFoAYvy6aWGtcMBrJR4VIgiwmuhbXDNx5Qk7Snv6Ucp")
                .asString();
            System.out.println("unirest call response "+unirestCall.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
