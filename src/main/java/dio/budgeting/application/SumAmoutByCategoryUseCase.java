package dio.budgeting.application;

import dio.budgeting.application.output.TransactionOutput;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SumAmoutByCategoryUseCase {

    public long sumAmountByCategory(List<TransactionOutput> list){
        long amouts = list.stream()
                .mapToLong(TransactionOutput::value)
                .sum();

       return amouts;
    }
}
