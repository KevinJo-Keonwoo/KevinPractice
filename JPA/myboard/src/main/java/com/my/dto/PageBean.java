package com.my.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter
@ToString
public class PageBean<T> {
	private List<T> list;

	private int currentPage;
	private int totalPage;
	private int startPage;
	private int endPage;
	private int cntPerPageGroup = 2;

	/**
	 * 
	 * @param list 페이지의 목록
	 * @param totalCnt 총 건수
	 * @param currentPage 검색할페이지
	 * @param cntPerPageGroup //페이지그룹별 보여줄 페이지수
	 * @param cntPerPage //한페이지당 보여줄 목록수
	 */
	public PageBean(List<T>list, long totalCnt, int currentPage, int cntPerPageGroup, int cntPerPage) {
		this.list = list;
		this.currentPage = currentPage;
		this.cntPerPageGroup = cntPerPageGroup;
		
		this.totalPage =  (int)Math.ceil((double)totalCnt/cntPerPage);//총페이지수 4, 5
		
		//currentPage  startPage  totalPage endPage
		// 1             1            5       2
		// 2             1                    2
		// 3             3                    4
		// 4             3                    4
		// 5             5                    5
		this.endPage =
				   (int)(Math.ceil((double)currentPage/cntPerPageGroup)*cntPerPageGroup);
		this.startPage = this.endPage - cntPerPageGroup + 1;
		if(this.totalPage <this.endPage) {
			this.endPage = this.totalPage;
		}
	}

	public PageBean(List<T> list) {
		this.list = list;
	}
}
