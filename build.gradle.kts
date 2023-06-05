import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	java
	id("org.springframework.boot") version "3.0.6"
	id("io.spring.dependency-management") version "1.1.0"
	id("jacoco")
}

group = "com.order.process"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
	all {
		exclude (group= "org.springframework.boot", module= "spring-boot-starter-logging")
	}

}

repositories {
	mavenCentral()
	maven{
		url = uri("https://splunk.jfrog.io/splunk/ext-releases-local")
		//url = uri("https://splunk.artifactoryonline.com/splunk/ext-releases-local")
		//url = uri("https://repo.spring.io/libs-milestone/")
		//url = uri("https://mvnrepository.com/artifact/com.splunk.logging/splunk-library-javalogging")
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-batch")
	implementation("org.springframework.boot:spring-boot-starter-log4j2")
	//runtimeOnly("com.splunk.logging:splunk-library-javalogging:1.11.7")
	implementation("com.splunk.logging:splunk-library-javalogging:1.11.7")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.4")
	implementation("com.splunk:splunk:1.6.5.0")
	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("com.fasterxml.jackson.core:jackson-core")
	implementation("com.fasterxml.jackson.core:jackson-databind")

	implementation("org.awaitility:awaitility")
	//implementation("com.google.code.gson:gson")

	//implementation("org.slf4j:slf4j-api")
	//implementation("org.slf4j:slf4j-simple")

	//implementation("org.apache.logging.log4j:log4j-slf4j-impl")

	implementation("org.apache.logging.log4j:log4j-api")
	implementation("org.apache.logging.log4j:log4j-core")
	//implementation("com.splunk.logging:splunk-library-logging-log4j2:1.6.0")
	implementation("org.slf4j:slf4j-api") {
		exclude(module = "org.slf4j:jcl-over-slf4j")
		exclude(module = "org.slf4j:jul-to-slf4j")
		exclude(module = "org.slf4j:log4j-over-slf4j")
	}

	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.batch:spring-batch-test")

}

dependencyManagement {
	imports {
		mavenBom("org.springframework.boot:spring-boot-dependencies:3.0.6")
	}
	repositories{
		mavenCentral()
		maven{
			url = uri("https://splunk.jfrog.io/splunk/ext-releases-local")
		}
	}
}

tasks.register<Copy>("copySplunkLoggingJar") {
	from(configurations.runtimeClasspath.get().filter { it.name.startsWith("splunk-library-logging-log4j2") })
	into("$buildDir/libs")
}

tasks.named<BootJar>("bootJar") {
	dependsOn("copySplunkLoggingJar")
	from("$buildDir/libs") {
		include("splunk-library-logging-log4j2-*.jar")
	}
}


sourceSets.main.configure {
	java.setSrcDirs(mutableListOf<String>())
	java.srcDirs("src/main/java")

	resources.setSrcDirs(mutableListOf<String>())
	resources.srcDirs("src/main/resources").includes.addAll(arrayOf("**/*.*"))

	println("MainSource...."+java.srcDirs)
	println("MainResources--"+ resources.srcDirs)
}

sourceSets.test.configure {
	java.setSrcDirs(mutableListOf<String>())
	java.srcDirs("src/test/unit/java", "src/test/integration/java")

	resources.setSrcDirs(mutableListOf<String>())
	resources.srcDirs("src/test/unit/resources", "src/test/integration/resources").includes.addAll(arrayOf("**/*.*"))

	println("TestSource...."+java.srcDirs)
	println("TestResources--"+ resources.srcDirs)
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("java.library.path", buildDir.resolve("libs").absolutePath)
	finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run

}
jacoco {
	toolVersion = "0.8.8"
	reportsDirectory.set(layout.buildDirectory.dir("$buildDir/reports/jacoco"))
}
tasks.jacocoTestReport {
	dependsOn(tasks.test) // tests are required to run before generating the report
	reports {
		xml.required.set(false)
		csv.required.set(false)
		html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
	}
	classDirectories.setFrom(
			files(classDirectories.files.map {
				fileTree(it) {
					exclude(
							"com/order/process/orderprcessingservice/config/**",
							"com/order/process/orderprcessingservice/advice/**",
							"com/order/process/orderprcessingservice/adapter/**",
							"com/order/process/orderprcessingservice/constant/**",
							"com/order/process/orderprcessingservice/entity/**",
							"com/order/process/orderprcessingservice/request/**",
							"com/order/process/orderprcessingservice/response/**",
							"com/order/process/orderprcessingservice/**.class",
					)
				}
			})
	)
}