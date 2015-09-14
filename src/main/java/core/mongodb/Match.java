package core.mongodb;

import org.springframework.util.Assert;

public class Match {
	
	private String name;
	private Boolean shown;
	
	public Match() {}
	
	public Match(String name, Boolean isShown) {
		this.name = name;
		this.shown = isShown;
	}
	
	public Boolean getShown() {
		return this.shown;
	}
	
	public void setShown(Boolean isShown) {
		this.shown = isShown;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
