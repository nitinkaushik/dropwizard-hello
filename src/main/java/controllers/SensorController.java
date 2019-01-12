package controllers;

import io.dropwizard.jersey.PATCH;
import models.FieldSensor;

import javax.ws.rs.*;
import java.util.HashMap;
import java.util.Map;

@Path("/sensor")
public class SensorController {
    private Map<String, FieldSensor> sensorMap;

    public SensorController() {
        sensorMap = new HashMap<>();
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
        }
    }

    @GET
    @Path("/{id}/sprinkler_off")
    public void turnOffSprinkler(@PathParam("id") String id) {
        if (sensorMap.get(id) != null) {
            sensorMap.get(id).setSprinklerRunning(Boolean.FALSE);
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
}
