package com.mysite.core.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
    name = "MySite - City Weather Service Configuration",
    description = "OSGi configuration for managing external Weather API integration and credentials"
)
public @interface WeatherConfig {

    @AttributeDefinition(
        name = "OpenWeatherMap API Key",
        description = "Secret API key for OpenWeatherMap. When empty, service falls back to default free weather provider.",
        type = AttributeType.PASSWORD
    )
    String apiKey() default "";

    @AttributeDefinition(
        name = "API Endpoint URL",
        description = "Base API URL for OpenWeatherMap service",
        type = AttributeType.STRING
    )
    String apiUrl() default "https://api.openweathermap.org/data/2.5/weather";
}

