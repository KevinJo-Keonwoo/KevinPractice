package com.my.control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.xml.transform.Result;

import com.my.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.my.exception.AddException;
import com.my.exception.FindException;
import com.my.exception.ModifyException;
import com.my.exception.RemoveException;
import com.my.service.BoardService;

import net.coobird.thumbnailator.Thumbnailator;

@RestController
@RequestMapping("board/*") //http://localhost:8888/backboard/board
public class BoardRestController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private BoardService service;
	
	@Autowired
	private ServletContext sc;
	
	@Value("${spring.servlet.multipart.location}")
	String saveDirectory;
	
	@GetMapping(value = {"list", "list/{optCp}"}) //http://localhost:8888/backboard/board/list/페이지번호
//	@ResponseBody      //{} 내부의 변수 이름과 아래 메서드의 매개변수명은 동일해야만 함 currentPage = currentPage
	public ResultBean<PageBean<Board>> list(@PathVariable Optional<Integer> optCp){
				//int currentPage) { //매개변수를 PathVariable로 사용하겠다
			 //PathVariable이 없는 경우도 생각해야 함 여기서는 required못쓰기때문 
//		currentPage.ifPresent(null) //존재하면~ 존재하지 않으면 ~ 
		ResultBean<PageBean<Board>> rb = new ResultBean<>();
		try {
			int currentPage;
			if(optCp.isPresent()) {  //currentPage에 값이 전달되었는지 확인 
				currentPage = optCp.get(); //값이 존재할 경우 get메서드로 해당 값을 int currentPage에 넣기 
			}else {
				currentPage = 1;
			}
			PageBean<Board> pb = service.boardList(currentPage);
			rb.setStatus(1);
			rb.setT(pb);
		} catch (FindException e) {
			e.printStackTrace();
			rb.setStatus(0);//실패 
			rb.setMsg(e.getMessage());
		}
		return rb;
	}
	@GetMapping(value = {"search/{optWord}", "search/{optWord}/{optCp}", "search"})
//	@ResponseBody
	public ResultBean<PageBean<Board>> search(
			@PathVariable Optional<Integer> optCp,
			@PathVariable Optional<String> optWord){
		ResultBean<PageBean<Board>> rb = new ResultBean<>();
		try {
			PageBean<Board> pb;
			String word;
			if(optWord.isPresent()) {
				word = optWord.get();
			}else {
				word = "";
			}
			logger.error(word);
			int currentPage = 1;
			if(optCp.isPresent()) {
				currentPage = optCp.get();
			}
			if("".equals(word)) {
				pb = service.boardList(currentPage); //전달된 값이 없을 시 전체검색 
			}
			pb = service.searchBoard(word, currentPage);
			rb.setStatus(1);
			rb.setT(pb);
		} catch (FindException e) {
			e.printStackTrace();
			rb.setStatus(0);
			rb.setMsg(e.getMessage());
		}
		return rb;
	}
	//GET /backboard/board/view/글번호
//	@GetMapping("view/{boardNo}")
	
	//GET /backboard/board/글번호1
	@GetMapping("{boardNo}") //이것도 가능 
//	@ResponseBody
	public ResultBean<Board> viewBoard(@PathVariable Long boardNo){
		ResultBean<Board> rb = new ResultBean<>();
		try {
			Board b = service.viewBoard(boardNo);
			rb.setStatus(1);
			rb.setT(b);
		}catch(FindException e) {
			e.printStackTrace();
			rb.setStatus(0);;
			rb.setMsg(e.getMessage());
		}
		return rb;
	}
	//POST /backboard/board/write/글제목/글내용(x) - 파일업로드를 하려면 반드시 폼데이터가 필요. 
	
