/**
 * 
 */
package com.github.damiansheldon.aop;

import java.util.Map;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.context.request.WebRequest;

/**
 * @author meiliang
 *
 * date 2022/04/18
 */
@Aspect
public class BeforeV230StackTraceLogAfterReturningAspect extends AbstractStackTraceLogAspect {

	/**
	 * @param errorAttributes
	 */
	public BeforeV230StackTraceLogAfterReturningAspect(ErrorAttributes errorAttributes) {
		super(errorAttributes);
	}
	
	@AfterReturning(pointcut = "execution(public java.util.Map org.springframework.boot.web.servlet.error.ErrorAttributes.getErrorAttributes(org.springframework.web.context.request.WebRequest, boolean, boolean, boolean)) && args(webRequest, includeStackTrace, includeMessage, includeBindingErrors)",
			returning = "retVal")
	public void afterGetErrorAttributes(WebRequest webRequest, 
			boolean includeStackTrace, 
			boolean includeMessage, 
			boolean includeBindingErrors, 
			Map<String, Object> retVal) throws Throwable {
		stackTraceLogIfNecessary(webRequest, retVal);
	}

}
