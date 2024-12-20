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
import lombok.NonNull;
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
            "/api/authentication/login-google",
            "/websocket/**",
            "/api/notification"
    );


    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
        boolean isPublicAPI = isPublicAPI(request.getRequestURI());
        if(isPublicAPI){
            filterChain.doFilter(request, response);
        }else{
            String token = getToken(request);
            if(token == null){
                handlerExceptionResolver.resolveException(request,response,null,new AuthException("Empty token"));
                return;
            }
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

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(users,token, users.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request,response);
        }
    }

    //Check api is a public api?
    private boolean isPublicAPI(String uri){
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return AUTH_PERMISSION.stream().anyMatch(pattern -> pathMatcher.match(pattern,uri));
    }


    private String getToken(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if(authHeader==null) return null;
        return authHeader.substring(7);
    }



}