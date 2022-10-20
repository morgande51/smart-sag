package org.nge.smartsag.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.nge.smartsag.domain.SAGRequest;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class SAGRequestDaoImpl implements PanacheRepository<SAGRequest>, SAGRequestDao {

	@Override
	public List<SAGRequest> findSAGRequestForUser(Long userId) {
		return list(SAGRequest.FIND_REQ_FOR_USER, userId);
	}
	
	@Override
	public List<SAGRequest> findSAGRequestForUser(Long userId, boolean active) {
		return stream(SAGRequest.FIND_REQ_FOR_USER, userId)
				.filter(r -> r.isActive() == active)
				.collect(Collectors.toList());
	}
}
