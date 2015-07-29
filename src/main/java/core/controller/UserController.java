package core.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import core.mysql.User;
import core.mysql.UserRepository;
import core.response.UserResp;

@RestController
@RequestMapping("/db")
public class UserController {
	
	@Autowired
	UserRepository userRepo;
	
	@RequestMapping(method=RequestMethod.GET)
	public List<UserResp> selectByUsername(@RequestParam(value="username") String username){
		List<UserResp> userList = new ArrayList<UserResp>();
		for (User user: userRepo.findByUsername(username)){
			userList.add(new UserResp(user.getUsername(), user.getPassword(), user.getEnabled()));
		}
		return userList;
	}

}
