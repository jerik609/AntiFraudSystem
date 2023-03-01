package antifraud.model;

import antifraud.enums.TransactionValidationResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_feedback")
public class Feedback {

    @Id
    @SequenceGenerator(
            name = "feedback_sequence",
            sequenceName = "feedback_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "feedback_sequence"
    )
    private long id;

    @Enumerated(EnumType.STRING)
    private TransactionValidationResult validationResult;

    @OneToOne(
            mappedBy = "id",
            optional = false//,
            //cascade = CascadeType.MERGE
    )
    private Transaction transaction;

}
