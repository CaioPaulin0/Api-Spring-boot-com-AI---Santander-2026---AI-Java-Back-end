package dio.budgeting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPEN_API_KEY", matches = ".+")
public class ToolCallingIT {

    @Autowired
    OpenAiChatModel openAiChatModel;

    static class MathTools{
        @Tool(description = "soma dois numeros inteiros, a e b")
        public int sum(int a, int b){
            return a + b;
        }

        @Tool(description = "subtrai dois numeros inteiros, a e b")
        public int diff(int a, int b){
            return a - b;
        }
    }

    @Test
    void should_exectuSum_when_prompted(){
        var chatCLient = ChatClient.builder(openAiChatModel)
                .defaultTools(new MathTools())
                .defaultSystem("voce é um matemático")
                .build();

        var response = chatCLient.prompt("soma 10 mais 20. e depois divide por 2, porfavor só exibir o resultado final, sem esplicar")
                .call().content();

        assertThat(response).contains("15");
        System.out.println(response);
    }
}
