package org.scaladebugger.api.dsl.methods

import com.sun.jdi.event.MethodEntryEvent
import org.scaladebugger.api.lowlevel.events.data.JDIEventDataResult
import org.scaladebugger.api.lowlevel.requests.JDIRequestArgument
import org.scaladebugger.api.pipelines.Pipeline
import org.scaladebugger.api.profiles.traits.methods.MethodEntryProfile
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FunSpec, Matchers, ParallelTestExecution}

import scala.util.Success

class MethodEntryDSLWrapperSpec extends FunSpec with Matchers
  with ParallelTestExecution with MockFactory
{
  private val mockMethodEntryProfile = mock[MethodEntryProfile]

  describe("MethodEntryDSLWrapper") {
    describe("#onMethodEntry") {
      it("should invoke the underlying profile method") {
        import org.scaladebugger.api.dsl.Implicits.MethodEntryDSL

        val className = "some.class.name"
        val methodName = "someMethodName"
        val extraArguments = Seq(mock[JDIRequestArgument])
        val returnValue = Success(Pipeline.newPipeline(classOf[MethodEntryEvent]))

        (mockMethodEntryProfile.tryGetOrCreateMethodEntryRequest _).expects(
          className,
          methodName,
          extraArguments
        ).returning(returnValue).once()

        mockMethodEntryProfile.onMethodEntry(
          className,
          methodName,
          extraArguments: _*
        ) should be (returnValue)
      }
    }

    describe("#onUnsafeMethodEntry") {
      it("should invoke the underlying profile method") {
        import org.scaladebugger.api.dsl.Implicits.MethodEntryDSL

        val className = "some.class.name"
        val methodName = "someMethodName"
        val extraArguments = Seq(mock[JDIRequestArgument])
        val returnValue = Pipeline.newPipeline(classOf[MethodEntryEvent])

        (mockMethodEntryProfile.getOrCreateMethodEntryRequest _).expects(
          className,
          methodName,
          extraArguments
        ).returning(returnValue).once()

        mockMethodEntryProfile.onUnsafeMethodEntry(
          className,
          methodName,
          extraArguments: _*
        ) should be (returnValue)
      }
    }

    describe("#onMethodEntryWithData") {
      it("should invoke the underlying profile method") {
        import org.scaladebugger.api.dsl.Implicits.MethodEntryDSL

        val className = "some.class.name"
        val methodName = "someMethodName"
        val extraArguments = Seq(mock[JDIRequestArgument])
        val returnValue = Success(Pipeline.newPipeline(
          classOf[(MethodEntryEvent, Seq[JDIEventDataResult])]
        ))

        (mockMethodEntryProfile.tryGetOrCreateMethodEntryRequestWithData _).expects(
          className,
          methodName,
          extraArguments
        ).returning(returnValue).once()

        mockMethodEntryProfile.onMethodEntryWithData(
          className,
          methodName,
          extraArguments: _*
        ) should be (returnValue)
      }
    }

    describe("#onUnsafeMethodEntryWithData") {
      it("should invoke the underlying profile method") {
        import org.scaladebugger.api.dsl.Implicits.MethodEntryDSL

        val className = "some.class.name"
        val methodName = "someMethodName"
        val extraArguments = Seq(mock[JDIRequestArgument])
        val returnValue = Pipeline.newPipeline(
          classOf[(MethodEntryEvent, Seq[JDIEventDataResult])]
        )

        (mockMethodEntryProfile.getOrCreateMethodEntryRequestWithData _).expects(
          className,
          methodName,
          extraArguments
        ).returning(returnValue).once()

        mockMethodEntryProfile.onUnsafeMethodEntryWithData(
          className,
          methodName,
          extraArguments: _*
        ) should be (returnValue)
      }
    }
  }
}
