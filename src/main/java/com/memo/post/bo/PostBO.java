package com.memo.post.bo;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memo.common.FileManagerService;
import com.memo.post.dao.PostMapper;
import com.memo.post.domain.Post;

@Service
public class PostBO {
	
	
	//private Logger logger = LoggerFactory.getLogger(PostBO.class);
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	
	private static final int POST_MAX_SIZE = 3;
	
	
	@Autowired
	private PostMapper postMapper; // mybatis
	
	@Autowired
	private FileManagerService fileManager;
	
	// input: userId(글쓴이)
	// output: List<Post>
	public List<Post> getPostListByUserId(int userId, Integer prevId, Integer nextId) {
		// 게시글 번호: 10 9 8 | 7 6 5 | 4 3 2 | 1
		// 만약 4 3 2 페이지에 있을 때
		// 1) 다음: 2보다 작은 3개 DESC
		// 2) 이전: 4보다 큰 3개 ASC(5 6 7) => List reverse(7 6 5)
		// 3) 첫페이지일 때(이전, 다음 없음) DESC 3개
		// 마지막 페이지일 때는??
		// 맨 앞 페이지 맨 뒷 페이지로 이동 시에는 prevId == 0 ??
		// 이전은 계속 누르면 무한으로 돌고 다음은 계속 누르면 에러나고 => 해결 how to?
		
		String direction = null; // 방향
		Integer standardId = null; // 기준 postId
		if (prevId != null) {
			// 이전
			direction = "prev";
			standardId = prevId;
			
			// get list
			List<Post> postList = postMapper.selectPostListByUserId(userId, direction, standardId, POST_MAX_SIZE);
			
			// reverse => 1) 내가 구현 2) 메소드 찾기
			// 5 6 7 => 7 6 5
			Collections.reverse(postList); // 뒤집고 저장까지 해준다.
			
			// return
			return postList; // method 종료
			
		} else if (nextId != null) {
			// 다음
			direction = "next";
			standardId = nextId;
		}
		
		
		return postMapper.selectPostListByUserId(userId, direction, standardId, POST_MAX_SIZE);
	}
	
	// 이전 방향의 끝인지 확인
	// input: prevId, userId
	// output: boolean
	public boolean isPrevLastPage(int prevId, int userId) {
		int postId = postMapper.selectPostIdByUserIdAndSort(userId, "DESC");
		return postId == prevId; // 같으면 끝, 아니면 끝 아님
	}
	
	// 다음 방향의 끝인지 확인
	public boolean isNextLastPage(int nextId, int userId) {
		return nextId == postMapper.selectPostIdByUserIdAndSort(userId, "ASC");
	}
	
	public int addPost(int userId, String userLoginId,
			String subject, String content,
			MultipartFile file) {
		
		String imagePath = null;
		
		// 이미지가 있으면 업로드 후 imagePath 받아옴
		if (file != null) {
			imagePath = fileManager.saveFile(userLoginId, file);
		}
		
		return postMapper.insertPost(userId, subject, content, imagePath);
	}
	
	
	
	public Post getPostByPostIdAndUserId(int postId, int userId) {
		return postMapper.selectPostByPostIdAndUserId(postId, userId);
	}
	
	
	
	
	
	
	
	
	
	
	public void updatePost(int userId, String userLoginId,
			int postId, String subject, String content,
			MultipartFile file) {
		// 업데이트 대상인 기존 글을 가져와본다. (validation, 이미지 교체시 기존 이미지 제거를 위해)
		Post post = postMapper.selectPostByPostIdAndUserId(postId, userId);
		//logger.warn("###[글 수정] post is null. postId:{}, userId:{}", postId, userId); // 임시 test
		if (post == null) {
			// System.out.println(post.getId()); 락을 걸어서 느려지게 함 / 다른 스레드를 느리게 만든다. 절대 사용 금지
			// 대신 적절히 warn debug info 등 수준을 정해서 logger 이용해서 로그를 찍음
			logger.warn("###[글 수정] post is null. postId:{}, userId:{}", postId, userId);
			return;
		}
		
		// 파일이 비어있지 않다면 업로드 후 imagePath 얻어옴
		// 업로드가 성공하면 기존 이미지 제거
		String  imagePath = null;
		if (file != null) {
			// 업로드
			imagePath = fileManager.saveFile(userLoginId, file);
			
			// 기존 이미지 제거
			// 업로드가 성공했고, 기존 이미지 존재하는 경우
			if (imagePath != null && post.getImagePath() != null) {
				// 이미지 제거
				fileManager.deleteFile(post.getImagePath());
			}
		}
		
		// 글 업데이트
		postMapper.updatePostByPostIdAndUserId(postId, userId, subject, content, imagePath);
	}
	
	
	public void deletePostByPostIdAndUserId(int postId, int userId) {
		// 기존 글을 가져온다. (이미지가 있으면 삭제해야 하기 때문)
		Post post = postMapper.selectPostByPostIdAndUserId(postId, userId);
		if (post == null) {
			logger.error("###[글 삭제] post is null. postId:{}, userId:{}", postId, userId);
			return;
		}
		
		// 기존 이미지가 있으면 삭제
		if (post.getImagePath() != null) {
			fileManager.deleteFile(post.getImagePath());
		}
		
		postMapper.deletePostByPostIdAndUserId(postId, userId);
	}
}