package com.iyzico.challenge.request;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ClientRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Surname is mandatory")
    private String surname;
    @NotBlank(message = "GSM Number is mandatory")
    private String gsmNumber;
    @NotBlank(message = "Email is mandatory")
    private String email;
    @NotBlank(message = "Identity Number is mandatory")
    private String identityNumber;
    @NotBlank(message = "Registration Address is mandatory")
    private String registrationAddress;
    @NotBlank(message = "City is mandatory")
    private String city;
    @NotBlank(message = "Country is mandatory")
    private String country;
    @NotBlank(message = "Zip Code is mandatory")
    private String zipCode;
    @NotBlank(message = "Card Holder Name is mandatory")
    private String cardHolderName;
    @NotBlank(message = "Card Number is mandatory")
    private String cardNumber;
    @Length(min = 2, max = 2)
    @NotBlank(message = "Expire Month is mandatory")
    private String expireMonth;
    @Length(min = 4, max = 4)
    @NotBlank(message = "Expire Year is mandatory")
    private String expireYear;
    @Length(min = 3, max = 3)
    @NotBlank(message = "CVC is mandatory")
    private String cvc;
}
