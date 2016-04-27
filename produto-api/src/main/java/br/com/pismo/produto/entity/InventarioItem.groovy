package br.com.pismo.produto.entity
import java.util.concurrent.atomic.AtomicInteger

import io.vertx.core.json.JsonObject;

class InventarioItem {

	private static final AtomicInteger COUNTER = new AtomicInteger()

	def int id	
	def String category	

	def InventarioItem(category) {
		this.id = COUNTER.getAndIncrement()
		this.category = category		
	}
	
	def InventarioItem(JsonObject json) {		
		this.id = json.getInteger("ID")
		this.category = json.getString("CATEGORY")
	}

	def InventarioItem() {
		this.id = COUNTER.getAndIncrement()
	}
}
