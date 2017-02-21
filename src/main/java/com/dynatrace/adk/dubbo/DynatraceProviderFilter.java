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
import com.dynatrace.adk.impl.DummyTaggingImpl;

@Activate(group = Constants.PROVIDER)
public class DynatraceProviderFilter implements Filter {
	
	private static final String DYNATRACE_TAG_KEY = "DYNATRACE_TAG_KEY";

	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		
		String tagString = invocation.getAttachments().get(DYNATRACE_TAG_KEY);
		
		if(tagString != null){
			Tagging tagging = null;
			try {
				// initialize the dynaTrace ADK
				DynaTraceADKFactory.initialize();
				
				// get an instance of the Tagging ADK
				tagging = DynaTraceADKFactory.createTagging();
				
				if(!(tagging instanceof DummyTaggingImpl)){
					
					tagging.setTagFromString(tagString);
					
					tagging.startServerPurePath();
					
					this.logTagging(tagString);
				}
			} catch (Throwable t) {
				// do nothing
			}
					
			Result result = invoker.invoke(invocation);
			
			try {
				if(tagging != null){
					if(!(tagging instanceof DummyTaggingImpl)){
						tagging.endServerPurePath();
					}
					DynaTraceADKFactory.uninitialize();
				}
			} catch (Throwable t) {
				// do nothing
			}
			
			return result;
		}
		return invoker.invoke(invocation);
		
	}
	
	private void logTagging(String tagString){
		return;
	}

}
