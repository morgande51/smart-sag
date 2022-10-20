package org.nge.smartsag.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.nge.smartsag.domain.User;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

@ApplicationScoped
public class UserDaoImpl implements UserDao, PanacheRepository<User> {
	
//	find(User.FIND_BY_EMAIL, getAuthSubjectEmail()).firstResult();
	
	@Override
	public User findByEmail(String email) {
		return find(User.FIND_BY_EMAIL, email).firstResult();
	}
	
	@Override
	public User findByPhone(String phone) {
		return find(User.FIND_BY_PHONE, phone).firstResult();
	}
	
	@Override
	public List<User> findAdminByOrgId(Long orgId) {
		return list(User.FIND_ADMIN_BY_ORG_ID, Parameters.with("orgId", orgId));
	}
	
	@Override
	public User getUser(Long id) {
		return findById(id);
	}
}