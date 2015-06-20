package main.java.core;

public class AccountResp {
	private final long id;
	private final String username;
	private final String password;
	
	public AccountResp(long id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
	}
	
	public Long getId(){
		return id;
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getPassword(){
		return password;
	}
}
