package core.postgresql;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface ImageRepository extends CrudRepository<Image, Long>{
	List<Image> findByHash(String hash);
	
	List<Image> findByIdBetween(int startId, int endId);
	
	long count();
}

