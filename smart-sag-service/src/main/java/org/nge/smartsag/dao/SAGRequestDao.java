package org.nge.smartsag.dao;

import java.util.List;

import org.nge.smartsag.domain.SAGRequest;

public interface SAGRequestDao {

	List<SAGRequest> findSAGRequestForUser(Long id, boolean active);

	List<SAGRequest> findSAGRequestForUser(Long userId);
}
