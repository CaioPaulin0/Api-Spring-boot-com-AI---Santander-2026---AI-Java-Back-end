package dio.budgeting.infrastructure.persistence.http.request;

import dio.budgeting.application.input.PersistTransactionInput;
import dio.budgeting.domain.Category;

import java.math.BigDecimal;

public record TransactionRequest(
        String description,
        Category category,
        long amount
) {
    public PersistTransactionInput toInput() {
        return new PersistTransactionInput(description, amount, category);
    }
}
