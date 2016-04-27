package br.com.pismo.test.compra

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import spock.lang.Specification
import spock.lang.Stepwise
import spock.util.concurrent.AsyncConditions
import br.com.pismo.compra.integration.InventarioRestIntegration


@Stepwise
class InventoryIntegrationSpecx extends Specification {

	private Vertx vertx
	private int port
	def clientHttp
	def jdbc

	def setup(){
		port = 8080
		vertx = Vertx.vertx()
		clientHttp = vertx.createHttpClient()
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


	def "should consume a item from the inventory through the integration"() {
		given: "A product info to be created"
		final def productName = "Playstation"
		final def productPrice = 2000
		final def productCategory = "playstation"

		and: "The product rest URI path"
		final def host = "localhost"
		final def api = "/api/v1/produto"

		and: "The product request header info"
		final jsonProductToBeCreated = "{\"name\":\"Playstation\",\"price\":2000,\"category\":\"playstation\"}"
		final String lengthJsonProduct = Integer.toString(jsonProductToBeCreated.length())

		and: "The item request header info"
		final String jsonInvetotyToBeCreated = "{\"category\":\"playstation\"}"
		final String lengthJsonInvetory = Integer.toString(jsonInvetotyToBeCreated.length())

		and: "A InventarioRestIntegraion"
		def integration = new InventarioRestIntegration(vertx)

		and:
		def conditions = new AsyncConditions(5)
		def createdProduct = null
		def responseCreateProduct = null
		def createdItem = null
		def responseCreateItem = null
		def consumedItemId

		def inventoryResponseHandler = { response ->
			responseCreateItem = response
			response.bodyHandler({ body ->
				createdItem = new JsonSlurper().parseText(body.toString())
				integration.getAvailableItem(createdProduct.id,{ result -> consumedItemId = result })
			})
		}

		def addItemToInventory = {
			def apiInventario = api + "/" + createdProduct.id + "/inventario"
			clientHttp.post(port, host, apiInventario)
					.putHeader("content-type", "application/json")
					.putHeader("content-length", lengthJsonInvetory)
					.handler(inventoryResponseHandler)
					.write(jsonInvetotyToBeCreated).end()
		}

		def createResponseHandler = { response ->
			responseCreateProduct = response
			response.bodyHandler({ body ->
				createdProduct = new JsonSlurper().parseText(body.toString())
				addItemToInventory()
			})
		}

		when: "A product is created and an item is add to the inventory of the product"
		clientHttp.post(port, host, api)
				.putHeader("content-type", "application/json")
				.putHeader("content-length", lengthJsonProduct)
				.handler(createResponseHandler).write(jsonProductToBeCreated).end()

		and: "Await for results"
		sleep(1000)


		then: "Verify if returned id is valid"
		assert consumedItemId != null
		assert consumedItemId != "-1"
		assert consumedItemId.toInteger()
	}
}
