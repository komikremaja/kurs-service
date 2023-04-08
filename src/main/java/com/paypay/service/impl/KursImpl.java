package com.paypay.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypay.Exception.BadRequestException;
import com.paypay.constant.VariableConstant;
import com.paypay.dto.Response.Response;
import com.paypay.dto.Response.ResponseKursBca;
import com.paypay.model.KursData;
import com.paypay.repository.BankDataRepo;
import com.paypay.repository.KursDataRepo;

@Service
public class KursImpl {

    private Response response;

    @Autowired
    private KursDataRepo kursDataRepo;

    @Autowired
    private BankDataRepo bankDataRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private VariableConstant variableConstant;

    @Value("${kurs.dsid}")
    private String dsid;

    @Transactional(rollbackOn = Exception.class)
    public Response inquiryKursBank() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {

            // Hit bca Kurs for kurs indication
            List<Timestamp> kursDb = kursDataRepo.findNewKurs();
            LocalDateTime today = LocalDateTime.now();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            String url = "https://www.bca.co.id/api/sitecore/currencies/RefreshKurs";
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("dsid", dsid);
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);
            String responseKursBca = restTemplate.postForEntity(url, entity, String.class).getBody();
            ResponseKursBca responseKurs = objectMapper.readValue(responseKursBca, ResponseKursBca.class);
            response = new Response(variableConstant.getSTATUS_OK(), "Success", responseKurs);
            List<KursData> kursDatas = new ArrayList<>();
            LocalDateTime localDateTime = LocalDateTime.now();
            if (kursDb.size() != 0) {
                localDateTime = kursDb.get(0).toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }
            if (kursDb.size() == 0) {
                for (int i = 0; i < 5; i++) {
                    KursData kursData = new KursData();
                    kursData.setCurrency(responseKurs.getKursRates().get(i).getCurrencyName());
                    kursData.setIdBank(new BigDecimal("1"));
                    kursData.setKursBuy(responseKurs.getKursRates().get(i).getRates().get(0).getBuyRate());
                    kursData.setKursSell(responseKurs.getKursRates().get(i).getRates().get(0).getSellRate());
                    if (!kursData.getCurrency().equalsIgnoreCase("IDR")) {
                        BigDecimal kursBuy = kursData.getKursBuy();
                        BigDecimal kursSell = kursData.getKursSell();
                        BigDecimal margin = kursBuy.multiply(new BigDecimal(0.02));
                        BigDecimal kursBi = kursBuy.multiply(new BigDecimal(0.05));
                        BigDecimal kursMarginBuy = kursBuy.subtract(margin);
                        BigDecimal kursMarginSell = kursSell.add(margin);
                        kursData.setKursBi(kursBuy.subtract(kursBi).setScale(2, RoundingMode.HALF_UP));
                        kursData.setKursMarginBuy(kursMarginBuy.setScale(2, RoundingMode.HALF_UP));
                        kursData.setKursMarginSell(kursMarginSell.setScale(2, RoundingMode.HALF_UP));
                    } else {
                        kursData.setKursBi(new BigDecimal("0"));
                        kursData.setKursMarginBuy(kursData.getKursBuy());
                        kursData.setKursMarginSell(kursData.getKursSell());
                    }
                    kursData.setCreatedDate(today);
                    kursData.setLastUpdate(today);
                    kursDatas.add(kursData);
                    if (i == 4) {
                        kursDataRepo.saveAll(kursDatas);
                    }
                }
            }else if(!localDateTime.toLocalDate().isEqual(LocalDate.now())){
                for (int i = 0; i < 5; i++) {
                    KursData kursData = new KursData();
                    kursData.setCurrency(responseKurs.getKursRates().get(i).getCurrencyName());
                    kursData.setIdBank(new BigDecimal("1"));
                    kursData.setKursBuy(responseKurs.getKursRates().get(i).getRates().get(0).getBuyRate());
                    kursData.setKursSell(responseKurs.getKursRates().get(i).getRates().get(0).getSellRate());
                    if (!kursData.getCurrency().equalsIgnoreCase("IDR")) {
                        BigDecimal kursBuy = kursData.getKursBuy();
                        BigDecimal kursSell = kursData.getKursSell();
                        BigDecimal margin = kursBuy.multiply(new BigDecimal(0.02));
                        BigDecimal kursBi = kursBuy.multiply(new BigDecimal(0.05));
                        BigDecimal kursMarginBuy = kursBuy.subtract(margin);
                        BigDecimal kursMarginSell = kursSell.add(margin);
                        kursData.setKursBi(kursBuy.subtract(kursBi).setScale(2, RoundingMode.HALF_UP));
                        kursData.setKursMarginBuy(kursMarginBuy.setScale(2, RoundingMode.HALF_UP));
                        kursData.setKursMarginSell(kursMarginSell.setScale(2, RoundingMode.HALF_UP));
                    } else {
                        kursData.setKursBi(new BigDecimal("0"));
                        kursData.setKursMarginBuy(kursData.getKursBuy());
                        kursData.setKursMarginSell(kursData.getKursSell());
                    }
                    kursData.setCreatedDate(today);
                    kursData.setLastUpdate(today);
                    kursDatas.add(kursData);
                    if (i == 4) {
                        kursDataRepo.saveAll(kursDatas);
                    }
                }
            }

            return response;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Error get kurs BCA: " + e.getMessage());
            throw new BadRequestException("Error Get Kurs");
        }
    }
}
