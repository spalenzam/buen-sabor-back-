package com.buenSabor.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buenSabor.entity.MercadoPagoDatos;
import com.buenSabor.services.MercadoPagoDatosService;
import com.buenSabor.services.MercadoPagoDatosServiceImpl;
import com.buenSabor.services.dto.DetallePedidoDTO;
import com.commons.controllers.CommonController;


import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.PreferencePaymentType;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePaymentMethodRequest;
import com.mercadopago.client.preference.PreferencePaymentMethodsRequest;
import com.mercadopago.client.preference.PreferencePaymentTypeRequest;
import com.mercadopago.client.preference.PreferenceRequest;

@RestController
@RequestMapping(path = "api/buensabor/mercadoPagoDatos")
public class MercadoPagoDatosController extends CommonController<MercadoPagoDatos, MercadoPagoDatosService> {

	@Autowired
	protected MercadoPagoDatosServiceImpl mercadoPagoDatosService;
	
	@PostMapping("/payment")
	public ResponseEntity<?> processPayment(@RequestBody DetallePedidoDTO detpedido) throws MPException, MPApiException {
		MercadoPagoConfig.setAccessToken("TEST-5308942090062149-050414-517f77a9e02222eef1cd89f17966b93f-447851281");
		
		// Crea un objeto de preferencia
		PreferenceClient client = new PreferenceClient();
		
		List<PreferenceItemRequest> items = new ArrayList<>();
		
			PreferenceItemRequest item =
			   PreferenceItemRequest.builder()
			       .title(detpedido.getDescripcion())
			       .quantity(detpedido.getCantidad())
			       .unitPrice(new BigDecimal(detpedido.getTotalPagar()))
			       .build();
			items.add(item);
		
		PreferenceBackUrlsRequest backUrls = 
				PreferenceBackUrlsRequest.builder()
				.success("http://localhost:3000/productos")
				.failure("http://localhost:3000/productos")
				.pending("http://localhost:3000/productos")
				.build();
		
		
		List<PreferencePaymentTypeRequest> excludedPaymentTypes = new ArrayList<>();
			excludedPaymentTypes.add(PreferencePaymentTypeRequest.builder().id("ticket").build());
		
		PreferencePaymentMethodsRequest paymentMethods =
		   PreferencePaymentMethodsRequest.builder()
		       .installments(3)
		       .excludedPaymentTypes(excludedPaymentTypes)
		       .build();
		
		PreferenceRequest request = 
				PreferenceRequest.builder().items(items).backUrls(backUrls).paymentMethods(paymentMethods).autoReturn("approved").build();
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(client.create(request));
	} 
}