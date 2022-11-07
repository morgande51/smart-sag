package org.nge.smartsag.domain;

import lombok.Getter;

public class UnknownDomainException extends IllegalArgumentException {
	
	@Getter
	private Class<?> type;
	
	public UnknownDomainException(Class<?> type) {
		this.type = type;
	}

	private static final long serialVersionUID = 1L;
}