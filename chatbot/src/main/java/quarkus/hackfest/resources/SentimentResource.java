package quarkus.hackfest.resources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quarkus.hackfest.client.SentimentRestClient;
import quarkus.hackfest.service.SentimentService;

@Path("/sentiment")
public class SentimentResource {
    private Logger logger = LoggerFactory.getLogger(SentimentResource.class);


    @Inject
    SentimentService sentimentService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{msg}")
    public String checkSentiment(@PathParam String msg) {

        logger.info("Checking sentiment for message {}", msg);
        
        return sentimentService.getSentiment(msg);

    }


    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String checkSentimentAgain(String msg) {

        logger.info("Checking sentiment for message {}", msg);
        
        return sentimentService.getSentiment(msg);

    }
}