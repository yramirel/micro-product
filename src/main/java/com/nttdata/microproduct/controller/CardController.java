package com.nttdata.microproduct.controller;

import com.nttdata.microproduct.model.Card;
import com.nttdata.microproduct.model.request.CardRequest;
import com.nttdata.microproduct.model.request.ClientProductRequest;
import com.nttdata.microproduct.service.CardService;
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
 * CardController Class.
 */
@RestController
@RequestMapping("/")
public class CardController {
  @Autowired
  private CardService cardService;

  @PostMapping(value = "/card")
  @ResponseStatus(HttpStatus.OK)
  public Maybe<Card> saveCard(@RequestBody CardRequest card) {
    return cardService.saveCard(card);
  }

  @PostMapping(value = "/card/{cardNumber}/associated")
  @ResponseStatus(HttpStatus.OK)
  public Maybe<Card> saveCardAssociated(@PathVariable(value = "cardNumber") String cardNumber,
                                        @RequestBody ClientProductRequest clientProductRequest) {
    return cardService.saveCardAssociated(cardNumber, clientProductRequest);
  }

  /**
   * getCient method.
   *
   * @param cardNumber ,
   * @return ,
   */
  @GetMapping(value = "/card/{cardNumber}")
  @ResponseStatus(HttpStatus.OK)
  public Maybe<Card> getCient(@PathVariable(value = "cardNumber") String cardNumber) {
    Maybe<Card> card = null;
    try {
      card = cardService.getCard(cardNumber);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return card;
  }

  /**
   * deleteCient mthod.
   *
   * @param cardNumber ,
   * @return ,
   */
  @DeleteMapping(value = "/card/{cardNumber}")
  @ResponseStatus(HttpStatus.OK)
  public Maybe<Card> deleteCient(@PathVariable(value = "cardNumber") String cardNumber) {
    Maybe<Card> card = null;
    try {
      card = cardService.deleteCard(cardNumber);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return card;
  }

  /**
   * listCient mthod.
   *
   * @return ,
   */
  @RequestMapping(value = "/cards", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Flowable<Card> listCient() {
    Flowable<Card> card = null;
    try {
      card = cardService.listCard("");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return card;
  }
}
