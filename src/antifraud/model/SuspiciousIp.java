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

    public static class IpValidator {

        private static final String ipRegex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";

        private static final Pattern pattern = Pattern.compile(ipRegex);

        public static boolean isValid(String ip) {
            final var matcher = pattern.matcher(ip);
            return matcher.matches();
        }

    }
}
