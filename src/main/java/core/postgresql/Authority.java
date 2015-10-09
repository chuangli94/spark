package core.postgresql;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="authorities")
public class Authority implements Serializable {

	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(nullable = false)
	private String authority;
	
	@Column(nullable = false)
	private String username;
	
	protected Authority() {}
	
	public Authority(User user, String authority){
		this.username = user.getUsername();
		this.authority = authority;
	}


	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	public String getUsername(){
		return username;
	}
	
	public void setUsername(String username){
		this.username = username;
	}
	
	public String getAuthority(){
		return authority;
	}
	
	public void setAuthority(String authority){
		this.authority = authority;
	}
}
