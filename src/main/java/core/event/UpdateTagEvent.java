package core.event;

import java.util.List;

import org.springframework.context.ApplicationEvent;

public class UpdateTagEvent extends ApplicationEvent {
	private List<String> tags;
	
	public UpdateTagEvent(Object source, List<String> tags){
		super(source);
		this.tags = tags;
	}
	
	public List<String> getTags(){
		return tags;
	}
	
	public void setTags(List<String> tags){
		this.tags = tags;
	}
}
