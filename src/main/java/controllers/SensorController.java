package controllers;

import io.dropwizard.jersey.PATCH;
import models.FieldSensor;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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

    @POST
    @Path("/{id}/sprinkler_on")
    public void turnOnSprinkler(@PathParam("id") String id) {
        if (sensorMap.get(id) != null) {
            sensorMap.get(id).setSprinklerRunning(Boolean.TRUE);
        }
    }

    @POST
    @Path("/{id}/sprinkler_off")
    public void turnOffSprinkler(@PathParam("id") String id) {
        if (sensorMap.get(id) != null) {
            sensorMap.get(id).setSprinklerRunning(Boolean.FALSE);
        }
    }

    @DELETE
    @Path("/{id}")
    public void undeploySensor(@PathParam("id") String id) {
        if (sensorMap.get(id) != null) {
            sensorMap.get(id).stop();
            sensorMap.remove(id);
        }
    }
}
