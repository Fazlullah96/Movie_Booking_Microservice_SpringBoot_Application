//package com.example.config;
//
//import feign.RequestInterceptor;
//import feign.RequestTemplate;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//@Configuration
//public class FeignClientRequestInterceptor implements RequestInterceptor {
//    private static final String AUTHORIZATION_HEADER = "Authorization";
//    @Override
//    public void apply(RequestTemplate requestTemplate) {
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//
//        if(attributes != null){
//            HttpServletRequest request = attributes.getRequest();
//            String authHeader = request.getHeader(AUTHORIZATION_HEADER);
//
//            if(authHeader != null){
//                requestTemplate.header(AUTHORIZATION_HEADER, authHeader);
//            }
//        }
//    }
//}
