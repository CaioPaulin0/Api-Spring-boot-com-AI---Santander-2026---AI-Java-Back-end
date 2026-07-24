package dio.budgeting.application;


import dio.budgeting.application.input.PersistTransactionInput;
import dio.budgeting.application.output.TransactionOutput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.domain.Transcation;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class PersistTransactionUserCase {
    private final TransactionRepository transactionRepository;

    public PersistTransactionUserCase(TransactionRepository transactionRepository){
        this.transactionRepository = transactionRepository;
    }
    @Tool(name= "perist-transaction", description = "Persiste uma nova transação financeira")
    public TransactionOutput execute(PersistTransactionInput input){
        var transaction =  transactionRepository.save(new Transcation(input.description(), input.amout(), input.category()));

        return TransactionOutput.from(transaction);
    }
}
