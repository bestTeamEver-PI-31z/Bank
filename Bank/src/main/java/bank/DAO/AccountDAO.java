package bank.DAO;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import bank.entity.Account;

public interface AccountDAO extends JpaRepository<Account, Long> {
	@Query(value = "select a from Account a where a.user.id = :userId")
	public List<Account> findAccountsByUserId(@Param("userId") Long userId);

	@Query(value = "select sum(a.balance) from Account a where a.user.id = :userId group by a.user.id")
	public Double calculateTotalBalanceByUserId(@Param("userId") Long userId);

	@Query(value = "select count(a.user.id) from Account a where a.user.id = :userId ")
	public Integer calculateNumberOfAccountsByUserId(@Param("userId") Long userId);
}
