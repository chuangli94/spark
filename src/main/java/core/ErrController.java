package core;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrController implements ErrorController {
	
	private static final String PATH = "/error";
	
	@RequestMapping(value = PATH)
	public String error(){
		return "You have entered an invalid request.";
	}
	
	@Override
	public String getErrorPath(){
		return PATH;
	}
}
