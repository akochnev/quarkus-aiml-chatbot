package quarkus.hackfest.chatbot.client;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.chat.v1.HangoutsChat;
import com.google.api.services.chat.v1.model.Message;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;


@ApplicationScoped
public class ChatClient {

    static final String CHAT_SCOPE = "https://www.googleapis.com/auth/chat.bot";

    @ConfigProperty(name = "hackfest.chatbot.googleCredsResource")
    public String googleCredsResource;

    public void sendResponse(String spaceName, String threadName, String msg) throws IOException, GeneralSecurityException {
        Message reply = new Message();
        
        reply.setText("HackFest Bot : " + msg);
    	    	
        com.google.api.services.chat.v1.model.Thread thread = new com.google.api.services.chat.v1.model.Thread();
        thread.setName(threadName);
        reply.setThread(thread);

        GoogleCredentials credentials = GoogleCredentials.fromStream(ChatClient.class.getResourceAsStream(googleCredsResource))
        		.createScoped(CHAT_SCOPE);
        
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
        HangoutsChat chatService = new HangoutsChat.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    requestInitializer)
                .setApplicationName("HackFest Bot")
                .build();
        chatService.spaces().messages().create(spaceName, reply).execute();
    }

}