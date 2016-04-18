package br.com.pismo.teste

import org.junit.Test

import br.com.pismo.produto.repository.ProdutoRepositoryInMemory
import br.com.pismo.produto.service.ProdutoService

class ProdutoServiceTest {
	
	@Test
	public void shouldBeAbleToAddAndRetrieveAProduct() {
		
		def expectedName = "Playstaxion2"
		def expectedPrice = 1001
		def expectedCategory = "playstation"
		
		def produtoService = new ProdutoService(new ProdutoRepositoryInMemory())
		
		def produtoIdCreated = produtoService.createProduto(expectedName, expectedPrice, expectedCategory)
		
		def produto = produtoService.getProdutoById(produtoIdCreated)		
		
		assert expectedName == produto.getName()
		assert expectedPrice == produto.getPrice()
		assert expectedCategory == produto.getCategory()
		
	}
	
	@Test
	public void shouldBeAbleToAddAndRetrieveAllProducts() {		
		
		def expectedName = "Playstaxion2"
		def expectedPrice = 1001
		def exepectedNumberOfProducts = 1
		def expectedCategory = "playstation"
		
		def produtoService = new ProdutoService(new ProdutoRepositoryInMemory())
		
		produtoService.removeAll()
		
		def produtoIdCreated = produtoService.createProduto(expectedName, expectedPrice, expectedCategory)
		
		def produtos = produtoService.getAllProdutos()		
		
		def produto = produtos.find{it.id == produtoIdCreated}
		
		assert exepectedNumberOfProducts == produtos.size()
		assert expectedName == produto.getName()
		assert expectedPrice == produto.getPrice()
		assert expectedCategory == produto.getCategory()
	}
	

}
