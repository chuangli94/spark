package core.mysql;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<Image, Long>{
	List<Image> findByHash(String hash);
	
	List<Image> findByIdBetween(int startId, int endId);
	
	long count();
}

