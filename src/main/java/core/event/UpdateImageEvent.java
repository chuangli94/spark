package core.event;

import org.springframework.context.ApplicationEvent;

import core.postgresql.Image;

public class UpdateImageEvent extends ApplicationEvent {
	private Image image;
	public UpdateImageEvent(Object source, Image image){
		super(source);
		this.image = image;
	}
	
	public Image getImage(){
		return image;
	}
}
