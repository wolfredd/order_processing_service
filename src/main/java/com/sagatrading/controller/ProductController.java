package com.sagatrading.controller;

import com.sagatrading.dto.ExchangeDto;
import com.sagatrading.model.ProductData;
import com.sagatrading.service.OrderService;
import com.sagatrading.service.ProductDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
@CrossOrigin(origins = "*")
public class ProductController {
    @Autowired
    OrderService orderService;

    @Autowired
    ProductDataService productDataService;


    @GetMapping("/all")
    public Map<ExchangeDto, List<ProductData>> getProducts() {
        return productDataService.getExchangeProductData();
    }
}





