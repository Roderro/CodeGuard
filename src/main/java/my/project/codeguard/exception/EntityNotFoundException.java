package my.project.codeguard.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String nameEntity, String idEntity) {
        super(nameEntity + " with id: " + idEntity + " not found");
    }
}
