package com.nttdata.microproduct.service;

import com.nttdata.microproduct.model.ClientProduct;
import com.nttdata.microproduct.model.request.ClientProductRequest;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;

/**
 * ClientProductService Interface.
 */
public interface ClientProductService {
  public Maybe<ClientProduct> saveClientProduct(ClientProductRequest clientProductRequest);

  public Flowable<ClientProduct> getClientProductByDocumentNumber(String documentNumber);
}
