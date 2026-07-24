package dio.budgeting;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
public class ChatClientController {
    // chatClient pode ter varias instancias, um para cada modelo //
    private final ChatClient chatClient;

    public ChatClientController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    String chat(String prompt){
        return this.chatClient.prompt().user(prompt).call().content();
    }

}
