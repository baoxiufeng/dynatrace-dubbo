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
	
	static {
		DynaTraceADKFactory.initialize();
	}
	
	public DynatraceConsumerFilter(){}

	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		try {
			Tagging tagging = DynaTraceADKFactory.createTagging();
			if(tagging != null ){
				String tagString = tagging.getTagAsString();
				boolean isAsync = RpcUtils.isAsync(invoker.getUrl(), invocation);
				tagging.linkClientPurePath(isAsync, tagString);
				invocation.getAttachments().put(DYNATRACE_TAG_KEY, tagString);
			}
			
		} catch (Throwable t) {}
		return invoker.invoke(invocation);
	}
	
	
}
