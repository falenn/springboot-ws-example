package com.talents.examples.ws.jugtours.utils.trace;

import io.opencensus.trace.SpanBuilder;
import io.opencensus.trace.SpanContext;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.propagation.SpanContextParseException;
import io.opencensus.trace.propagation.TextFormat;
import io.opencensus.trace.samplers.Samplers;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

public class SpanUtils {

  private static final Logger logger = LoggerFactory.getLogger(SpanUtils.class);
  private static final TextFormat textFormat = Tracing.getPropagationComponent().getB3Format();
  private static final TextFormat.Getter<HttpServletRequest> getter = new TextFormat.Getter<HttpServletRequest>() {
    @Override
    public String get(HttpServletRequest httpServletRequest, String s) {
      return httpServletRequest.getHeader(s);
    }

  };

  public static SpanBuilder buildSpan(Tracer tracer, String name) {
    logger.debug("Begin buildSpan" +
        " with parent spanId: " + tracer.getCurrentSpan().getContext().getSpanId() +
        " and name: " + name);
    return tracer.spanBuilderWithExplicitParent(name,tracer.getCurrentSpan()).setRecordEvents(true);
  }

  public static SpanBuilder buildSpan(Tracer tracer, HttpServletRequest request, double samplingProbability) {
    logger.debug("Begin buildSpan" +
        " with parent spanId: " + tracer.getCurrentSpan().getContext().getSpanId() +
        " and HttpServletURI: " + request.getRequestURI() +
        " and samplingProb: " + samplingProbability);

    SpanContext spanContext = null;
    SpanBuilder spanBuilder = null;

    String spanName = request.getMethod() + ":" + request.getRequestURI();

    try {
      spanContext = textFormat.extract(request,getter);
      spanBuilder = tracer.spanBuilderWithRemoteParent(spanName, spanContext);
      logger.debug("Parent span present: " + spanContext.getSpanId());
    } catch (SpanContextParseException e) {
      //This is somewhat bad, using an exception as business logic flow - how do I determine more efficiently if this is not a continuation of a trace?

      //Starting a new trace
      spanBuilder = tracer.spanBuilder(spanName)
          .setSampler(Samplers.probabilitySampler(samplingProbability));
    }
    return spanBuilder;
  }

}
