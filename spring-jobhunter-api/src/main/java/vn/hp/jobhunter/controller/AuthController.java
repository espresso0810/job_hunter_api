package vn.hp.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hp.jobhunter.domain.User;
import vn.hp.jobhunter.domain.dto.LoginDTO;
import vn.hp.jobhunter.domain.dto.ResLoginDTO;
import vn.hp.jobhunter.service.UserService;
import vn.hp.jobhunter.util.SecurityUtil;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        //Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        //xác thực người dùng => cần viết hàm loadUserByUsername

        // cơ chế của authentication: không lưu mật khẩu nếu login thành công
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // Tạo token
        String accessToken = this.securityUtil.createToken(authentication);

        // Nạp thông tin vào securityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ResLoginDTO responseLoginDTO = new ResLoginDTO();

        // Lấy thông tin người dùng trong db thay vì thay đổi spring security
        User currentUserDB = this.userService.getUserByUsername(loginDTO.getUsername());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUserDB.getId(), currentUserDB.getEmail(), currentUserDB.getName());
            responseLoginDTO.setUser(userLogin);
        }
        
        responseLoginDTO.setAccessToken(accessToken);
        return ResponseEntity.ok(responseLoginDTO);
    }
}
