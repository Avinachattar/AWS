package com.zensar.aws.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.lambda.runtime.Context;

public class RepositoryImpl {
	private final static AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion("ap-south-1")
			.build();
	static String tableName = System.getenv("tableName");;

	public static void repositoryImpl(String id, List<String> combinations, Context context) {
		Map<String, AttributeValue> item = newItem(id, combinations);
		PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
		dynamoDB.putItem(putItemRequest);
		context.getLogger().log("Data Successfully Stored in : " + tableName);
	}

	private static Map<String, AttributeValue> newItem(String id, List<String> combinations) {
		Map<String, AttributeValue> item = new HashMap<>();
		item.put("ID", new AttributeValue().withS(id));
		item.put("Data", new AttributeValue().withSS(combinations));
		return item;
	}
}
