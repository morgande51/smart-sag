package org.nge.smartsag;

import java.time.LocalTime;
import java.time.ZonedDateTime;

public class TimeToEpocTest {

	public static void main(String[] args) {
		ZonedDateTime zdt = ZonedDateTime.now();
		LocalTime lt = LocalTime.now();
		long epoc = System.currentTimeMillis();
		
		System.out.println(zdt.toEpochSecond() * 1000);
		System.out.println(lt.toEpochSecond(zdt.toLocalDate(), zdt.getOffset()) * 1000);
		System.out.println(epoc);
		
	}

}
