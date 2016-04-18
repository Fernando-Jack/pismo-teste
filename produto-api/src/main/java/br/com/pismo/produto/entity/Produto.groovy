package br.com.pismo.produto.entity

import java.util.concurrent.atomic.AtomicInteger

class Produto {

  private static final AtomicInteger COUNTER = new AtomicInteger()

  def final int id

  def String name

  def int price
  
  def String category

  def Produto(name, price, category) {
    this.id = COUNTER.getAndIncrement()
    this.name = name
    this.price = price
	this.category = category
  }

  def Produto() {
    this.id = COUNTER.getAndIncrement()
  }
  
 
}