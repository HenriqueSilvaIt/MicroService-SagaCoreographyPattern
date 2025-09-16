package br.com.microservices.choreography.productvalidationservice.core.saga;

import br.com.microservices.choreography.productvalidationservice.core.dto.Event;
import br.com.microservices.choreography.productvalidationservice.core.producer.KafkaProducer;
import br.com.microservices.choreography.productvalidationservice.core.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static br.com.microservices.choreography.productvalidationservice.core.enums.ESagaStatus.SUCCESS;
import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaExecutionController {

    private final static String SAGA_LOG_ID = "ORDER ID: %s | TRANSACTION ID %s | EVENT ID %s";

    private final JsonUtil jsonUtil;
    private final KafkaProducer producer;


    @Value("${spring.kafka.topic.payment-success}")
    private String paymentSuccessTopic; /*pega o nome do tópico passado no application.yl*/


    @Value("${spring.kafka.topic.product-validation-fail}")
    private String productValidationFailTopic; /*pega o nome do tópico passado no application.yl*/


    @Value("${spring.kafka.topic.notify-ending}")
    private String notifyEndingTopic; /*pega o nome do tópico passado no application.yl*/

    public void handleSaga (Event event) {
        /*Switch case para fazer  a ação de acordo
        * com o status do evento*/

        switch (event.getStatus()) {

            case SUCCESS -> handleSuccess(event);
            case ROLLBACK_PENDING -> handleRollbackPending(event);
            case FAIL -> handleFail(event) ;
    }


    }

    private void handleSuccess(Event event) {

         log.info("### CURRENT SAGA: {} | SUCCESS | NEXT TOPIC {} | {}",
                event.getSource() /*serviços atual*/, paymentSuccessTopic /*tópico passado*/, createSagaId(event) /*composto porId do pedido, id da transação e o id do evento */  );
                sendEvent(event, paymentSuccessTopic);
    }

    private void handleRollbackPending(Event event) {
        log.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK CURRENT SERVICE | NEXT TOPIC {} | {}",
                event.getSource() /*serviços atual*/, productValidationFailTopic /*tópico passado*/, createSagaId(event) /*composto porId do pedido, id da transação e o id do evento */  );
                sendEvent(event, productValidationFailTopic);

    }

    private void handleFail(Event event) {

        log.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK PREVIOUS SERVICE | NEXT TOPIC {} | {}",
                event.getSource() /*serviços atual*/, notifyEndingTopic /*tópico passado*/, createSagaId(event) /*composto porId do pedido, id da transação e o id do evento */  );
                sendEvent(event, notifyEndingTopic);

    }


    private void sendEvent(Event event, String topic) {

        var json = jsonUtil.toJson(event);
        producer.sendEvent(json, topic);
    }
    private String createSagaId(Event event) {

        return format(SAGA_LOG_ID, event.getPayload().getId(), event.getTransactionId(), event.getId());
        /*Tras o Id do pedido, id da transação e o id do evento*/

    }
}
