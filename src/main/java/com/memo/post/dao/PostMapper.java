package com.memo.post.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

//MyBatis
@Repository
public interface PostMapper {
	public List<Map<String, Object>> selectPostList();
}