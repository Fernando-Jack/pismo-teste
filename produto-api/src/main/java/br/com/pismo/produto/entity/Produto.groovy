package br.com.pismo.produto.entity

import io.vertx.core.json.JsonObject

import java.util.concurrent.atomic.AtomicInteger

class Produto {

	private static final AtomicInteger COUNTER = new AtomicInteger()

	def int id

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

	def Produto(JsonObject json) {
		this.name = json.getString("NAME")
		this.price = json.getInteger("PRICE")
		this.id = json.getInteger("ID")
		this.category = json.getString("CATEGORY")
	}
}