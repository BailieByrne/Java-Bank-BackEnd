package Account;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


@Repository
@Service
public interface CrudAccRepository extends ListCrudRepository<CheckingAcc, Integer>{
	
	
}