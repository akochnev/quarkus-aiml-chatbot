package org.acme;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.kafka.clients.producer.Producer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/hello")
public class ChatbotResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    @ConfigProperty(name = "mp.messaging.outgoing.messages.bootstrap.servers")
    public String bootstrapServers;

    @ConfigProperty(name = "mp.messaging.outgoing.messages.topic")
    public String paymentsTopic;

    @ConfigProperty(name = "mp.messaging.outgoing.messages.value.serializer")
    public String paymentsTopicValueSerializer;

    @ConfigProperty(name = "mp.messaging.outgoing.messages.key.serializer")
    public String paymentsTopicKeySerializer;

    private Producer<String, String> producer;

    public static final Logger log = LoggerFactory.getLogger(ExampleResource.class);

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode helloPost(JsonNode body) {
        System.out.println("Received body post : " + body);

        String senderNode = body.at("/message/sender/displayName").asText();
        
        ObjectMapper objectMapper = new ObjectMapper();


        ObjectNode response = objectMapper.createObjectNode();
        response.put("text","hello from AK Hack bot :" + senderNode);

        return response;
    }


}