package com.order.process.orderprcessingservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Port API",version = "1.0", description = "API to provide information on port"))
@Slf4j
@EnableAsync
public class OrderProcessingServiceApplication {
		//extends DefaultBatchConfiguration {

	private JobRepository jobRepository;

	public static void main(String[] args) {
		SpringApplication.run(OrderProcessingServiceApplication.class, args);
	}
/*
	@Autowired
	TaskExecutor taskExecutor;*/

	/*@Override
	@Bean(name = "orderProcessingJobLauncher")
	public JobLauncher jobLauncher() {

		try {

			TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
			jobLauncher.setJobRepository(jobRepository);
			jobLauncher.setTaskExecutor(taskExecutor);
			jobLauncher.afterPropertiesSet();

			return jobLauncher;

		} catch (Exception e) {
			log.error("Exception occurred while creating custom job launcher",e);
			return super.jobLauncher();

		}
	}*/
}