//	@DeleteMapping(value = {"removeboard"})
	@DeleteMapping(value = "{boardNo}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> remove(@PathVariable Long boardNo){
		
		try {
			service.removeBoard(boardNo);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (RemoveException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	//RequestBody통해서도 할 수있음 
//	@PutMapping("modify/{boardNo}/{boardContent}")
//	public ResponseEntity<?> modify(@PathVariable int boardNo,
//									@PathVariable String boardContent){
//		Board b = new Board();
//		b.setBoardNo(boardNo);
//		b.setBoardContent(boardContent);
//		try {
//			service.modifyBoard(b);
//			return new ResponseEntity<>(HttpStatus.OK);
//		} catch (ModifyException e) {
//			e.printStackTrace();
//			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
	@PutMapping(value = "{boardNo}", produces = MediaType.APPLICATION_JSON_VALUE)
//	@PutMapping(value = "{boardNo}", produces = MediaType.TEXT_HTML_VALUE) //이렇게도 가능 
	//return이 ResultBean타입인경우 produces지정할 필요 없음 
	//ResponseEntity형태일 경우에만 컴파일러가 잘 인지 못하기에 설정해주는 것 -> JSON String으로 컨버팅이 안됨 
	//{"boardContent" : "1번내용수정2"}
	public ResponseEntity<Object> modify(@PathVariable Long boardNo,
									@RequestBody Board b){
		
		try {
			if(b.getBoardContent() == null || b.getBoardContent().equals("")) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
				//back에서 빈 내용이 가지 않게 유효성검사를 해야함
				//front쪽의 유효성검사만 믿지 않기 
			}
			b.setBoardNo(boardNo);
			service.modifyBoard(b);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (ModifyException e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); 
//			return new ResponseEntity<>("<html><body><h1>" + e.getMessage() + "</h1></body></html>", HttpStatus.INTERNAL_SERVER_ERROR);
			//이렇게도 가능 
			
			//jquery에서 ajax 사용 시 headers를 반드시 넣어줘야함 
		}
	}
	//답글쓰기 
	//{"boardTitle" : "0번답글의 글제목", "boardContent" : "1번의 답글"}
	@PostMapping(value = "reply/{boardParentNo}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> reply(@PathVariable Long boardParentNo,
			@RequestBody Board b) {
		if(b.getBoardTitle() == null || b.getBoardTitle().equals("") ||
				b.getBoardContent() == null || b.getBoardContent().equals("")){
			return new ResponseEntity<>("글제목이나 글내용은 반드시 입력하세요", HttpStatus.BAD_REQUEST);   
		}
//		String loginedId = (String)session.getAttribute("loginInfo");
		//---로그인대신할 샘플데이터--
		String loginedId = "id1";
		b.setBoardId(loginedId);
		b.setBoardParentNo(boardParentNo);
		try {
			service.replyBoard(b);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (AddException e) {
			e.printStackTrace();
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}		
	}

	
	
	@PostMapping("write")
//	@PostMapping("/writeboard")
//	@ResponseBody
	public ResponseEntity<?> write( //응답내용은 중요치 않으나 응답이 성공과 실패여부만 알고 싶다 -> ResponseEntity 
			@RequestPart(required = false) List<MultipartFile> letterFiles
			,@RequestPart(required = false) MultipartFile imageFile
			,Board board
			){
//			,String greeting
//			,HttpSession session){
		logger.info("요청전달데이터 title=" + board.getBoardTitle() + ", content=" + board.getBoardContent());
		logger.info("파일갯수 : letterFiles.size()=" + letterFiles.size()); 
		logger.info("파일의 크기 : imageFile.getSize()=" + imageFile.getSize() + ", 업로드된 파일이름 : imageFile.getOriginalFileName()=" + imageFile.getOriginalFilename());
//		logger.info(greeting);
		//게시글내용 DB에 저장
		try {
			// ---로그인 대신할 샘플 데이터 --
			String loginedId = "id1";
			//-----------------------
			board.setBoardTitle(loginedId);
			service.writeBoard(board);
//			return new ResponseEntity<>(HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
//		//파일 경로 생성
//		String saveDirectory = "c:\\files";
//		String saveDirectory = sc.getInitParameter("filePath");
		
		if ( ! new File(saveDirectory).exists()) {
			logger.info("업로드 실제경로생성");
			new File(saveDirectory).mkdirs();
		}
		Long wroteBoardNo = board.getBoardNo();//저장된 글의 글번호
		
		//letterFiles 저장
		int savedletterFileCnt = 0;//서버에 저장된 파일수
		if(letterFiles != null) {
			for(MultipartFile letterFile: letterFiles) {
				long letterFileSize = letterFile.getSize();
				if(letterFileSize > 0) {
					String letterOriginFileName = letterFile.getOriginalFilename(); //자소서 파일원본이름얻기
					//지원서 파일들 저장하기
					logger.info("지원서 파일이름: " + letterOriginFileName +" 파일크기: " + letterFile.getSize());
					//저장할 파일이름을 지정한다 ex) 글번호_letter_XXXX_원본이름
					String letterfileName = wroteBoardNo + "_letter_" + UUID.randomUUID() + "_" + letterOriginFileName;
					File savevdLetterFile = new File(saveDirectory, letterfileName);//파일생성
					try {
						FileCopyUtils.copy(letterFile.getBytes(), savevdLetterFile);
						logger.info("지원서 파일저장:" + savevdLetterFile.getAbsolutePath());
					} catch (IOException e) {
						e.printStackTrace();
						return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
					}
					savedletterFileCnt++;
				}//end if(letterFileSize > 0)
			}
		}//end if(letterFiles != null)
		logger.info("저장된 letter 파일개수: " + savedletterFileCnt);
		
		
		
		File thumbnailFile = null;
		long imageFileSize = imageFile.getSize(); //파일의 크기 
		int imageFileCnt = 0; //서버에 저장된 이미지파일수
		if(imageFileSize > 0) {
			//이미지파일 저장하기
			String imageOrignFileName = imageFile.getOriginalFilename(); //이미지파일원본이름얻기
			logger.info("이미지 파일이름:" + imageOrignFileName +", 파일크기: " + imageFile.getSize());

			//저장할 파일이름을 지정한다 ex) 글번호_image_XXXX_원본이름
			String imageFileName = wroteBoardNo + "_image_" + UUID.randomUUID() + "_" + imageOrignFileName;
			//이미지파일생성
			File savedImageFile = new File(saveDirectory, imageFileName);

			try {
				FileCopyUtils.copy(imageFile.getBytes(), savedImageFile);
				logger.info("이미지 파일저장:" + savedImageFile.getAbsolutePath());

				//파일형식 확인
				String contentType = imageFile.getContentType();
				if(!contentType.contains("image/")) { //이미지파일형식이 아닌 경우
					return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
				//이미지파일인 경우 섬네일파일을 만듦
				String thumbnailName =  "s_"+imageFileName; //섬네일 파일명은 s_글번호_XXXX_원본이름
				thumbnailFile = new File(saveDirectory,thumbnailName);
				FileOutputStream thumbnailOS;
				thumbnailOS = new FileOutputStream(thumbnailFile);
				InputStream imageFileIS = imageFile.getInputStream();
				int width = 100;
				int height = 100;
				Thumbnailator.createThumbnail(imageFileIS, thumbnailOS, width, height);
				logger.info("섬네일파일 저장:" + thumbnailFile.getAbsolutePath() + ", 섬네일파일 크기:" + thumbnailFile.length());

				//이미지 썸네일다운로드하기
				HttpHeaders responseHeaders = new HttpHeaders();
				responseHeaders.set(HttpHeaders.CONTENT_LENGTH, thumbnailFile.length()+"");
				responseHeaders.set(HttpHeaders.CONTENT_TYPE, Files.probeContentType(thumbnailFile.toPath()));
				responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename="+URLEncoder.encode("a", "UTF-8"));
				logger.info("섬네일파일 다운로드");
				return new ResponseEntity<>(FileCopyUtils.copyToByteArray(thumbnailFile), 
						                     responseHeaders, 
						                     HttpStatus.OK);
				//바이트배열로 만들어 응답하겠다. (스트링도가능) 스테이터스/응답헤더/응답내용 
				
			} catch (IOException e2) {
				e2.printStackTrace();
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}//end if(imageFileSize > 0) 
		else {
			logger.error("이미지파일이 없습니다");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "tag")
	public ResultBean<PageBean<TagInfo>> viewTag (@PathVariable Long boardNo){
		PageBean<TagInfo> pb = service.viewTag(boardNo);
		ResultBean<PageBean<TagInfo>> rb = new ResultBean<>();
		rb.setT(pb);
			return rb;
//		ResultBean<Tag> rb = new ResultBean<>();
//		try {
//			List<ResultBean<Tag>> = ;
//			rb.setStatus(1);
//			rb.setT(b);
//			List<ResultBean> rbList = new ArrayList<ResultBean>();
//			rbList.add(rb);
//		} catch (FindException e) {
//			rb.setStatus(0);
//			rb.setMsg(e.getMessage());
//		}


	}


}
