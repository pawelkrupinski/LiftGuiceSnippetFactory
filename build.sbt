name := "LifGuiceSnippetFactory"

scalaVersion := "2.9.1"

resolvers += "Maven" at "http://central.maven.org/maven2"

libraryDependencies += "net.liftweb" %% "lift-webkit" % "2.4" % "compile->default"

libraryDependencies += "com.google.inject" % "guice" % "3.0"
