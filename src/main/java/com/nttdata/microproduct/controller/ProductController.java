package com.nttdata.microproduct.controller;

import com.nttdata.microproduct.model.Product;
import com.nttdata.microproduct.model.request.ProductRequest;
import com.nttdata.microproduct.service.ProductRulesService;
import com.nttdata.microproduct.service.ProductService;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProductController Class.
 */
@RestController
@RequestMapping("/")
public class ProductController {
  @Autowired
  private ProductService productService;

  @Autowired
  private ProductRulesService productRulesService;

  /**
   * listProducts method.
   *
   * @return ,
   */
  @RequestMapping(value = "/products", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Flowable<Product> listProducts() {
    Flowable<Product> products = null;
    try {
      products = productService.listProducts();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return products;
  }

  @PostMapping(value = "/product")
  @ResponseStatus(HttpStatus.OK)
  public Maybe<Product> saveProduct(@RequestBody ProductRequest productRequest) {
    return productService.saveProduct(productRequest);
  }

  /**
   * getCient method.
   *
   * @param codeProduct ,
   * @return ,
   */
  @GetMapping(value = "/product/{codeProduct}")
  @ResponseStatus(HttpStatus.OK)
  public Maybe<Product> getCient(@PathVariable(value = "codeProduct") String codeProduct) {
    Maybe<Product> product = null;
    try {
      product = productService.getProduct(codeProduct);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return product;
  }

  /**
   * deleteProduct method.
   *
   * @param codeProduct ,
   * @return ,
   */
  @DeleteMapping(value = "/product/{codeProduct}")
  @ResponseStatus(HttpStatus.OK)
  public Maybe<Product> deleteProduct(@PathVariable(value = "codeProduct") String codeProduct) {
    Maybe<Product> client = null;
    try {
      client = productService.deleteProduct(codeProduct);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return client;
  }

  /**
   * saveProductRules method.
   */
  @RequestMapping(value = "/saveProductRules", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public void saveProductRules() {

    try {
      productRulesService.saveProductRules();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
