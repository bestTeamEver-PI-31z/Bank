package bank.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bank.entity.User;

@Repository
public interface UserDAO extends JpaRepository<User, Long> {
	@Query(value = "select u from User u where u.phoneNumber = :phoneNumber")
	public User findUserByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
