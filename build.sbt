import sbt._

lazy val root = (project in file(".")).
    settings(
      organization := "com.devicehive",
      name := "devicehive-message-loader",
      version := "0.1",
      crossScalaVersions  := Seq("2.10.6", "2.11.7"),
      assemblyJarName in assembly := s"${name.value}-${scalaVersion.value}-${version.value}.jar",
      ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) },
      resolvers += Resolver.mavenLocal,
      assemblyMergeStrategy in assembly := {
        case PathList("META-INF", xs@_*) => MergeStrategy.discard
        case x if Assembly.isConfigFile(x) => MergeStrategy.concat
        case x => MergeStrategy.first
      },

      libraryDependencies ++= {
        val sparkVersion            = "1.6.0"
        val connectorVersion        = "1.6.0"
        val jsonLibVersion          = "3.2.11"

        Seq(
          "org.apache.spark"          %% "spark-sql"                        % sparkVersion,
          "org.apache.spark"          %% "spark-core"                       % sparkVersion,
          "org.apache.spark"          %% "spark-streaming"                  % sparkVersion,    
          "org.apache.spark"          %% "spark-streaming-kafka"            % sparkVersion, 
          "com.datastax.spark"        %% "spark-cassandra-connector"        % connectorVersion,
          "org.json4s"                %% "json4s-jackson"                   % jsonLibVersion
        )
      }
    )