package com.mysite.core.services.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysite.core.services.WeatherConfig;
import com.mysite.core.services.WeatherService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component(
    service = WeatherService.class,
    immediate = true
)
@Designate(ocd = WeatherConfig.class)
public class WeatherServiceImpl implements WeatherService {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherServiceImpl.class);

    private String apiKey;
    private String apiUrl;

    @Activate
    @Modified
    protected void activate(final WeatherConfig config) {
        this.apiKey = config.apiKey();
        this.apiUrl = config.apiUrl();
        LOG.info("WeatherService activated with apiUrl: {}", this.apiUrl);
    }

    @Override
    public String getWeather(final String city) {
        JsonObject result = new JsonObject();
        if (city == null || city.trim().isEmpty()) {
            result.addProperty("error", "City name cannot be empty");
            return result.toString();
        }

        String trimmedCity = city.trim();
        result.addProperty("city", trimmedCity);

        try {
            String encodedCity = URLEncoder.encode(trimmedCity, StandardCharsets.UTF_8.toString());
            boolean useOpenWeather = (apiKey != null && !apiKey.trim().isEmpty());

            String requestUrl = useOpenWeather
                ? apiUrl + "?q=" + encodedCity + "&appid=" + apiKey.trim() + "&units=metric"
                : "https://wttr.in/" + encodedCity + "?format=j1";

            LOG.debug("Fetching weather data from: {}", requestUrl);
            String rawJson = executeHttpRequest(requestUrl);

            JsonObject root = JsonParser.parseString(rawJson).getAsJsonObject();

            if (useOpenWeather) {
                // OpenWeatherMap JSON parsing
                if (root.has("main")) {
                    JsonObject main = root.getAsJsonObject("main");
                    result.addProperty("temp", Math.round(main.get("temp").getAsDouble()));
                    result.addProperty("feels", Math.round(main.get("feels_like").getAsDouble()));
                    result.addProperty("humidity", main.get("humidity").getAsInt());
                }
                if (root.has("weather")) {
                    JsonArray weatherArr = root.getAsJsonArray("weather");
                    if (weatherArr.size() > 0) {
                        result.addProperty("desc", weatherArr.get(0).getAsJsonObject().get("description").getAsString());
                    }
                }
                if (root.has("wind")) {
                    JsonObject wind = root.getAsJsonObject("wind");
                    result.addProperty("wind", Math.round(wind.get("speed").getAsDouble() * 3.6)); // m/s to km/h
                }
            } else {
                // wttr.in JSON parsing
                if (root.has("current_condition")) {
                    JsonArray currentArr = root.getAsJsonArray("current_condition");
                    if (currentArr.size() > 0) {
                        JsonObject current = currentArr.get(0).getAsJsonObject();
                        result.addProperty("temp", current.get("temp_C").getAsInt());
                        result.addProperty("feels", current.get("FeelsLikeC").getAsInt());
                        result.addProperty("humidity", current.get("humidity").getAsInt());
                        result.addProperty("wind", current.get("windspeedKmph").getAsInt());

                        if (current.has("weatherDesc")) {
                            JsonArray descArr = current.getAsJsonArray("weatherDesc");
                            if (descArr.size() > 0) {
                                result.addProperty("desc", descArr.get(0).getAsJsonObject().get("value").getAsString().trim());
                            }
                        }
                    }
                }
            }

            return result.toString();
        } catch (Exception e) {
            LOG.error("Failed to fetch weather for city '{}': {}", trimmedCity, e.getMessage(), e);
            result.addProperty("error", "Unable to retrieve weather details");
            return result.toString();
        }
    }

    private String executeHttpRequest(final String requestUrl) throws Exception {
        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int status = conn.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("HTTP GET failed with status code " + status);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        } finally {
            conn.disconnect();
        }
    }
}

