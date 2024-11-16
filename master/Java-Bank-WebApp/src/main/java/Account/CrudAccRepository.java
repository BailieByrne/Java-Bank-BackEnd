package Account;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import Users.User;

@Repository
public interface CrudAccRepository extends ListCrudRepository<CheckingAcc, Integer> {

    @Query("SELECT * FROM users WHERE RegisteredKeeper = :username")
    User findByUsername(@Param("username") String username);

    @Modifying
    @Query(value = "UPDATE checkingaccounts SET Balance = :balance WHERE id = :id")
    void updateBalance(@Param("balance") double balance, @Param("id") Integer id);

    @Modifying
    @Query(value = "UPDATE checkingaccounts SET OwnerID = id WHERE OwnerID = -1")
    void setOwnerID();

    @Query("SELECT * FROM checkingaccounts WHERE OwnerID = :id")
    List<CheckingAcc> findAllAccountsById(@Param("id") Integer id);
}
