package quarkus.hackfest.sentiment;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class SentimentService {
    private Logger logger = LoggerFactory.getLogger(SentimentService.class);

    @Inject
    @RestClient 
    SentimentRestClient sentimentRestClient; 

    
    public String getSentiment(String msg) {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode msgNode = objectMapper.createObjectNode();
        msgNode.put("strData", msg);
        JsonNode sentimentResp = sentimentRestClient.getMessageSentiment(msgNode);

        logger.info("Returned response: {} ", sentimentResp);

        String sentiment = sentimentResp.at("/data/ndarray").elements().next().asText();
        logger.info("Converted message {} to score {}", msg, sentiment);

        return sentiment;
    }
}