package org.nge.smartsag.dao;

import java.util.List;

import org.nge.smartsag.domain.User;

public interface UserDao {

	User findByEmail(String email);
	
	User findByPhone(String phone);

	List<User> findAdminByOrgId(Long orgId);

	User getUser(Long id);
}