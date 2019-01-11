package controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

    private String message;

    public HelloWorldResource(String message) {
        this.message = message;
    }

    @GET
    public Response sayHello() {
        return Response.ok(message).build();
    }
}
