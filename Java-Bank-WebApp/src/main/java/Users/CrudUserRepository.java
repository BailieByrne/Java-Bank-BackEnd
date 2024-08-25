package Users;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


@Repository
@Service
public interface CrudUserRepository extends ListCrudRepository<User,String>{
	
	
	@Query("SELECT * FROM users WHERE RegisteredKeeper = :username")
	User findByUsername(@Param("username") String username);

}
