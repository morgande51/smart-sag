package org.nge.smartsag.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.LockModeType;

import org.nge.smartsag.domain.SAGRequest;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

@ApplicationScoped
public class SAGRequestDaoImpl implements PanacheRepository<SAGRequest>, SAGRequestDao {
	
	private static final String REFERENCE_ID = "referenceId";
	private static final String USER_ID = "userId";
	
	@Override
	public SAGRequest getRequest(Long id) {
		return findById(id);
	}
	
	@Override
	public SAGRequest getRequestForUpdate(Long id) {
		return findById(id, LockModeType.PESSIMISTIC_WRITE);
	}
	
	@Override
	public SAGRequest getRequestFromRefId(String refId) {
		return find(SAGRequest.GET_FROM_REF_ID, Parameters.with(REFERENCE_ID, refId)).singleResult();
	}
	
	@Override
	public List<SAGRequest> findRequestForUser(Long userId) {
		return list(SAGRequest.FIND_REQ_FOR_USER, Parameters.with(USER_ID, userId));
	}
	
	@Override
	public List<SAGRequest> findRequestForUser(Long userId, boolean active) {
		return stream(SAGRequest.FIND_REQ_FOR_USER, Parameters.with(USER_ID, userId))
				.filter(r -> r.isActive() == active)
				.collect(Collectors.toList());
	}
}
