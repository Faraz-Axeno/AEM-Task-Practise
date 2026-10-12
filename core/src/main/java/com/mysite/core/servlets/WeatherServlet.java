package com.mysite.core.servlets;

import com.mysite.core.services.WeatherService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Weather Proxy Servlet mounted at /bin/mysite/weather.
 * Fetches weather for a requested city through the secure backend WeatherService,
 * keeping external API credentials completely hidden from the browser.
 */
@Component(
    service = { Servlet.class },
    property = {
        "sling.servlet.paths=/bin/mysite/weather",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET
    }
)
@ServiceDescription("City Weather Proxy Servlet")
public class WeatherServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private transient WeatherService weatherService;

    @Override
    protected void doGet(final SlingHttpServletRequest request,
                         final SlingHttpServletResponse response) throws ServletException, IOException {
        String city = request.getParameter("city");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (city == null || city.trim().isEmpty()) {
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"City parameter is required\"}");
            return;
        }

        String jsonResponse = weatherService.getWeather(city);
        response.getWriter().write(jsonResponse);
    }
}

