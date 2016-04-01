package com.dynatrace.adk.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.support.RpcUtils;
import com.dynatrace.adk.DynaTraceADKFactory;
import com.dynatrace.adk.Tagging;

@Activate(group = Constants.CONSUMER, order=Integer.MAX_VALUE)
public class DynatraceConsumerFilter implements Filter {
	
	private static final String DYNATRACE_TAG_KEY = "DYNATRACE_TAG_KEY";
	
	public DynatraceConsumerFilter() {
		DynaTraceADKFactory.class.getName();
	}

	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
			
		// initialize the dynaTrace ADK
		DynaTraceADKFactory.initialize();
		
		// get an instance of the Tagging ADK
		Tagging tagging = DynaTraceADKFactory.createTagging();
		
		String tagString = tagging.getTagAsString();
		
		recordTagString(tagString);
					
		boolean isAsync = RpcUtils.isAsync(invoker.getUrl(), invocation);
		
		tagging.linkClientPurePath(isAsync, tagString);
		
		invocation.getAttachments().put(DYNATRACE_TAG_KEY, tagString);
		
		Result result = invoker.invoke(invocation);
		
		DynaTraceADKFactory.uninitialize();
		
		return result;		
	}
	
	//this is a empty method for dynaTrace to monitor the argument
	private void recordTagString(String tagString){
		//System.out.println("invoke in " + this.getClass().getName() + "wit tagString: " + tagString);
		return;
	}

}
