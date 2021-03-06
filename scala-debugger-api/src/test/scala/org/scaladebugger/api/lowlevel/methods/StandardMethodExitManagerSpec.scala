package org.scaladebugger.api.lowlevel.methods
import acyclic.file

import com.sun.jdi.request.{EventRequest, EventRequestManager, MethodExitRequest}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FunSpec, Matchers, ParallelTestExecution}

import scala.util.{Failure, Success}

class StandardMethodExitManagerSpec extends FunSpec with Matchers with MockFactory
  with ParallelTestExecution with org.scalamock.matchers.Matchers
{
  private val TestRequestId = java.util.UUID.randomUUID().toString
  private val mockEventRequestManager = mock[EventRequestManager]

  private val methodExitManager = new StandardMethodExitManager(mockEventRequestManager) {
    override protected def newRequestId(): String = TestRequestId
  }

  describe("StandardMethodExitManager") {
    describe("#methodExitRequestList") {
      it("should contain all method exit requests in the form of (class, method) stored in the manager") {
        val methodExitRequests = Seq(
          MethodExitRequestInfo(TestRequestId, false, "class1", "method1"),
          MethodExitRequestInfo(TestRequestId + 1, false, "class2", "method2")
        )

        // NOTE: Must create a new method exit manager that does NOT override
        //       the request id to always be the same since we do not allow
        //       duplicates of the test id when storing it
        val methodExitManager = new StandardMethodExitManager(mockEventRequestManager)

        methodExitRequests.foreach { case MethodExitRequestInfo(requestId, _, className, methodName, _) =>
          (mockEventRequestManager.createMethodExitRequest _).expects()
            .returning(stub[MethodExitRequest]).once()
          methodExitManager.createMethodExitRequestWithId(requestId, className, methodName)
        }

        methodExitManager.methodExitRequestList should
          contain theSameElementsAs (methodExitRequests)
      }
    }

    describe("#methodExitRequestListById") {
      it("should contain all method exit request ids") {
        val methodExitRequests = Seq(
          ("id1", "class1", "method1"),
          ("id2", "class2", "method2")
        )

        methodExitRequests.foreach { case (requestId, className, methodName) =>
          (mockEventRequestManager.createMethodExitRequest _).expects()
            .returning(stub[MethodExitRequest]).once()
          methodExitManager.createMethodExitRequestWithId(
            requestId,
            className,
            methodName
          )
        }

        methodExitManager.methodExitRequestListById should
          contain theSameElementsAs (methodExitRequests.map(_._1))
      }
    }

    describe("#createMethodExitRequestWithId") {
      it("should create the method exit request using the provided id") {
        val expected = Success(java.util.UUID.randomUUID().toString)
        val testClassName = "some class name"
        val testMethodName = "some method name"

        val mockMethodExitRequest = mock[MethodExitRequest]
        (mockEventRequestManager.createMethodExitRequest _).expects()
          .returning(mockMethodExitRequest).once()

        // Should apply the class filter, set enabled to true by default, and
        // set the suspend policy to thread level by default
        (mockMethodExitRequest.addClassFilter(_: String))
          .expects(testClassName).once()
        (mockMethodExitRequest.setSuspendPolicy _)
          .expects(EventRequest.SUSPEND_EVENT_THREAD).once()
        (mockMethodExitRequest.setEnabled _).expects(true).once()

        val actual = methodExitManager.createMethodExitRequestWithId(
          expected.get,
          testClassName,
          testMethodName
        )
        actual should be(expected)
      }

      it("should create the method exit request with a class inclusion filter for the class name") {
        val expected = Success(TestRequestId)
        val testClassName = "some class name"
        val testMethodName = "some method name"

        val mockMethodExitRequest = mock[MethodExitRequest]
        (mockEventRequestManager.createMethodExitRequest _).expects()
          .returning(mockMethodExitRequest).once()

        // Should apply the class filter, set enabled to true by default, and
        // set the suspend policy to thread level by default
        (mockMethodExitRequest.addClassFilter(_: String))
          .expects(testClassName).once()
        (mockMethodExitRequest.setSuspendPolicy _)
          .expects(EventRequest.SUSPEND_EVENT_THREAD).once()
        (mockMethodExitRequest.setEnabled _).expects(true).once()

        val actual = methodExitManager.createMethodExitRequestWithId(
          expected.get,
          testClassName,
          testMethodName
        )
        actual should be (expected)
      }

      it("should return the exception if unable to create the request") {
        val expected = Failure(new Throwable)
        val testClassName = "some class name"
        val testMethodName = "some method name"

        (mockEventRequestManager.createMethodExitRequest _).expects()
          .throwing(expected.failed.get).once()

        val actual = methodExitManager.createMethodExitRequestWithId(
          TestRequestId,
          testClassName,
          testMethodName
        )
        actual should be (expected)
      }
    }

    describe("#hasMethodExitRequestWithId") {
      it("should return true if it exists") {
        val expected = true

        val testClassName = "some class name"
        val testMethodName = "some method name"

        (mockEventRequestManager.createMethodExitRequest _).expects()
          .returning(stub[MethodExitRequest]).once()

        methodExitManager.createMethodExitRequestWithId(
          TestRequestId,
          testClassName,
          testMethodName
        )

        val actual = methodExitManager.hasMethodExitRequestWithId(TestRequestId)
        actual should be (expected)
      }

      it("should return false if it does not exist") {
        val expected = false

        val actual = methodExitManager.hasMethodExitRequestWithId(TestRequestId)
        actual should be (expected)
      }
    }

    describe("#hasMethodExitRequest") {
      it("should return true if it exists") {
        val expected = true

        val testClassName = "some class name"
        val testMethodName = "some method name"

        (mockEventRequestManager.createMethodExitRequest _).expects()
          .returning(stub[MethodExitRequest]).once()

        methodExitManager.createMethodExitRequest(testClassName, testMethodName)

        val actual = methodExitManager.hasMethodExitRequest(testClassName, testMethodName)
        actual should be (expected)
      }

      it("should return false if it does not exist") {
        val expected = false

        val testClassName = "some class name"
        val testMethodName = "some method name"

        val actual = methodExitManager.hasMethodExitRequest(testClassName, testMethodName)
        actual should be (expected)
      }
    }

    describe("#getMethodExitRequestInfoWithId") {
      it("should return Some(MethodExitRequestInfo(id, not pending, class name, method name)) if the id exists") {
        val expected = Some(MethodExitRequestInfo(
          requestId = TestRequestId,
          isPending = false,
          className = "some.class.name",
          methodName = "someMethodName"
        ))

        // Stub out the call to create a breakpoint request
        (mockEventRequestManager.createMethodExitRequest _).expects()
          .returning(stub[MethodExitRequest]).once()

        methodExitManager.createMethodExitRequestWithId(
          expected.get.requestId,
          expected.get.className,
          expected.get.methodName
        )

        val actual = methodExitManager.getMethodExitRequestInfoWithId(
          TestRequestId
        )

        actual should be (expected)
      }

      it("should return None if there is no breakpoint with the id") {
        val expected = None

        val actual = methodExitManager.getMethodExitRequestInfoWithId(
          TestRequestId
        )

        actual should be (expected)
      }
    }

    describe("#getMethodExitRequestWithId") {
      it("should return Some(MethodExitRequest) if found") {
        val expected = stub[MethodExitRequest]

        val testClassName = "some class name"
        val testMethodName = "some method name"

        (mockEventRequestManager.createMethodExitRequest _).expects()
          .returning(expected).once()

        methodExitManager.createMethodExitRequestWithId(
          TestRequestId,
          testClassName,
          testMethodName
        )

        val actual = methodExitManager.getMethodExitRequestWithId(TestRequestId)
        actual should be (Some(expected))
      }

      it("should return None if not found") {
        val expected = None

        val actual = methodExitManager.getMethodExitRequestWithId(TestRequestId)
        actual should be (expected)
      }
    }

    describe("#getMethodExitRequest") {
      it("should return Some(collection of MethodExitRequest) if found") {
        val expected = Seq(stub[MethodExitRequest])

        val testClassName = "some class name"
        val testMethodName = "some method name"

        (mockEventRequestManager.createMethodExitRequest _).expects()
          .returning(expected.head).once()

        methodExitManager.createMethodExitRequest(testClassName, testMethodName)

        val actual =
          methodExitManager.getMethodExitRequest(testClassName, testMethodName)
        actual should be (Some(expected))
      }

      it("should return None if not found") {
        val expected = None

        val testClassName = "some class name"
        val testMethodName = "some method name"

        val actual =
          methodExitManager.getMethodExitRequest(testClassName, testMethodName)
        actual should be (expected)
      }
    }

    describe("#removeMethodExitRequestWithId") {
      it("should return true if the method exit request was removed") {
        val expected = true
        val stubRequest = stub[MethodExitRequest]

        val testClassName = "some class name"
        val testMethodName = "some method name"

        (mockEventRequestManager.createMethodExitRequest _).expects()
          .returning(stubRequest).once()

        methodExitManager.createMethodExitRequestWithId(
          TestRequestId,
          testClassName,
          testMethodName
        )

        (mockEventRequestManager.deleteEventRequest _)
          .expects(stubRequest).once()

        val actual =
          methodExitManager.removeMethodExitRequestWithId(TestRequestId)
        actual should be (expected)
      }

      it("should return false if the method exit request was not removed") {
        val expected = false

        val testClassName = "some class name"
        val testMethodName = "some method name"

        val actual =
          methodExitManager.removeMethodExitRequestWithId(TestRequestId)
        actual should be (expected)
      }
    }

    describe("#removeMethodExitRequest") {
      it("should return true if the method exit request was removed") {
        val expected = true
        val stubRequest = stub[MethodExitRequest]

        val testClassName = "some class name"
        val testMethodName = "some method name"

        (mockEventRequestManager.createMethodExitRequest _).expects()
          .returning(stubRequest).once()

        methodExitManager.createMethodExitRequest(testClassName, testMethodName)

        (mockEventRequestManager.deleteEventRequest _)
          .expects(stubRequest).once()

        val actual =
          methodExitManager.removeMethodExitRequest(testClassName, testMethodName)
        actual should be (expected)
      }

      it("should return false if the method exit request was not removed") {
        val expected = false

        val testClassName = "some class name"
        val testMethodName = "some method name"

        val actual =
          methodExitManager.removeMethodExitRequest(testClassName, testMethodName)
        actual should be (expected)
      }
    }
  }
}
