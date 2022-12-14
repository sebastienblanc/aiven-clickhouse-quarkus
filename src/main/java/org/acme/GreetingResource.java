package org.acme;

import java.sql.SQLException;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/towers")
@ApplicationScoped
public class GreetingResource {

    @javax.inject.Inject
    ClickhouseService service;
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{countryCode}")
    public String hello(String countryCode) throws SQLException {
       
        return  "Number of Cell Towers for MCC " + countryCode + " : " + service.getResult(countryCode);
    }
}