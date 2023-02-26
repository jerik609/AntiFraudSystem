package antifraud.model;

import antifraud.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

// https://javabydeveloper.com/spring-boot-loading-initial-data/

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tbl_roles")
public class Role {

    @Id
    @SequenceGenerator(
            name = "user_roles_sequence",
            sequenceName = "user_roles_sequence",
            initialValue = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_roles_sequence"
    )
    private long id;

    @Column(
            nullable = false,
            unique = true
    )
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

}
