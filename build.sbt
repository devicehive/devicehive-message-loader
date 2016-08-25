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
          "org.apache.spark"          %% "spark-sql"                        % sparkVersion,
          "org.apache.spark"          %% "spark-core"                       % sparkVersion,
          "org.apache.spark"          %% "spark-streaming"                  % sparkVersion,    
          "org.apache.spark"          %% "spark-streaming-kafka"            % sparkVersion, 
          "com.datastax.spark"        %% "spark-cassandra-connector"        % connectorVersion,
          "org.json4s"                %% "json4s-jackson"                   % "3.2.11"
        )
      }
    )