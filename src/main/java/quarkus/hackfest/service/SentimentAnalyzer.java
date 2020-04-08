package quarkus.hackfest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import io.smallrye.reactive.messaging.annotations.Broadcast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ApplicationScoped
public class SentimentAnalyzer {
    public static final Logger log = LoggerFactory.getLogger(SentimentAnalyzer.class);

    private Random random = new Random();

    @Incoming("messages")               
    @Outgoing("chat-sentiment-scores")      
    @Broadcast                       
    public String processSentiment(String chatMessage) {
        log.info("Received chat message from message queue: {}", chatMessage);
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode resp = objectMapper.createObjectNode();

        String respStr = ""; 
        try {
            JsonNode chatMsg = objectMapper.readTree(chatMessage);
        
            String msg = chatMsg.at("/message").asText();

            resp.put("date", new Date().getTime());
            resp.put("sentimentScore", getSentimentScore(msg));

            respStr = objectMapper.writeValueAsString(resp);
        } catch (JsonProcessingException jpe) {
            log.error("Failed in json conversion {}", jpe.getMessage());
        }

        log.info("Returning processed sentiment score {} ", respStr);
        return respStr;
    }

    private int getSentimentScore(String msg) {
        int score = random.nextInt(5);
        log.info("Converted message {} to score {}", msg, score);
        return score;
    }
}
