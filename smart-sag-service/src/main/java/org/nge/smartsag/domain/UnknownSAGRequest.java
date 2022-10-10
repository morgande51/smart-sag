package org.nge.smartsag.domain;

import lombok.Getter;

public class UnknownSAGRequest extends SAGException {
	
	@Getter
	private String referenceId;
	
	public UnknownSAGRequest(String id) {
		this.referenceId = id;
	}
	
	private static final long serialVersionUID = 1L;
}