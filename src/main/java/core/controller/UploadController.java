package core.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import core.event.UpdateTagEvent;
import core.postgresql.TagRepository;
import core.postgresql.User;
import core.postgresql.UserRepository;
import core.task.MediaProcessTask;

@EnableAsync
@RestController
public class UploadController{
	@Autowired
	UserRepository userRepo;
	@Autowired
	TagRepository tagRepo;
    @Autowired
	private ApplicationEventPublisher publisher;
    @Autowired
    private MediaProcessTask mediaProcessTask;
	
//	@Value("${awsstsendpoint}")
//	private String awsSTSEndpoint;
//	@Value("${awsuploadpolicybegin}")
//	private String awsUploadPolicyBegin;
//	@Value("${awsuploadpolicyend}")
//	private String awsUploadPolicyEnd;
	
//	@RequestMapping(value="/gets3uploadfolder")
//	public UploadResp getS3UploadFolder(@RequestHeader(value="Authorization") String token){
//		User user = userRepo.findByAccessToken(token.substring("Bearer ".length()));
//		UploadResp userFolderResp = new UploadResp(user.getUsername());
//		return userFolderResp;
//	}

//	int itr = 0;
	
//	public UploadController(){}
//	
//	@Autowired
//	public UploadController(EntityManagerFactory factory){
//	    if(factory.unwrap(SessionFactory.class) == null){
//	        throw new NullPointerException("factory is not a hibernate factory");
//	      }
//	      this.hibernateFactory = factory.unwrap(SessionFactory.class);
//	}
//	@Scheduled(fixedDelay=5000)
//	@Transactional
//	public void checkForUpdates(){
//	    System.out.println("Method executed at every 5 seconds. Current time is :: "+ new Date());
//		tagRepo.save(new Tag("finished" + itr, itr));
//		itr++;
//	}


//	@PersistenceContext
//	private EntityManager em;
	
    
    
	@RequestMapping(value="/app/uploaditem", method=RequestMethod.POST)
	public String uploadItem(@RequestHeader("Authorization") String token, @RequestParam("file") MultipartFile multiPartFile){
		

		User user = userRepo.findByAccessToken(token.substring("Bearer ".length()));
		if (user == null){
			return null;
		}
		String name = user.getUsername();
		String fileName = multiPartFile.getOriginalFilename();
		BufferedImage bi;
		try {
			bi = ImageIO.read (multiPartFile.getInputStream());
		} catch (IOException ioe){
			System.out.println(ioe);
			return null;
		}
		
		if (bi == null){
			return null;
		}
		
		mediaProcessTask.processMedia(token, bi, fileName, name);
        return "success";
	}
	

}
