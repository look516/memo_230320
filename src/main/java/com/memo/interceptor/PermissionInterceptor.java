package com.memo.interceptor;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class PermissionInterceptor implements HandlerInterceptor {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws IOException {
		
		// 요청 url path를 가져온다.
		// url 과 uri의 차이
		String uri = request.getRequestURI();
		logger.info("[$$$$$$$$$] preHandle uri:{}", uri);
		
		// 로그인 여부 확인 - session 확인
		HttpSession session = request.getSession();
		Integer userId = (Integer)session.getAttribute("userId");

		// 비로그인 && /post로 온 경우 => 로그인 페이지로 이동, 컨트롤러 수행 방지
		if (userId == null && uri.startsWith("/post")) {
			response.sendRedirect("/user/sign_in_view");
			return false; // 컨트롤러 수행 안 함
		}
			
		// 로그인됐는데 && 로그인, 회원가입 창(/user)으로 온 경우 => 글 목록 페이지로 이동, 컨트롤러 수행 방지
		// 이 상태로 작성 시 로그아웃도 /user라 로그아웃 불가능이라 WebMvcConfig에 exclude함
		if (userId != null && uri.startsWith("/user")) {
			response.sendRedirect("/post/post_list_view");
			return false; // 컨트롤러 수행 안 함
		}
		
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView mav) {
		// 공통 처리 시 사용 가능
		
		// view 객체가 존재한다는 건 아직 jsp가 html로 변환되기 전이다.
		logger.info("[############ postHandle]");
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			Exception ex) {
		// jsp가 html로 최종 변환된 후
		logger.info("[@@@@@@@@] afterCompletion");
	}
}
