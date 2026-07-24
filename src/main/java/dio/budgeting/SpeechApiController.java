package dio.budgeting;

import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/speech")
public class SpeechApiController {
    private final TextToSpeechModel textToSpeechModel;

    public SpeechApiController(TextToSpeechModel textToSpeechModel){
        this.textToSpeechModel = textToSpeechModel;
    }

    @PostMapping(value = "/enviar", produces= "audio/mp3")
    public ResponseEntity<Resource> sintheze(@RequestBody SpeechRequest request){
        byte [] audio = textToSpeechModel.call(request.text());
        var resource = new ByteArrayResource(audio);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename("audio.mp3")
                        .build()
                        .toString())
                .body(resource);
    }

    public record SpeechRequest(String text){}
}
