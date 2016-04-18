package br.com.pismo.produto.entity
import java.util.concurrent.atomic.AtomicInteger

class InventarioItem {

	private static final AtomicInteger COUNTER = new AtomicInteger()

	def final int id	
	def String category	

	def InventarioItem(category) {
		this.id = COUNTER.getAndIncrement()
		this.category = category		
	}

	def InventarioItem() {
		this.id = COUNTER.getAndIncrement()
	}
}
