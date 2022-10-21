package org.nge.smartsag.domain;

import java.util.stream.Stream;

import lombok.Getter;

public enum SAGRequestStatusType {

	NEW('N'), ACKNOWLEGED('A'), COMPLETE('F'), CANCELED('C');
	
	@Getter
	Character code;
	
	private SAGRequestStatusType(char code) {
		this.code = code;
	}

	public static SAGRequestStatusType from(Character data) {
		return Stream.of(SAGRequestStatusType.values())
				.filter(s -> s.code.equals(data))
				.findAny()
				.orElseThrow(IllegalArgumentException::new);
	}
}