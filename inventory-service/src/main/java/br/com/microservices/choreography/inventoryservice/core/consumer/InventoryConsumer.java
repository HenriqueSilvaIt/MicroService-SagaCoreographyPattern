package br.com.microservices.choreography.inventoryservice.core.consumer;



import br.com.microservices.choreography.inventoryservice.core.dto.Event;
import br.com.microservices.choreography.inventoryservice.core.services.InventoryService;
import br.com.microservices.choreography.inventoryservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j /*anotação do lombok que instancia a classe Logger do java*/
@Component /*para especificar que essa classe é um componente do spring
e vamos conseguir injetar depêndencia dessa classe*/
@AllArgsConstructor /*constroi construtor padrão e com argumento*/
public class InventoryConsumer {

    private final InventoryService inventoryService;

    private final JsonUtil jsonUtil; /* PRECISa dele
    para fazer a conversão de Json que vamos recuperar do kafk para objeto*/


    /*Método para consumir um evento*/
    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}", /*colocamos o group id que está application.yl*/
            topics = "${spring.kafka.topic.inventory-success}"
    )
    public void consumeSuccessEvent(String payload) {
        log.info("Receiving success notification event {} from inventory-success topic", payload); /*mostrar
        informação que recebemos*/
        Event event = jsonUtil.toEvent(payload); /*converte json recebido para objeto*/
        log.info(event.toString()); /*só para mostrar  o objeto que foi criado*/
        inventoryService.updateInventory(event); /*atualiza estoque*/
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}", /*colocamos o group id que está application.yl*/
            topics = "${spring.kafka.topic.inventory-fail}"
    )
    public void consumeFailEvent(String payload) {
        log.info("Receiving rollback event {} from inventory-fail topic", payload); /*mostrar
        informação que recebemos*/
        Event event = jsonUtil.toEvent(payload); /*converte json recebido para objeto*/
        log.info(event.toString()); /*só para mostrar  o objeto que foi criado*/
        inventoryService.rollbackInventory(event); /*faz rollback na atualização
        do estoque*/

    }


}
