package shopdev.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import shopdev.dto.request.auth.LoginRequest;
import shopdev.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    private PasswordEncoder passwordEncoder;
    @PostMapping("/login")
    public ResponseEntity<?> loginPage(@RequestBody LoginRequest req, HttpServletRequest request) {
        UserDetails user = userService.loadUserByUsername(req.getUsername());

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }


        request.getSession(true).setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            new SecurityContextImpl(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()))
        );

        String sessionId = request.getSession().getId();

        return ResponseEntity.ok(Map.of(
            "sessionId", sessionId,
            "message", "Login successful"
        ));
    }

    @PostMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public String adminPage() {
        return "Welcome Admin!";
    }

    @GetMapping("/home")
    @ResponseBody
    public String home() {
        return "Hello, authenticated user!";
    }
}