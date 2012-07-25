package net.pawelkrupinski

import com.google.inject.Injector
import net.liftweb.http.LiftRules
import xml.NodeSeq
import net.liftweb.util.Helpers._
import java.lang.reflect.Method

class LiftGuiceSnippetFactory(val injector: Injector, val liftRules: LiftRules) {
  def registerWithLift() {
    liftRules.snippets.append(snippetFunction)
  }

  private def snippetFunction: PartialFunction[scala.List[String], (NodeSeq) => NodeSeq] = {
    case className :: method :: Nil if (findSnippetMethod(className, method).isDefined) => fetchSnippet(className, method)
    case className :: Nil if (findSnippetMethod(className).isDefined) => fetchSnippet(className, "render")
  }

  private def findSnippetMethod(className: String, method: String = "render") =
    findClass(className, liftRules.buildPackage("snippet"))
      .flatMap(_.getMethods().find(_.getName == method))

  private def fetchSnippet(className: String, method: String): (NodeSeq) => NodeSeq =
    findSnippetMethod(className, method).map(method =>
      callMethodOrApplyFunction(instantiate(method.getDeclaringClass.asInstanceOf[Class[AnyRef]]), method) _
    ).openTheBox

  private def instantiate[T](theClass: Class[T]): T = injector.getInstance(theClass)

  private def callMethodOrApplyFunction(instance: AnyRef, method: Method)(nodes: NodeSeq): NodeSeq = {
    if (classOf[NodeSeq].isAssignableFrom(method.getReturnType)) {
      method.invoke(instance, nodes).asInstanceOf[NodeSeq]
    } else {
      method.invoke(instance).asInstanceOf[NodeSeq => NodeSeq].apply(nodes)
    }
  }
}
