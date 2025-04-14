package my.project.codeguard.controller.users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import my.project.codeguard.dto.MessageTemplateDTO;
import my.project.codeguard.dto.UserDTO;
import my.project.codeguard.entity.User;
import my.project.codeguard.security.UserDetailsImpl;
import my.project.codeguard.service.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/user")
@Tag(name = "Пользователи", description = "Управление пользователями и их настройками")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;


    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Operation(
            summary = "Получить информацию о текущем пользователе",
            description = "Возвращает данные аутентифицированного пользователя. Требуется авторизация"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Данные пользователя",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "403", description = "Требуется авторизация")
    })
    @GetMapping
    public ResponseEntity<?> userInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(convertToUserDTO(userDetails.getUser()));
    }


    @Operation(
            summary = "Установить шаблон сообщения",
            description = "Обновляет шаблон сообщения для OTP кода. Требуется авторизация"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Шаблон успешно обновлен",
                    content = @Content(schema = @Schema(example = "Шаблон обновлен"))),
            @ApiResponse(responseCode = "400", description = "Некорректный шаблон",
                    content = @Content(schema = @Schema(example = "{\"template\": \"Template must contain '{code}' substring\"}"))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен",
                    content = @Content()
            )
    })
    @PatchMapping("/setMessageTemplate")
    public ResponseEntity<?> setTemplateMessage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @Valid @RequestBody MessageTemplateDTO templateDTO) {
        userService.setMessageTemplate(userDetails.getUser().getId(), templateDTO.getTemplate());
        return ResponseEntity.ok("Шаблон обновлен");
    }

    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список всех пользователей. Требуется роль ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список пользователей",
                    content = @Content(schema = @Schema(implementation = UserDTO[].class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.findAllUsers();
        List<UserDTO> usersDTO = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = convertToUserDTO(user);
            usersDTO.add(userDTO);
        }
        return ResponseEntity.ok(usersDTO);
    }

    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по username. Требуется роль ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь удален",
                    content = @Content(schema = @Schema(example = "User deleted successfully"))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "400", description = "Пользователь не найден")
    })

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok("User deleted successfully");
    }

    private UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
