package org.nge.smartsag.domain;

import lombok.Getter;

public class UnknownSAGRequest extends RuntimeException {
	
	@Getter
	private Long sagRequestId;
	
	public UnknownSAGRequest(Long id) {
		this.sagRequestId = id;
	}
	
	private static final long serialVersionUID = 1L;
}