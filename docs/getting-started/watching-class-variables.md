# Working with watchpoints

---

## Overview

The following will provide you with a brief overview of creating watchpoints,
chaining together reactions to watchpoint events, and removing watchpoints.

## Creating

The Scala debugger API provides a simple way to create watchpoints in your
debugger application. Given a _ScalaVirtualMachine_ instance, you can execute
the `onAccessWatchpoint` or `onUnsafeAccessWatchpoint` method to place a
watchpoint that triggers when a field is accessed (read) or
`onModificationWatchpoint` or `onUnsafeModificationWatchpoint` method to
place a watchpoint that triggers when a field is modified (written).

```scala
val s: ScalaVirtualMachine = /* some virtual machine */
s.onUnsafeAccessWatchpoint("some.class.name", "fieldName")
```

You do not need to worry about creating the same watchpoint over and over
as these methods cache your request based on the provided arguments.
So, you can reference the same watchpoint multiple times.

The difference between safe and unsafe operations is that the unsafe operation
will throw an exception if an error occurs that prevents the watchpoint from
being created while the safe operation will wrap the error in a `Try` object.

## Pipelining

Once you have created a watchpoint, you can chain together a pipeline of
functions that you want to execute when the watchpoint is triggered. These
functions are consistent with standard Scala `map`, `flatMap`, and `foreach`
structure.

```scala
val s: ScalaVirtualMachine = /* some virtual machine */
s.onUnsafeAccessWatchpoint("some.class.name", "fieldName").foreach(e => {
  val className = e.field().declaringType().name()
  val fieldName = e.field().name()

  println(s"$className had field $fieldName accessed!")
})
```

## Removing

Whenever you are finished with the watchpoint, you can remove it by closing the
associated pipeline.

```scala
val s: ScalaVirtualMachine = /* some virtual machine */
val w = s.onUnsafeAccessWatchpoint("some.class.name", "fieldName")

w.foreach(_ => println("Field accessed!"))

import org.scaladebugger.api.profiles.Constants.CloseRemoveAll
w.close(data = CloseRemoveAll)
```

!!! warning "Warning:"
    Currently, you must provide close with the flag `CloseRemoveAll` in order
    to remove the request and any underlying event handlers.

    Specifying close without that flag will only stop events going through
    that specific pipeline. If all pipelines associated with a request are
    closed, the request will also be removed.

By default, `close` will be triggered immediately. Alternatively, you can
invoke `close` with `now = false` to close the pipeline once the next
event occurs.

## Cookbook

See the [cookbook][cookbook] for a working example.

[cookbook]: /cookbook/watching-modification-of-a-class-variable/

