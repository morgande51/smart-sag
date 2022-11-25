package org.nge.smartsag.service;

import static javax.ws.rs.core.Response.Status.*;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.nge.smartsag.domain.SAGRequestException;
import org.nge.smartsag.domain.UnknownDomainException;

@ApplicationScoped
public class ServiceExceptionMapper {
	
	@ServerExceptionMapper
	public RestResponse<String> mapException(UnknownDomainException e) {
		return RestResponse.status(NOT_FOUND, "Unknown Domain: " + e.getType().getSimpleName());
	}
	
	@ServerExceptionMapper
	public RestResponse<String> mapException(SAGRequestException e) {
		String responseText;
		Status status;
		switch (e.getReason()) {
			case RIDE_INACTIVE:
				responseText = "The Ride is not active";
				status = Status.BAD_REQUEST;
				break;
				
			case DUPLICATE_REQ:
				responseText = "User already has active request";
				status = Status.BAD_REQUEST;
				break;
				
			case UNAUTHORIZED:
				responseText = "User is unauthorized to perform this action";
				status = Status.UNAUTHORIZED;
				break;
				
			case NOT_ACTIVE:
				responseText = "SAGRequest is not active";
				status = Status.PRECONDITION_FAILED;
				break;
				
			default:
				responseText = "No SAGRequest for: " + e.getReferenceId();
				status = Response.Status.NOT_FOUND;
		}
		
		return RestResponse.status(status, responseText);
	}

}