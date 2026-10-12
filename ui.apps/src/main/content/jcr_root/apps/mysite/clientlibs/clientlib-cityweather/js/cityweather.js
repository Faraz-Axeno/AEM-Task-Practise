(function() {
    function initWeather() {
        var widgets = document.querySelectorAll(".cmp-cityweather:not([data-weather-init])");
        widgets.forEach(function(el) {
            el.setAttribute("data-weather-init", "true");
            var city = el.getAttribute("data-city");
            var apiKey = el.getAttribute("data-api-key");
            var statusEl = el.querySelector(".cmp-cityweather__status");
            var bodyEl = el.querySelector(".cmp-cityweather__body");
            var tempEl = el.querySelector(".cmp-cityweather__temp");
            var descEl = el.querySelector(".cmp-cityweather__desc");
            var humEl = el.querySelector(".cmp-cityweather__humidity");
            var windEl = el.querySelector(".cmp-cityweather__wind");
            var feelsEl = el.querySelector(".cmp-cityweather__feels");

            if (!city) return;

            var url = apiKey 
                ? "https://api.openweathermap.org/data/2.5/weather?q=" + encodeURIComponent(city) + "&appid=" + encodeURIComponent(apiKey) + "&units=metric"
                : "https://wttr.in/" + encodeURIComponent(city) + "?format=j1";

            fetch(url)
                .then(function(res) {
                    if (!res.ok) throw new Error("API error: " + res.status);
                    return res.json();
                })
                .then(function(data) {
                    var temp, desc, humidity, wind, feels;
                    if (apiKey) {
                        temp = Math.round(data.main.temp);
                        desc = data.weather && data.weather[0] ? data.weather[0].description : "";
                        humidity = data.main.humidity;
                        wind = Math.round(data.wind.speed * 3.6);
                        feels = Math.round(data.main.feels_like);
                    } else {
                        var curr = data.current_condition && data.current_condition[0] ? data.current_condition[0] : {};
                        temp = curr.temp_C;
                        desc = curr.weatherDesc && curr.weatherDesc[0] ? curr.weatherDesc[0].value.trim() : "";
                        humidity = curr.humidity;
                        wind = curr.windspeedKmph;
                        feels = curr.FeelsLikeC;
                    }

                    if (tempEl) tempEl.textContent = temp;
                    if (descEl) descEl.textContent = desc;
                    if (humEl) humEl.textContent = humidity;
                    if (windEl) windEl.textContent = wind;
                    if (feelsEl) feelsEl.textContent = feels;

                    if (statusEl) statusEl.style.display = "none";
                    if (bodyEl) bodyEl.style.display = "block";
                })
                .catch(function(err) {
                    console.error("CityWeather error:", err);
                    if (statusEl) statusEl.textContent = "Unable to load weather.";
                });
        });
    }

    // Run immediately if DOM is already loaded, otherwise listen for DOMContentLoaded
    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", initWeather);
    } else {
        initWeather();
    }

    // Support AEM authoring mode (dialog save / component refresh in Touch UI)
    if (window.MutationObserver) {
        var observer = new MutationObserver(function() {
            initWeather();
        });
        observer.observe(document.documentElement || document.body, { childList: true, subtree: true });
    }
})();
