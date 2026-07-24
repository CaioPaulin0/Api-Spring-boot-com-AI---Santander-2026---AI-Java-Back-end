package dio.budgeting.infrastructure.persistence.entity;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transcation;
import dio.budgeting.domain.TranscationId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntity {
    @Id
    private UUID id;
    private String description;
    private long amount;
    private Category category;

    public static TransactionEntity from(Transcation transcation){
        return new TransactionEntity(transcation.getId().uuid(), transcation.getDescripition(), transcation.getAmount(), transcation.getCategory());
    }

    public Transcation toDomain(){
        return new Transcation(
                new TranscationId(this.id),
                this.description,
                this.amount,
                this.category
        );
    }
}
