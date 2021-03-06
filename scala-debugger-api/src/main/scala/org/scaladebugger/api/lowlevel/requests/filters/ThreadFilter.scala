package org.scaladebugger.api.lowlevel.requests.filters
//import acyclic.file

import com.sun.jdi.ThreadReference
import org.scaladebugger.api.lowlevel.requests.JDIRequestProcessor
import org.scaladebugger.api.lowlevel.requests.filters.processors.ThreadFilterProcessor

/**
 * Represents a filter used to limit requests to a specific thread.
 *
 * @note Only used by AccessWatchpointRequest, BreakpointRequest,
 *       ExceptionRequest, MethodEntryRequest, MethodExitRequest,
 *       ModificationWatchpointRequest, MonitorContendedEnteredRequest,
 *       MonitorContendedEnterRequest, MonitorWaitedRequest,
 *       MonitorWaitRequest, ThreadDeathEvent, and ThreadStartEvent.
 *
 * @param threadReference The thread reference used to specify the thread
 */
case class ThreadFilter(
  threadReference: ThreadReference
) extends JDIRequestFilter {
  /**
   * Creates a new JDI request processor based on this filter.
   *
   * @return The new JDI request processor instance
   */
  override def toProcessor: JDIRequestFilterProcessor =
    new ThreadFilterProcessor(this)
}
