package core.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.filters.Canvas;
import net.coobird.thumbnailator.geometry.Positions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import core.mongodb.UserDocument;
import core.mongodb.UserDocumentRepository;
import core.mysql.User;
import core.mysql.UserRepository;
import core.task.ProfilePicProcessThread;

@RestController
public class ProfileController {
	@Autowired
	UserRepository userRepo;
	@Autowired
	UserDocumentRepository userDocumentRepo;
	@Value("${awsuserprofilebucketname}")
	private String awsUserProfileBucketName;
	
	@RequestMapping(value="/app/checkprofilepic", method=RequestMethod.GET)
	public String checkProfilePic(@RequestHeader("Authorization") String token){
		User user = userRepo.findByAccessToken(token.substring("Bearer ".length()));
		if (user == null){
			return "false";
		}
		String name = user.getUsername();
		UserDocument userDocument = userDocumentRepo.findByName(name);
		if (userDocument.getProfilePictureKey() != null){
			return "true";
		} else return "false";
	}
	
	@RequestMapping(value="/app/uploadprofilepic", method=RequestMethod.POST)
	public String uploadProfilePic(@RequestHeader("Authorization") String token, @RequestParam("file") MultipartFile multiPartFile){
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
		
		new Thread(new ProfilePicProcessThread(token, bi, fileName, name, null) {
		    public void run() {
		    	File tempFile = null;
		    	try {
		    		String extension = null;
		    		try {
						extension = this.fileName.substring(this.fileName.lastIndexOf("."));
			    		} catch(StringIndexOutOfBoundsException siobe){
			    			System.out.println("Cannot get filename extension, assuming jpg");
			    			extension = ".jpg";
			    		}
					 
//					tempFile = new File(this.name + String.valueOf(System.currentTimeMillis()));
					
		           
		            
					ByteArrayOutputStream byteArrOutput = new ByteArrayOutputStream();
//		            ByteArrayOutputStream imageByteArrOutput = new ByteArrayOutputStream();
//		            BufferedImage bufferedImage = null;
					if (extension.equals(".jpg")){
						Thumbnails.of(this.bi).size(960, 1200).crop(Positions.CENTER).addFilter(new Canvas(960, 1600, Positions.CENTER, true)).outputQuality(0.8).outputFormat("jpg").toOutputStream(byteArrOutput);
//			            bufferedImage = ImageIO.read (new ByteArrayInputStream(byteArr));
//			            ImageIO.write(bufferedImage, "jpg", imageByteArrOutput);
					}

					byte[] compressedByteArr = byteArrOutput.toByteArray();
					InputStream is = new ByteArrayInputStream(compressedByteArr);
		            
	            	String key = this.name + "/profilepic.jpg";

		            ObjectMetadata objectMetaData = new ObjectMetadata();
		            objectMetaData.setContentLength(compressedByteArr.length);
		            objectMetaData.setContentType("image/jpeg");
		            
		            	AmazonS3Client s3client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
		            	try { 
				            System.out.print(" Uploading to S3...");
		            		s3client.putObject(new PutObjectRequest(awsUserProfileBucketName, key, is, objectMetaData));   	
		            	}  catch (AmazonServiceException ase) {
			                System.out.println("Caught an AmazonServiceException, which " +
			                		"means your request made it " +
			                        "to Amazon S3, but was rejected with an error response" +
			                        " for some reason.");
			                System.out.println("Error Message:    " + ase.getMessage());
			                System.out.println("HTTP Status Code: " + ase.getStatusCode());
			                System.out.println("AWS Error Code:   " + ase.getErrorCode());
			                System.out.println("Error Type:       " + ase.getErrorType());
			                System.out.println("Request ID:       " + ase.getRequestId());
			            } catch (AmazonClientException ace) {
			                System.out.println("Caught an AmazonClientException, which " +
			                		"means the client encountered " +
			                        "an internal error while trying to " +
			                        "communicate with S3, " +
			                        "such as not being able to access the network.");
			                System.out.println("Error Message: " + ace.getMessage());
			            }
		            
		            // update mongodb
		            UserDocument userDocument = userDocumentRepo.findByName(name);
		            userDocument.setProfilePictureKey(key);
		            userDocumentRepo.save(userDocument);
		            
		            // update SQL library as well
		            user.setProfilePic(key);
		            userRepo.save(user);
		            

				} catch (Exception e){
					System.out.println(e);
				} finally {
					if (tempFile != null){
						tempFile.delete();
					}
				}
		    }	
		}).start();
		
        return "success";
		}
}
