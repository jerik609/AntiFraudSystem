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
@Table(name = "tbl_configuration")
public class Configuration {

    @Id
    @SequenceGenerator(
            name = "configuration_sequence",
            sequenceName = "configuration_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "configuration_sequence"
    )
    private long id;

    @Column(
            nullable = false,
            unique = true
    )
    private String key;

    @Column(
            nullable = false
    )
    private String value;

}
