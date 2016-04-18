package br.com.pismo.compra.repository

import br.com.pismo.compra.entity.Compra


class CompraRepositoryInMemory implements CompraRepository {
	
	def compras = []
	
	def public CompraRepositoryInMemory(){
		loadInitialData()
	}	
	
	def private loadInitialData(){		
		compras << new Compra(0, 0, 0,0)		
		compras << new Compra(1, 1, 1, 1)
	}

	@Override
	public int createCompra(productId, price, itemId, userId) {
		def compra = new Compra(productId, price, itemId, userId)
		compras << compra		
		return compra.id;
	}

	@Override
	public Compra getCompraById(Object id) {
		return compras.find{it.id == id};
	}

}
