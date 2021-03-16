package com.github.damiansheldon.validation.groups;

import javax.validation.GroupSequence;

/*
 * https://www.baeldung.com/javax-validation-groups
 */
@GroupSequence({
	FirstStep.class, 
	SecondStep.class, 
	ThirdStep.class, 
	FourthStep.class,
	FifthStep.class,
	SixthStep.class,
	SeventhStep.class,
	EighthStep.class,
	NinthStep.class,
	TenthStep.class
	})
public interface Ordinal {

}
