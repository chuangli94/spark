package core.task;

import java.awt.image.BufferedImage;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class ProfilePicProcessThread implements Runnable {
	
	public String token;
	public BufferedImage bi;
	public String fileName;
	public String name;
	public static String clarifaiAccessToken;
	
	//DO NOT USE THIS CLASS, code is migrating over to spring-based threads, this is just here until migration is complete
	public ProfilePicProcessThread(){}
	public ProfilePicProcessThread(String token, BufferedImage bi, String fileName, String name, String clarifaiAccessToken) { 
//			UserDocumentRepository userDocumentRepo, ImageRepository imageRepo, TagRepository tagRepo,
//			KeywordRepository keywordRepo, CategoryRepository categoryRepo)
//	{
		this.token = token;
		this.bi = bi;
		this.fileName = fileName;
		this.name = name;
		clarifaiAccessToken = clarifaiAccessToken;
	}

	public void run() {
		
	}
}

