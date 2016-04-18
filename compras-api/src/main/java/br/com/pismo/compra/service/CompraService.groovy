package br.com.pismo.compra.service

import br.com.pismo.compra.entity.Compra
import br.com.pismo.compra.integration.InventarioIntegration
import br.com.pismo.compra.integration.InventarioRestIntegration
import br.com.pismo.compra.repository.CompraRepository


class CompraService {

	def CompraRepository repository
	def InventarioRestIntegration inventario

	public CompraService(
			CompraRepository repository,
			InventarioIntegration inventario
		){
		this.repository = repository
		this.inventario = inventario
	}

	def  int comprar(productId, price, userId, handler){
		def compra = {itemId ->
			print itemId
			def id = repository.createCompra(productId, price, itemId, userId)
			handler(id)
		}
		def itemId = inventario.getAvailableItem(productId,compra)
	}

	def  Compra getCompraById(id){
		repository.getCompraById(id)
	}
}
