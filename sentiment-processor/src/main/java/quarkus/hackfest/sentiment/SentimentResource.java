package quarkus.hackfest.sentiment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.runtime.StartupEvent;
import java.util.Date;
import java.util.Properties;
import javax.enterprise.event.Observes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import javax.inject.Inject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class SentimentResource {

    private Logger logger = LoggerFactory.getLogger(SentimentResource.class);

    @ConfigProperty(name = "mp.messaging.outgoing.chat-sentiment-scores-results.bootstrap.servers")
    public String bootstrapServers;

    @ConfigProperty(name = "mp.messaging.outgoing.chat-sentiment-scores-results.topic")
    public String sentimentsTopic;

    @ConfigProperty(name = "mp.messaging.outgoing.chat-sentiment-scores-results.value.serializer")
    public String sentimentsTopicValueSerializer;

    @ConfigProperty(name = "mp.messaging.outgoing.chat-sentiment-scores-results.key.serializer")
    public String sentimentsTopicKeySerializer;

    private Producer<String, String> producer;

    public void init(@Observes StartupEvent ev) {
        Properties props = new Properties();

        props.put("bootstrap.servers", bootstrapServers);
        props.put("value.serializer", sentimentsTopicValueSerializer);
        props.put("key.serializer", sentimentsTopicKeySerializer);
        producer = new KafkaProducer<>(props);
        
    }

    @Inject
    SentimentService sentimentService;

    @POST
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String checkSentiment(String chatMsgStr) {
        
        logger.info("Received incoming message: {} ", chatMsgStr);

        String respStr = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode chatMsg = objectMapper.readTree(chatMsgStr);

            logger.info("Checking sentiment for message {}", chatMsg);

            ObjectNode resp = objectMapper.createObjectNode();

            String msg = chatMsg.at("/message").asText();
            String spaceName = chatMsg.at("/spaceName").asText();
            String threadName = chatMsg.at("/threadName").asText();

            resp.put("date", new Date().getTime());
            resp.put("sentiment", sentimentService.getSentiment(msg));
            resp.put("spaceName", spaceName);
            resp.put("threadName", threadName);

            respStr = objectMapper.writeValueAsString(resp);

            producer.send(new ProducerRecord<>(sentimentsTopic, respStr));

            logger.info("Returning processed sentiment score {} ", respStr);
        } catch (JsonProcessingException ex) {
            logger.error("Failed to serialize json string", ex);
        }

        return respStr;

    }
}
