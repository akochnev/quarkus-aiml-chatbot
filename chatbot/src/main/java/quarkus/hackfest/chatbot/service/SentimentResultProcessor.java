package quarkus.hackfest.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;
import quarkus.hackfest.chatbot.client.ChatClient;

@ApplicationScoped
public class SentimentResultProcessor {

    public static final Logger logger = LoggerFactory.getLogger(SentimentResultProcessor.class);

    @Inject
    ChatClient gchatClient;

    Map<String,List<String>> responses = new HashMap();

    void onStart(@Observes StartupEvent ev) { 
        // from https://www.greetingcardpoet.com/happy-life-quotes-and-sayings/
        List<String> happyThoughts = new ArrayList();
        happyThoughts.add("A happy life is a virtuous life");
        happyThoughts.add("I’ll have a cup full of Happiness and a pocket full of Rainbows to go.");
        happyThoughts.add("The best life is the one which is lived happily without being noticed by others!");
        happyThoughts.add("Most folks are as happy as they make up their minds to be");
        happyThoughts.add("Read books, be happy");

        List<String> sadThoughts = new ArrayList();
        sadThoughts.add("Our greatest joy and our greatest pain come in our relationships with others.");
        sadThoughts.add("Some days are just bad days, that’s all. You have to experience sadness to know happiness, and I remind myself that not every day is going to be a good day, that’s just the way it is!");
        sadThoughts.add("Heavy hearts, like heavy clouds in the sky, are best relieved by the letting of a little water");
        sadThoughts.add("It’s sad when someone you know becomes someone you knew");
        sadThoughts.add("Today my forest is dark. The trees are sad and all the butterflies have broken wings.");

        responses.put("positive",happyThoughts);
        responses.put("negative",sadThoughts);
        logger.info("Initialized responses {}", responses);
    }

    @Incoming("chat-sentiment-scores")               
    public void processSentimentResult(final String sentimentResult) {
        logger.info("Processing sentiment result: {}", sentimentResult);
        try {

            final ObjectMapper objectMapper = new ObjectMapper();

            final JsonNode sentimentNode = objectMapper.readTree(sentimentResult);

            final String spaceName = sentimentNode.at("/spaceName").asText();
            final String threadName = sentimentNode.at("/threadName").asText();
            final String sentiment = sentimentNode.at("/sentiment").asText().trim().toLowerCase();

            if (spaceName!=null && !"".equals(spaceName)) {
                StringBuffer sb = new StringBuffer("Today you feel " + sentiment + ". ");
                
                if (responses.containsKey(sentiment)) {                
                    List<String> quotes = responses.get(sentiment);
                    Collections.shuffle(quotes);

                    String quote = quotes.iterator().next();
                    sb.append(quote);
                }
                gchatClient.sendResponse(spaceName, threadName, sb.toString());
            } else {
                logger.info("Skipping sentiment result, no space to respond to");
            }
        } catch (final JsonProcessingException jpe) {
            logger.error("Failed in json conversion {}", jpe.getMessage());
        } catch (final Exception e) {
            logger.error("Failed to send response message", e);
        }

    }

}