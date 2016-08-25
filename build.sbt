import sbt._

lazy val root = (project in file(".")).
    settings(
      organization := "com.devicehive",
      name := "devicehive-message-loader",
      version := "0.1",
      crossScalaVersions  := Seq("2.10.6", "2.11.7"),
      assemblyJarName in assembly := s"${name.value}-${scalaVersion.value}-${version.value}.jar",

      libraryDependencies ++= {
        val sparkVersion            = "1.6.0"
        val connectorVersion        = "1.6.0"

        Seq(
          "org.apache.spark"          %% "spark-core"                       % sparkVersion      %      "provided",
          "org.apache.spark"          %% "spark-streaming"                  % sparkVersion      %      "provided",
          "org.apache.spark"          %% "spark-streaming-kafka"            % sparkVersion      %      "provided",
          "com.datastax.spark"        %% "spark-cassandra-connector"        % connectorVersion
        )
      }
    )