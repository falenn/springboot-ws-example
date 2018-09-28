package com.talents.examples.ws.jugtours.interceptors;

import com.talents.examples.ws.jugtours.utils.trace.SpanUtils;
import io.opencensus.common.Scope;
import io.opencensus.trace.MessageEvent;
import io.opencensus.trace.Span;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.propagation.TextFormat;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;

public class TracingClientHTTPRequestInterceptor implements ClientHttpRequestInterceptor {

  protected static Logger log = LoggerFactory.getLogger(TracingClientHTTPRequestInterceptor.class);

  private static Tracer tracer = Tracing.getTracer();
  private static final TextFormat textFormat = Tracing.getPropagationComponent().getB3Format();
  private static final TextFormat.Setter<HttpHeaders> setter = new TextFormat.Setter<HttpHeaders>() {
    @Override public void put(HttpHeaders httpHeaders, String key, String value) {
      httpHeaders.add(key, value);
    }
  };

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws
      IOException {

    //Create span based off current context - this is done in the SpanUtils.buildSpan helper
    Span span = SpanUtils.buildSpan(tracer, request.getMethod() + ":" + request.getURI()).startSpan();

    //start the span setting the new span as the current span
    try (Scope s = tracer.withSpan(span)) {

      //Inject headers to propagate the trace
      textFormat.inject(tracer.getCurrentSpan().getContext(), request.getHeaders(), setter);
      tracer.getCurrentSpan().addMessageEvent(
          MessageEvent.builder(MessageEvent.Type.SENT,1l).build()
      );

      ClientHttpResponse response = execution.execute(request, body);

      tracer.getCurrentSpan().addMessageEvent(
          MessageEvent.builder(MessageEvent.Type.RECEIVED,1l).build()
      );
      return response;
    } catch (RestClientException e) {
      log.error("Error making rest call[" + request.getURI() + "]", e);
      return null;
    } finally {
      span.end();
    }
  }
}
