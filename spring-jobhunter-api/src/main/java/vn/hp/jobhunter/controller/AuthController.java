package vn.hp.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.hp.jobhunter.domain.User;
import vn.hp.jobhunter.domain.dto.LoginDTO;
import vn.hp.jobhunter.domain.dto.ResLoginDTO;
import vn.hp.jobhunter.service.UserService;
import vn.hp.jobhunter.util.SecurityUtil;
import vn.hp.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${hp.jwt.refreshtoken-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        //Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        //xác thực người dùng => cần viết hàm loadUserByUsername

        // cơ chế của authentication: không lưu mật khẩu nếu login thành công
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Nạp thông tin vào securityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResLoginDTO responseLoginDTO = new ResLoginDTO();

        // Lấy thông tin người dùng trong db thay vì thay đổi spring security
        User currentUserDB = this.userService.getUserByUsername(loginDTO.getUsername());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserDB.getId(), currentUserDB.getEmail(), currentUserDB.getName());
            responseLoginDTO.setUser(userLogin);
        }

        // Tạo token
        String accessToken = this.securityUtil.createAccessToken(authentication, responseLoginDTO.getUser());
        responseLoginDTO.setAccessToken(accessToken);
        // Tạo RF token
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), responseLoginDTO);
        // Update RF token vào db
        this.userService.updateUserToken(refresh_token, loginDTO.getUsername());
        // Set cookie
        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(responseLoginDTO);
    }

    @GetMapping("auth/account")
    @ApiMessage("Get user")
    public ResponseEntity<ResLoginDTO.UserLogin> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        // Lấy thông tin người dùng trong db
        User currentUserDB = this.userService.getUserByUsername(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        if (currentUserDB != null) {
           userLogin.setId(currentUserDB.getId());
           userLogin.setEmail(currentUserDB.getEmail());
           userLogin.setName(currentUserDB.getName());
        }
        return ResponseEntity.ok(userLogin);
    }
}
