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
	
	private static final String DYNATRACE_TAG_KEY = "dtdTraceTagInfo";
	
	static {
		DynaTraceADKFactory.initialize();
	}
	
	public DynatraceProviderFilter(){}

	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		String tagString = invocation.getAttachments().get(DYNATRACE_TAG_KEY);
		Tagging tagging = null;
		if(tagString != null){
			try {
				tagging = DynaTraceADKFactory.createTagging();
				tagging.setTagFromString(tagString);
				tagging.startServerPurePath();
			} catch (Throwable t) {}
		}
		try {
			return invoker.invoke(invocation);
		} finally {
			try {
				if (tagString != null) {
					if (tagging != null ) {
						tagging.endServerPurePath();
					}
				}
			} catch (Throwable t) {}
		}
		
		
	}

}
