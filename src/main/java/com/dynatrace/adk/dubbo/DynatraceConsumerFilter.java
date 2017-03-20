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

@Activate(group = Constants.CONSUMER)
public class DynatraceConsumerFilter implements Filter {

	private static final String DYNATRACE_TAG_KEY = "dtdTraceTagInfo";
	
	public DynatraceConsumerFilter(){
//		System.out.println("dynatrace adk initialized in " + Constants.CONSUMER);
	}

	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		try {
			DynaTraceADKFactory.initialize();
			Tagging tagging = DynaTraceADKFactory.createTagging();
			if(tagging != null ){
				//create a custom tag based on the unique message id
//				byte[] customTagBytes = invocation.getArguments()[0].toString().getBytes();
//				Tagging.CustomTag customTag = tagging.createCustomTag(customTagBytes);
				
				//get already existing tag from current thread
				String tagString = tagging.getTagAsString();
				boolean isAsync = RpcUtils.isAsync(invoker.getUrl(), invocation);
				tagging.linkClientPurePath(isAsync, tagString);
//				tagging.linkClientPurePath(isAsync, customTag);
				invocation.getAttachments().put(DYNATRACE_TAG_KEY, tagString);
				
			}
			
		} catch (Throwable t) {
			// do nothing
		}

		Result result = invoker.invoke(invocation);

		try {
			DynaTraceADKFactory.uninitialize();
		} catch (Throwable t) {
			// do nothing
		}

		return result;
	}
}
