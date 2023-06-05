package com.order.process.orderprcessingservice.scheduler;

import com.order.process.orderprcessingservice.service.FileProcessingService;
import org.awaitility.Awaitility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.ScheduledMethodRunnable;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OrderProcessSchedulerTest implements SchedulingConfigurer {

    @InjectMocks
    OrderProcessScheduler orderProcessScheduler;

    @Mock
    FileProcessingService fileProcessingService;

    ScheduledTaskRegistrar taskRegistrar;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.configureTasks(taskRegistrar);

        taskRegistrar = new ScheduledTaskRegistrar();
        taskRegistrar.setScheduler(taskExecutor());

    }

    @Test
    public void testLaunchJob() throws NoSuchMethodException {
        Awaitility.setDefaultTimeout(Duration.ofSeconds(30));
        Awaitility.setDefaultPollInterval(Duration.ofSeconds(1));

        ScheduledMethodRunnable scheduledMethod = new ScheduledMethodRunnable(orderProcessScheduler, "launchJob");
        CronTrigger cronTrigger = new CronTrigger("*/20 * * * * *");
        Objects.requireNonNull(taskRegistrar.getScheduler()).schedule(scheduledMethod, cronTrigger);

        Awaitility.await().atMost(25, TimeUnit.SECONDS).untilAsserted(() ->
                Mockito.verify(fileProcessingService).processOrderFileForScheduler()
        );
    }

    @Override
    public void configureTasks(@NotNull ScheduledTaskRegistrar taskRegistrar) {}

    private ScheduledExecutorService taskExecutor() {
        return Executors.newScheduledThreadPool(1);
    }
}
