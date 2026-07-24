package dio.budgeting.infrastructure.persistence.repository;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.domain.Transcation;
import dio.budgeting.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaTransactionRepository implements TransactionRepository {

    private final TransactionEntityRepository transactionEntityRepository;

    public JpaTransactionRepository(TransactionEntityRepository transactionEntityRepository) {
        this.transactionEntityRepository = transactionEntityRepository;
    }


    @Override
    public Transcation save(Transcation transcation) {
        var entity = TransactionEntity.from(transcation);
        return transactionEntityRepository.save(entity).toDomain();
    }


    @Override
    public List<Transcation> findAllByCategory(Category category) {
        return transactionEntityRepository.findAllByCategory(category)
                .stream()
                .map(TransactionEntity::toDomain)
                .toList();
    }

}
