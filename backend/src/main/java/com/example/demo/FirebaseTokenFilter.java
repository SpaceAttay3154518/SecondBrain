package com.example.demo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class FirebaseTokenFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        // Add CORS headers to all responses
        res.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "*");
        res.setHeader("Access-Control-Allow-Credentials", "true");

        // Allow CORS preflight requests to pass through
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String authHeader = req.getHeader("Authorization");
        
        System.out.println("🔍 Incoming request: " + req.getMethod() + " " + req.getRequestURI());
        System.out.println("🔍 Authorization header: " + (authHeader != null ? "Present" : "Missing"));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ No valid Authorization header");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("🔍 Token (first 20 chars): " + token.substring(0, Math.min(20, token.length())) + "...");

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String uid = decodedToken.getUid();
            System.out.println("✅ Token verified! UID: " + uid);
            req.setAttribute("uid", uid);
            chain.doFilter(req, res);
        } catch (Exception e) {
            System.out.println("❌ Token verification failed: " + e.getMessage());
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
