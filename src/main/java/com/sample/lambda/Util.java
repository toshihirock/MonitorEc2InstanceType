package com.sample.lambda;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.util.json.JSONException;

public class Util {
    
    public static List<Instance> getInvalidEc2ist(String instanceType) throws JSONException {
    	List<Instance> instances = new ArrayList<Instance>();

    	AmazonEC2Client ec2 = new AmazonEC2Client();
    	ec2.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
    	DescribeInstancesResult result = ec2.describeInstances();

    	for(Reservation reservation : result.getReservations()) {
    	  for(Instance instance : reservation.getInstances()) {
    		  if (!(instance.getInstanceType().equals(instanceType))) {
    			  instances.add(instance);
    		  }
    	  }
    	}
    	return instances;
    }
    
    public static List<String> publish(List<Instance> instances, String topicArn) throws JSONException {
    	List<String> messageIds = new ArrayList<String>();
    	
    	AmazonSNSClient sns = new AmazonSNSClient();
    	Region northEast1 = Region.getRegion(Regions.AP_NORTHEAST_1);
    	sns.setRegion(northEast1);
    	
    	for (Instance instance: instances) {
    		PublishResult result = sns.publish(topicArn,
    				"invalid EC2 instance type launched.Instance id = " 
    		+ instance.getInstanceId() + ", Instance Type = " + instance.getInstanceType());
    		messageIds.add(result.getMessageId());
    	}
    	return messageIds;
    }

}
