package com.memo.post.bo;

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
	
	
	@Autowired
	private PostMapper postMapper; // mybatis
	
	@Autowired
	private FileManagerService fileManager;
	
	// input: userId(글쓴이)
	// output: List<Post>
	public List<Post> getPostListByUserId(int userId) {
		return postMapper.selectPostListByUserId(userId);
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
		logger.warn("###[글 수정] post is null. postId:{}, userId:{}", postId, userId); // 임시
		if (post == null) {
			// System.out.println(post.getId()); 락을 걸어서 느려지게 함 / 다른 스레드를 느리게 만든다. 절대 사용 금지
			// 대신 적절히 warn debug info 등 수준을 정해서 logger 이용해서 로그를 찍음
			logger.warn("###[글 수정] post is null. postId:{}, userId:{}", postId, userId);
			
		}
		
		// 파일이 비어있지 않다면 업로드 후 imagePath 얻어옴
		// 업로드가 성공하면 기존 이미지 제거
		
		// 글 업데이트
	}
}
