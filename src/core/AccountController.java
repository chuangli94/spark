package core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {
	
	@Autowired
	AccountRepository accRepo;
	
	@RequestMapping("/insertbyusernameandpassword")
	public void insertByUsernameAndPassword(@RequestParam(value="username") String username, @RequestParam(value="password") String password){
		accRepo.save(new Account(username, password));
	}
	
	@RequestMapping("/selectbyusername")
	public List<AccountResp> selectByUsername(@RequestParam(value="username") String username){
		List<AccountResp> accList = new ArrayList<AccountResp>();
		for (Account acc: accRepo.findByUsername(username)){
			accList.add(new AccountResp(acc.getId(), acc.getUsername(), acc.getPassword()));
		}
		return accList;
	}
}
