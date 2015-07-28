package core.mongodb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="User")
public class UserDocument {

	@Id
	private String id;
	
	private String name;
	private String profilePictureKey;
	private List<String> queue;
	private List<String> alreadySeen;
	
	public UserDocument(){}
	
	public UserDocument(String name, List<String> queue, List<String> alreadySeen){
		this.name = name;
		this.queue = queue;
		this.alreadySeen = alreadySeen;
		this.profilePictureKey = null;
	}
	
	public void setProfilePictureKey(String profilePictureKey){
		this.profilePictureKey = profilePictureKey;
	}
	
	public String getProfilePictureKey(){
		return this.profilePictureKey;
	}
	
	public void enQueue(String item){
		if (queue == null){
			queue = new ArrayList<String>();
		}
		queue.add(item);
	}
	
	public void enQueueAll(List<String> items){
		if (queue == null){
			queue = new ArrayList<String>();
		}
		queue.addAll(items);
	}
	
	public String deQueue(){
		if ((queue == null) || (queue.isEmpty())){
			return null;
		}
		
		return queue.remove(0);
	}
	public String getName(){
		return name;
	}
	
	public List<String> getQueue(){
		return queue;
	}
	
	public List<String> getAlreadySeen(){
		return alreadySeen;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setQueue(List<String> queue){
		this.queue = queue;
	}
	
	public void setAlreadySeen(List<String> alreadySeen){
		this.alreadySeen = alreadySeen;
	
	}
	
}
