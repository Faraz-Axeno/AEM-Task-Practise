package com.mysite.core.services;

/**
 * Service interface for fetching and standardizing weather data.
 */
public interface WeatherService {

    /**
     * Retrieves weather information for the specified city.
     *
     * @param city Name of the city
     * @return Standardized JSON string containing weather details
     */
    String getWeather(String city);
}

