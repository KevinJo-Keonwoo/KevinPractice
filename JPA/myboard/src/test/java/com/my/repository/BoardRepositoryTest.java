package com.my.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.my.dto.Tag;
import com.my.dto.TagInfo;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import com.my.dto.Board;

@SpringBootTest
class BoardRepositoryTest {
	@Autowired
	BoardRepository repository;
	
	Logger logger = LoggerFactory.getLogger(getClass());
	@Test
	void testFindByIdValid() {
		Long boardNo = 1L;
		Optional<Board> optB1 = repository.findById(boardNo);
		assertTrue(optB1.isPresent());
	}
	@Test
	void testFindByIdInValid() {
		Long boardNo = 1L;
		Optional<Board> optB1 = repository.findById(boardNo);
		assertFalse(optB1.isPresent());
	}
	
	@Test
	//@Transactional //save를 커밋하지말고 롤백하려고 설정 
	void testWrite() {
		Board b = new Board();
		b.setBoardTitle("title_t2");
		b.setBoardContent("content_t2");
		b.setBoardId("id2");
//		b.setBoardParentNo(0);
		repository.save(b); 
		
		//정적 SQL구문에서는 위와같이 save하면 Dt값이 null이 가는데
		//동적으로 설정하고 sql에 디폴트를 선언해놓으면 해결됨 
	}
	@Test
	void testReply() {
		Board b = new Board();
		b.setBoardParentNo(1L);
		b.setBoardTitle("1_re_title"); //1번글의 답글 
		b.setBoardContent("1_re_content");
		b.setBoardId("id2");
		repository.save(b); 
	}
	@Test
	void testModify() {
//		Board board = new Board();
//		board.setBoardNo(3L);
//		board.setBoardContent("글3내용수정");
//		repository.save(board); 
		
		Optional<Board> optB = repository.findById(4L);
		optB.ifPresent((b) -> {  //get메서드 호출과 같은 효과 
			b.setBoardContent("글4내용수정");
			repository.save(b); 
		});
	}
	@Test
	void testUpdateViewCount() {
		Long boardNo = 1L;
		Optional<Board>optB = repository.findById(1L);
		optB.ifPresent((b) -> {
			int oldViewCount = b.getBoardViewcount();
			int newViewCount = oldViewCount+1;
			b.setBoardViewcount(newViewCount);
			repository.save(b);
			
			int expectedNewViewCount = newViewCount;
			assertEquals(expectedNewViewCount, repository.findById(boardNo).get().getBoardViewcount());
		});
	}
	@Test
	void testDelete() {
		Long boardNo = 1L;
		repository.deleteById(boardNo);
		repository.deleteReply(boardNo);
		
		assertFalse(repository.findById(boardNo).isPresent());
	}
	@Test
	void testFindAllPage() {
		int currentPage = 2;
		Pageable pageable = PageRequest.of(currentPage-1, 4);
		List<Board>	list = repository.findAll(pageable);  //플젝에선 findAll말고  네이티브쿼리로써야함 
		list.forEach((b) -> {
			logger.error(b.toString());
		});
	}
	@Test
	void testFindByPage() {
		int currentPage = 1;
		int cntPerPage = 3;
		int endRow = currentPage * cntPerPage;
		int startRow = endRow - cntPerPage + 1;
		List<Board> list = repository.findByPage(startRow, endRow);
		list.forEach((b)->{
			logger.error(b.toString());
		});
	}
	@Test
	void testFindByWord() {
		String word = "2";
		int currentPage = 1;
		int cntPerPage = 3;
		List<Board> list = repository.findByWord(word, currentPage, cntPerPage);
		list.forEach((b)->{
			logger.error(b.toString());
		});
	}
//	@Test
//	void testFindTagById(){
//		Long boardNo = 1L;
////		Optional<Tag> optT = repository.findTagById(boardNo);
//		List<TagInfo> list = repository.findTagById(boardNo);
//		list.forEach((tagInfo)->{
//			logger.error(tagInfo.toString());
//		});
//		Long expectedTagNo= 1L;
//		Long tagNo = list.get(0).getTagNo();
//
//		assertEquals(expectedTagNo,tagNo);
////		assertTrue(list.contains("Java"));
//	}


	@Test void testA(){
		List<TagInfo> list = repository.a(1L);
		TagInfo ti = list.get(0);
		logger.error(ti.getTagName());
//		for(Object obj: arr) {
//			logger.error(obj.getClass().getName() + ":" + obj.toString());
//		}
		logger.error(ti.getTagNo() + ":" + ti.getTagName());
	}
}
