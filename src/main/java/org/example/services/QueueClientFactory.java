package org.example.services;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Component
public class QueueClientFactory {
    SqsClient client = null;
    public SqsClient getClient() {

        client = client == null
            ? SqsClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build()
            : client;

        return client;
    }
}
