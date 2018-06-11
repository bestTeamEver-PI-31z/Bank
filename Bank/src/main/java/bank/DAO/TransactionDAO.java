package bank.DAO;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bank.entity.Transaction;

@Repository
public interface TransactionDAO extends JpaRepository<Transaction, Long> {
	@Query(value = "select t from Transaction t where t.accountNumberFrom = :accountId or t.accountNumberTo = :accountId order by t.date desc")
	public List<Transaction> findTransferHistoryByAccountId(@Param("accountId") Long accountId);

	@Query(value = "select t from Transaction t where (t.accountNumberFrom = :accountId or t.accountNumberTo = :accountId) and t.date between :fromDate and :toDate")
	public List<Transaction> findTransfersByDates(@Param("accountId") Long accountId, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
}
