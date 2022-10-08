/**
 * 
 */
package com.github.damiansheldon.aop;

import java.util.Map;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.context.request.WebRequest;

/**
 * @author meiliang
 *
 * date 2021/12/29
 */
@Aspect
public class StackTraceLogAfterReturningAspect extends AbstractStackTraceLogAspect {

	/**
	 * @param errorAttributes
	 */
	public StackTraceLogAfterReturningAspect(ErrorAttributes errorAttributes) {
		super(errorAttributes);
	}

	@AfterReturning(pointcut = "execution(public java.util.Map org.springframework.boot.web.servlet.error.ErrorAttributes.getErrorAttributes(org.springframework.web.context.request.WebRequest, org.springframework.boot.web.error.ErrorAttributeOptions)) && args(webRequest, options)",
			returning = "retVal")
	public void afterGetErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options, Map<String, Object> retVal) throws Throwable {
		stackTraceLogIfNecessary(webRequest, retVal);
	}
}
