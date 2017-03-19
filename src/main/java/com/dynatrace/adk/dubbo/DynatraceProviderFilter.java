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
	
	private static final String DYNATRACE_TAG_KEY = "dtdTraceTagInfo";
	
	public DynatraceProviderFilter(){
		System.out.println("dynatrace adk initialized in " + Constants.PROVIDER);
	}

	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		
		String tagString = invocation.getAttachments().get(DYNATRACE_TAG_KEY);
		Tagging tagging = null;
		if(tagString != null){
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
		}
		
		try {
			return invoker.invoke(invocation);
		} finally {
			try {
				if (tagString != null) {
					if (tagging != null && !(tagging instanceof DummyTaggingImpl)) {
						tagging.endServerPurePath();
					}
				}
			} catch (Throwable t) {
				// do nothing
			}
		}
		
		
	}
	
	private void logTagging(String tagString){
		return;
	}

}
