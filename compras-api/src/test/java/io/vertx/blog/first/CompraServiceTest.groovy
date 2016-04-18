package io.vertx.blog.first

import io.vertx.core.Vertx

import org.junit.Test

import br.com.pismo.compra.integration.InventarioRestIntegration
import br.com.pismo.compra.repository.CompraRepositoryInMemory
import br.com.pismo.compra.service.CompraService

class CompraServiceTest {
	
	@Test
	public void shouldBeAbleToBuyAProduct() {
		
		def expectedProductId = 0
		def expectedPrice = 0		
		def expectedCategory = "playstation"
		def expectedUserId = 0
		
		def vertx = Vertx.vertx()
		
		def compraService = new CompraService(
									new CompraRepositoryInMemory()
									,new InventarioRestIntegration(vertx))
		
		def handler = { compraId ->
		
			def compra = compraService.getCompraById(compraId)
			
			assert expectedProductId == compra.getProductId()
			assert expectedPrice == compra.getPrice()
			assert expectedUserId == compra.getUserId()
			assert compra.getItemId() != null
			assert compra.getItemId() > -1
		
		}
		
		compraService.comprar(expectedProductId, expectedPrice, expectedUserId, handler)		
		
	}

}
