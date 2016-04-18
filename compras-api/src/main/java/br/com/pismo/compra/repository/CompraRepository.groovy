package br.com.pismo.compra.repository

import br.com.pismo.compra.entity.Compra


public interface CompraRepository {
	
	def int createCompra(productId, price, itemId, userId)
	def Compra getCompraById(id)
}
