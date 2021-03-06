package org.example.myapp.web.api

import org.avaje.metric.*
import org.example.extension.loggerFor
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.PathParam
import javax.ws.rs.core.MediaType

/**
 * Controls metrics.
 */
@Singleton
@Path("/metric")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MetricResource {

  private val logger = loggerFor(javaClass)

  private val metricEvents: MetricWebSocket

  @Inject
  constructor(metricEvents: MetricWebSocket) {
    this.metricEvents = metricEvents
  }

  /**
   * Return all the timing metrics.
   */
  @GET
  @Path("/allTiming/{match}")
  fun allTiming(@PathParam("match") match: String): MutableList<TimingMetricInfo>? {

    return MetricManager.getAllTimingMetrics(match)
  }

  /**
   * Return all the timing metrics that are active 'request timing'.
   */
  @GET
  @Path("/requestTiming/{match}")
  fun collecting(@PathParam("match") match: String): MutableList<TimingMetricInfo>? {

    return MetricManager.getRequestTimingMetrics(match)
  }


  /**
   * Set the number of request timings to collect on the timing metrics that match the matchExpression.
   *
   * @param matchExpression the expression using '*' wildcard to match timing metrics
   * @param count the number of requests to collect per request timing on
   */
  @GET
  @Path("/collectUsingMatch/{matchExpression}/{count}")
  fun setCollection(@PathParam("matchExpression") matchExpression: String,
                    @PathParam("count") count: Int): MutableList<TimingMetricInfo>? {

    logger.info("set collect {} using matchExpression {}", count, matchExpression)

    return MetricManager.setRequestTimingCollectionUsingMatch(matchExpression, count);
  }

  /**
   * Collect request timing on a specific timing metric based on the className and methodName.
   */
  @GET
  @Path("/collect/{className}/{methodName}")
  @Produces(MediaType.TEXT_PLAIN)
  fun setCollection(@PathParam("className") className: String,
                    @PathParam("methodName") methodName: String): String {

    logger.info("set collect 8 on {}.{}", className, methodName)

    val clazz = Class.forName(className);
    val success = MetricManager.setRequestTimingCollection(clazz, methodName, 6);

    return if (success) "done" else "not found"
  }

  /**
   * Test endpoint to broadcast a message to webSocket listeners.
   */
  @GET
  @Path("/broadcast/{message}")
  fun testBroadcast(@PathParam("message") message: String): Int {

    return metricEvents.broadcast(message)
  }

}