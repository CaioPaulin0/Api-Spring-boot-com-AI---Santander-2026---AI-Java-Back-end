package dio.budgeting.domain;

import java.util.List;

public interface TransactionRepository {
    Transcation save(Transcation transcation);
    List<Transcation> findAllByCategory(Category category);
}
