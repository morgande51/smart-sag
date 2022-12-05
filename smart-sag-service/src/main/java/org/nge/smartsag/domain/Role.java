package org.nge.smartsag.domain;

import java.util.stream.Stream;

import lombok.Getter;

public enum Role {
	
	HOST('H'), SUPPORT('S'), MARSHAL('M');

	@Getter
	Character code;

	private Role(Character code) {
		this.code = code;
	}
	
	public static Role from(Character data) {
		return Stream.of(Role.values())
				.filter(r -> r.code.equals(data))
				.findAny()
				.orElseThrow(IllegalArgumentException::new);
	}
}