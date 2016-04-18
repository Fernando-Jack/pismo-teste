package br.com.pismo.compra.entity

import java.util.concurrent.atomic.AtomicInteger

class Compra {

  private static final AtomicInteger COUNTER = new AtomicInteger()

  def final int id
  def int productId
  def int price  
  def int itemId
  def int userId

  def Compra(productId, price, itemId, userId) {
    this.id = COUNTER.getAndIncrement()
    this.productId = productId
    this.price = price
	this.itemId = itemId
	this.userId = userId
  }

  def Compra() {
    this.id = COUNTER.getAndIncrement()
  }  
 
}