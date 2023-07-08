package com.nttdata.microproduct.controller;

import com.nttdata.microproduct.model.ClientProduct;
import com.nttdata.microproduct.model.request.ClientProductRequest;
import com.nttdata.microproduct.service.ClientProductService;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClientProductController Class.
 */
@RestController
@RequestMapping("/")
public class ClientProductController {
  @Autowired
  private ClientProductService clientProductService;
  @Autowired
  private ClientProductService clientService;

  /**
   * saveClient method.
   *
   * @param clientProductRequest ,
   * @return ,
   */
  @PostMapping(value = "/clientproduct")
  @ResponseStatus(HttpStatus.CREATED)
  public Maybe<ClientProduct> saveClient(@RequestBody ClientProductRequest clientProductRequest) {
    Maybe<ClientProduct> product = null;
    try {
      product = clientProductService.saveClientProduct(clientProductRequest);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return product;
  }

  /**
   * getClientProductByDocumentNumber method.
   *
   * @param documentNumber ,
   * @return ,
   */
  @RequestMapping(value = "/clientproduct/{documentNumber}",
      produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flowable<ClientProduct> getClientProductByDocumentNumber(
      @PathVariable(value = "documentNumber") String documentNumber) {
    Flowable<ClientProduct> transactions = null;
    try {
      transactions = clientProductService.getClientProductByDocumentNumber(documentNumber);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return transactions;
  }
}
