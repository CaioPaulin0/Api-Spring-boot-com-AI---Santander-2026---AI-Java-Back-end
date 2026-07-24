package dio.budgeting.infrastructure.persistence.http.response;

import dio.budgeting.application.output.TransactionOutput;

import java.math.BigDecimal;

public record TransactionReponse(String id, String category, String description, double amout) {
    public static TransactionReponse from(TransactionOutput output){
        return new TransactionReponse(output.id(),output.description(), output.category(), output.value());
    }
}
