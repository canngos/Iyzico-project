package com.iyzico.challenge.service;

import com.iyzico.challenge.request.ClientRequest;
import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.CreatePaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RealPaymentService {

        private final Options options;

        public String pay(BigDecimal price, ClientRequest clientRequest) {
                CreatePaymentRequest request = new CreatePaymentRequest();
                request.setLocale(Locale.TR.getValue());
                request.setConversationId("123456789");
                request.setPrice(price);
                request.setPaidPrice(price.add(BigDecimal.valueOf(0.2)));
                request.setCurrency(Currency.TRY.name());
                request.setInstallment(1);
                request.setBasketId("B67832");
                request.setPaymentChannel(PaymentChannel.WEB.name());
                request.setPaymentGroup(PaymentGroup.PRODUCT.name());

                PaymentCard paymentCard = new PaymentCard();
                BeanUtils.copyProperties(clientRequest, paymentCard);
                paymentCard.setRegisterCard(0);
                request.setPaymentCard(paymentCard);

                Buyer buyer = new Buyer();
                buyer.setId("BY789");
                BeanUtils.copyProperties(clientRequest, buyer);
                buyer.setLastLoginDate("2015-10-05 12:43:35");
                buyer.setRegistrationDate("2013-04-21 15:12:09");
                buyer.setIp("85.34.78.112");
                request.setBuyer(buyer);

                Address shippingAddress = new Address();
                shippingAddress.setContactName(clientRequest.getName() + " " + clientRequest.getSurname());
                shippingAddress.setCity(clientRequest.getCity());
                shippingAddress.setCountry(clientRequest.getCountry());
                shippingAddress.setAddress(clientRequest.getRegistrationAddress());
                shippingAddress.setZipCode(clientRequest.getZipCode());
                request.setShippingAddress(shippingAddress);

                Address billingAddress = new Address();
                billingAddress.setContactName(clientRequest.getName() + " " + clientRequest.getSurname());
                billingAddress.setCity(clientRequest.getCity());
                billingAddress.setCountry(clientRequest.getCountry());
                billingAddress.setAddress(clientRequest.getRegistrationAddress());
                billingAddress.setZipCode(clientRequest.getZipCode());
                request.setBillingAddress(billingAddress);

                List<BasketItem> basketItems = new ArrayList<>();
                BasketItem firstBasketItem = new BasketItem();
                firstBasketItem.setId("BI101");
                firstBasketItem.setName("Flight Ticket");
                firstBasketItem.setCategory1("Transportation");
                firstBasketItem.setItemType(BasketItemType.VIRTUAL.name());
                firstBasketItem.setPrice(price);
                basketItems.add(firstBasketItem);
                request.setBasketItems(basketItems);
                Payment payment = Payment.create(request, options);
                return payment.getStatus();

        }

}
