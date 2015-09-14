package core.mysql;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="users")
public class User implements Serializable {
	
	@Column(nullable = false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Id
	@Column(nullable = false, unique = true)
	private String username;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false)
	private boolean enabled;
	
	@Column
	private String accessToken;
	
	@Column
	private String gcmRegId;
	
	@Column
	private String profilePic;
	
	protected User() {}
	
	public User(String username, String password, String gcmRegId){
		this.username = username;
		this.password = password;
		this.gcmRegId = gcmRegId;
		this.enabled = true;
	}
	
	public String getGcmRegId(){
		return gcmRegId;
	}
	
	public void setGcmRegId(String gcmRegId){
		this.gcmRegId = gcmRegId;
	}

	public String getAccessToken(){
		return accessToken;
	}
	
	public void setAccessToken(String accessToken){
		this.accessToken = accessToken;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public boolean getEnabled(){
		return enabled;
	}
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	public String getUsername(){
		return username;
	}
	public void setUsername(String username){
		this.username = username;
	}
	
	public String getPassword(){
		return password;
	}
	
	public void setPassword(String password){
		this.password = password;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}
	
	public String getProfilePic() {
		return this.profilePic;
	}

}
