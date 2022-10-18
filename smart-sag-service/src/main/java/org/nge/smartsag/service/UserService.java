package org.nge.smartsag.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.nge.smartsag.dao.UserDao;
import org.nge.smartsag.domain.User;

@ApplicationScoped
@Path("/users")
public class UserService {

	@Inject
	UserDao userDao;
	
	@GET
	public User findUser(@QueryParam("email") String email, @QueryParam("phone") String phone) {
		User user = null;
		if (email == null && phone == null) {
			// TODO: this call is not allowed
		}
		
		if (phone != null) {
			user = userDao.findByPhone(phone);
		}
		else {
			user = userDao.findByEmail(email);
		}
		return user;
	}
}