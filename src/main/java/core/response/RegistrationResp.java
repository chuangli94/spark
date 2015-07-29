package core.response;

public class RegistrationResp {
	private String status;
	
	public RegistrationResp(String status){
		this.status = status;
	}
	
	public String getStatus(){
		return status;
	}
}
