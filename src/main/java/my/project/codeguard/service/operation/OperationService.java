package my.project.codeguard.service.operation;

import my.project.codeguard.dto.OperationDTO;
import my.project.codeguard.entity.Operation;
import my.project.codeguard.entity.User;
import my.project.codeguard.exception.EntityNotFoundException;
import my.project.codeguard.repository.OperationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OperationService {
    private final ModelMapper modelMapper;
    private final OperationRepository operationRepository;

    public OperationService(ModelMapper modelMapper, OperationRepository operationRepository) {
        this.modelMapper = modelMapper;
        this.operationRepository = operationRepository;
    }

    @Transactional
    public Operation findOperation(Long userId, String operationId) {
        return operationRepository.findByUserIdAndOperationId(userId, operationId).orElseThrow(() -> new EntityNotFoundException("operation", operationId));
    }

    @Transactional
    public Operation createOperation(OperationDTO operationDTO, User user) {
        if(operationRepository.findByUserIdAndOperationId(user.getId(), operationDTO.getOperationId()).isPresent()) {
            throw new IllegalArgumentException("OperationId already exists");
        }
        Operation operation = modelMapper.map(operationDTO, Operation.class);
        operation.setUser(user);
        return operationRepository.save(operation);
    }


}
