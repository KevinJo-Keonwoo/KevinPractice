package com.my.service;

import java.util.List;
import java.util.Optional;

import com.my.dto.Tag;
import com.my.dto.TagInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.exception.ModifyException;
import com.my.exception.RemoveException;
import com.my.repository.BoardRepository;

import com.my.dto.Board;
import com.my.dto.PageBean;
@Service
public class BoardService {
	private static final int CNT_PER_PAGE = 3; //페이지별 보여줄 목록수 
	@Autowired
	private BoardRepository repository;
	/**
	 * 페이지별 게시글 목록을 페이지그룹정보를 반환한다
	 * @param currentPage 검색할 페이지
	 * @return
	 * @throws FindException
	 */
	public PageBean<Board> boardList(int currentPage) throws FindException{
		
//		List<Board>list = repository.selectByPage(currentPage, CNT_PER_PAGE);
//		int totalCnt = repository.selectCount(); //총 행수 12,13
//		int totalPage = (int)Math.ceil((double)totalCnt/CNT_PER_PAGE);//총페이지수 4, 5 
//		int cntPerPageGroup = 2; //페이지별 보여줄 페이지 수 
//		//자바에서 정수/정수는 정수값만 나옴 따라서 한 값을 실수로 바꿔줘야함
//
////		PageBean<Board> pb1 = new PageBean<>(list, totalCnt);
//		PageBean<Board> pb = new PageBean<>(list, totalCnt, currentPage, cntPerPageGroup, CNT_PER_PAGE);
//		return pb;
		
//		Pageable pageable = PageRequest.of(currentPage, CNT_PER_PAGE);
//		repository.findAll(pageable);
//		return null;
		
		int endRow = currentPage * CNT_PER_PAGE;
		int startRow = endRow - CNT_PER_PAGE + 1;
		List<Board> list =repository.findByPage(startRow, endRow);
		long totalCnt = repository.count();
		int cntPerPageGroup = 2;
		PageBean<Board> pb = new PageBean<>(list, totalCnt, currentPage, cntPerPageGroup, CNT_PER_PAGE);
		return pb;
	}
	/**
	 * 검색어를 이용한 게시글 검색 목록과 페이지 그룹정보를 반환한다
	 * @param word 검색어
	 * @param currentPage 검색할 페이지
	 * @return
	 * @throws FindException
	 */
	public PageBean<Board> searchBoard(String word, int currentPage) throws FindException{
//		List<Board> list = repository.selectByWord(word, currentPage, CNT_PER_PAGE);
//		int totalCnt = repository.selectCount(word);
//		int cntPerPageGroup = 2;
//		PageBean<Board> pb = new PageBean<>(list, totalCnt, currentPage, cntPerPageGroup, CNT_PER_PAGE);
//		return pb;
		List<Board> list = repository.findByWord(word, currentPage, CNT_PER_PAGE);
		long totalCnt = repository.count();
		int cntPerPageGroup = 2;
		PageBean<Board> pb = new PageBean<>(list, totalCnt, currentPage, cntPerPageGroup, CNT_PER_PAGE);
		return pb;
	}
	/**
	 * 게시글번호의 조회수를 1증가한다
	 * 게시글번호의 게시글을 반환한다
	 * @param boardNo 게시글번호
	 * @return
	 * @throws FindException
	 */
	public Board viewBoard(Long boardNo) throws FindException{
			//조회수를 1증가한다
			Optional<Board> optB = repository.findById(boardNo);
			if(optB.isPresent()) {
				Board b = optB.get();
				b.setBoardViewcount(b.getBoardViewcount()+1);
				repository.save(b); //내부에서 persist메서드가 호출되어 update해줌 
			}else {
				throw new FindException("게시글이 없습니다");
			}
			
			//게시글번호의 게시글 조회한다
			Optional<Board> optB1 = repository.findById(boardNo);
			if(optB1.isPresent()) {
				Board b1 = optB1.get();
				return b1;
			}else {
				throw new FindException("게시글이 없습니다");
			}
	}
	/**
	 * 글쓰기
	 * @param board
	 * @throws AddException
	 */
	public void writeBoard(Board board) throws AddException {
		board.setBoardParentNo(0L);
//		repository.insert(board);
		repository.save(board);
	}
	/**
	 * 답글쓰기
	 * @param board
	 * @throws AddException
	 */
	public void replyBoard(Board board) throws AddException{
		if(board.getBoardParentNo() == 0L) {
			throw new AddException("답글쓰기의 부모글번호가 없습니다");
		}		
		repository.save(board);
	}
//		if(board.getBoardParentNo() == 0) {
//			throw new AddException("답글쓰기의 부모글번호가 없습니다");
//		}
//		repository.insert(board);
//	}
	public void modifyBoard(Board board) throws ModifyException {
//		repository.update(board);
		Optional<Board> optB = repository.findById(board.getBoardNo());
		if(!optB.isPresent()) {
			throw new ModifyException("글이 없습니다");
		}else {
			Board b = optB.get(); //기존 내용가져와서 그대로 사용하고 
			b.setBoardContent(board.getBoardContent());
			repository.save(b);
		}
//		optB.ifPresent((b) -> {   //원래 들어있던 보드객체 얻어올수 잇음 
//			
//		});
	}
	public void removeBoard(Long boardNo) throws RemoveException {
		Optional<Board> optB = repository.findById(boardNo);
		if(!optB.isPresent()) {
			throw new RemoveException("삭제할 글이 없습니다");
		}else {
			repository.deleteReply(boardNo);
			repository.deleteById(boardNo);
		}
	}
	public PageBean<TagInfo> viewTag(Long boardNo){
		List<TagInfo> list = repository.findTagById(boardNo);
		PageBean<TagInfo> pb = new PageBean<>(list);
		return pb;
	}
}
