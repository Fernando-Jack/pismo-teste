package br.com.pismo.teste

import org.junit.Test

import br.com.pismo.produto.repository.InventarioRepositoryInMemory
import br.com.pismo.produto.service.InventarioService

class InventarioServiceTest {
	
	@Test
	public void shouldRetrieveAnItemFromInventory() {		
		
		def expectedCategory = "playstation"
		
		def invetarioService = new InventarioService(new InventarioRepositoryInMemory())
		
		def invetarioIdCreated = invetarioService.addItem(expectedCategory)
		
		def item = invetarioService.getInventarioItemById(invetarioIdCreated)
		
		assert expectedCategory == item.getCategory()
		
	}
	
	@Test
	public void shouldConsumeAnItemFromInventoryByCategory() {
		
		def expectedCategory = "playstation2"
		
		def invetarioService = new InventarioService(new InventarioRepositoryInMemory())
		
		def invetarioIdCreated = invetarioService.addItem(expectedCategory)
		
		def itemID = invetarioService.consumeInventarioItem(expectedCategory)
		
		def itemNullId = invetarioService.consumeInventarioItem(expectedCategory)
		
		assert itemID == invetarioIdCreated
		assert itemNullId == -1
		
	}

}
