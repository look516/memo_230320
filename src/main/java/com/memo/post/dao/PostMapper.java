package com.memo.post.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.memo.post.domain.Post;

//MyBatis
@Repository
public interface PostMapper {
	public List<Map<String, Object>> selectPostList();
	
	public List<Post> selectPostListByUserId(
			@Param("userId") int userId,
			@Param("direction") String direction,
			@Param("standardId") Integer standardId,
			@Param("limit") int limit);
	
	
	
	
	
	public int selectPostIdByUserIdAndSort(
			@Param("userId") int userId,
			@Param("sort") String sort);
	
	
	
	
	
	
	
	
	public int insertPost(
			@Param("userId") int userId,
			@Param("subject") String subject,
			@Param("content") String content,
			@Param("imagePath") String imagePath);
	
	public Post selectPostByPostIdAndUserId(
			@Param("postId") int postId,
			@Param("userId") int userId);
	
	public void updatePostByPostIdAndUserId(
			@Param("postId") int postId,
			@Param("userId") int userId,
			@Param("subject") String subject,
			@Param("content") String content,
			@Param("imagePath") String imagePath);
	
	public void deletePostByPostIdAndUserId(
			@Param("postId") int postId,
			@Param("userId") int userId);

}