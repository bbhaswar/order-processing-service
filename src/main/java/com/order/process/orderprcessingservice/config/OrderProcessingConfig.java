package com.order.process.orderprcessingservice.config;

import com.order.process.orderprcessingservice.entity.Order;
import com.order.process.orderprcessingservice.repository.OrderProcessingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
@Slf4j
public class OrderProcessingConfig {

    private OrderProcessingRepository orderRepository;

    /*public OrderProcessingConfig(){}

    public OrderProcessingConfig(@Value("${order.file.temp.storage.path}") String tempDirectory,
                          @Value("${order.file.processed.storage.path}") String processedDirectory){
        this.tempDirectory = tempDirectory;
        this.processedDirectory = processedDirectory;
    }*/
    @Bean
    @StepScope
    public FlatFileItemReader<Order> reader(@Value("#{jobParameters[fullPathFileName]}") String pathToFIle) {

        FlatFileItemReader<Order> itemReader = new FlatFileItemReader<>();
        //itemReader.setResource(new FileSystemResource("src/main/resources/Orders.csv"));
        itemReader.setResource(new FileSystemResource(new File(pathToFIle)));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<Order> lineMapper() {
        DefaultLineMapper<Order> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "email", "phoneNumber", "parcelWeight");

        BeanWrapperFieldSetMapper<Order> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Order.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;

    }

    @Bean
    public OrderProcessor processor() {
        return new OrderProcessor();
    }

    @Bean
    public RepositoryItemWriter<Order> writer() {
        RepositoryItemWriter<Order> writer = new RepositoryItemWriter<>();
        writer.setRepository(orderRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step1(FlatFileItemReader<Order> itemReader,
                      JobRepository jobRepository,
                      PlatformTransactionManager transactionManager) {

        return new StepBuilder("csv-step",jobRepository).
                <Order, Order>chunk(10,transactionManager)
                .reader(itemReader)
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job runJob(FlatFileItemReader<Order> itemReader,
                      JobRepository jobRepository,
                      PlatformTransactionManager transactionManager) {

        return new JobBuilder("processOrder",jobRepository)
                //.flow(step1(itemReader,jobRepository,transactionManager))
                .start(flow(itemReader,jobRepository,transactionManager))
                .end().build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }

    @Bean
    public Flow flow(FlatFileItemReader<Order> itemReader,
                     JobRepository jobRepository,
                     PlatformTransactionManager transactionManager) {
        return new FlowBuilder<SimpleFlow>("flow1")
                .start(step1(itemReader,jobRepository,transactionManager))
                .next(step2(jobRepository,transactionManager))
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository,
                      PlatformTransactionManager transactionManager) {
        FileDeletingTasklet task = new FileDeletingTasklet();
        Map<String, String> directoryMap = directoryMap();
        task.setDirectoryResource(directoryMap.get("tempDirectory"), directoryMap.get("processedDirectory"));
        return new StepBuilder("move-and-delete-file",jobRepository)
                .tasklet(task,transactionManager)
                .build();
    }

    @Bean
    public Map<String, String> numberPatternCountryMap(){

        Map<String, String> numberPatternCountryMap = new HashMap<>();

        numberPatternCountryMap.put("(237)\\ ?[2368]\\d{7,8}$", "Cameroon");

        numberPatternCountryMap.put("(251)\\ ?[1-59]\\d{8}$", "Ethiopia");

        numberPatternCountryMap.put("(212)\\ ?[5-9]\\d{8}$", "Morocco");

        numberPatternCountryMap.put("(258)\\ ?[28]\\d{7,8}$", "Mozambique");

        numberPatternCountryMap.put("(256)\\ ?\\d{9}$", "Uganda");

        return numberPatternCountryMap;
    }

    @Bean
    public Map<String, String> directoryMap(){

        Map<String, String> directoryMap = new HashMap<>();

        directoryMap.put("tempDirectory", "C:/Users/bbhas/source/order-processing-service/temp");

        directoryMap.put("processedDirectory", "C:/Users/bbhas/source/order-processing-service/processed");

        return directoryMap;
    }

}
