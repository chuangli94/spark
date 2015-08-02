package core.mysql;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface KeywordRepository extends CrudRepository<Keyword, Long>{

	List<Keyword> findByName(String name);
	
	List<Keyword> findAll();
	
	Keyword findById(Integer id);
}
