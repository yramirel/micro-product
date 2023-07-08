package com.nttdata.microproduct.service.impl;

import com.nttdata.microproduct.errorhandler.ConflictException;
import com.nttdata.microproduct.model.Product;
import com.nttdata.microproduct.model.request.ProductRequest;
import com.nttdata.microproduct.repository.ClientProductRepository;
import com.nttdata.microproduct.repository.ClientRepository;
import com.nttdata.microproduct.repository.ProductRepository;
import com.nttdata.microproduct.service.ProductService;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ProductServiceImpl Class for product.
 */
@Service
public class ProductServiceImpl implements ProductService {

  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private ClientProductRepository clientProductRepository;
  @Autowired
  private ClientRepository clientRepository;

  @Override
  public Flowable<Product> listProducts() {
    return productRepository.listProducts();
  }

  @Override
  public Maybe<Product> saveProduct(ProductRequest productRequest) {
    return productRepository.getByName(productRequest.getName()).isEmpty()
               .flatMap(isEmpty -> {
                 productRequest.setState(1);
                 return isEmpty ? productRepository.save(productRequest.toProduct())
                            : Single.error(new ConflictException("Error"));
               }).toMaybe();
  }

  @Override
  public Maybe<Product> getProduct(String codeProduct) throws Exception {
    return this.productRepository.getByCodeProduct(codeProduct);
  }

  @Override
  public Maybe<Product> deleteProduct(String codProduct) {
    return productRepository.getByCodeProduct(codProduct).toSingle()
               .flatMap(product -> {
                 product.setState(0);
                 return productRepository.save(product);
               }).toMaybe();
  }
}
