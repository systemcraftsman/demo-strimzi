package cz.scholz.kafka.kafkaclients;

import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SslConfigs;

public class MessageConsumerSSL {
    private static int timeout = 5000;
    private static int timeTick = 1000;

    private static Boolean debug = true;

    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "my-cluster-kafka-bootstrap-kafka-demo.apps.ist-jug-3b4a.openshiftworkshop.com:443");

        props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-other-group");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "truststore.jks");
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "123456");
        props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "user.p12");
        props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "123456");
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "HTTPS"); // Hostname verification

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Collections.singletonList("my-topic"));

        Date totalStartTime = new Date();
        Date blockStartTime = new Date();
        int messageNo = 0;
        int size = 0;
        int sizeBlock = 0;

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(timeout);

            if (records.isEmpty()) {
                System.out.println("-I- No message in topic for " + timeout / 1000 + " seconds. Finishing ...");
                break;
            }

            for (ConsumerRecord<String, String> record : records) {
                size += record.serializedValueSize() + record.serializedKeySize();
                sizeBlock += record.serializedValueSize() + record.serializedKeySize();
                messageNo++;

                if (debug) {
                    System.out.println(
                            "-I- received message no. " + messageNo + ": " + record.key() + " / " + record.value());
                }

                if (messageNo % timeTick == 0) {
                    Date blockEndTime = new Date();
                    System.out.println("-I- " + messageNo + " messages received: "
                            + ((float) timeTick / (blockEndTime.getTime() - blockStartTime.getTime()) * 1000)
                            + " msg/s, "
                            + ((float) sizeBlock / (blockEndTime.getTime() - blockStartTime.getTime()) * 1000 / 1000)
                            + " kB/s, average size " + ((float) sizeBlock / (float) timeTick));
                    blockStartTime = new Date();
                    sizeBlock = 0;
                }
            }
        }

        Date blockEndTime = new Date();
        Date totalEndTime = new Date();

        consumer.close();

        if (messageNo % timeTick != 0) {
            System.out.println("-I- " + messageNo + " messages received: "
                    + ((float) (messageNo % timeTick) / (blockEndTime.getTime() - blockStartTime.getTime() - timeout)
                            * 1000)
                    + " msg/s, "
                    + ((float) sizeBlock / (blockEndTime.getTime() - blockStartTime.getTime()) * 1000 / 1000)
                    + " kB/s, average size " + ((float) sizeBlock / (float) (messageNo % timeTick)));
        }

        System.out.println("-I- ####################################");
        System.out.println("-I- Total " + messageNo + " messages received: avg "
                + ((float) messageNo / (totalEndTime.getTime() - totalStartTime.getTime() - timeout) * 1000)
                + " msg/s, "
                + ((float) size / (totalEndTime.getTime() - totalStartTime.getTime() - timeout) * 1000 / 1000)
                + " kB/s, average size " + ((float) size / (float) messageNo));
    }
}