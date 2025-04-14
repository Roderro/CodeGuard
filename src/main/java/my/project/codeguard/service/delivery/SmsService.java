package my.project.codeguard.service.delivery;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import my.project.codeguard.config.SmppConfig;
import my.project.codeguard.exception.DeliveryFailedException;
import org.smpp.*;
import org.smpp.pdu.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@Slf4j
public class SmsService implements DeliveryChannel {
    private final SmppConfig.SmppProperties properties;
    private Session session;


    public SmsService(SmppConfig.SmppProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init(){
        try {
            connectAndBind();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @PreDestroy
    public void cleanup() throws WrongSessionStateException, IOException, PDUException, TimeoutException {
        if (session != null && session.isBound()) {
            session.unbind();
            session.close();
        }
    }


    private void connectAndBind() throws Exception {
        Connection connection = new TCPIPConnection(properties.getHost(), properties.getPort());
        connection.setConnectionTimeout(properties.getConnectionTimeout());
        connection.setReceiveTimeout(properties.getReceiveTimeout());
        connection.setCommsTimeout(properties.getCommsTimeout());
        session = new Session(connection);
        BindRequest bindRequest = new BindTransmitter();
        bindRequest.setSystemId(properties.getSystemId());
        bindRequest.setPassword(properties.getPassword());
        bindRequest.setSystemType(properties.getSystemType());
        bindRequest.setInterfaceVersion((byte) 0x34);
        bindRequest.setAddressRange(properties.getSourceAddr());
        BindResponse bindResponse = session.bind(bindRequest);
        if (bindResponse.getCommandStatus() != 0) {
            throw new Exception("Bind failed: " + bindResponse.getCommandStatus());
        }
    }

    @Override
    public void send(String code, String destination, String template) {
        try {
            if (!session.isBound()) {
                connectAndBind();
            }
            SubmitSM submitSM = createSubmitSm(code, destination, template);
            SubmitSMResp response = session.submit(submitSM);

            validateResponse(response, destination);

            log.info("SMS sent successfully to {}, message ID: {}", destination, response.getMessageId());
        } catch (DeliveryFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new DeliveryFailedException("Failed to send SMS to " + destination, e);
        }
    }

    private SubmitSM createSubmitSm(String code, String destination, String template) throws WrongLengthOfStringException {
        String message = template.replace("{code}", code);
        SubmitSM submitSM = new SubmitSM();
        submitSM.setSourceAddr(properties.getSourceAddr());
        submitSM.setDestAddr(destination);
        submitSM.setShortMessage(message);
        return submitSM;
    }

    private void validateResponse(SubmitSMResp response, String destination) {
        if (response.getCommandStatus() != 0) {
            throw new DeliveryFailedException(
                    "SMPP error for " + destination +
                            ": status=" + response.getCommandStatus() +
                            ", messageId=" + response.getMessageId()
            );
        }
        log.info("SMS delivered to {}, messageId: {}", destination, response.getMessageId());
    }
}
