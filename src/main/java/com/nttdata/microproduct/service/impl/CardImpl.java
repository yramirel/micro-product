package com.nttdata.microproduct.service.impl;

import com.nttdata.microproduct.errorhandler.ConflictException;
import com.nttdata.microproduct.model.Card;
import com.nttdata.microproduct.model.ClientProduct;
import com.nttdata.microproduct.model.request.CardRequest;
import com.nttdata.microproduct.model.request.ClientProductRequest;
import com.nttdata.microproduct.repository.CardRepository;
import com.nttdata.microproduct.repository.ClientProductRepository;
import com.nttdata.microproduct.service.CardService;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * CardImpl Class.
 */
@Service
public class CardImpl implements CardService {
  @Autowired
  private CardRepository cardRepository;
  @Autowired
  private ClientProductRepository clientProductRepository;

  @Override
  public Maybe<Card> saveCard(CardRequest card) {
    return cardRepository.getByCardNumber(card.getCardNumber()).isEmpty()
               .flatMap(isEmpty -> {
                 card.setState(1);
                 return isEmpty ? cardRepository.save(card.toCard())
                            : Single.error(new ConflictException("El La tarjeta ya Existe"));
               }).toMaybe();
  }

  @Override
  public Maybe<Card> saveCardAssociated(String cardNumber,
                                        ClientProductRequest clientProductRequest) {
    return cardRepository.getByCardNumber(cardNumber)
         .switchIfEmpty(Maybe.error(new ConflictException("El La tarjeta No Existe")))
         .flatMap(card -> {
           ClientProduct clientProduct = clientProductRepository
                                       .getByAccountNumber(clientProductRequest.getAccountNumber())
                                       .switchIfEmpty(
                                           Maybe.error(
                                               new ConflictException("La Cuenta No Existe")))
                                           .blockingGet();
           List<ClientProduct> associatedAccounts = card.getAssociatedAccounts();
           associatedAccounts = associatedAccounts == null ? new ArrayList<>() : associatedAccounts;
           associatedAccounts.add(clientProduct);
           card.setAssociatedAccounts(associatedAccounts);
           cardRepository.save(card).subscribe();
           return Maybe.just(card);
         });
  }

  @Override
  public Maybe<Card> deleteCard(String cardNumber) {
    return cardRepository.getByCardNumber(cardNumber).toSingle()
               .flatMap(card -> {
                 card.setState(0);
                 return cardRepository.save(card);
               }).toMaybe();
  }

  @Override
  public Maybe<Card> getCard(String cardNumber) throws Exception {
    return this.cardRepository.getByCardNumber(cardNumber);
  }

  @Override
  public Flowable<Card> listCard(String cardNumber) throws Exception {
    return this.cardRepository.getAllCard();
  }
}
