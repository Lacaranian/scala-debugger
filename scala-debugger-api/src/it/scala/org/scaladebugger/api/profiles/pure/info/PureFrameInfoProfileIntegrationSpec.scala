package org.scaladebugger.api.profiles.pure.info
import acyclic.file

import com.sun.jdi.ThreadReference
import org.scaladebugger.api.lowlevel.events.misc.NoResume
import org.scaladebugger.api.profiles.pure.PureDebugProfile
import org.scaladebugger.api.utils.JDITools
import org.scaladebugger.api.virtualmachines.DummyScalaVirtualMachine
import org.scalatest.concurrent.Eventually
import org.scalatest.{FunSpec, Matchers, ParallelTestExecution}
import test.{TestUtilities, VirtualMachineFixtures}

class PureFrameInfoProfileIntegrationSpec extends FunSpec with Matchers
  with ParallelTestExecution with VirtualMachineFixtures
  with TestUtilities with Eventually
{
  implicit override val patienceConfig = PatienceConfig(
    timeout = scaled(test.Constants.EventuallyTimeout),
    interval = scaled(test.Constants.EventuallyInterval)
  )

  describe("PureFrameInfoProfile") {
    it("should be able to get the location of the frame") {
      val testClass = "org.scaladebugger.test.info.Variables"
      val testFile = JDITools.scalaClassStringToFileString(testClass)

      @volatile var t: Option[ThreadReference] = None
      val s = DummyScalaVirtualMachine.newInstance()

      // NOTE: Do not resume so we can check the variables at the stack frame
      s.withProfile(PureDebugProfile.Name)
        .getOrCreateBreakpointRequest(testFile, 32, NoResume)
        .foreach(e => t = Some(e.thread()))

      withVirtualMachine(testClass, pendingScalaVirtualMachines = Seq(s)) { (s) =>
        logTimeTaken(eventually {
          val location = s.withProfile(PureDebugProfile.Name)
            .thread(t.get).topFrame
            .location

          location.sourcePath should be (testFile)
          location.lineNumber should be (32)
        })
      }
    }

    it("should be able to get the 'this' object of the current scope") {
      val testClass = "org.scaladebugger.test.info.Variables"
      val testFile = JDITools.scalaClassStringToFileString(testClass)

      @volatile var t: Option[ThreadReference] = None
      val s = DummyScalaVirtualMachine.newInstance()

      // NOTE: Do not resume so we can check the variables at the stack frame
      s.withProfile(PureDebugProfile.Name)
        .getOrCreateBreakpointRequest(testFile, 32, NoResume)
        .foreach(e => t = Some(e.thread()))

      withVirtualMachine(testClass, pendingScalaVirtualMachines = Seq(s)) { (s) =>
        logTimeTaken(eventually {
          val thisTypeName = s.withProfile(PureDebugProfile.Name)
            .thread(t.get).topFrame
            .thisObject.typeInfo.name

          thisTypeName should be ("org.scaladebugger.test.info.Variables$")
        })
      }
    }

    it("should be able to get the thread associated with the frame") {
      val testClass = "org.scaladebugger.test.info.Variables"
      val testFile = JDITools.scalaClassStringToFileString(testClass)

      @volatile var t: Option[ThreadReference] = None
      val s = DummyScalaVirtualMachine.newInstance()

      // NOTE: Do not resume so we can check the variables at the stack frame
      s.withProfile(PureDebugProfile.Name)
        .getOrCreateBreakpointRequest(testFile, 32, NoResume)
        .foreach(e => t = Some(e.thread()))

      withVirtualMachine(testClass, pendingScalaVirtualMachines = Seq(s)) { (s) =>
        logTimeTaken(eventually {
          val thread = s.withProfile(PureDebugProfile.Name)
            .thread(t.get)

          thread.uniqueId should be (thread.topFrame.currentThread.uniqueId)
        })
      }
    }

    it("should be able to get a list of all variables") {
      val testClass = "org.scaladebugger.test.info.Variables"
      val testFile = JDITools.scalaClassStringToFileString(testClass)

      @volatile var t: Option[ThreadReference] = None
      val s = DummyScalaVirtualMachine.newInstance()

      // NOTE: Do not resume so we can check the variables at the stack frame
      s.withProfile(PureDebugProfile.Name)
        .getOrCreateBreakpointRequest(testFile, 32, NoResume)
        .foreach(e => t = Some(e.thread()))

      withVirtualMachine(testClass, pendingScalaVirtualMachines = Seq(s)) { (s) =>
        logTimeTaken(eventually {
          val variableNames = s.withProfile(PureDebugProfile.Name)
            .thread(t.get).topFrame
            .allVariables.map(_.name)

          variableNames should contain theSameElementsAs Seq(
            // Scala-specific variable
            "MODULE$",

            // Local argument variables
            "args",

            // Local non-argument variables
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",

            // Field variables
            "z1", "z2", "z3"
          )
        })
      }
    }

    it("should be able to get a list of all argument values") {
      val testClass = "org.scaladebugger.test.info.Variables"
      val testFile = JDITools.scalaClassStringToFileString(testClass)

      @volatile var t: Option[ThreadReference] = None
      val s = DummyScalaVirtualMachine.newInstance()

      // NOTE: Do not resume so we can check the variables at the stack frame
      s.withProfile(PureDebugProfile.Name)
        .getOrCreateBreakpointRequest(testFile, 32, NoResume)
        .foreach(e => t = Some(e.thread()))

      withVirtualMachine(testClass, pendingScalaVirtualMachines = Seq(s)) { (s) =>
        logTimeTaken(eventually {
          val frame = s.withProfile(PureDebugProfile.Name)
            .thread(t.get).topFrame
          val values = frame.argumentValues.map(_.toLocalValue)

          values should contain theSameElementsAs Seq(
            frame.variable("args").toValueInfo.toLocalValue
          )
        })
      }
    }

    it("should be able to get a list of all arguments") {
      val testClass = "org.scaladebugger.test.info.Variables"
      val testFile = JDITools.scalaClassStringToFileString(testClass)

      @volatile var t: Option[ThreadReference] = None
      val s = DummyScalaVirtualMachine.newInstance()

      // NOTE: Do not resume so we can check the variables at the stack frame
      s.withProfile(PureDebugProfile.Name)
        .getOrCreateBreakpointRequest(testFile, 32, NoResume)
        .foreach(e => t = Some(e.thread()))

      withVirtualMachine(testClass, pendingScalaVirtualMachines = Seq(s)) { (s) =>
        logTimeTaken(eventually {
          val variableNames = s.withProfile(PureDebugProfile.Name)
            .thread(t.get).topFrame
            .argumentLocalVariables.map(_.name)

          variableNames should contain theSameElementsAs Seq(
            // Local argument variables
            "args"
          )
        })
      }
    }

    it("should be able to get a list of all non-argument local variables") {
      val testClass = "org.scaladebugger.test.info.Variables"
      val testFile = JDITools.scalaClassStringToFileString(testClass)

      @volatile var t: Option[ThreadReference] = None
      val s = DummyScalaVirtualMachine.newInstance()

      // NOTE: Do not resume so we can check the variables at the stack frame
      s.withProfile(PureDebugProfile.Name)
        .getOrCreateBreakpointRequest(testFile, 32, NoResume)
        .foreach(e => t = Some(e.thread()))

      withVirtualMachine(testClass, pendingScalaVirtualMachines = Seq(s)) { (s) =>
        logTimeTaken(eventually {
          val variableNames = s.withProfile(PureDebugProfile.Name)
            .thread(t.get).topFrame
            .nonArgumentLocalVariables.map(_.name)

          variableNames should contain theSameElementsAs Seq(
            // Local non-argument variables
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l"
          )
        })
      }
    }

    it("should be able to get a list of all fields") {
      val testClass = "org.scaladebugger.test.info.Variables"
      val testFile = JDITools.scalaClassStringToFileString(testClass)

      @volatile var t: Option[ThreadReference] = None
      val s = DummyScalaVirtualMachine.newInstance()

      // NOTE: Do not resume so we can check the variables at the stack frame
      s.withProfile(PureDebugProfile.Name)
        .getOrCreateBreakpointRequest(testFile, 32, NoResume)
        .foreach(e => t = Some(e.thread()))

      withVirtualMachine(testClass, pendingScalaVirtualMachines = Seq(s)) { (s) =>
        logTimeTaken(eventually {
          val variableNames = s.withProfile(PureDebugProfile.Name)
            .thread(t.get).topFrame
            .fieldVariables.map(_.name)

          variableNames should contain theSameElementsAs Seq(
            // Scala-specific variable
            "MODULE$",

            // Field variables
            "z1", "z2", "z3"
          )
        })
      }
    }

    it("should be able to get a list of all local variables") {
      val testClass = "org.scaladebugger.test.info.Variables"
      val testFile = JDITools.scalaClassStringToFileString(testClass)

      @volatile var t: Option[ThreadReference] = None
      val s = DummyScalaVirtualMachine.newInstance()

      // NOTE: Do not resume so we can check the variables at the stack frame
      s.withProfile(PureDebugProfile.Name)
        .getOrCreateBreakpointRequest(testFile, 32, NoResume)
        .foreach(e => t = Some(e.thread()))

      withVirtualMachine(testClass, pendingScalaVirtualMachines = Seq(s)) { (s) =>
        logTimeTaken(eventually {
          val variableNames = s.withProfile(PureDebugProfile.Name)
            .thread(t.get).topFrame
            .localVariables.map(_.name)

          variableNames should contain theSameElementsAs Seq(
            // Local argument variables
            "args",

            // Local non-argument variables
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l"
          )
        })
      }
    }

    it("should be able to retrieve a single variable by name") {
      val testClass = "org.scaladebugger.test.info.Variables"
      val testFile = JDITools.scalaClassStringToFileString(testClass)

      @volatile var t: Option[ThreadReference] = None
      val s = DummyScalaVirtualMachine.newInstance()

      // NOTE: Do not resume so we can check the variables at the stack frame
      s.withProfile(PureDebugProfile.Name)
        .getOrCreateBreakpointRequest(testFile, 32, NoResume)
        .foreach(e => t = Some(e.thread()))

      withVirtualMachine(testClass, pendingScalaVirtualMachines = Seq(s)) { (s) =>
        logTimeTaken(eventually {
          // Scala specific
          s.withProfile(PureDebugProfile.Name)
            .thread(t.get).topFrame
            .variable("MODULE$").name should be ("MODULE$")

          // Argument
          s.withProfile(PureDebugProfile.Name)
            .thread(t.get).topFrame
            .variable("args").name should be ("args")

          // Local
          s.withProfile(PureDebugProfile.Name)
            .thread(t.get).topFrame
            .variable("a").name should be ("a")

          // Field
          s.withProfile(PureDebugProfile.Name)
            .thread(t.get).topFrame
            .variable("z1").name should be ("z1")
        })
      }
    }

    it("should be able to get variables from a closure") {
      val testClass = "org.scaladebugger.test.info.Variables"
      val testFile = JDITools.scalaClassStringToFileString(testClass)

      @volatile var t: Option[ThreadReference] = None
      val s = DummyScalaVirtualMachine.newInstance()

      // NOTE: Do not resume so we can check the variables at the stack frame
      s.withProfile(PureDebugProfile.Name)
        .getOrCreateBreakpointRequest(testFile, 41, NoResume)
        .foreach(e => t = Some(e.thread()))

      withVirtualMachine(testClass, pendingScalaVirtualMachines = Seq(s)) { (s) =>
        logTimeTaken(eventually {
          val variableNames = s.withProfile(PureDebugProfile.Name)
            .thread(t.get).topFrame
            .allVariables.map(_.name)

          // NOTE: As there is no custom logic, this depicts the raw, top-level
          //       variables seen within the closure
          variableNames should contain theSameElementsAs Seq(
            "h$1", "b$1",

            "serialVersionUID"
          )
        })
      }
    }
  }
}
