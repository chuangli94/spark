package core.mongodb;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserDocumentRepository extends MongoRepository<UserDocument, String>{
	public UserDocument findByName(String name);
	public List<UserDocument> findByNameIn(List<String> nameList);
}
