package com.rsk.frt.ingestion.pubsub;

import org.springframework.cloud.gcp.pubsub.PubSubAdmin;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
public class EventPublisher {

    private final PubSubTemplate pubSubTemplate;

    private final PubSubAdmin pubSubAdmin;

    public EventPublisher(PubSubTemplate pubSubTemplate, PubSubAdmin pubSubAdmin) {
        this.pubSubTemplate = pubSubTemplate;
        this.pubSubAdmin = pubSubAdmin;
    }

    public void publish( String topicName, String message) {
        this.pubSubTemplate.publish(topicName, message);
    }

}