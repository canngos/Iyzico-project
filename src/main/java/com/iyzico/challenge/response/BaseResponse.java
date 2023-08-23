package com.iyzico.challenge.response;

import com.iyzico.challenge.exception.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse<T> {
    private BaseBody<T> body;
    private Status status;

}
