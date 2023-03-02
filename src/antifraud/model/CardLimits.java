package antifraud.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_card_limits")
public class CardLimits {

    @Id
    @SequenceGenerator(
            name = "card_limits_sequence",
            sequenceName = "card_limits_sequence",
            initialValue = 0,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "card_limits_sequence"
    )
    private long id;

    @Column(
            nullable = false,
            unique = true
    )
    private String number;

    private double limitAllowed;

    private double limitManual;
}
