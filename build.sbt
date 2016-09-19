import sbt._

lazy val root = (project in file(".")).
    settings(
      organization := "com.devicehive",
      name := "devicehive-message-loader",
      version := "0.1",
      crossScalaVersions  := Seq("2.10.6", "2.11.7"),
      assemblyJarName in assembly := s"${name.value}-${scalaVersion.value}-${version.value}.jar",
      ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) },
      resolvers ++= Seq(
        Resolver.mavenLocal,
        "Basho Bintray Repo" at "https://dl.bintray.com/basho/data-platform"),
      assemblyMergeStrategy in assembly := {
        case PathList("META-INF", xs@_*) => MergeStrategy.discard
        case x if Assembly.isConfigFile(x) => MergeStrategy.concat
        case x => MergeStrategy.first
      },

      libraryDependencies ++= {
        val sparkVersion              = "1.6.0"
        val cassandraConnectorVersion = "1.6.0"
        val riakConnectorVersion      = "1.5.1"
        val jsonLibVersion            = "3.2.11"

        Seq(
          "org.apache.spark"          %% "spark-sql"                        % sparkVersion,
          "org.apache.spark"          %% "spark-core"                       % sparkVersion,
          "org.apache.spark"          %% "spark-streaming"                  % sparkVersion,    
          "org.apache.spark"          %% "spark-streaming-kafka"            % sparkVersion, 
          "com.datastax.spark"        %% "spark-cassandra-connector"        % cassandraConnectorVersion,
          "com.basho.riak"             % "spark-riak-connector"             % riakConnectorVersion classifier "uber"
            exclude("org.apache.spark", "spark-core")
            exclude("org.apache.spark", "spark-sql"),
          "org.json4s"                %% "json4s-jackson"                   % jsonLibVersion
        )
      }
    )