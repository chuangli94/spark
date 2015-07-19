package core;

import java.util.List;

public class QueueResp {
	private List<String> queue;
	
	public QueueResp(List<String> queue){
		this.queue = queue;
	}
	
	public List<String> getQueue(){
		return queue;
	}
	
}
