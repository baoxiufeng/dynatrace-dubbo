package com.dynatrace.adk.dubbo;

import java.util.UUID;

import com.dynatrace.adk.DynaTraceADKFactory;
import com.dynatrace.adk.Tagging;

public abstract class TraceTagUtil {

	public static void logTraceTag(){
		// initialize the dynaTrace ADK
		DynaTraceADKFactory.initialize();
		// get an instance of the Tagging ADK
		Tagging tagging = DynaTraceADKFactory.createTagging();
		//
		logTraceTag(tagging.getTagAsString());
	}
	
	public static void logTraceTag(String tagString){
		
		System.out.println("thread=" + Thread.currentThread().getName() + ", tagString=" + tagString);
		return;
	}
	
	public static byte[] generateCustomTag(){
		return UUID.randomUUID().toString().getBytes();
	}
}
