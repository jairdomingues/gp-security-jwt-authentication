package com.bezkoder.springjwt.services;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionHistoryRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idAccount;
	private String operation;
	private String transactionType;
	private String status;
	private String history;
	private BigDecimal amount;
	private Long orderId;

}
