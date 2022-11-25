package org.nge.smartsag.dao;

import java.util.List;

import org.nge.smartsag.domain.SAGRequest;

public interface SAGRequestDao {

	List<SAGRequest> findRequestForUser(Long id, boolean active);

	List<SAGRequest> findRequestForUser(Long userId);

	SAGRequest getRequest(Long id);

	SAGRequest getRequestFromRefId(String refId);

	SAGRequest getRequestForUpdate(Long id);
}
