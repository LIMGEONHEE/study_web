package com.itwill.spring2.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itwill.spring2.dto.PostCreateDto;
import com.itwill.spring2.dto.PostListDto;
import com.itwill.spring2.dto.PostSearchDto;
import com.itwill.spring2.dto.PostUpdateDto;
import com.itwill.spring2.repository.Post;
import com.itwill.spring2.repository.PostDao;
import com.itwill.spring2.service.CommentService;
import com.itwill.spring2.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor // final 필드들을 초기화하는 생성자.
@Controller
@RequestMapping("/post")
//-> PostController 클래스의 모든 컨트롤러 메서드의 매핑 주소는 "/post"로 시작.
public class PostController {
    
    private final PostService postService; // 생성자에 의한 의존성 주입
    private final CommentService commentService; 

    @GetMapping("/list")
    public void list(Model model) {
        log.debug("list()");
        
        // 서비스 컴포넌트의 메서드를 호출, 포스트 목록을 읽어옴 -> 뷰에 전달.
        List<PostListDto> list = postService.read();
        model.addAttribute("posts", list);
        
        // 뷰: /WEB-INF/views/post/list.jsp
    }
    
    @GetMapping({"/details", "/modify"})
    //-> Get 방식의 "/post/details", "/post/modify" 2개의 요청을 처리하는 메서드.
    public void detail(@RequestParam(name="id") Integer id, Model model) {
    	log.debug("detail(id={})",id);
    	
    	// 서비스 컴포넌트의 메서드를 호출해서 해당 id의 포스트를 검색(select).
    	Post post = postService.read(id);
    	
    	// 뷰에 데이터를 전달하기 위해서 model 객체에 post를 속성으로 추가.
    	model.addAttribute("post", post);
    	
    	// 리턴 타입이 void이므로 뷰의 이름은
        // (1) 요청 주소가 /post/details인 경우 /WEB-INF/views/post/details.jsp
        // (2) 요청 주소가 /post/modify인 경우 /WEB-INF/views/post/modify.jsp
    }
    
    @GetMapping("/create") // class에 /post가 되어 있기 때문에 여기서는 /create만 입력하면 된다.
    public void create() {
    	log.debug("create() GET");
    }
    
//    @PostMapping("/create")
//    public String create(@RequestParam(name = "title") String title, 
//    		@RequestParam(name = "content") String content,
//    		@RequestParam(name = "author") String author) {
//    	log.debug("POST: create(title={}, content={}, author={}", title, content, author);
//    	
//    	return "";
//    }
    
    @PostMapping("/create")
    public String create(PostCreateDto dto) {
    	log.debug("POST: create(dto={})", dto);
    	
    	// 서비스 컴포넌트의 메서드를 호출해 데이터베이스에 새 글을 저장.
    	postService.create(dto);
    	
    	return "redirect:/post/list"; // 포스트 목록 페이지로 리다이렉트
    }
    
//    @GetMapping("/modify")
//    public void modify(@RequestParam(name="id") Integer id, Model model) {
//    	log.debug("modify()");
//    	
//    	Post post = postService.read(id); // 읽어오고	
//    	model.addAttribute("post", post); // 뷰에서 보여주기
//    }
    
     @GetMapping("/delete")
    public String delete(@RequestParam(name = "id") int id) {
        log.debug("delete(id={})", id);
        
        // 포스트를 지우기 전에 포스트에 있는 댓글들을 먼저 삭제.
        commentService.deleteByPostId(id);
        
        // 서비스 컴포넌트의 메서드를 호출해서 데이터베이스의 테이블에서 해당 아이디의 글을 삭제.
        postService.delete(id);
        
        // 포스트 목록 페이지로 리다이렉트.
        return "redirect:/post/list";
    }
     
     @PostMapping("/update")
     public String update(PostUpdateDto dto) {
    	 log.debug("POST: update(dto={}", dto);
    	 
    	 // 서비스 컴포넌트의 메서드를 호출해서 데이터베이스 테이블 업데이트를 수행.
    	 postService.update(dto);
    	 
    	 // 상세보기 페이지로 리다이렉트
    	 return "redirect:/post/details?id=" + dto.getId();
     }
     
     @GetMapping("/search")
     public String search(PostSearchDto dto, Model model) {
    	 
    	 List<PostListDto> list = postService.search(dto);
    	 model.addAttribute("posts", list);
    	 
    	 return "post/list";
     }
     
     
}