package com.sample.lambda;

import java.util.List;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;
import com.amazonaws.util.json.JSONException;

public class LambdaFunctionHandler implements RequestHandler<SNSEvent, Object> {
	
	private static final String ACCEPTABLE_INSTANCE_TYPE = "t2.micro";
	private static final String TOPIC_ARN = "arn:aws:sns:ap-northeast-1:694273932022:NginxErrorTopic";

	@Override
	public Object handleRequest(SNSEvent input, Context context) {
    	LambdaLogger logger = context.getLogger();
   		logger.log("start");
   		try {

   			logger.log("Input(json): " + input.toString());
   			List<SNSRecord> records = input.getRecords();
   			for(SNSRecord record : records) {
   				record.toString();
   			}
    	
   			List<Instance> instances = Util.getInvalidEc2ist(ACCEPTABLE_INSTANCE_TYPE);
   			
   			if(instances == null || instances.isEmpty()) {
   				logger.log("don't exist invalid EC2 instance type.(EC2(" 
   			+ ACCEPTABLE_INSTANCE_TYPE + ") is acceptable.");
   				return "";
   			}

  			List<String> messageIds = Util.publish(instances, TOPIC_ARN);

    		if(messageIds == null || messageIds.isEmpty()) {
    			logger.log("cloud not publish SNS");
    			return "";
    		}

    		for( String messageId: messageIds) {
    			logger.log("messageId is " + messageId);
    		}
   		} catch (JSONException e) {
    		logger.log(e.getMessage());
            throw new RuntimeException(e);
		}

   		logger.log("end");
    	
    	return "OK";
	}
}