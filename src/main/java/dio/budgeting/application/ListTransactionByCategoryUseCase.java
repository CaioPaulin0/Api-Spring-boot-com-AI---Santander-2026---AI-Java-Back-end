package dio.budgeting.application;

import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListTransactionByCategoryUseCase {
    private final TransactionRepository transactionRepository;

    public ListTransactionByCategoryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name= "list-transactions-by-category", description = "Lista de transações financeiras por categoria")
    public List<TransactionOutput> execute(@ToolParam(description = "categoria de uma transação") Category category){
        return transactionRepository.findAllByCategory(category)
                .stream()
                .map(TransactionOutput::from)
                .toList();

    }
}
