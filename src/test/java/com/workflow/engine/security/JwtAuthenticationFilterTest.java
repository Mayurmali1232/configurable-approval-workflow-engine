package com.workflow.engine.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationFilterTest {

    private JwtTokenProvider tokenProvider;
    private JwtAuthenticationFilter authenticationFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        tokenProvider = mock(JwtTokenProvider.class);
        authenticationFilter = new JwtAuthenticationFilter(tokenProvider);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_ValidToken_SetsAuthentication() throws ServletException, IOException {
        String mockToken = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + mockToken);
        when(tokenProvider.validateToken(mockToken)).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(mockToken)).thenReturn("mayur");
        when(tokenProvider.getRoleFromToken(mockToken)).thenReturn("ROLE_REQUESTER");

        authenticationFilter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("mayur", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_MissingAuthorizationHeader_SkipFilter() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        authenticationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidToken_DoesNotSetAuthentication() throws ServletException, IOException {
        String mockToken = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + mockToken);
        when(tokenProvider.validateToken(mockToken)).thenReturn(false);

        authenticationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NotBearerToken_SkipFilter() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Basic bWF5dXI6cGFzcw==");

        authenticationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}