package br.com.pismo.teste

import groovy.json.JsonBuilder
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import spock.lang.Specification
import spock.lang.Stepwise
import br.com.pismo.produto.repository.ProdutoRepositoryJDBCSQL
import br.com.pismo.produto.service.ProdutoService


@Stepwise
class ProdutoRespositoryJDBCSQLSpec extends Specification {

	private Vertx vertx
	private int port
	def clientHttp
	def jdbc

	def setup(){
		vertx = Vertx.vertx()
		def json = new JsonBuilder()

		json{
			url "jdbc:hsqldb:file:db/produto"
			driver_class "org.hsqldb.jdbcDriver"
		}

		def config = new JsonObject(json.toString())
		jdbc = JDBCClient.createShared(vertx, config, "produtos-api")
		sleep(1000)
	}

	def cleanup(){
		vertx.close()
	}


	def "should add and retrieve a product"() {

		given: "A product info to be used to create a product"
		def expectedName = "Playstaxion2"
		def expectedPrice = 1001
		def expectedCategory = "playstation"

		and: "A new productService"
		def produtoService = new ProdutoService(new ProdutoRepositoryJDBCSQL(jdbc))
		sleep(1000)

		when: "A product is created and the same product is retrieved"
		def produtoIdCreated
		produtoService.createProduto(expectedName, expectedPrice, expectedCategory, {result -> produtoIdCreated = result.getId()})
		sleep(200)

		def produto
		produtoService.getProdutoById(produtoIdCreated,{result -> produto = result})
		sleep(200)

		then: "The retrieved product info must be same info used to create de product"
		assert expectedName == produto.getName()
		assert expectedPrice == produto.getPrice()
		assert expectedCategory == produto.getCategory()
	}


	def "shoul add and get all products"() {

		given: "A product info to be used to create a product"
		def expectedName = "Playstaxion2"
		def expectedPrice = 1001
		def exepectedNumberOfProducts = 1
		def expectedCategory = "playstation"

		and: "A new empty productService"
		def produtoService = new ProdutoService(new ProdutoRepositoryJDBCSQL(jdbc))
		sleep(500)

		when: "A new product is created and the same product is retrieved"
		def produtoIdCreated
		produtoService.createProduto(expectedName, expectedPrice, expectedCategory, {result -> produtoIdCreated = result.getId()})
		sleep(200)
		def produtos
		produtoService.getAllProdutos({result -> produtos = result})
		sleep(200)

		then: "The number of products save should be equals 1"
		assert exepectedNumberOfProducts == produtos.size()

		and: "The retrieved product info must be same info used to create de product"
		assert expectedName == produtos[0].getName()
		assert expectedPrice == produtos[0].getPrice()
		assert expectedCategory == produtos[0].getCategory()
	}

	def "shoul add and delete a product"() {

		given: "A product info to be used to create a product"
		def expectedName = "Playstaxion2"
		def expectedPrice = 1001
		def exepectedNumberOfProducts = 0
		def expectedCategory = "playstation"

		and: "A new empty productService"
		def produtoService = new ProdutoService(new ProdutoRepositoryJDBCSQL(jdbc))
		sleep(500)

		when: "A new product is created and the same product is deleted"
		def produtoIdCreated
		produtoService.createProduto(expectedName, expectedPrice, expectedCategory, {result -> produtoIdCreated = result.getId()})
		sleep(200)
		produtoService.removeById(produtoIdCreated,{})
		sleep(200)
		def produtos
		produtoService.getAllProdutos({result -> produtos = result})
		sleep(200)

		then: "The number of products save should be equals 0"
		assert exepectedNumberOfProducts == produtos.size()
	}

	def "shoul add and update a product"() {

		given: "A product info to be used to create a product"
		def createdName = "Playstaxion2"
		def createdPrice = 1001
		def createdCategory = "playstation"

		and: "A product info to be updated"
		def updateName = "Playstaxion22"
		def updatePrice = 1002
		def updateCategory = "playstation22"

		and: "A new empty productService"
		def produtoService = new ProdutoService(new ProdutoRepositoryJDBCSQL(jdbc))
		sleep(500)

		when: "A new product is created"
		def produtoCreated
		produtoService.createProduto(createdName, createdPrice, createdCategory, {result -> produtoCreated = result})
		sleep(200)

		and: "The product name is changed"
		produtoCreated.setName(updateName)
		produtoCreated.setPrice(updatePrice)
		produtoCreated.setCategory(updateCategory)

		and: "The product is updated"
		produtoService.update(produtoCreated, {result ->})
		sleep(200)
		
		and: "The product is retrived"
		def produtoUpdated
		produtoService.getProdutoById(produtoCreated.getId(),{result -> produtoUpdated = result})
		sleep(200)

		then: "The retrieved product info must be same info used to create de product"
		assert produtoCreated.getId() == produtoUpdated.getId()
		assert updateName == produtoUpdated.getName()
		assert updatePrice == produtoUpdated.getPrice()
		assert updateCategory == produtoUpdated.getCategory()
	}
}
