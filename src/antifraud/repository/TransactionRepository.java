package antifraud.repository;

import antifraud.model.Region;
import antifraud.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "select count(distinct t.region) from Transaction t where t.number = ?1 and t.region != ?2 and t.date > ?3 and t.date <= ?4")
    Integer countDistinctRegionsForCreditCardAndNotRegionAndWithinPeriod(String cardNumber, Region region, Date fromDate, Date toDate);

    @Query(value = "select count(distinct t.ip) from Transaction t where t.number = ?1 and t.ip != ?2 and t.date > ?3 and t.date <= ?4")
    Integer countDistinctIpsForCreditCardAndNotIpAndWithinPeriod(String cardNumber, String ip, Date fromDate, Date toDate);

    List<Transaction> findByNumberOrderByIdAsc(String number);

}
