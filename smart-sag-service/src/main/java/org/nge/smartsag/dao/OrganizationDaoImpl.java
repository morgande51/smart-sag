package org.nge.smartsag.dao;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.nge.smartsag.domain.Organization;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class OrganizationDaoImpl implements OrganizationDao, PanacheRepository<Organization> {

	@Override
	public Organization get(Long orgId) {
		return findById(orgId);
	}
	
	@Override
	public List<Organization> findOrgsByUserId(Long userId) {
		return list(Organization.FIND_BY_ADMIN, userId);
	}
}