package com.zensar.aws.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.zensar.aws.repository.RepositoryImpl;

public class QueueService {

	private final static AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion("ap-south-1").build();

	static String messageBody;
	static String id;
	static String queueName = System.getenv("queueName");

	public static List<String> service(String fileContent, Context context) {
		String body = fileContent;
		context.getLogger().log("Sending message to " + queueName);
		sqs.sendMessage(new SendMessageRequest(queueName, body));
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueName);
		context.getLogger().log("Receiving messages from " + queueName);
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		for (Message message1 : messages) {
			messageBody = message1.getBody();
			id = message1.getMessageId();

		}
		context.getLogger().log("Message is: " + messageBody);
		// extracting string out of uploaded file
		JSONObject json = new JSONObject(messageBody);
		String input = json.getString("input");
		context.getLogger().log("Extracted string is: " + input);
		for (int i = 0; i < input.length(); i++) {
			if (Character.isAlphabetic(input.charAt(i)) == false) {
				input = input.substring(0, i) + input.substring(i + 1);
				i--;
			}
		}

		// method for getting possible combination out of a string
		List<String> combinations = combination(input);
		context.getLogger().log("The Combinations are: " + combinations);
		RepositoryImpl.repositoryImpl(id, combinations, context);
		String messageReceiptHandle = messages.get(0).getReceiptHandle();
		sqs.deleteMessage(new DeleteMessageRequest(queueName, messageReceiptHandle));

		return combinations;
	}

	public static List<String> combination(String message) {
		List<String> list = new ArrayList<String>();
		String str = "";
		for (int i = 0; i < message.length(); i++) {
			list.add(str + message.charAt(i));
		}
		for (int i = 0; i < message.length(); i++) {
			for (int j = 0; j < message.length(); j++)
				list.add(str + message.charAt(i) + message.charAt(j));
		}
		for (int i = 0; i < message.length(); i++) {
			for (int j = 0; j < message.length(); j++)
				for (int k = 0; k < message.length(); k++)

					list.add(str + message.charAt(i) + message.charAt(j) + message.charAt(k));
		}
		return list;
	}
}
