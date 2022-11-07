package org.nge.smartsag.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.nge.smartsag.domain.UnknownDomainException;

@ApplicationScoped
public class ServiceExceptionMapper {
	
	@ServerExceptionMapper
	public RestResponse<String> mapException(UnknownDomainException e) {
		return RestResponse.status(Response.Status.NOT_FOUND, "Unknown Domain: " + e.getType().getSimpleName());
	}

}