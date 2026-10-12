(function() {
    function initWeather() {
        var widgets = document.querySelectorAll(".cmp-cityweather:not([data-weather-init])");
        widgets.forEach(function(el) {
            el.setAttribute("data-weather-init", "true");
            var city = el.getAttribute("data-city");
            var statusEl = el.querySelector(".cmp-cityweather__status");
            var bodyEl = el.querySelector(".cmp-cityweather__body");
            var tempEl = el.querySelector(".cmp-cityweather__temp");
            var descEl = el.querySelector(".cmp-cityweather__desc");
            var humEl = el.querySelector(".cmp-cityweather__humidity");
            var windEl = el.querySelector(".cmp-cityweather__wind");
            var feelsEl = el.querySelector(".cmp-cityweather__feels");

            if (!city) return;

            // Secure internal servlet endpoint (API key is kept hidden in OSGi backend)
            var url = "/bin/mysite/weather?city=" + encodeURIComponent(city);

            fetch(url)
                .then(function(res) {
                    if (!res.ok) throw new Error("Server responded with status " + res.status);
                    return res.json();
                })
                .then(function(data) {
                    if (data.error) {
                        throw new Error(data.error);
                    }

                    if (tempEl && data.temp !== undefined) tempEl.textContent = data.temp;
                    if (descEl && data.desc) descEl.textContent = data.desc;
                    if (humEl && data.humidity !== undefined) humEl.textContent = data.humidity;
                    if (windEl && data.wind !== undefined) windEl.textContent = data.wind;
                    if (feelsEl && data.feels !== undefined) feelsEl.textContent = data.feels;

                    if (statusEl) statusEl.style.display = "none";
                    if (bodyEl) bodyEl.style.display = "block";
                })
                .catch(function(err) {
                    console.error("CityWeather error:", err);
                    if (statusEl) statusEl.textContent = "Unable to load weather.";
                });
        });
    }

    // Run immediately if DOM is ready, otherwise wait for DOMContentLoaded
    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", initWeather);
    } else {
        initWeather();
    }

    // AEM Authoring support (auto-initializes upon editing or dragging components)
    if (window.MutationObserver) {
        var observer = new MutationObserver(function() {
            initWeather();
        });
        observer.observe(document.documentElement || document.body, { childList: true, subtree: true });
    }
})();
