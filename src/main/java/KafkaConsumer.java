

import com.google.common.collect.ImmutableMap;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractKafkaMessageProducer implements MessageProducer {
  private static final Logger logger = LoggerFactory.getLogger(AbstractKafkaMessageProducer.class);

  private KafkaConsumerProvider kafkaConsumerProvider;
  private ExecutorService executorService;
  private List<AbstractProducerJob> producerJobList;
  private Integer producerThreads;
  private String producerId;
  private AtomicLong restartCount;

  public AbstractKafkaMessageProducer(KafkaConsumerProvider kafkaConsumerProvider, Integer producerThreads, String producerId) {
    this.kafkaConsumerProvider = kafkaConsumerProvider;
    this.executorService = Executors.newFixedThreadPool(producerThreads);
    this.producerJobList = new ArrayList<>();
    this.producerThreads = producerThreads;
    this.producerId = producerId;
    this.restartCount = new AtomicLong(0);
  }

  @Override
  public Boolean startProducing() {
    for (int i=0; i<producerThreads; i++) {
      producerJobList.add(new AbstractProducerJob(kafkaConsumerProvider.get(), i));
    }
    for (AbstractProducerJob producerJob : producerJobList) {
      executorService.submit(producerJob);
    }
    return Boolean.TRUE;
  }

  @Override
  public Boolean stopProducing() {
    for (AbstractProducerJob abstractProducerJob : producerJobList) {
      abstractProducerJob.stopProduction();
    }
    producerJobList = new ArrayList<>();
    return Boolean.TRUE;
  }

  @Override
  public String getStatus() {
    if (producerJobList.isEmpty()) {
      return "Not running";
    }
    return "Job running with " + String.valueOf(producerJobList.size()) + " threads.";
  }

  @Override
  public Boolean healthCheck() {
    if (restartCount.get() > 10) {
      return Boolean.FALSE;
    }
    return Boolean.TRUE;
  }

  public abstract Boolean consume(List<String> messageList);

  private synchronized void producerJobError(Integer id) {
    restartCount.incrementAndGet();
    LoggerHelper.logErrorMessage(logger, ImmutableMap.of("procuderId", producerId, "threadId", id, "restartCount", String.valueOf(restartCount.get())),
        "PRODUCER_THREAD_RESTARTED", "");
    AbstractProducerJob abstractProducerJob = new AbstractProducerJob(kafkaConsumerProvider.get(), id);
    producerJobList.set(id, abstractProducerJob);
    executorService.submit(abstractProducerJob);
  }

  private class AbstractProducerJob implements Runnable {
    private KafkaConsumer kafkaConsumer;
    private Boolean stopProduction;
    private Integer id;

    public AbstractProducerJob(KafkaConsumer kafkaConsumer, Integer id) {
      this.kafkaConsumer = kafkaConsumer;
      this.stopProduction = false;
      this.id = id;
    }

    @Override
    public void run() {
      logger.info("Start thread for producer {} thread id {}", producerId, id);
      try {
        while (!stopProduction) {
          ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(1000 * 60 * 10);
          if (!stopProduction) {
            List<String> messageList = new ArrayList<>();
            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
              messageList.add(consumerRecord.value());
            }
            Boolean status = Boolean.FALSE;
            try {
              status = consume(messageList);
            } catch (Exception e) {
              logger.error("Error in consumer for producer {} thread id {}.", producerId, id, e);
            }
            if (status) {
              kafkaConsumer.commitSync();
            } else {
              logger.error("Consumer couldn't process message. Stopping the thread for producer {} thread id {}.", producerId, id);
              kafkaConsumer.close();
              producerJobError(id);
              return;
            }
          }
        }
      } catch (Exception e) {
        LoggerHelper.logErrorMessage(logger, "UNCAUGHT_EXCEPTION_IN_PRODUCER",
            producerId + ":" + String.valueOf(id));
        logger.error("Uncaught exception in producer {}.", producerId, e);
        kafkaConsumer.close();
        producerJobError(id);
        return;
      }
      kafkaConsumer.close();
    }

    public void stopProduction() {
      stopProduction = true;
    }
  }
}