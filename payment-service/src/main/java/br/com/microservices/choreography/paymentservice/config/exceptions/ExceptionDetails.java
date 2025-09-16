package br.com.microservices.choreography.paymentservice.config.exceptions;

public record ExceptionDetails (Integer status, String message)/* record é um
novo recurso do java 17, classe para pegar apenas
a messagem de erro e o status n quero todo log do Java*/{


}
