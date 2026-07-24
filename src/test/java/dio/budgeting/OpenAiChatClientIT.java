package dio.budgeting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPEN_API_KEY", matches = ".+")
public class OpenAiChatClientIT {

    @Autowired
    OpenAiChatModel openAiChatModel;

    @Test
    void should_exectuSum_when_prompted(){
        // configuração do client, em defaultSystem estamos passando como se fosse uma skill"
        var chatCLient = ChatClient.builder(openAiChatModel)
                .defaultSystem("voce é um matemático")
                .build();

        var response = chatCLient.prompt("soma 10 mais 20. e depois divide por 2, porfavor só exibir o resultado final, sem esplicar")
                .call().content();

        assertThat(response).contains("15");
        System.out.println(response);
    }
}
