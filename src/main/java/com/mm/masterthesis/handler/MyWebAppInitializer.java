package com.mm.masterthesis.handler;

import jakarta.servlet.ServletContext;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.request.RequestContextListener;

public class MyWebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext container) {
        container.addListener(new RequestContextListener());
    }
}
