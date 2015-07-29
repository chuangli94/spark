package core.thread;

import java.awt.image.BufferedImage;

public class MediaProcessThread implements Runnable {
	
	public String token;
	public BufferedImage bi;
	public String fileName;
	public String name;
	
	public MediaProcessThread(String token, BufferedImage bi, String fileName, String name){
		this.token = token;
		this.bi = bi;
		this.fileName = fileName;
		this.name = name;
	}
	
	public void run() {
		
	}
}

