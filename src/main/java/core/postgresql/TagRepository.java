package core.postgresql;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface TagRepository extends CrudRepository<Tag, Long>{
	List<Tag> findByName(String tag);
	List<Tag> findByNameIn(List<String> tagNames);
}
