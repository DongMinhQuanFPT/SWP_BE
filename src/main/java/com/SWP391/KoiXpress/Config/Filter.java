package com.SWP391.KoiXpress.Config;

import com.SWP391.KoiXpress.Entity.Users;
import com.SWP391.KoiXpress.Service.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class Filter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    HandlerExceptionResolver handlerExceptionResolver;

    private final List<String> AUTH_PERMISSION = List.of(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/api/authentication/login",
            "/api/free-access/**",
            "/api/authentication/register",
            "/api/authentication/forgot-password",
            "/api/authentication/login-google"
    );

    public boolean isPublicAPI(String uri){
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return AUTH_PERMISSION.stream().anyMatch(pattern -> pathMatcher.match(pattern,uri));
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //check api do co phai la 1 api public?
        boolean isPublicAPI = isPublicAPI(request.getRequestURI());
        if(isPublicAPI){
            filterChain.doFilter(request, response);
        }else{
            String token = getToken(request);
            if(token == null){
                //chan quyen truy cap
                handlerExceptionResolver.resolveException(request,response,null,new AuthException("Empty token"));
                return;
            }
            //co token
            Users users;
            try{
                users = tokenService.getUserByToken(token);
            }catch(ExpiredJwtException e){
                handlerExceptionResolver.resolveException(request,response,null,new AuthException("Token expired"));
                return;
            }catch(MalformedJwtException malformedJwtException){
                handlerExceptionResolver.resolveException(request,response,null,new AuthException("Invalid token"));
                return;
            }

            //token chuẩn, cho phép truy cập, đồng thời lưu thông tin người dùng
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(users,token, users.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request,response);
        }
    }


    public String getToken(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if(authHeader==null) return null;
        return authHeader.substring(7);
    }



}