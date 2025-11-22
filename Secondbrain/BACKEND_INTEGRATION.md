# Backend Integration Guide

## Frontend Setup (✓ Complete)

The frontend is now configured to send Firebase ID tokens to your Java backend.

### Files Created:
1. **`composables/useBackend.js`** - Composable for making authenticated API calls
2. **`app/pages/test-backend.vue`** - Test page to verify backend connection

### How to Test:
1. Login to your app
2. Navigate to `/test-backend`
3. Click "Get Current Token" to see your Firebase ID token
4. Enter your backend URL (default: http://localhost:8080)
5. Click "Test Connection" to call `/api/test` endpoint

---

## Java Backend Setup (TODO)

### 1. Add Firebase Admin SDK

Add to your `pom.xml`:
```xml
<dependency>
    <groupId>com.google.firebase</groupId>
    <artifactId>firebase-admin</artifactId>
    <version>9.2.0</version>
</dependency>
```

### 2. Initialize Firebase Admin

Create a configuration class:

```java
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import java.io.FileInputStream;

@Configuration
public class FirebaseConfig {
    
    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .setProjectId("second-brain-30911")
                .build();

            FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### 3. Create Token Verification Filter

```java
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class FirebaseAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Skip OPTIONS requests (CORS preflight)
        if ("OPTIONS".equals(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        
        String authHeader = httpRequest.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                String uid = decodedToken.getUid();
                String email = decodedToken.getEmail();
                
                // Set user info as request attributes
                httpRequest.setAttribute("uid", uid);
                httpRequest.setAttribute("email", email);
                httpRequest.setAttribute("firebaseToken", decodedToken);
                
                chain.doFilter(request, response);
            } catch (Exception e) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("{\"error\": \"Invalid or expired token\"}");
                return;
            }
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("{\"error\": \"No authorization token provided\"}");
        }
    }
}
```

### 4. Create Test Endpoint

```java
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // Your Nuxt dev server
public class TestController {
    
    @GetMapping("/test")
    public Map<String, Object> testEndpoint(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        String uid = (String) request.getAttribute("uid");
        String email = (String) request.getAttribute("email");
        
        response.put("success", true);
        response.put("message", "Backend connection successful!");
        response.put("user", Map.of(
            "uid", uid,
            "email", email,
            "timestamp", System.currentTimeMillis()
        ));
        
        return response;
    }
}
```

### 5. Configure CORS

Add to your Spring Boot application:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000"); // Nuxt dev server
        config.addAllowedOrigin("http://localhost:3001"); // Alternative port
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

---

## Testing the Integration

### Step 1: Start Your Java Backend
```bash
mvn spring-boot:run
```
Make sure it's running on `http://localhost:8080`

### Step 2: Start Your Nuxt Frontend
```bash
npm run dev
```

### Step 3: Test the Connection
1. Open browser and login at `http://localhost:3000/login`
2. Navigate to `http://localhost:3000/test-backend`
3. Click "Test Connection"
4. You should see a success response with your user data

---

## Using the Backend API in Your App

Import and use the `useBackend` composable in any component:

```javascript
import { useBackend } from '~/composables/useBackend'

const { apiCall } = useBackend()

// Example: Get user data
const getUserData = async () => {
  try {
    const data = await apiCall('/api/user/profile', {
      method: 'GET'
    })
    console.log(data)
  } catch (err) {
    console.error('Error:', err)
  }
}

// Example: Post data
const saveDocument = async (document) => {
  try {
    const data = await apiCall('/api/documents', {
      method: 'POST',
      body: JSON.stringify(document)
    })
    console.log('Saved:', data)
  } catch (err) {
    console.error('Error:', err)
  }
}
```

---

## Security Notes

1. **Never expose Firebase Admin SDK credentials in frontend code**
2. **Always verify tokens on the backend** - Don't trust client-side authentication
3. **Use HTTPS in production** - Never send tokens over HTTP in production
4. **Tokens expire after 1 hour** - The frontend automatically gets fresh tokens
5. **Implement rate limiting** on your backend to prevent abuse

---

## Troubleshooting

### CORS Errors
- Make sure CORS is configured to allow your frontend origin
- Check that all headers are allowed including Authorization

### 401 Unauthorized
- Token may be expired - the frontend will automatically refresh it
- Check that Firebase Admin SDK is initialized correctly
- Verify your Firebase project ID matches

### Connection Refused
- Make sure your Java backend is running
- Check the port number (default: 8080)
- Verify firewall settings

---

## Next Steps

1. ✓ Test the basic connection using `/test-backend`
2. Implement your actual backend endpoints
3. Add error handling and retry logic
4. Set up production deployment with environment variables
5. Configure production CORS settings
