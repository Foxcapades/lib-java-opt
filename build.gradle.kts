import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
  `java-library`
  `maven-publish`
  signing
}

group = "io.foxcapades.lib"
version = "1.1.0"

repositories {
  mavenLocal()
  mavenCentral()
}

java {
  sourceCompatibility = JavaVersion.VERSION_16
  targetCompatibility = JavaVersion.VERSION_16

  withSourcesJar()
  withJavadocJar()

  modularity.inferModulePath.set(true)
}

dependencies {
  implementation("org.jetbrains:annotations:21.0.1")
  testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
  testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.2")
}

tasks.test {
  useJUnitPlatform()

  testLogging {
    events = setOf(
      TestLogEvent.PASSED,
      TestLogEvent.FAILED,
      TestLogEvent.SKIPPED,
    )
  }
}

publishing {
  repositories {
    maven {
      name = "nexus"
      url  = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

      credentials {
        username = project.findProperty("nexus.user") as String?
        password = project.findProperty("nexus.pass") as String?
      }
    }
  }

  publications {
    create<MavenPublication>("maven") {
      from(components["java"])

      pom {
        name.set("Extensible Option Types")
        description.set("Extensible and 3 state option types.")
        inceptionYear.set("2021")

        url.set("https://github.com/Foxcapades/lib-java-array-suppliers")

        developers {
          developer {
            id.set("epharper")
            name.set("Elizabeth Paige harper")
            email.set("foxcapade@gmail.com")
            url.set("https://github.com/Foxcapades")
          }
        }

        licenses {
          license {
            name.set("MIT")
          }
        }

        scm {
          connection.set("scm:git:git://github.com/Foxcapades/lib-java-opt.git")
          developerConnection.set("scm:git:ssh://github.com/Foxcapades/lib-java-opt.git")
          url.set("https://github.com/Foxcapades/lib-java-opt")
        }
      }
    }
  }
}

signing {
  useGpgCmd()
  sign(configurations.archives.get())
  sign(publishing.publications["maven"])
}