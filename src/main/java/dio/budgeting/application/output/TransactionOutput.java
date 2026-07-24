package dio.budgeting.application.output;

import dio.budgeting.domain.Transcation;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record TransactionOutput(String id, String description, String category, long value) {
    public static TransactionOutput from(Transcation transcation){
        return new TransactionOutput(transcation.getId().uuid().toString(),
                transcation.getDescripition(),
                transcation.getCategory().name(),
                transcation.getAmount());
    }
}
