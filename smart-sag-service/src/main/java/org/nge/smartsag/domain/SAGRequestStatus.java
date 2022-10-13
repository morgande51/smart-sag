package org.nge.smartsag.domain;

import java.util.stream.Stream;

import lombok.Getter;

public enum SAGRequestStatus {

	NEW('N'), ACKNOWLEGED('A'), COMPLETE('F'), CANCELED('C');
	
	@Getter
	Character code;
	
	private SAGRequestStatus(char code) {
		this.code = code;
	}

	public static SAGRequestStatus from(Character data) {
		return Stream.of(SAGRequestStatus.values())
				.filter(s -> s.code.equals(data))
				.findAny()
				.orElseThrow(IllegalArgumentException::new);
	}
}