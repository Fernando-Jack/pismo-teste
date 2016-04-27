package br.com.pismo.teste

import spock.lang.Specification
import br.com.pismo.produto.repository.ProdutoRepositoryInMemory
import br.com.pismo.produto.service.ProdutoService

class ProdutoServiceSpec extends Specification {
	
	
	def "should add and retrieve a product"() {
		
		given: "A product info to be used to create a product"
			def expectedName = "Playstaxion2"
			def expectedPrice = 1001
			def expectedCategory = "playstation"
		
		and: "A new productService"
			def produtoService = new ProdutoService(new ProdutoRepositoryInMemory())
		
		when: "A product is created and the same product is retrieved"
			def produtoIdCreated = produtoService.createProduto(expectedName, expectedPrice, expectedCategory, {})		
			def produto = produtoService.getProdutoById(produtoIdCreated, {})		
		
		then: "The retrieved product info must be same info used to create de product"
			assert expectedName == produto.getName()
			assert expectedPrice == produto.getPrice()
			assert expectedCategory == produto.getCategory()
		
	}
	
	
	def "shoul add and retrieve all products"() {		
		
		given: "A product info to be used to create a product"
			def expectedName = "Playstaxion2"
			def expectedPrice = 1001
			def exepectedNumberOfProducts = 1
			def expectedCategory = "playstation"
		
		and: "A new empty productService"
			def produtoService = new ProdutoService(new ProdutoRepositoryInMemory())			
			produtoService.removeAll()
			
		when: "A new product is created and the same product is retrieved"
			def produtoIdCreated = produtoService.createProduto(expectedName, expectedPrice, expectedCategory, {})			
			def produtos = produtoService.getAllProdutos({})			
			def produto = produtos.find{it.id == produtoIdCreated}
			
		then: "The number of products save should be equals 1"
			assert exepectedNumberOfProducts == produtos.size()
		
		and: "The retrieved product info must be same info used to create de product"
			assert expectedName == produto.getName()
			assert expectedPrice == produto.getPrice()
			assert expectedCategory == produto.getCategory()
	}
	

}
