package com.paypay.dto.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ResponseDataKursByDate {
    
    private BigDecimal idKurs;

    private BigDecimal idBank;
    
    private String currency;

    private BigDecimal kursBuy;

    private BigDecimal kursBi;

    private BigDecimal kursMarginBuy;

    private BigDecimal kursMarginSell;

    private BigDecimal kursSell;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDateTime createdDate;
    
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDateTime lastUpdate;
    
}
