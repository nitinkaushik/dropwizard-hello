package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import services.RedisDataService;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

    private String message;

    private RedisDataService redisDataService;
    private ObjectMapper objectMapper;

    @Inject
    public HelloWorldResource(String message, RedisDataService redisDataService) {
        this.message = message;
        this.redisDataService = redisDataService;
        this.objectMapper = new ObjectMapper();
    }

    @GET
    public Response sayHello() {
        return Response.ok(message).build();
    }

//    @GET
//    public Response getData() {
//        return Response.ok(redisDataService.getData()).build();
//    }

    @GET
    @Path("/redis-data")
    public Response getRedisResponse() {
      return Response.ok(redisDataService.getData()).build();
    }
}
