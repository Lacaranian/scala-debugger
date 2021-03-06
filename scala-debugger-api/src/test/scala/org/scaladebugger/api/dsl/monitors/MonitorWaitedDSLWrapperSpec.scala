package org.scaladebugger.api.dsl.monitors

import com.sun.jdi.event.MonitorWaitedEvent
import org.scaladebugger.api.lowlevel.events.data.JDIEventDataResult
import org.scaladebugger.api.lowlevel.requests.JDIRequestArgument
import org.scaladebugger.api.pipelines.Pipeline
import org.scaladebugger.api.profiles.traits.monitors.MonitorWaitedProfile
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FunSpec, Matchers, ParallelTestExecution}

import scala.util.Success

class MonitorWaitedDSLWrapperSpec extends FunSpec with Matchers
  with ParallelTestExecution with MockFactory
{
  private val mockMonitorWaitedProfile = mock[MonitorWaitedProfile]

  describe("MonitorWaitedDSLWrapper") {
    describe("#onMonitorWaited") {
      it("should invoke the underlying profile method") {
        import org.scaladebugger.api.dsl.Implicits.MonitorWaitedDSL

        val extraArguments = Seq(mock[JDIRequestArgument])
        val returnValue = Success(Pipeline.newPipeline(classOf[MonitorWaitedEvent]))

        (mockMonitorWaitedProfile.tryGetOrCreateMonitorWaitedRequest _).expects(
          extraArguments
        ).returning(returnValue).once()

        mockMonitorWaitedProfile.onMonitorWaited(
          extraArguments: _*
        ) should be (returnValue)
      }
    }

    describe("#onUnsafeMonitorWaited") {
      it("should invoke the underlying profile method") {
        import org.scaladebugger.api.dsl.Implicits.MonitorWaitedDSL

        val extraArguments = Seq(mock[JDIRequestArgument])
        val returnValue = Pipeline.newPipeline(classOf[MonitorWaitedEvent])

        (mockMonitorWaitedProfile.getOrCreateMonitorWaitedRequest _).expects(
          extraArguments
        ).returning(returnValue).once()

        mockMonitorWaitedProfile.onUnsafeMonitorWaited(
          extraArguments: _*
        ) should be (returnValue)
      }
    }

    describe("#onMonitorWaitedWithData") {
      it("should invoke the underlying profile method") {
        import org.scaladebugger.api.dsl.Implicits.MonitorWaitedDSL

        val extraArguments = Seq(mock[JDIRequestArgument])
        val returnValue = Success(Pipeline.newPipeline(
          classOf[(MonitorWaitedEvent, Seq[JDIEventDataResult])]
        ))

        (mockMonitorWaitedProfile.tryGetOrCreateMonitorWaitedRequestWithData _).expects(
          extraArguments
        ).returning(returnValue).once()

        mockMonitorWaitedProfile.onMonitorWaitedWithData(
          extraArguments: _*
        ) should be (returnValue)
      }
    }

    describe("#onUnsafeMonitorWaitedWithData") {
      it("should invoke the underlying profile method") {
        import org.scaladebugger.api.dsl.Implicits.MonitorWaitedDSL

        val extraArguments = Seq(mock[JDIRequestArgument])
        val returnValue = Pipeline.newPipeline(
          classOf[(MonitorWaitedEvent, Seq[JDIEventDataResult])]
        )

        (mockMonitorWaitedProfile.getOrCreateMonitorWaitedRequestWithData _).expects(
          extraArguments
        ).returning(returnValue).once()

        mockMonitorWaitedProfile.onUnsafeMonitorWaitedWithData(
          extraArguments: _*
        ) should be (returnValue)
      }
    }
  }
}
