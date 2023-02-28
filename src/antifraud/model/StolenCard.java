package antifraud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tbl_stolen_card")
public class StolenCard {

    @Id
    @SequenceGenerator(
            name = "stolen_card_sequence",
            sequenceName = "stolen_card_sequence",
            allocationSize = 1
    )
    private long id;

    @Column(
            nullable = false,
            unique = true
    )
    private String number;

}
