/**
 * 
 */
package com.github.damiansheldon.aop;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.context.request.WebRequest;

/**
 * @author meiliang
 *
 * @date 2022/04/18
 */
abstract public class AbstractStackTraceLogAspect {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private final ErrorAttributes errorAttributes;
	
	public AbstractStackTraceLogAspect(ErrorAttributes errorAttributes) {
		this.errorAttributes = errorAttributes;
	}

	protected void stackTraceLogIfNecessary(WebRequest webRequest, Map<String, Object> retVal) {
		if (logger.isWarnEnabled()) {
			Map<String, Object> logResult = retVal;

			if (!retVal.containsKey("trace")) {
				Throwable error = this.errorAttributes.getError(webRequest);

				if (error != null) {
					StringWriter stackTrace = new StringWriter();
					error.printStackTrace(new PrintWriter(stackTrace));
					stackTrace.flush();

					logResult = new LinkedHashMap<>(retVal);
					logResult.put("trace", stackTrace.toString());	
				}
				
			}

			logger.warn(logResult.toString());
		}

	}
	
}
