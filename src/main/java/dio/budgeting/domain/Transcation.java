package dio.budgeting.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class Transcation {
    public TranscationId id;
    private String descripition;
    private long amount;
    private Category category;

    public Transcation(String descripition, long amount, Category category){
        this.id = new TranscationId();
        this.amount = amount;
        this.descripition = descripition;
        this.category = category;
    }


}
