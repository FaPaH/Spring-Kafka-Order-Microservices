package com.fapah.ordermicroservice;

import com.fapah.core.dto.ItemsDto;
import com.fapah.core.event.OrderCreateEvent;
import com.fapah.ordermicroservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 3, count = 3, controlledShutdown = true)
@SpringBootTest(properties = "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}")
@RequiredArgsConstructor
public class OrderServiceIntegrationTest {

    private final OrderService orderService;

    private final Environment environment;

    private final EmbeddedKafkaBroker embeddedKafkaBroker;

    private KafkaMessageListenerContainer<String, OrderCreateEvent> container;

    private BlockingQueue<ConsumerRecord<String, OrderCreateEvent>> records;

    @BeforeAll
    void setup() {
        DefaultKafkaConsumerFactory<String, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerProperties());
        ContainerProperties containerProperties = new ContainerProperties(environment.getProperty("order-created-events-topic-name"));

        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, OrderCreateEvent>) records::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());

    }

    @Test
    void testCreateOrder_whenGivenValidOrderDetails_successfullySendKafkaMessage() throws InterruptedException {

        //Arrange
        List<ItemsDto> itemsDtoList = new ArrayList<>();
        ItemsDto itemOne = ItemsDto.builder()
                .productName("Iphone 16")
                .price(BigDecimal.valueOf(1600))
                .quantity(20)
                .build();

        ItemsDto itemTwo = ItemsDto.builder()
                .productName("Samsung Galaxy")
                .price(BigDecimal.valueOf(1000))
                .quantity(30)
                .build();

        itemsDtoList.add(itemOne);
        itemsDtoList.add(itemTwo);

        OrderCreateEvent orderCreateEvent = OrderCreateEvent.builder()
                .orderId(UUID.randomUUID().toString())
                .orderItemsDto(itemsDtoList)
                .build();

        //Act
        orderService.sendOrderStockRequest(orderCreateEvent);

        //Assert
        ConsumerRecord<String, OrderCreateEvent> message = records.poll(3000, TimeUnit.MILLISECONDS);
        assertNotNull(message);
        assertNotNull(message.key());
        OrderCreateEvent orderCreateEventTest = message.value();
        assertEquals(orderCreateEvent.getOrderId(), orderCreateEventTest.getOrderId());
        assertNotNull(orderCreateEvent.getOrderItemsDto());

        for (ItemsDto itemTest : orderCreateEventTest.getOrderItemsDto()) {
            for (ItemsDto item : itemsDtoList) {
                assertEquals(item.getProductName(), itemTest.getProductName());
                assertEquals(item.getPrice(), itemTest.getPrice());
                assertEquals(item.getQuantity(), itemTest.getQuantity());
            }
        }
    }

    private Map<String, Object> getConsumerProperties() {
        return Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class,
                ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id"),
                JsonDeserializer.TRUSTED_PACKAGES, environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, environment.getProperty("spring.kafka.consumer.auto-offset-reset")
        );
    }

    @AfterAll
    void tearDown() {
        container.stop();
    }
}
