package core.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.ImageOutputStreamImpl;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.filters.Canvas;
import net.coobird.thumbnailator.geometry.Positions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.neo4j.core.GraphDatabase;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import core.mongodb.UserDocumentRepository;
import core.mysql.ImageRepository;
import core.mysql.User;
import core.mysql.UserRepository;
import core.neo4j.ItemRelationshipRepository;
import core.neo4j.SubscribedRelationshipRepository;
import core.neo4j.UserNodeRepository;
import core.thread.MediaProcessThread;

@RestController
public class UploadController {
	@Autowired
	UserRepository userRepo;
	@Autowired
	UserNodeRepository userNodeRepo;
	@Autowired
	ItemRelationshipRepository itemRelationshipRepo;
	@Autowired
	SubscribedRelationshipRepository subscribedRelationshipRepo;
	@Autowired
	GraphDatabase graphDatabase;
	@Autowired
	Neo4jTemplate neo4jTemplate;
	@Autowired
	UserDocumentRepository userDocumentRepo;
	@Autowired
	ImageRepository imageRepo;
	@Value("${awsuseruploadbucketname}")
	private String awsUserUploadBucketName;
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
		
		new Thread(new MediaProcessThread(token, bi, fileName, name) {
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
					} else {
						return;
					}

					byte[] compressedByteArr = byteArrOutput.toByteArray();
					InputStream is = new ByteArrayInputStream(compressedByteArr);
					ByteArrayInputStream inputStream = null;
					String hashedName = null;

		            try {
						ByteArrayOutputStream originalByteArrOutput = new ByteArrayOutputStream();
						ImageIO.write(bi, "jpg", originalByteArrOutput);
		            	inputStream = new ByteArrayInputStream(originalByteArrOutput.toByteArray());
		                MessageDigest digest = MessageDigest.getInstance("MD5");
		         
		                byte[] bytesBuffer = new byte[1024];
		                int bytesRead = -1;
		         
		                while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
		                    digest.update(bytesBuffer, 0, bytesRead);
		                }
		         
		                byte[] hashedBytes = digest.digest();

		                
		                hashedName = convertByteArrayToHexString(hashedBytes) + extension;
		            } catch (Exception e){
		            	System.out.println("Failed to Hash.");
		            } finally {
		            	if (inputStream != null){
		                    inputStream.close();
		            	}
		            		
		            }
		            
	            	String key = this.name + "/" + hashedName;

		            ObjectMetadata objectMetaData = new ObjectMetadata();
		            objectMetaData.setContentLength(compressedByteArr.length);
		            objectMetaData.setContentType("image/jpeg");

		            try{

		            	AmazonS3Client s3client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
		            	GetObjectMetadataRequest getObjectMetadataRequest = new GetObjectMetadataRequest(awsUserUploadBucketName, key);
			            	try { 
			            		s3client.getObjectMetadata(getObjectMetadataRequest);
					            System.out.print(" Key already exists...");
			            		return;
			            	} catch (AmazonS3Exception e){  // this means the key does not exist 
					            System.out.print(" Uploading to S3...");
			            		s3client.putObject(new PutObjectRequest(awsUserUploadBucketName, key, is, objectMetaData));
			            	}
			            	
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
		            
		            
		            try {
		            	final String broadcastItemUrl = "http://localhost:8080" + "/app/broadcastitem";
		                RestTemplate restTemplate = new RestTemplate();
		                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		                HttpHeaders headers = new HttpHeaders();
		                headers.add("Authorization", this.token);
		                headers.add("Item", "item/" + key);
		                HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		                ResponseEntity<String> responseEntity;
		                responseEntity = restTemplate.exchange(broadcastItemUrl, HttpMethod.GET, httpEntity, String.class);
		                String response = responseEntity.getBody();
		            	if (response == null || !response.equals("success")){
		            		System.out.println("Failed to broadcast uploaded item");
		            	}
		            	
		            } catch (Exception e){
		            	System.out.println(e);
		            	System.out.println("Failed to broadcast uploaded item because cannot get String with restTemplate");
		            }
		            
//		            try {
//		            	final String pushItemUrl = "http://localhost:8080" + "/pushitem";
//		                RestTemplate restTemplate = new RestTemplate();
//		                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
//		                HttpHeaders headers = new HttpHeaders();
//		                headers.add("Authorization", this.token);
//		                headers.add("Item", key);
//		                headers.add("Like", "1");
//		                HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
//		                ResponseEntity<String> responseEntity;
//		                responseEntity = restTemplate.exchange(pushItemUrl, HttpMethod.GET, httpEntity, String.class);
//		                String response = responseEntity.getBody();
//		            	if (!response.equals("success")){
//		            		System.out.println("Failed to push uploaded item");
//		            	}
//		            	
//		            } catch (Exception e){
//		            	System.out.println(e);
//		            }
		            

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
	
    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

}
