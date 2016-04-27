package br.com.pismo.compra.entity

import java.util.concurrent.atomic.AtomicInteger

import io.vertx.core.json.JsonObject;

class Compra {

  private static final AtomicInteger COUNTER = new AtomicInteger()

  def int id  
  def int price  
  def int itemId
  def int userId

  def Compra(itemId, userId, price) {
    this.id = COUNTER.getAndIncrement()   
    this.price = price
	this.itemId = itemId
	this.userId = userId
  }

  def Compra() {
    this.id = COUNTER.getAndIncrement()
  } 
  
  def Compra(JsonObject json) {
	  this.itemId = json.getInteger("ITEMID")
	  this.price = json.getInteger("PRICE")
	  this.id = json.getInteger("ID")
	  this.userId = json.getInteger("USERID")
  }
 
}