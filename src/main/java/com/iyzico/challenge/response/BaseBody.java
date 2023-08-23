package com.iyzico.challenge.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseBody<T> {

    @Valid
    private T data;
}
