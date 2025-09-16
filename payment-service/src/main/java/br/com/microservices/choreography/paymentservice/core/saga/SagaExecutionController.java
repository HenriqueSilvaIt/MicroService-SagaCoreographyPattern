package br.com.microservices.choreography.paymentservice.core.saga;

import br.com.microservices.choreography.paymentservice.core.dto.Event;
import br.com.microservices.choreography.paymentservice.core.producer.KafkaProducer;
import br.com.microservices.choreography.paymentservice.core.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class SagaExecutionController {

    private final static String SAGA_LOG_ID = "ORDER ID: %s | TRANSACTION ID %s | EVENT ID %s";

    private final JsonUtil jsonUtil;
    private final KafkaProducer producer;


    //Caso de Sucesso
    @Value("${spring.kafka.topic.inventory-success}")
    private String inventorySuccessTopic; /*pega o nome do tópico passado no application.yl*/

    //Caso de rollback
    @Value("${spring.kafka.topic.payment-fail}")
    private String paymentFailTopic; /*pega o nome do tópico passado no application.yl*/

    //Caso de falha ou seja já fez o rollback tem que devolver para o serviço anterior
    @Value("${spring.kafka.topic.product-validation-fail}")
    private String productValidationFailTopic; /*pega o nome do tópico passado no application.yl*/

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
                event.getSource() /*serviços atual*/, inventorySuccessTopic /*tópico passado*/, createSagaId(event) /*composto porId do pedido, id da transação e o id do evento */  );
                sendEvent(event, inventorySuccessTopic);
    }

    private void handleRollbackPending(Event event) {
        log.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK CURRENT SERVICE | NEXT TOPIC {} | {}",
                event.getSource() /*serviços atual*/, productValidationFailTopic /*tópico passado*/, createSagaId(event) /*composto porId do pedido, id da transação e o id do evento */  );
                sendEvent(event, productValidationFailTopic);

    }

    private void handleFail(Event event) {

        log.info("### CURRENT SAGA: {} | SENDING TO ROLLBACK PREVIOUS SERVICE | NEXT TOPIC {} | {}",
                event.getSource() /*serviços atual*/, productValidationFailTopic /*tópico passado*/, createSagaId(event) /*composto porId do pedido, id da transação e o id do evento */  );
                sendEvent(event, productValidationFailTopic);

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
