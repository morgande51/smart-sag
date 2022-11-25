package org.nge.smartsag.domain;

import org.nge.smartsag.SmartException;

import lombok.Getter;

public class SAGRequestException extends SmartException {
	
	public enum Reason {UNKNOWN_REF_ID, UNAUTHORIZED, DUPLICATE_REQ, RIDE_INACTIVE, NOT_ACTIVE};
	
	@Getter
	private String referenceId;
	
	@Getter
	private Reason reason;
	
	public SAGRequestException(Reason reason) {
		this.reason = reason;
	}

	public SAGRequestException(String referenceId, Reason reason) {
		this(reason);
		this.referenceId = referenceId;
	}

	private static final long serialVersionUID = 1L;
}