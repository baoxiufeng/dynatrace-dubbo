package com.dynatrace.adk.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.dynatrace.adk.DynaTraceADKFactory;
import com.dynatrace.adk.Tagging;

@Activate(group = Constants.PROVIDER, order=Integer.MIN_VALUE)
public class DynatraceProviderFilter implements Filter {
	
	private static final String DYNATRACE_TAG_KEY = "DYNATRACE_TAG_KEY";
	
	public DynatraceProviderFilter() {
		DynaTraceADKFactory.class.getName();
	}

	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		
		String tagString = invocation.getAttachments().get(DYNATRACE_TAG_KEY);
		if(tagString != null){// this is a consumer
			
			recordTagString(tagString);
			
			// initialize the dynaTrace ADK
			DynaTraceADKFactory.initialize();
			
			// get an instance of the Tagging ADK
			Tagging tagging = DynaTraceADKFactory.createTagging();
			
			tagging.setTagFromString(tagString);
			
			tagging.startServerPurePath();
			
			Result result = invoker.invoke(invocation);
			
			tagging.endServerPurePath();
			
			return result;
		}else{
			return invoker.invoke(invocation);
		}
		
	}
	
	//this is a empty method for dynaTrace to monitor the argument
	private void recordTagString(String tagString){
		System.out.println("invoke in " + this.getClass().getName() + "wit tagString: " + tagString);
		return;
	}

}
