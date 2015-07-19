package core.mysql;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="images")
public class Image implements Serializable{
	@Column(nullable = false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Id
	@Column(nullable = false)
	private String hash;
	
	@Column
	private String query;
	
	@Column
	private String tags;
	
	
	protected Image (){}

	
	public Image(String hash, String query, String tags){
		this.hash = hash;
		this.query = query;
		this.tags = tags;
	}
	
	
	public String getHash(){
		return hash;
	}
	
	public void setHash(String hash){
		this.hash = hash;
	}
	
	public String getQuery(){
		return query;
	}
	
	public void setQuery(String query){
		this.query = query;
	}
	public String getTags(){
		return tags;
	}
	
	public void setTags(String tags){
		this.tags = tags;
	}
}

	

