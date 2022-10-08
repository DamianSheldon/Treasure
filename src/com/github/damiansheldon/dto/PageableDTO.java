/**
 * 
 */
package com.github.damiansheldon.dto;

import java.util.List;

/**
 * @author meiliang
 *
 * date 2022/03/21
 */
public class PageableDTO<T> {

	private int totalPages;
	
	private long totalElements;
	
	private int number;

	private int size;

	private int numberOfElements;
	
	private List<T> content;
	
	public PageableDTO(int totalPages, long totalElements, int number, int size, int numberOfElements,
			List<T> content) {
		super();
		this.totalPages = totalPages;
		this.totalElements = totalElements;
		this.number = number;
		this.size = size;
		this.numberOfElements = numberOfElements;
		this.content = content;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getNumberOfElements() {
		return numberOfElements;
	}

	public void setNumberOfElements(int numberOfElements) {
		this.numberOfElements = numberOfElements;
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}
	
}
