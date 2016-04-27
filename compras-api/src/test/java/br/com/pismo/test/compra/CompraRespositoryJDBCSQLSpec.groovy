package br.com.pismo.test.compra

import groovy.json.JsonBuilder
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import spock.lang.Specification
import spock.lang.Stepwise
import br.com.pismo.compra.integration.InventarioRestIntegration
import br.com.pismo.compra.repository.CompraRepositoryJDBCSQL
import br.com.pismo.compra.service.CompraService


@Stepwise
class CompraRespositoryJDBCSQLSpec extends Specification {

	private Vertx vertx
	private int port
	def clientHttp
	def jdbc

	def setup(){
		vertx = Vertx.vertx()
		def config = dataBaseConfig()
		jdbc = JDBCClient.createShared(vertx, config, "compra-api")
		sleep(1000)
	}

	private JsonObject dataBaseConfig() {
		def json = new JsonBuilder()

		json{
			url "jdbc:hsqldb:file:db/compra"
			driver_class "org.hsqldb.jdbcDriver"
		}

		def config = new JsonObject(json.toString())
		return config
	}

	def cleanup(){
		vertx.close()
	}


	def "should add and retrieve a compra"() {

		given: "A product info to be used to create a product"
		def expectedUserId = 10
		def expectedItemId = 20
		def expectedPrice = 1001
		def expectedProductId = 100

		and: "A mock InvetarioRestIntegration"
		def integration = Mock(InventarioRestIntegration)					
		1 * integration.getAvailableItem(_, _) >> { productId, callback ->
			callback(expectedItemId)
		}

		and: "A new productService"
		def compraService = new CompraService(
				new CompraRepositoryJDBCSQL(jdbc),
				integration)
		sleep(1000)

		when: "A product is created and the same product is retrieved"
		def compraIdCreated
		compraService.comprar(expectedProductId, expectedUserId, expectedPrice, {result -> compraIdCreated = result.getId()})
		sleep(200)

		def compra
		compraService.getCompraById(compraIdCreated,{result -> compra = result})
		sleep(200)		

		then: "The retrieved product info must be same info used to create de product"
		assert expectedItemId == compra.getItemId()
		assert expectedPrice == compra.getPrice()
		assert expectedUserId == compra.getUserId()
	}
}
