package quarkus.hackfest.sentiment; 

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.fasterxml.jackson.databind.JsonNode;

@Path("/api/v0.1")
@RegisterRestClient(configKey="sentiment-rest-client")
public interface SentimentRestClient {

    /**
     * 
     * 
     * @param msg
     * @return a json response
     * 
     * Example response: 
        {
            "meta": {
                "puid": "sni4086clonsfltkp5a1b4aucg",
                "tags": {},
                "routing": {},
                "requestPath": {
                "sentiment-analysis-proxy": "quay.io/opendatahub/ai-library-sentiment-analysis-proxy"
                },
                "metrics": []
            },
            "data": {
                "names": [],
                "ndarray": [
                    "Negative",
                    []
                ]
            }
        }
     */
    @POST
    @Path("/predictions")
    JsonNode getMessageSentiment(JsonNode msg);
}