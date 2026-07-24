package dio.budgeting.infrastructure.persistence.http;

import dio.budgeting.application.ListTransactionByCategoryUseCase;
import dio.budgeting.application.PersistTransactionUserCase;
import dio.budgeting.application.SumAmoutByCategoryUseCase;
import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transcation;
import dio.budgeting.infrastructure.persistence.http.request.TransactionRequest;
import dio.budgeting.infrastructure.persistence.http.response.TransactionReponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final PersistTransactionUserCase persistTransactionUserCase;
    private final ListTransactionByCategoryUseCase listTransactionByCategoryUseCase;
    private final TranscriptionModel transcriptionModel;
    private final ChatClient chatClient;
    private final TextToSpeechModel textToSpeechModel;
    private final SumAmoutByCategoryUseCase sumAmoutByCategoryUseCase;


    public TransactionController(
            @Value("classpath:/prompts/system.st") Resource systemPrompt,
            PersistTransactionUserCase persistTransactionUserCase,
            ListTransactionByCategoryUseCase listTransactionByCategoryUseCase,
            TranscriptionModel transcriptionModel,
            ChatClient.Builder chatClientBuilder, TextToSpeechModel textToSpeechModel, SumAmoutByCategoryUseCase sumAmoutByCategoryUseCase
    ) throws IOException {
        this.persistTransactionUserCase = persistTransactionUserCase;
        this.listTransactionByCategoryUseCase = listTransactionByCategoryUseCase;
        this.transcriptionModel = transcriptionModel;
        this.textToSpeechModel = textToSpeechModel;
        this.sumAmoutByCategoryUseCase = sumAmoutByCategoryUseCase;
        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .defaultTools(persistTransactionUserCase, listTransactionByCategoryUseCase)
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionReponse createTransaction(@RequestBody TransactionRequest request){
        var transaction = persistTransactionUserCase.execute(request.toInput());
        return TransactionReponse.from(transaction);
    }

    @GetMapping("/{category}")
    @ResponseStatus(HttpStatus.OK)
    public List<TransactionReponse> readTranscation(@PathVariable Category category){
        return listTransactionByCategoryUseCase.execute(category)
                .stream()
                .map(TransactionReponse::from)
                .toList();
    }

    @GetMapping(value = "/todos/{category}", produces = "audio/mp3")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> sumOfAmout(@PathVariable Category category){
        var total =  sumAmoutByCategoryUseCase.sumAmountByCategory(listTransactionByCategoryUseCase.execute(category));
        BigDecimal amount = BigDecimal.valueOf(total, 2);

        // Sem precisar usar chatClient //
        String result = "O valor total das suas contas da categoria "
                + category
                + " é de R$ "
                + amount.toString().replace(".", ",")
                + ".";
        byte[] audio = textToSpeechModel.call(result);
        var resource = new ByteArrayResource(audio);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename("audio.mp3")
                        .build()
                        .toString())
                .body(resource);
    }

    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mp3")
    public ResponseEntity<Resource> transcribe(@RequestParam("file") MultipartFile file){
        var userMessage = transcriptionModel.transcribe(file.getResource());

        var result = chatClient.prompt().user(userMessage).call().content();
        System.out.println(result);
        byte [] audio = textToSpeechModel.call(result);
        var resource = new ByteArrayResource(audio);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename("audio.mp3")
                        .build()
                        .toString())
                .body(resource);
    }
}
