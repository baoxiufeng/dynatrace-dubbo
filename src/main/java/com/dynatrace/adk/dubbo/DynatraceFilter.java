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

@Activate(group = {Constants.PROVIDER, Constants.CONSUMER})
public class DynatraceFilter implements Filter {
	
	
	private static final String DYNATRACE_TAG_KEY = "DYNATRACE_TAG_KEY";

	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		
		String tagString = invocation.getAttachments().get(DYNATRACE_TAG_KEY);
		if(tagString != null){
			
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
			
			// initialize the dynaTrace ADK
			DynaTraceADKFactory.initialize();
			
			// get an instance of the Tagging ADK
			Tagging tagging = DynaTraceADKFactory.createTagging();
			tagString = tagging.getTagAsString();
			// insert a synchronous link node
			tagging.linkClientPurePath(false, tagString);
			invocation.getAttachments().put(DYNATRACE_TAG_KEY, tagString);
			// TODO Auto-generated method stub
			Result result = invoker.invoke(invocation);
			
			DynaTraceADKFactory.uninitialize();
			
			return result;
		}
	}

}
