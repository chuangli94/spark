package core.event;

import java.util.List;

import org.springframework.context.ApplicationEvent;

public class BroadcastItemEvent extends ApplicationEvent {
	private String name;
	private String itemName;
	private List<String> finalCategories;
	public BroadcastItemEvent(Object source, String name, String itemName, List<String> finalCategories){
		super(source);
		this.name = name;
		this.itemName = itemName;
		this.finalCategories = finalCategories;
	}
	
	public String getName(){
		return name;
	}
	
	public String getItemName(){
		return itemName;
	}
	
	public List<String> getFinalCategories(){
		return finalCategories;
	}
}

