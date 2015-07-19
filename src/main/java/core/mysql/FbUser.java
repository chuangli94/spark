package core.mysql;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="fb_users")
public class FbUser implements Serializable{
	
	@Column(nullable = false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Id
	@Column(nullable = false)
	private String userId;
	
	@Column
	private String accessToken;
	
	protected FbUser (){}

	
	public FbUser(String userId){
		this.userId = userId;
	}
	
	
	public String getAccessToken(){
		return accessToken;
	}
	
	public void setAccessToken(String accessToken){
		this.accessToken = accessToken;
	}
	
	public String getUserId(){
		return userId;
	}
	
	public int getId(){
		return id;
	}
	
	public void setUserId(String userId){
		this.userId = userId;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
}
