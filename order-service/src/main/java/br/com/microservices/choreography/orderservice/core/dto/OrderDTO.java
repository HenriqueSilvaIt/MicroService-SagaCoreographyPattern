package br.com.microservices.choreography.orderservice.core.dto;


import br.com.microservices.choreography.orderservice.core.document.OrderProducts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data /*cria get,set, equals hash code e tostring*/
@NoArgsConstructor /*Construtor  sem argumentos*/
@AllArgsConstructor /*Construtor com argumentos*/
public class OrderDTO {


    private List<OrderProducts> products; /*Recebe a lista de produtos*/


}
