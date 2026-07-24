package dio.budgeting.application.input;

import dio.budgeting.domain.Category;
import org.springframework.ai.tool.annotation.ToolParam;

import java.math.BigDecimal;

public record PersistTransactionInput(
       @ToolParam(description = "descrição do gasto") String description,
       @ToolParam(description = "valor do gasto em centavos") long amout,
       @ToolParam(description = "categoria de uma transação") Category category
) {
}
