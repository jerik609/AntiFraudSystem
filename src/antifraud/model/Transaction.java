package antifraud.model;

import antifraud.enums.TransactionValidationResult;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tbl_transactions")
public class Transaction {

    @Id
    @SequenceGenerator(
            name = "transaction_sequence",
            sequenceName = "transaction_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "transaction_sequence"
    )
    private Long id;

    private Long amount;

    private String ip;

    private String number;

    @ManyToOne(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            optional = false
    )
    @JoinColumn(
            name = "region_id",
            referencedColumnName = "id"
    )
    private Region region;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String owner;

    @Enumerated(EnumType.STRING)
    private TransactionValidationResult validationResult;

    @OneToOne(
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}
    )
    @JoinColumn(
            name = "feedback_id",
            referencedColumnName = "id"
    )
    private Feedback feedback;

    public void addFeedback(Feedback feedback) {
        this.feedback = feedback;
        feedback.setTransaction(this);
    }

}
