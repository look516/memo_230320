package com.memo.post.bo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memo.post.dao.PostMapper;
import com.memo.post.domain.Post;

@Service
public class PostBO {
	
	@Autowired
	private PostMapper postMapper; // mybatis
	
	// input: userId(글쓴이)
	// output: List<Post>
	public List<Post> getPostListByUserId(int userId) {
		return postMapper.selectPostListByUserId(userId);
	}
	
	public int addPost(int userId, String subject, String content,
			MultipartFile file) {
		
		// 이미지가 있으면 업로드 후 imagePath 받아옴
		
		String imagePath = null;
		
		return postMapper.
	}
}
