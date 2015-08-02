package core.task;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import core.event.UpdateImageEvent;
import core.event.UpdateTagEvent;
import core.mysql.ImageRepository;
import core.mysql.Tag;
import core.mysql.TagRepository;

@Service
@Transactional("mysqlTransactionManager")
public class MySqlTaskQueue {
	
	@Autowired
	TagRepository tagRepo;
	@Autowired
	ImageRepository imageRepo;
    @Autowired
	private ApplicationEventPublisher publisher;
    
	Queue<UpdateTagEvent> tagUpdateEvents = new LinkedList<UpdateTagEvent>();
	
	@EventListener
	public void handleUpdateTag(UpdateTagEvent event){
        try {
			List<String> tagStrings= event.getTags();
			List<String> existingTagStrings = new ArrayList<String>();
			List<Tag> newTags = new ArrayList<Tag>();
			List<Tag> existingTags = tagRepo.findByNameIn(tagStrings);
			
			for (Tag tag: existingTags){
				tag.setCount(tag.getCount() + 1);
				existingTagStrings.add(tag.getName());
			}
			for (String tagString: tagStrings){
				if (!existingTagStrings.contains(tagString)){
					newTags.add(new Tag(tagString, 1));
				}
			}
			
			existingTags.addAll(newTags);
			tagRepo.save(existingTags);
            System.out.println("Done Tags!");        
        } catch (Exception e){
        	System.out.println("Failed to save tags to database.");
        }	
	}
	
	@EventListener
	public void handleUpdateImage(UpdateImageEvent event){
        try {
    		imageRepo.save(event.getImage());
            System.out.println("Done Images!");      
        } catch (Exception e){
        	System.out.println("Failed to save image to database.");
        }   
	}
    
    
}


