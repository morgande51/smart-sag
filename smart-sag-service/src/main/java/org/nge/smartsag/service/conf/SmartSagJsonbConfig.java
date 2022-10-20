package org.nge.smartsag.service.conf;

import javax.inject.Singleton;
import javax.json.bind.JsonbConfig;

import io.quarkus.jsonb.JsonbConfigCustomizer;

@Singleton
public class SmartSagJsonbConfig implements JsonbConfigCustomizer {

	@Override
	public void customize(JsonbConfig config) {
		config.withPropertyVisibilityStrategy(new SmartSagPropertyStrategy());
	}
}