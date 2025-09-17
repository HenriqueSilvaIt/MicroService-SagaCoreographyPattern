package br.com.microservices.choreography.orderservice.core.services;


import br.com.microservices.choreography.orderservice.core.document.Event;
import br.com.microservices.choreography.orderservice.core.document.Order;
import br.com.microservices.choreography.orderservice.core.dto.OrderDTO;
import br.com.microservices.choreography.orderservice.core.producer.SagaProducer;
import br.com.microservices.choreography.orderservice.core.repositories.OrderRepository;
import br.com.microservices.choreography.orderservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor /*Construtor com todos argumentp*/
public class OrderService  /*para de fato fazer o pedido , que é só salvar o pedido e mandar
para o producer*/{


    private static final String TRANSACTIONAL_ID_PATTERN = "%s_%s";
    /*UnixTime(EPOCtime) é o instante atual
     * converte o para valor número
     * %s_%s é formato do isntante atual*/

    private final OrderRepository repository;

    private final JsonUtil jsonUtil; /*para converter objeto para string
    para conguirmos mandar para o Kafka*/
    private final SagaProducer producer; /*Classe que vai enviar de fato
    para o producer do kafka*/
    private final EventService eventService; /*cria esse objeto
    de event gerando um id para ele etc*/


    /*Método para cria pédido que recebe um Json do tipo OderDTO para
    *    retorna um pedido*/
    public Order createOrder(OrderDTO orderDto) {

        Order order = Order
                .builder() /*vem do @Builder que colocamos na entidade*/
                .products(orderDto.getProducts()) /*recebe uma lista de produto do OrderDTO que cliente mando*/
                .createAt(LocalDateTime.now()) /*instante atual*/
                .transactionId(

                        /*Retorna  um instante atual com um id único*/
                        String.format(TRANSACTIONAL_ID_PATTERN, Instant.now().toEpochMilli(), UUID.randomUUID())
                /*UnixTime(EPOCtime) é o instante atual
                * converte o para valor número
                * %s_%s é formato do isntante atual, depois
                * da virgula é passado o valor que queremos converter
                *  Instant.now().toEpochMilli() Epoc mili retorna o valor
                * em número no formato que colocamos
                *
                * UUID.randomUUID() /*gera um Id aleatório para ser
                um id unico*/)
                .build(); /*para finalizar instanciação do objeto*/

        repository.save(order);

        /*cria o payload, então crio um objeto do tipo evento
        * com os dados do pedido */
       // Event event = createPayload(order);
        /*envia o evento*/
        producer.sendEvent(jsonUtil.toJson(eventService.createEvent(order)));/*converte para Json(string e envia o evento)*/
        return order; /*retorna o objeto criado com id do mongodb*/

    }



}
