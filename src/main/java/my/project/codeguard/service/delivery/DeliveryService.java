package my.project.codeguard.service.delivery;

import lombok.extern.slf4j.Slf4j;
import my.project.codeguard.dto.OtpGenerationResponse;
import my.project.codeguard.entity.Operation;
import my.project.codeguard.entity.OtpCode;
import my.project.codeguard.service.user.UserService;
import my.project.codeguard.util.enums.OtpDeliveryStatus;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class DeliveryService {
    private final DeliveryChannelFactory deliveryChannelFactory;
    private final UserService userService;
    private final TaskExecutor taskExecutor;

    public DeliveryService(DeliveryChannelFactory deliveryChannelFactory,
                           UserService userService, TaskExecutor taskExecutor) {
        this.deliveryChannelFactory = deliveryChannelFactory;
        this.userService = userService;
        this.taskExecutor = taskExecutor;
    }

    public OtpGenerationResponse sentOtpCode(OtpCode otpCode, Operation operation) {
        Map<String, String> deliveryChannels = getDeliveryChannels(operation);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        String messageTemplate = userService.findById(operation.getUser().getId()).getMessageTemplate();
        List<String> successfulChannels = Collections.synchronizedList(new ArrayList<>());
        Map<String, String> failedChannels = new ConcurrentHashMap<>();
        // Запускаем асинхронные отправки
        deliveryChannels.forEach((channel, destination) -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                DeliveryChannel deliveryChannel = deliveryChannelFactory.getChannel(channel);
                deliveryChannel.send(otpCode.getCode(), destination, messageTemplate);
                successfulChannels.add(channel);// Успешная отправка
            }, taskExecutor).exceptionally(ex -> {
                log.error("Failed to deliver OTP via channel: {}, error: {}", channel, ex.getMessage());
                failedChannels.put(channel, ex.getMessage());
                return null;
            });
            futures.add(future);
        });

        // Получаем статусы по каждому каналу

        for (int i = 0; i < futures.size(); i++) {
            String channel = new ArrayList<>(deliveryChannels.keySet()).get(i);
            try {
                futures.get(i).get();
            } catch (Exception e) {
                log.error("Failed to get OTP via channel: {},error : {}", channel, e.getMessage());
                failedChannels.put(channel, "Error: " + e.getMessage());
            }
        }

        return OtpGenerationResponse.builder()
                .code(otpCode.getCode())
                .status(determineDeliveryStatus(successfulChannels, deliveryChannels.size()))
                .successfulChannels(successfulChannels)
                .failedChannels(failedChannels)
                .expiresAt(otpCode.getExpiresAt())
                .build();

    }


    private Map<String, String> getDeliveryChannels(Operation operation) {
        return Stream.of(
                        Map.entry("email", getValue(operation.getEmail())),
                        Map.entry("sms", getValue(operation.getPhone())),
                        Map.entry("telegram", getValue(operation.getTgUsername()))
                )
                .filter(entry -> !entry.getValue().isBlank())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private String getValue(Object value) {
        return value == null ? "" : value.toString();
    }

    private OtpDeliveryStatus determineDeliveryStatus(List<String> successfulChannels, int totalChannels) {
        if (successfulChannels.isEmpty()) {
            return OtpDeliveryStatus.FAILED;
        } else if (successfulChannels.size() < totalChannels) {
            return OtpDeliveryStatus.PARTIAL;
        }
        return OtpDeliveryStatus.SENT;
    }
}
