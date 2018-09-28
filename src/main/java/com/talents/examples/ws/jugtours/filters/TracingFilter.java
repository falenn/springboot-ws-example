package com.talents.examples.ws.jugtours.filters;

import com.talents.examples.ws.jugtours.interceptors.TracingClientHTTPRequestInterceptor;
import com.talents.examples.ws.jugtours.utils.trace.SpanUtils;
import io.opencensus.common.Scope;
import io.opencensus.trace.AttributeValue;
import io.opencensus.trace.MessageEvent;
import io.opencensus.trace.Span;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.propagation.TextFormat;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * TracingFilter extends {@link OncePerRequestFilter}
 * This filter implements a scoped span around the {@link ControllerAdvice} being executed
 */
public class TracingFilter extends OncePerRequestFilter {

  protected static Logger log = LoggerFactory.getLogger(TracingFilter.class);

  private static Tracer tracer = Tracing.getTracer();
  private static final TextFormat textFormat = Tracing.getPropagationComponent().getB3Format();
  private static final TextFormat.Setter<HttpHeaders> setter = new TextFormat.Setter<HttpHeaders>() {
    @Override public void put(HttpHeaders httpHeaders, String key, String value) {
      httpHeaders.add(key, value);
    }
  };

  private double samplingProbability;

  public TracingFilter(double samplingProbability) {
    this.samplingProbability = samplingProbability;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    log.debug("Begin doFilterInternal with probSampling " + samplingProbability);

    Span span = SpanUtils.buildSpan(tracer, request, samplingProbability).startSpan();
    try(Scope s = tracer.withSpan(span)) {
      span.addMessageEvent(
          MessageEvent.builder(MessageEvent.Type.RECEIVED,1l).build()
      );
      span.putAttribute(request.getRequestURI(), AttributeValue.stringAttributeValue("BEGIN"));

      //Do work
      log.debug("Before filterChain");
      filterChain.doFilter(request,response);
      response.setStatus(202);
      log.debug("After filterChain");
    } catch (Exception e) {
      response.setStatus(500);
    } finally{
      span.putAttribute(request.getRequestURI(), AttributeValue.stringAttributeValue("DONE"));
      span.addMessageEvent(
          MessageEvent.builder(MessageEvent.Type.SENT,1l).build()
      );
      log.debug("Done with span: " + span.getContext().getSpanId());
      span.end();
    }
  }

}
