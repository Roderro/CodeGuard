package my.project.codeguard.controller.users;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import my.project.codeguard.dto.AuthenticationDTO;
import my.project.codeguard.entity.User;
import my.project.codeguard.security.UserDetailsImpl;
import my.project.codeguard.service.user.UserService;
import my.project.codeguard.util.JwtUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "Регистрация и авторизация пользователей")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserService userService, ModelMapper modelMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }


    @Operation(
            summary = "Регистрация пользователя",
            description = "Создает нового пользователя в системе"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован",
                    content = @Content(schema = @Schema(example = "Пользователь сохранен"))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные пользователя",
                    content = @Content(schema = @Schema(example = "{\"username\": \"Username must be between 3 and 50 characters\"}")))
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid AuthenticationDTO AuthenticationDTO) {
        User user = convertToUser(AuthenticationDTO);
        userService.registerUser(user);
        return ResponseEntity.ok("Пользователь сохранен");
    }

    @Operation(
            summary = "Авторизация пользователя",
            description = "Аутентифицирует пользователя и возвращает JWT токен"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешная авторизация",
                    content = @Content(schema = @Schema(example = "{\"jwt-token\": \"string\"}"))),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Incorrect credentials!\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationDTO authenticationDTO) {
        try {
            Authentication authentication = authenticationManager.
                    authenticate(new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(),
                            authenticationDTO.getPassword()));
            var userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String token = jwtUtils.generateJwtToken(userDetails);
            return ResponseEntity.ok().body(Map.of("jwt-token", token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Incorrect credentials!"));
        }
    }

    private User convertToUser(AuthenticationDTO AuthenticationDTO) {
        return modelMapper.map(AuthenticationDTO, User.class);
    }
}


