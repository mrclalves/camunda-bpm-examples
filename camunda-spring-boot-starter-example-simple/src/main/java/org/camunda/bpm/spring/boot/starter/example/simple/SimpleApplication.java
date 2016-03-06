package org.camunda.bpm.spring.boot.starter.example.simple;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.SQLException;

@SpringBootApplication
@EnableScheduling
public class SimpleApplication {

  public static void main(final String... args) throws Exception {
    SpringApplication.run(SimpleApplication.class, args);
  }

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private HistoryService historyService;

  @Autowired
  private ConfigurableApplicationContext context;

  @Autowired
  private Showcase showcase;


  @Scheduled(fixedDelay = 500L)
  public void waitForProcessFinished() {
    String processInstanceId = showcase.getProcessInstanceId();

    if (processInstanceId == null) {
      logger.info("processInstance not yet started!");
      return;
    }

    if (isProcessInstanceFinished()) {
      logger.info("processinstance ended!");

      SpringApplication.exit(context, new ExitCodeGenerator() {

        @Override
        public int getExitCode() {
          return 0;
        }
      });
      return;
    }
    logger.info("processInstance not yet ended!");
  }

  public boolean isProcessInstanceFinished() {
      final HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
        .processInstanceId(showcase.getProcessInstanceId())
        .singleResult();

      return historicProcessInstance != null && historicProcessInstance.getEndTime() != null;

  }

}