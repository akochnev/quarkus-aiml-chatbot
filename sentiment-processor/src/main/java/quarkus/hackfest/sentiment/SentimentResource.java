package quarkus.hackfest.sentiment;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/sentiment")
public class SentimentResource {
    private Logger logger = LoggerFactory.getLogger(SentimentResource.class);

    @Inject
    SentimentService sentimentService;

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String checkSentimentAgain(String msg) {

        logger.info("Checking sentiment for message {}", msg);
        
        return sentimentService.getSentiment(msg);

    }
}