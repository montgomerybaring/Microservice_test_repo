package com.rsk.frt.ingestion.pubsub;
import com.rsk.frt.ingestion.validation.DbtRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;


@Service
public class EventReader {

    @Value("${gcp.projectId}")
    String projectID;

    @Value("${gcp.pubSub.subscriptionName}")
    String subscriptionName;

    @Autowired
    DbtRun dbtRun;

    @Value("${gcp.pubSub.errorTopicName}")
    String errorTopicName;

    @Value("${gcp.pubSub.notificationTriggerTopicName}")
    String triggerTopicName;

    @Autowired
    EventPublisher eventPublisher;

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter(
            @Qualifier("pubsubInputChannel") MessageChannel inputChannel,
            PubSubTemplate pubSubTemplate) {
        System.out.println(projectID +"::" +subscriptionName);
        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionName);
        adapter.setOutputChannel(inputChannel);
        adapter.setAckMode(AckMode.MANUAL);

        return adapter;
    }

    @Bean
    public MessageChannel pubsubInputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "pubsubInputChannel")
    public MessageHandler messageReceiver() {
        return message -> {
            String incomingMessage = new String((byte[]) message.getPayload());
            System.out.println(incomingMessage);
            //call business logic here
            try {
                dbtRun.executeDbtRun();
                //dataValidator.runValidation();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            BasicAcknowledgeablePubsubMessage originalMessage =
                    message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
            originalMessage.ack();
        };
    }


}
