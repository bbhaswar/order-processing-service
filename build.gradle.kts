plugins {
	java
	id("org.springframework.boot") version "3.0.6"
	id("io.spring.dependency-management") version "1.1.0"
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
	runtimeOnly("com.splunk.logging:splunk-library-javalogging:1.11.7")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.4")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.batch:spring-batch-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
