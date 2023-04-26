package com.order.process.orderprcessingservice.config;

import com.order.process.orderprcessingservice.constant.FileProcessingConstant;
import com.order.process.orderprcessingservice.constant.OrderProcessingConstant;
import com.order.process.orderprcessingservice.entity.Order;
import com.order.process.orderprcessingservice.entity.OrderConfig;
import com.order.process.orderprcessingservice.repository.OrderConfigRepository;
import com.order.process.orderprcessingservice.repository.OrderProcessingRepository;
import jakarta.servlet.MultipartConfigElement;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.unit.DataSize;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
@AllArgsConstructor
public class OrderProcessingConfig {

    static Logger log = LogManager.getLogger(OrderProcessingConfig.class);

    @Autowired
    OrderProcessingRepository orderRepository;

    @Autowired
    OrderConfigRepository orderConfigRepository;

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
        Map<String, String> configMap = configMap();

        task.setDirectoryResource(
                configMap.get(FileProcessingConstant.FILE_TEMP_DIRECTORY),
                configMap.get(FileProcessingConstant.FILE_PROCESSED_DIRECTORY));

        return new StepBuilder("move-and-delete-file",jobRepository)
                .tasklet(task,transactionManager)
                .build();
    }

    @Bean
    public Map<String, String> numberPatternCountryMap(){

        Map<String, String> numberPatternCountryMap = new HashMap<>();

        numberPatternCountryMap.put(OrderProcessingConstant.COUNTRY_REGEX_CAMEROON,
                OrderProcessingConstant.COUNTRY_CAMEROON);

        numberPatternCountryMap.put(OrderProcessingConstant.COUNTRY_REGEX_ETHIOPIA,
                OrderProcessingConstant.COUNTRY_ETHIOPIA);

        numberPatternCountryMap.put(OrderProcessingConstant.COUNTRY_REGEX_MOROCCO,
                OrderProcessingConstant.COUNTRY_MOROCCO);

        numberPatternCountryMap.put(OrderProcessingConstant.COUNTRY_REGEX_MOZAMBIQUE,
                OrderProcessingConstant.COUNTRY_MOZAMBIQUE);

        numberPatternCountryMap.put(OrderProcessingConstant.COUNTRY_REGEX_UGANDA,
                OrderProcessingConstant.COUNTRY_UGANDA);

        return numberPatternCountryMap;
    }

    @Bean
    public Map<String, String> configMap(){
        return orderConfigRepository.findAll().stream()
                .filter(Objects::nonNull)
                .collect(Collectors
                        .toMap(OrderConfig::getName,OrderConfig::getValue));
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        Map<String, String> configMap = configMap();

        factory.setMaxFileSize(DataSize.ofBytes(Long.parseLong(configMap
                .get(FileProcessingConstant.MULTIPART_MAX_FILE_SIZE))));

        factory.setMaxRequestSize(DataSize.ofBytes(Long.parseLong(configMap
                .get(FileProcessingConstant.MULTIPART_MAX_REQUEST_SIZE))));
        return factory.createMultipartConfig();
    }


}
