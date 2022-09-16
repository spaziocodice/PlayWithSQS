package org.example.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.ListQueuesRequest;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
@RequestMapping("/queues")
public class QueueController {

    @Autowired
    private SqsClient sqsClient;

    private static final String QUEUE_PREFIX = "test-aws-api-";
    private static String queueUrl;

    @GetMapping("create")
    public String createQueue() {
        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName(QUEUE_PREFIX + System.currentTimeMillis())
                .build();

        var response = sqsClient.createQueue(createQueueRequest);

        return response.queueUrl();
    }

    @GetMapping("list")
    public List<String> listQueues() {
        ListQueuesRequest listQueuesRequest = ListQueuesRequest.builder()
                .queueNamePrefix(QUEUE_PREFIX)
                .build();
        ListQueuesResponse listQueuesResponse = sqsClient.listQueues(listQueuesRequest);

        return listQueuesResponse.queueUrls();
    }

    @GetMapping("url")
    public String getQueueUrl(@PathParam("name") String name) {
        try {
            GetQueueUrlResponse getQueueUrlResponse =
                    sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(name).build());
            String queueUrl = getQueueUrlResponse.queueUrl();
            return queueUrl;
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
            return null;
        }
    }

    @GetMapping("delete")
    public boolean deleteSQSQueue(@PathParam("queueName") String queueName) {
        try {
            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build();

            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();

            DeleteQueueRequest deleteQueueRequest = DeleteQueueRequest.builder()
                    .queueUrl(queueUrl)
                    .build();

            sqsClient.deleteQueue(deleteQueueRequest);

            return true;
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
            return false;
        }
    }
}
