package org.nge.smartsag.dao;

import java.util.List;

import org.nge.smartsag.domain.Organization;

public interface OrganizationDao {

	Organization get(Long orgId);

	List<Organization> findOrgsByUserId(Long userId);
}