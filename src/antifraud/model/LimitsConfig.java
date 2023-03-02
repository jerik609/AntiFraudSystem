package antifraud.model;

import antifraud.enums.LimitOperation;
import antifraud.enums.TransactionValidationResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

// https://www.baeldung.com/jpa-unique-constraints

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "tbl_limits_config",
        uniqueConstraints = { @UniqueConstraint(columnNames = {"validity", "feedback"}) }
)
public class LimitsConfig {
    @Id
    @SequenceGenerator(
            name = "limits_config_sequence",
            sequenceName = "limits_config_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "limits_config_sequence"
    )
    private long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionValidationResult validity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionValidationResult feedback;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LimitOperation operation;

}
