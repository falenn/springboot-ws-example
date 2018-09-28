package com.talents.examples.ws.jugtours;

import com.talents.examples.ws.jugtours.filters.TracingFilter;
import com.talents.examples.ws.jugtours.interceptors.TracingClientHTTPRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

  @Value("${tracing.sampling.probability:0.01d}")
  private double samplingProbability;

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate template = new RestTemplate();
    template.getInterceptors().add(new TracingClientHTTPRequestInterceptor());
    return template;
  }

  @Bean
  public FilterRegistrationBean tracingFilter() {
    FilterRegistrationBean registrationBean = new FilterRegistrationBean();
    registrationBean.setFilter(new TracingFilter(samplingProbability));
    registrationBean.addUrlPatterns("/*");
    return registrationBean;
  }

}
