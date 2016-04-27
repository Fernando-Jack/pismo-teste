package br.com.pismo.compra.repository

import br.com.pismo.compra.entity.Compra


public interface CompraRepository {
	
	def int createCompra(itemId, userId, price, nextHandler)
	def Compra getCompraById(id, nextHandler)
}
