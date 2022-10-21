package org.nge.smartsag.aws;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.location.LocationClient;

@Singleton
public class LocationClientFactory {

	@ConfigProperty(name = "aws.username")
	String userName;
	
	@ConfigProperty(name = "aws.secret")
	String secret;
	
	@ConfigProperty(name = "aws.accessKey")
	String key;
	
	@ConfigProperty(name = "aws.location.region")
	String region;
	
	@Produces
	public LocationClient createLocationClient() {
		AwsBasicCredentials creds = AwsBasicCredentials.create(key, secret);
		StaticCredentialsProvider provider = StaticCredentialsProvider.create(creds);
		return LocationClient.builder()
				.credentialsProvider(provider)
				.region(Region.of(region))
				.build();
	}
}