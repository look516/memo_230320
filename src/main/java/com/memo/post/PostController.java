package com.memo.post;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.memo.post.bo.PostBO;
import com.memo.post.domain.Post;

@RequestMapping("/post")
@Controller
public class PostController {
	
	@Autowired
	private PostBO postBO;
	
	@GetMapping("/post_list_view")
	public String postListView(
			@RequestParam(value = "prevId", required = false) Integer prevIdParam,
			@RequestParam(value = "nextId", required = false) Integer nextIdParam,
			HttpSession session, Model model) {
		
		// 로그인 여부 조회
		Integer userId = (Integer)session.getAttribute("userId");
		if (userId == null) {
			// 비로그인이면 로그인 페이지로 이동
			return "redirect:/user/sign_in_view";
		}
		
		// DB 글 목록 조회
		List<Post> postList = postBO.getPostListByUserId(userId, prevIdParam, nextIdParam);
		
		int nextId = 0;
		int prevId = 0; // 원래는 직접 로직을 사용하지 않고 페이징 bo를 통해 리턴되는 페이징 객체를 담는 게 좋다.
		
		if (postList.isEmpty() == false) {
			// postList가 비어있을 때 오류 방지 []
			nextId = postList.get(postList.size() - 1).getId(); // 가져온 리스트의 가장 끝값(작은 id)
			prevId = postList.get(0).getId();
			
			// 이전 방향의 끝인가? select해서 가져온 가장 최신 글 id가 페이지에 포함되어있다면 첫 페이지다.
			// prevId와 post 테이블의 가장 큰 id값과 같다면 이전 페이지 없음
			if (postBO.isPrevLastPage(prevId, userId)) {
				prevId = 0;
			}
			// 다음 방향의 끝인가? select해서 가져온 가장 예전 글 id가 페이지에 포함되어있다면 끝 페이지다.
			// nextId와 post 테이블의 가장 작은 id값과 같다면 다음 페이지 없음
			if (postBO.isNextLastPage(nextId, userId)) {
				nextId = 0;
			}
		}
		
		model.addAttribute("prevId", prevId);
		model.addAttribute("nextId", nextId);
		
		model.addAttribute("postList", postList);
		
		model.addAttribute("view", "post/postList");
		return "template/layout";
	}
	
	
	/**
	 * 글쓰기 화면
	 * @param model
	 * @return
	 */
	// 로그인 여부 추후 일괄 처리 예정
	@GetMapping("/post_create_view")
	public String postCreateView(Model model) {
		model.addAttribute("view", "post/postCreate");
		return "template/layout";
	}
	
	@GetMapping("/post_detail_view")
	public String postDetailView(
			@RequestParam("postId") int postId,
			HttpSession session,
			Model model) {
		
		int userId = (int)session.getAttribute("userId"); // 일부러 로그아웃 상태에서 오류냄
		
		// post select by postId, userId
		Post post = postBO.getPostByPostIdAndUserId(postId, userId);
		
		model.addAttribute("post", post);
		
		model.addAttribute("view", "post/postDetail");
		return "template/layout";
	}
}
