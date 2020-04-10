package quarkus.hackfest.chatbot.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;
import quarkus.hackfest.chatbot.client.ChatClient;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

@Path("/chatbot")
public class ChatbotResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        return "hello";
    }

    @ConfigProperty(name = "mp.messaging.outgoing.chat-messages.bootstrap.servers")
    public String bootstrapServers;

    @ConfigProperty(name = "mp.messaging.outgoing.chat-messages.topic")
    public String messagesTopic;

    @ConfigProperty(name = "mp.messaging.outgoing.chat-messages.value.serializer")
    public String messagesTopicValueSerializer;

    @ConfigProperty(name = "mp.messaging.outgoing.chat-messages.key.serializer")
    public String messagesTopicKeySerializer;

    private Producer<String, String> producer;

    public static final Logger log = LoggerFactory.getLogger(ChatbotResource.class);

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode onChatMessage(JsonNode body) {
        log.info("Received body post : " + body);

        String senderName = body.at("/message/sender/displayName").asText();
        String messageText = body.at("/message/text").asText(); 
        String spaceName = body.at("/message/space/name").asText();
        String threadName = body.at("/message/thread/name").asText();
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode chatMsg = objectMapper.createObjectNode();
        chatMsg.put("spaceName", spaceName);
        chatMsg.put("threadName", threadName);
        chatMsg.put("message", messageText);

        ObjectNode chatResponse = objectMapper.createObjectNode();
        try {
            producer.send(new ProducerRecord<String,String>(messagesTopic,objectMapper.writeValueAsString(chatMsg)));
        
            chatResponse.put("text","Message received from:" + senderName);
        } catch (JsonProcessingException jpe) {
            chatResponse.put("text","Failed to received message from:" + senderName);
        }
        

        return chatResponse;
    }

    public void init(@Observes StartupEvent ev) {
        Properties props = new Properties();

        props.put("bootstrap.servers", bootstrapServers);
        props.put("value.serializer", messagesTopicValueSerializer);
        props.put("key.serializer", messagesTopicKeySerializer);
        producer = new KafkaProducer<String, String>(props);
    }

    
    @Inject
    ChatClient gchatClient;

    @Path("/reply")
    @POST    
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String asyncReply(JsonNode responseContent) throws IOException, GeneralSecurityException {		
        // @PathParam String spaceName /*, @PathParam String threadName*/, String msg
        String spaceName = responseContent.at("/spaceName").asText();
        String threadName = responseContent.at("/threadName").asText();
        String msg = responseContent.at("/message").asText();

		gchatClient.sendResponse(spaceName, threadName, msg);

        return "Message sent";
	}


}