package antifraud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.regex.Pattern;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tbl_suspicious_ip")
public class SuspiciousIp {

    @Id
    @SequenceGenerator(
            name = "suspicious_ip_sequence",
            sequenceName = "suspicious_ip_sequence",
            allocationSize = 1
    )
    private long id;

    @Column(
            nullable = false,
            unique = true
    )
    private String ip;

}
