package br.com.microservices.choreography.productvalidationservice.core.consumer;



import br.com.microservices.choreography.productvalidationservice.core.dto.Event;
import br.com.microservices.choreography.productvalidationservice.core.services.ProductValidationService;

import br.com.microservices.choreography.productvalidationservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j /*anotação do lombok que instancia a classe Logger do java*/
@Component /*para especificar que essa classe é um componente do spring
e vamos conseguir injetar depêndencia dessa classe*/
@AllArgsConstructor /*constroi construtor padrão e com argumento*/
public class ProductValidationConsumer {

    private final ProductValidationService productValidationService;

    private final JsonUtil jsonUtil; /* PRECISa dele
    para fazer a conversão de Json que vamos recuperar do kafk para objeto*/


    /*Método para consumir um evento*/
    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}", /*colocamos o group id que está application.yl*/
            topics = "${spring.kafka.topic.product-validation-start}"
    )
    public void consumeSuccessEvent(String payload) {
        log.info("Receiving success notification event {} from product-validation-start topic", payload); /*mostrar
        informação que recebemos*/
        Event event = jsonUtil.toEvent(payload); /*converte json recebido para objeto*/
        log.info(event.toString()); /*só para mostrar  o objeto que foi criado*/
        productValidationService.validateExistingProducts(event); /*
        faz validação do produto, quando o orchestrador manda
        um evento de sucesso*/
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}", /*colocamos o group id que está application.yl*/
            topics = "${spring.kafka.topic.product-validation-fail}"
    )
    public void consumeFailEvent(String payload) {
        log.info("Receiving rollback event {} from product-validation-fail topic", payload); /*mostrar
        informação que recebemos*/
        Event event = jsonUtil.toEvent(payload); /*converte json recebido para objeto*/
        log.info(event.toString()); /*só para mostrar  o objeto que foi criado*/
        productValidationService.rollbackEvent(event); /*
        faz rollback quando orchestrador manda um evento de falha*/

    }


}
