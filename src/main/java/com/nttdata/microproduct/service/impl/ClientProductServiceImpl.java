package com.nttdata.microproduct.service.impl;

import com.nttdata.microproduct.errorhandler.ConflictException;
import com.nttdata.microproduct.model.Client;
import com.nttdata.microproduct.model.ClientProduct;
import com.nttdata.microproduct.model.Product;
import com.nttdata.microproduct.model.ProductRules;
import com.nttdata.microproduct.model.request.ClientProductRequest;
import com.nttdata.microproduct.repository.ClientProductRepository;
import com.nttdata.microproduct.repository.ClientRepository;
import com.nttdata.microproduct.repository.ProductRepository;
import com.nttdata.microproduct.repository.ProductRulesRepository;
import com.nttdata.microproduct.service.ClientProductService;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ClientProductServiceImpl CLass for assign client for product logic.
 */
@Service
public class ClientProductServiceImpl implements ClientProductService {
  @Autowired
  private ClientProductRepository clientProductRepository;
  @Autowired
  private ClientRepository clientRepository;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private ProductRulesRepository productRulesRepository;

  @Override
  public Maybe<ClientProduct> saveClientProduct(ClientProductRequest clientProductRequest) {
    return clientRepository.getByDocumentNumber(clientProductRequest.getDocumentNumber())
               .switchIfEmpty(Single.error(new ConflictException("El Cliente No existe")))
               .flatMap(client -> {

                 Maybe<Product> product = productRepository
                      .getByCodeProduct(clientProductRequest.getCodeProduct())
                      .switchIfEmpty(Maybe.error(new ConflictException("El Producto No existe")));

                 Boolean successRules = this.verifyRules(client, clientProductRequest);
                 if (successRules) {
                   clientProductRequest.setClient(client);
                   clientProductRequest.setDate(LocalDateTime.now());
                   clientProductRequest.setState(1);
                   clientProductRequest.setProduct(product.blockingGet());
                   return clientProductRepository.save(clientProductRequest.toClientProduct());
                 } else {
                   return Single.error(new ConflictException("El ciente no cumple con las Reglas "
                                                                 + "de Negocio"));
                 }
               }).toMaybe();
  }

  /**
   * verifyRules method, verify rules.
   *
   * @param client ,
   * @param clientProductRequest ,
   * @return ,
   */
  public Boolean verifyRules(Client client, ClientProductRequest clientProductRequest) {
    Boolean successRules = true;
    ProductRules productRules = null;
    Flowable<ProductRules> productRulesFlowable = productRulesRepository
                                    .getByCodeProduct(clientProductRequest.getCodeProduct())
                                    .filter(item -> item.getTypeClient() == client.getTypeClient());
    Long numberAccountCreated = clientProductRepository
                                    .getByDocumentNumberAndCodeProduct(client.getDocumentNumber(),
        clientProductRequest.getCodeProduct()).count().blockingGet();

    if (!productRulesFlowable.isEmpty().blockingGet()) {
      productRules = productRulesFlowable.blockingFirst();
    }
    if (productRules != null) {
      successRules = productRules.getMaxAccount() == -1
                         || (productRules.getMaxAccount() >= numberAccountCreated.intValue());
      if (clientProductRequest.getCodeProduct().equals("cuentacorrientepyme")
              || clientProductRequest.getCodeProduct().equals("ahorrovip")) {
        successRules = this.verifyRulesAdditionals(client, clientProductRequest);
      }
    }
    return successRules;
  }

  /**
   * verifyRulesAdditionals method.
   *
   * @param client ,
   * @param clientProductRequest ,
   * @return ,
   */
  public Boolean verifyRulesAdditionals(Client client, ClientProductRequest clientProductRequest) {
    Boolean successRules = true;
    Long numberCreditCard = clientProductRepository
                                .getByDocumentNumberAndCodeProduct(client.getDocumentNumber(),
        "tarjetacredito").count().blockingGet();
    if (clientProductRequest.getCodeProduct().equals("cuentacorrientepyme")) {
      Long numberCorrientAccount = clientProductRepository
                                   .getByDocumentNumberAndCodeProduct(client.getDocumentNumber(),
          "cuentacorriente").count().blockingGet();
      successRules = (numberCorrientAccount >= 1 && numberCreditCard >= 1);
    } else {
      successRules = numberCreditCard >= 1;
    }
    return successRules;
  }

  /**
   * getClientProductByDocumentNumber method.
   *
   * @param documentNumber ,
   * @return ,
   */
  @Override
  public Flowable<ClientProduct> getClientProductByDocumentNumber(String documentNumber) {
    return clientProductRepository.getByDocumentNumber(documentNumber);
  }
}
