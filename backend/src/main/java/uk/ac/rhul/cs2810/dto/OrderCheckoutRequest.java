package uk.ac.rhul.cs2810.dto;

import java.math.BigDecimal;

public class OrderCheckoutRequest {
  private Long orderId;
  private String cardNumber;
  private String nameOnCard;
  private Integer expiryMonth;
  private Integer expiryYear;
  private String cvv;
  private BigDecimal amount;

  public Long getOrderId() {
    return orderId;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public String getNameOnCard() {
    return nameOnCard;
  }

  public Integer getExpiryMonth() {
    return expiryMonth;
  }

  public Integer getExpiryYear() {
    return expiryYear;
  }

  public String getCvv() {
    return cvv;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public void setNameOnCard(String nameOnCard) {
    this.nameOnCard = nameOnCard;
  }

  public void setExpiryMonth(Integer expiryMonth) {
    this.expiryMonth = expiryMonth;
  }

  public void setExpiryYear(Integer expiryYear) {
    this.expiryYear = expiryYear;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
}
