package org.cru.obieewsping;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/ping")
public class PingResource {

    private final ObieeWsTransactionService service;

    @Inject
    public PingResource(ObieeWsTransactionService service) {
        this.service = service;
    }

    @GET
    @Path("/pinger")
    @Produces(MediaType.TEXT_PLAIN)
    public String pinger() {
        return "ok";
    }

    @GET
    @Path("/obiee")
    @Produces(MediaType.TEXT_PLAIN)
    public Response hello() {
        return Response.status(Status.METHOD_NOT_ALLOWED)
            .entity("POST a username and password here")
            .build();
    }

    @POST
    @Path("/obiee")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(
        @FormParam("username") String username,
        @FormParam("password") String password
    ) {
        service.performSyntheticTransaction(username, password);
        return "ok";
    }

}
