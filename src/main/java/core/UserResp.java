package core;


public class UserResp {

	private final String username;
	private final String password;
	private final boolean enabled;
	
	public UserResp(String username, String password, boolean enabled) {
		this.username = username;
		this.password = password;
		this.enabled = enabled;
	}
	
	public boolean getEnabled(){
		return enabled;
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getPassword(){
		return password;
	}
}
