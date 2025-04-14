package my.project.codeguard.service.delivery;


public interface DeliveryChannel {

    void send(String code, String destination, String template);
}
