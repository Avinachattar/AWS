package com.zensar.aws;

import java.net.URLDecoder;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.zensar.aws.service.QueueService;

public class LambdaFunctionHandler implements RequestHandler<S3Event, String> {
	private AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("ap-south-1").build();

	public LambdaFunctionHandler() {
	}

	LambdaFunctionHandler(AmazonS3 s3) {
		this.s3 = s3;
	}

	@Override
	public String handleRequest(S3Event event, Context context) {
		context.getLogger().log("Received event: " + event);

		try {

			S3EventNotificationRecord record = event.getRecords().get(0);
			String bucket = record.getS3().getBucket().getName();
			String key = record.getS3().getObject().getKey();
			key = URLDecoder.decode(key, "UTF-8");
			String body = s3.getObjectAsString(bucket, key);
			context.getLogger().log(key + "  file uploaded in  " + bucket);
			context.getLogger().log("File content: " + body);
			QueueService.service(body, context);
			return "success";
		} catch (Exception e) {
			System.out.println("Exception: " + e);
			return "error";
		}

	}

}