package core.task;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.filters.Canvas;
import net.coobird.thumbnailator.geometry.Positions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import core.event.BroadcastItemEvent;
import core.event.UpdateImageEvent;
import core.event.UpdateTagEvent;
import core.mongodb.UserDocument;
import core.mongodb.UserDocumentRepository;
import core.postgresql.Category;
import core.postgresql.CategoryRepository;
import core.postgresql.Image;
import core.postgresql.ImageRepository;
import core.postgresql.Keyword;
import core.postgresql.KeywordRepository;
import core.postgresql.Tag;
import core.postgresql.TagRepository;
import core.response.ClarifaiResp;
import core.response.ClarifaiResult;
import core.response.ClarifaiTag;
import core.response.OAuth2AccessTokenResp;
import core.response.RekognitionMatch;
import core.response.RekognitionResp;
import core.response.RekognitionSceneUnderstanding;
import core.wordutils.WS4JCalculateWordSimilarity;
import edu.washington.cs.knowitall.morpha.MorphaStemmer;

@Service
public class MediaProcessTask {
	@Autowired
	UserDocumentRepository userDocumentRepo;
	@Autowired
	ImageRepository imageRepo;
	@Autowired
	TagRepository tagRepo;
	@Autowired
	KeywordRepository keywordRepo;
	@Autowired
	CategoryRepository categoryRepo;
	@Autowired
    private ApplicationEventPublisher publisher;
	private static String clarifaiAccessToken;
	@Value("${awsuseruploadbucketname}")
	private String awsUserUploadBucketName;
	@Value("${awstagprocessbucketname}")
	private String awsTagProcessBucketName;
	@Value("${clarifaiapiurl}")
	private String clarifaiApiUrl;
	@Value("${clarifaiapikey}")
	private String clarifaiApikey;
	@Value("${clarifaiapisecret}")
	private String clarifaiApiSecret;
	@Value("${rekognitionapikey}")
	private String rekognitionApiKey;
	@Value("${rekognitionapisecret}")
	private String rekognitionApiSecret;



//	@PersistenceContext
//	private EntityManager em;
	
    
	@Async
	public void processMedia(String token, BufferedImage bi, String fileName, String name){
		File tempFile = null;
        AmazonS3Client s3client = null;
        String key = null;
		try {
			
			String extension = null;
			try {
			extension = fileName.substring(fileName.lastIndexOf("."));
			} catch(StringIndexOutOfBoundsException siobe){
				System.out.println("Cannot get filename extension, assuming jpg");
				extension = ".jpg";
			}
	//		tempFile = new File(this.name + String.valueOf(System.currentTimeMillis()));
			
	       
	        
			ByteArrayOutputStream byteArrOutput = new ByteArrayOutputStream();
			ByteArrayOutputStream tagProcessByteArrOutput = new ByteArrayOutputStream();
	//        ByteArrayOutputStream imageByteArrOutput = new ByteArrayOutputStream();
	//        BufferedImage bufferedImage = null;
			if (extension.equals(".jpg")){
				Thumbnails.of(bi).size(960, 1200).crop(Positions.CENTER).addFilter(new Canvas(960, 1600, Positions.CENTER, true)).outputQuality(0.8).outputFormat("jpg").toOutputStream(byteArrOutput);
				Thumbnails.of(bi).size(1024, 1024).outputQuality(0.8).outputFormat("jpg").toOutputStream(tagProcessByteArrOutput);
	//            bufferedImage = ImageIO.read (new ByteArrayInputStream(byteArr));
	//            ImageIO.write(bufferedImage, "jpg", imageByteArrOutput);
			} else {
				return;
			}
	
			byte[] compressedByteArr = byteArrOutput.toByteArray();
			byte[] tagProcessByteArr = tagProcessByteArrOutput.toByteArray();
			InputStream is = new ByteArrayInputStream(compressedByteArr);
			InputStream tagProcessIs = new ByteArrayInputStream(tagProcessByteArr);
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
	        
	    	key = name + "/" + hashedName;
	    	String itemName = "item/" + key;
	
	        ObjectMetadata objectMetaData = new ObjectMetadata();
	        objectMetaData.setContentLength(compressedByteArr.length);
	        objectMetaData.setContentType("image/jpeg");
	        
	        ObjectMetadata tagProcessMetaData = new ObjectMetadata();
	        tagProcessMetaData.setContentLength(tagProcessByteArr.length);
	        tagProcessMetaData.setContentType("image/jpeg");
	
	        String s3TagProcessUrl = null;
	
	        if (imageRepo.findByHash(itemName).isEmpty()){
	            	
	        	s3client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
	
	           try {
	        	   	System.out.println(" Uploading to S3...");
	        		s3client.putObject(new PutObjectRequest(awsUserUploadBucketName, key, is, objectMetaData));
	            	System.out.println("Uploading to s3 to generate tags...");
	        		s3client.putObject(
	        				   new PutObjectRequest(awsTagProcessBucketName, key, tagProcessIs, tagProcessMetaData)
	        				      .withCannedAcl(CannedAccessControlList.PublicRead));
	        		s3TagProcessUrl = s3client.getResourceUrl(awsTagProcessBucketName, key);
	        		s3TagProcessUrl = s3TagProcessUrl.replaceFirst("https", "http");
	            	
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
	        
	        if (s3TagProcessUrl != null){
	        	
	            try{
	            	System.out.print(" Generating Clarifai Tags...");
	            	
	
	            	ClarifaiResp clarifaiResp = null;
	            	try {
		                String clarifaiRequestUrl = clarifaiApiUrl + "tag/?url=" + s3TagProcessUrl;
		                RestTemplate restTemplate = new RestTemplate();
		                MappingJackson2HttpMessageConverter jsonToPojo = new MappingJackson2HttpMessageConverter();
		                jsonToPojo.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		                restTemplate.getMessageConverters().add(jsonToPojo);
		                HttpHeaders headers = new HttpHeaders();
		                headers.add("Authorization", "Bearer " + clarifaiAccessToken);
		                HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		                ResponseEntity<ClarifaiResp> responseEntity;
		                responseEntity = restTemplate.exchange(clarifaiRequestUrl, HttpMethod.GET, httpEntity, ClarifaiResp.class);
		                clarifaiResp = responseEntity.getBody();
	            	} catch (Exception e){
	            		System.out.println("Attempting to obtain clarifai token...");
	            		String clarifaiTokenRequestUrl = clarifaiApiUrl + "token/?grant_type=client_credentials&client_id="
	            				+ clarifaiApikey + "&client_secret=" + clarifaiApiSecret;
		                RestTemplate restTemplate2 = new RestTemplate();
		                MappingJackson2HttpMessageConverter jsonToPojo2 = new MappingJackson2HttpMessageConverter();
		                jsonToPojo2.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		                restTemplate2.getMessageConverters().add(jsonToPojo2);
		                HttpHeaders headers2 = new HttpHeaders();
		                HttpEntity<String> httpEntity2 = new HttpEntity<String>(headers2);
		                ResponseEntity<OAuth2AccessTokenResp> responseEntity2;
		                responseEntity2 = restTemplate2.exchange(clarifaiTokenRequestUrl, HttpMethod.POST, httpEntity2, OAuth2AccessTokenResp.class);
		                OAuth2AccessTokenResp oAuth2TokenResp = responseEntity2.getBody();
		                clarifaiAccessToken = oAuth2TokenResp.getAccessToken();
		                
		                String clarifaiRequestUrl = clarifaiApiUrl + "tag/?url=" + s3TagProcessUrl;
		                RestTemplate restTemplate = new RestTemplate();
		                MappingJackson2HttpMessageConverter jsonToPojo = new MappingJackson2HttpMessageConverter();
		                jsonToPojo.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		                restTemplate.getMessageConverters().add(jsonToPojo);
		                HttpHeaders headers = new HttpHeaders();
		                headers.add("Authorization", "Bearer " + clarifaiAccessToken);
		                HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		                ResponseEntity<ClarifaiResp> responseEntity;
		                responseEntity = restTemplate.exchange(clarifaiRequestUrl, HttpMethod.GET, httpEntity, ClarifaiResp.class);
		                clarifaiResp = responseEntity.getBody();
	            	}
	                
	                
	            	System.out.print(" Generating Rekognition Tags...");
	                String rekognitionRequestUrl = "http://rekognition.com/func/api?api_key="
	                + rekognitionApiKey + "&api_secret=" + rekognitionApiSecret + "&jobs=scene_understanding_3&urls="
	                + s3TagProcessUrl;
	                RestTemplate restTemplate2 = new RestTemplate();
	                MappingJackson2HttpMessageConverter jsonToPojo2 = new MappingJackson2HttpMessageConverter();
	                jsonToPojo2.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
	                restTemplate2.getMessageConverters().add(jsonToPojo2);
	                RekognitionResp rekognitionResp = restTemplate2.getForObject(rekognitionRequestUrl, RekognitionResp.class);
	                RekognitionSceneUnderstanding rekognitionSceneUnderstanding = rekognitionResp.getRekognitionSceneUnderstanding();
	
	                
	            	ClarifaiResult clarifaiResult = clarifaiResp.getResults().get(0);
	            	ClarifaiTag clarifaiTag = clarifaiResult.getClarifaiFinalResult().getClarifaiTag();
	            	
	               
	                
	            	List<Category> categoryList = categoryRepo.findAll();
	            	List<Keyword> keywordList = keywordRepo.findAll();
	            	HashSet<String> keywordStringSet = new HashSet<>();
	            	
	            	List<String> clarifaiSuccessKeywordStrings = new ArrayList<>();
	            	List<String> rekognitionSuccessKeywordStrings = new ArrayList<>();
	            	List<Keyword> successKeywordList = new ArrayList<>();
	            	List<String> successCategoryTags = new ArrayList<>();
	            	List<String> stemmedKeywordString = new ArrayList<String>();
	            	List<String> stemmedClarifaiTags = new ArrayList<>();
	            	List<String> stemmedRekognitionTags = new ArrayList<>();
	            	Hashtable<String, Keyword> stemmedStringToKeyword = new Hashtable<String, Keyword>();
	            	for (Keyword keyword : keywordList){
	            		String keywordString = keyword.getName().toLowerCase();
	            		String stemmed = MorphaStemmer.stem(keywordString);
	            		stemmedStringToKeyword.put(stemmed, keyword);
	            		stemmedKeywordString.add(stemmed);
	            	}
	            	int resultSize = clarifaiTag.getClasses().size();
	            	
	            	for (int itr = 0; itr < 4; itr++){
	            		stemmedClarifaiTags.add(MorphaStemmer.stem(clarifaiTag.getClasses().get(itr).toLowerCase()));
	            	}
	            	
	                for (RekognitionMatch match: rekognitionSceneUnderstanding.getRekognitionMatchList()){
	                	if (match.getScore() >= 0.5){
	                		stemmedRekognitionTags.add(MorphaStemmer.stem(match.getTag().toLowerCase()));
	                	}
	                }
	                
	                
	        		for (int keyworditr = 0; keyworditr < stemmedKeywordString.size(); keyworditr++){
	        			String keyword = stemmedKeywordString.get(keyworditr);
	                	for (int itr = 0; itr < stemmedClarifaiTags.size(); itr++){
	                		String tagString = stemmedClarifaiTags.get(itr);
	            			if (keyword.equals(tagString)){
	            				clarifaiSuccessKeywordStrings.add(keyword);
	            				System.out.println("MATCH " + keyword);
	            			} else if (WS4JCalculateWordSimilarity.getJcn(keyword, tagString) >= 0.42){
	            				System.out.println("JCN " + keyword + "-" + tagString + " " + WS4JCalculateWordSimilarity.getJcn(keyword, tagString));
	            				rekognitionSuccessKeywordStrings.add(keyword);
	            			} else if (WS4JCalculateWordSimilarity.getLin(keyword, tagString) >= 0.85){
	            				System.out.println("LIN " + keyword + "-" + tagString + " " + WS4JCalculateWordSimilarity.getLin(keyword, tagString));
	            				rekognitionSuccessKeywordStrings.add(keyword);
	            			} else if (WS4JCalculateWordSimilarity.getPath(keyword, tagString) >= 0.60){
	            				System.out.println("PATH " + keyword + "-" + tagString + " " + WS4JCalculateWordSimilarity.getPath(keyword, tagString));
	            				clarifaiSuccessKeywordStrings.add(keyword);
	            			} else if ((WS4JCalculateWordSimilarity.getWup(keyword, tagString) < 1.0) && (WS4JCalculateWordSimilarity.getWup(keyword, tagString) >= 0.94)){
	            				System.out.println("WUP " + keyword + "-" + tagString + " " + WS4JCalculateWordSimilarity.getWup(keyword, tagString));
	            				clarifaiSuccessKeywordStrings.add(keyword);
	            			}
	//            			else if (WS4JCalculateWordSimilarity.getLcn(keyword, tagString) >= 3.0){
	//            				System.out.println("LCN " + keyword + "-" + tagString + " " + WS4JCalculateWordSimilarity.getLcn(keyword, tagString));
	//            				rekognitionSuccessKeywordStrings.add(keyword);
	//            			} 
	//            			else if (WS4JCalculateWordSimilarity.getResnik(keyword, tagString) >= 1){
	//            				System.out.println("RESNIK " + keyword + "-" + tagString + " " + WS4JCalculateWordSimilarity.getResnik(keyword, tagString));
	//            				rekognitionSuccessKeywordStrings.add(keyword);
	//            			} 
	//            			} else if (AdwCalculateWordSimilarity.getADWScore(keyword, tagString) >= 0.80){
	//            				System.out.println("ADW " + keyword + "-" + tagString + " " + AdwCalculateWordSimilarity.getADWScore(keyword, tagString));
	//            				clarifaiSuccessKeywordStrings.add(keyword);
	//            			}
	                	}
	                	for (int itr = 0; itr < stemmedRekognitionTags.size(); itr++){
	                		String tagString = stemmedRekognitionTags.get(itr);
	                		if (keyword.equals(tagString)){
	                			rekognitionSuccessKeywordStrings.add(keyword);
	            				System.out.println("MATCH " + keyword);
	            			} else if (WS4JCalculateWordSimilarity.getJcn(keyword, tagString) >= 0.42){
	            				System.out.println("JCN " + keyword + "-" + tagString + " " + WS4JCalculateWordSimilarity.getJcn(keyword, tagString));
	            				rekognitionSuccessKeywordStrings.add(keyword);
	            			} else if (WS4JCalculateWordSimilarity.getPath(keyword, tagString) >= 0.60){
	            				System.out.println("PATH " + keyword + "-" + tagString + " " + WS4JCalculateWordSimilarity.getPath(keyword, tagString));
	            				rekognitionSuccessKeywordStrings.add(keyword);
	            			} else if (WS4JCalculateWordSimilarity.getLin(keyword, tagString) >= 0.85){
	            				System.out.println("LIN " + keyword + "-" + tagString + " " + WS4JCalculateWordSimilarity.getLin(keyword, tagString));
	            				clarifaiSuccessKeywordStrings.add(keyword);
	            			} else if ((WS4JCalculateWordSimilarity.getWup(keyword, tagString) < 1.0) && (WS4JCalculateWordSimilarity.getWup(keyword, tagString) >= 0.94)){
	            				System.out.println("WUP " + keyword + "-" + tagString + " " + WS4JCalculateWordSimilarity.getWup(keyword, tagString));
	            				clarifaiSuccessKeywordStrings.add(keyword);
	            			}
	//                		else if (WS4JCalculateWordSimilarity.getLcn(keyword, tagString) >= 3.0){
	//            				System.out.println("LCN " + keyword + "-" + tagString + " " + WS4JCalculateWordSimilarity.getLcn(keyword, tagString));
	//            				rekognitionSuccessKeywordStrings.add(keyword);
	//            			} 
	//            			else if (WS4JCalculateWordSimilarity.getResnik(keyword, tagString) >= 7){
	//            				System.out.println("RESNIK " + keyword + "-" + tagString + " " + WS4JCalculateWordSimilarity.getResnik(keyword, tagString));
	//            				rekognitionSuccessKeywordStrings.add(keyword);
	//            			} 
	//            			else if (AdwCalculateWordSimilarity.getADWScore(keyword, tagString) >= 0.80){
	//            				System.out.println("ADW " + keyword + "-" + tagString + " " + AdwCalculateWordSimilarity.getADWScore(keyword, tagString));
	//            				rekognitionSuccessKeywordStrings.add(keyword);
	//            			}
	                	}
	        		}
	            
	            	
	        		for (Keyword keyword: keywordList){
	                	for (String success: clarifaiSuccessKeywordStrings){
	                		if (success.equals(MorphaStemmer.stem(keyword.getName().toLowerCase()))){
	                			successKeywordList.add(keyword);
	                		}
	                	}
	                	for (String success: rekognitionSuccessKeywordStrings){
	                		if (success.equals(MorphaStemmer.stem(keyword.getName().toLowerCase()))){
	                			successKeywordList.add(keyword);
	                		}
	                	}
	        		}
	            	
	            	
	            	for (Keyword successKeyword: successKeywordList){
	            		int categoryId = successKeyword.getCategory();
	            			for (Category category: categoryList){
	            				if (category.getId() == categoryId){
	            					successCategoryTags.add(category.getName());
	            				}
	            			}
	            	}
	            	
	
	            	if (stemmedClarifaiTags.contains("one") || stemmedClarifaiTags.contains("nobody") 
	            			|| stemmedClarifaiTags.contains("isolated")){
	            			List<String> removedSocialCategoryTags = new ArrayList<String>();
	            		for (String successCategoryTag: successCategoryTags){
	            			if (!successCategoryTag.equalsIgnoreCase("Social")){
	            				removedSocialCategoryTags.add(successCategoryTag);
	            			}
	            		}
	            			successCategoryTags = removedSocialCategoryTags;
	            	}
	            	
	
	            	List<String> finalCategories = new ArrayList<>();
	            	double sizeCategories = (double) successCategoryTags.size();
	            	Collections.sort(successCategoryTags);
	            	double maxCategoryNum = 0D;
	            	double currentCategoryNum = 0D;
	            	String maxCategoryString = null;
	            	String currentCategoryString = null;
	            	String last = null;
	            	for (String current: successCategoryTags){
	            		if ((last == null) || current.equals(last)){
	            			currentCategoryNum++;
	            			currentCategoryString = current;
	            		} else {
	            			if (currentCategoryNum > maxCategoryNum){
	            				maxCategoryNum = currentCategoryNum;
	            				maxCategoryString = currentCategoryString;
	            			}
	            			currentCategoryNum = 1D;
	            			currentCategoryString = current;
	            		}
	            		last = current;
	            	}
	            	
	    			if (currentCategoryNum > maxCategoryNum){
	    				maxCategoryNum = currentCategoryNum;
	    			}
	    			
	    			if ((maxCategoryNum / sizeCategories) > 0.40){
	    				finalCategories.add(maxCategoryString);
	    			}
	    			
	    			String categoriesToSave = new String();
	    			for (String finalCategory: finalCategories){
	    				categoriesToSave = categoriesToSave + finalCategory + ",";
	    			}
	    			
	    			if (!categoriesToSave.isEmpty()){
	    				categoriesToSave = categoriesToSave.substring(0, categoriesToSave.length() - 1);
	    			}
	    			
	            	System.out.println("Finished Calculating Category Score");
	                
	                    	
	            	
	                String tagsString = new String();
	                List<String> listOfTags = new ArrayList<String>();
	                for (String tag: stemmedClarifaiTags){
	                	listOfTags.add(tag);
	                	tagsString = tagsString + tag + ",";
	                }
	                for (String tag: stemmedRekognitionTags){
	                	listOfTags.add(tag);
	                	tagsString = tagsString + tag + ",";
	                }
	                
	                
	                if (!tagsString.isEmpty()){
	                	tagsString = tagsString.substring(0, tagsString.lastIndexOf(","));
	            	}
	                
	                HashSet<String> uniqueSet = new HashSet<String>(listOfTags);
	                
	                
	                
	    			if (categoriesToSave.isEmpty()){
	    				categoriesToSave = null;
	    			}
	    			if (finalCategories.isEmpty()){
	    				finalCategories = null;
	    			}
	                listOfTags = new ArrayList<String>(uniqueSet);    
	                Image image = new Image(itemName, null, tagsString, categoriesToSave);          
	    			
	            	System.out.print(" Saving to database...");
	            	
	    			publisher.publishEvent(new UpdateTagEvent(this, listOfTags));
	    			publisher.publishEvent(new UpdateImageEvent(this, image));
	    			publisher.publishEvent(new BroadcastItemEvent(this, name, itemName, finalCategories));
	                
	            } catch (Exception e){
	            	System.out.println(e.getMessage());
	            }
            } else {
            	System.out.println(" s3processUrl is null");
            }
        } else {
        	System.out.println(" Image already exists in database");
        }
	} catch (Exception e){
		System.out.println(e);
	} finally {
		if (tempFile != null){
			tempFile.delete();
		} 
		if (s3client != null){
			if (key != null)
				s3client.deleteObject(awsTagProcessBucketName, key);
		}
		
	}
		
	return;
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
