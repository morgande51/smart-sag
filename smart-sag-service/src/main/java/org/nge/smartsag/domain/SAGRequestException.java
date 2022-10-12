package org.nge.smartsag.domain;

import org.nge.smartsag.SmartException;

import lombok.Getter;

public class SAGRequestException extends SmartException {
	
	public enum Reason {UNKNOWN_REF_ID, UNAUTHORIZED};
	
	@Getter
	private String referenceId;
	
	@Getter
	private Reason reason;

	public SAGRequestException(String referenceId, Reason reason) {
		this.referenceId = referenceId;
		this.reason = reason;
	}

	private static final long serialVersionUID = 1L;
}