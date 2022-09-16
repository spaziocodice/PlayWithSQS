package org.example.controllers;

import org.example.services.QueueClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private QueueClientFactory queueClientFactory;

    private static final String QUEUE_PREFIX = "test-aws-api-";
    private static String queueUrl;

    @PostMapping("send")
    public void send(@RequestParam("queueUrl") String queueUrl, @RequestParam("message") String message) {
        queueClientFactory.getClient().sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .delaySeconds(10)
                .build());
    }

    @GetMapping("get")
    public List<String> getMessagesFromQueue(@PathParam("queueUrl") String queueUrl) {
        try {
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(5)
                    .build();
            return queueClientFactory.getClient().receiveMessage(receiveMessageRequest).messages().stream()
                    .map(Message::body)
                    .collect(Collectors.toList());
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            return List.of();
        }
    }

}
