package br.com.microservices.choreography.paymentservice.core.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j /*anotação do lombok que instancia a classe Logger do java*/
@Component /*para especificar que essa classe é um componente do spring
e vamos conseguir injetar depêndencia dessa classe*/
@RequiredArgsConstructor /*cria um construtor apenas com os campos necessários
para o construtor, n utiliza nem todos e nem um vazio*/
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate; /*esse método
    é um bean da classe KafkaConfig e vamos injetar ele aqui atraves
    do @RequiredArgsConstructor toda classe final precisa
    instanciar um valor para e o @RequiredArgsConstructor  instancia um construtor
      apenas com os valores final que definimos na nossa classe*/
    ;

    public void sendEvent(String payload, String topic) {
        try {
            log.info("Sending event to topic {} with data {}", topic, payload);
            /*envio do evento*/
            kafkaTemplate.send(topic, payload); /*send recebe o nome do tópico e o payload*/
                /*é possível vocÊ trabalhar com partição também (msa o nosso n tem replica nem partições)
                * pssando partição e a chave e valor dela no send
               * send(String topic, Integer partition, Long timestamp, K key, @Nullable V data)*/
        } catch (Exception e ) {
            /*vai mostrar porque deu erro ao enviar o evento*/
            log.error("Error trying to send data to topic {} with data {}", topic, payload,e.getMessage());
        }
    }


}
