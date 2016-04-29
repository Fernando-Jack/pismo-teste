package br.com.pismo.test.compra

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import spock.lang.Specification
import spock.lang.Stepwise
import spock.util.concurrent.AsyncConditions
import br.com.pismo.compra.integration.InventarioRestIntegration


/**
 * 
 * @author jack
 *
 * InventoryIntegrationSpec test the integration between compras and produto API.
 * To run this test is necessary to start produto API otherwise it will fail.
 */
@Stepwise
class InventoryIntegrationSpec extends Specification {

	private vertx
	private productAPIPort = 8080
	private testHttpClient
	private jdbc
	private tokenKey
	private integrationHttpClient

	def setup(){
		//TODO:FH remove hardcoded. create config-file for port
		vertx = Vertx.vertx()
		testHttpClient = vertx.createHttpClient()
		createIntegrationHttpClient()
		sleep(500)
		//TODO:FH remove hardcoded. create config-file for end-point info
		authenticateHttpClient(testHttpClient, productAPIPort, "localhost", "/api/v1/produto/login",{token -> tokenKey = ("Bearer " + token)})
		sleep(500)
	}

	def cleanup(){
		vertx.close()
	}


	def "should consume a item from the inventory through the integration"() {
		given: "A product info to be created"
		final def productName = "Playstation"
		final def productPrice = 2000
		final def productCategory = "playstation"

		//TODO:FH remove hardcoded. create config-file for end-point info
		and: "The product rest URI path"
		final def host = "localhost"
		final def api = "/api/v1/produto"

		and: "The product request header info"
		//TODO:FH chage to use groovy Json
		final jsonProductToBeCreated = '{"name":"Playstation","price":2000,"category":"playstation"}'
		final String lengthJsonProduct = Integer.toString(jsonProductToBeCreated.length())

		and: "The item request header info"
		//TODO:FH chage to use groovy Json
		final String jsonInvetotyToBeCreated = '{"category":"playstation"}'
		final String lengthJsonInvetory = Integer.toString(jsonInvetotyToBeCreated.length())

		and: "A InventarioRestIntegraion"
		def integration = new InventarioRestIntegration(integrationHttpClient)

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
			testHttpClient.post(productAPIPort, host, apiInventario)
					.putHeader("content-type", "application/json")
					.putHeader("content-length", lengthJsonInvetory)
					.putHeader(HttpHeaders.AUTHORIZATION, tokenKey)
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
		testHttpClient.post(productAPIPort, host, api)
				.putHeader("content-type", "application/json")
				.putHeader("content-length", lengthJsonProduct)
				.putHeader(HttpHeaders.AUTHORIZATION, tokenKey)
				.handler(createResponseHandler).write(jsonProductToBeCreated).end()

		and: "Await for results"
		sleep(2000)

		then: "Verify if product returned is valid"
		assert createdProduct.id != null
		assert createdProduct.id != "-1"
		assert createdProduct.id.toInteger()

		and: "Verify if returned item created is valid"
		assert createdItem.id != null
		assert createdItem.id != "-1"
		assert createdItem.id.toInteger()

		and: "Verify if returned item consumed id is valid"
		assert consumedItemId != null
		assert consumedItemId != "-1"
		assert consumedItemId.toInteger()

		and: "Verify if the created item id is the same as from the consumed item"
		assert consumedItemId == createdItem.id
	}

	//TODO:FH create helper to reuse func
	def authenticateHttpClient(httpClient, port, host, api ,tokenHandler){
		def userName = 'admin'
		def password = '123'
		def jsonAuth =  JsonOutput.toJson([username: userName, password: password]).toString()

		httpClient.post(port, host, api)
				.putHeader("content-type", "application/json")
				.putHeader("content-length", jsonAuth.length().toString())
				.handler({ response ->
					response.bodyHandler({ body ->
						def responseToken = body.toString()
						tokenHandler(responseToken)
					})
				})
				.write(jsonAuth)
				.end()
	}

	private createIntegrationHttpClient() {
		def configHttp = new HttpClientOptions()
				.setDefaultHost("localhost")
				.setDefaultPort(8080)
		integrationHttpClient = vertx.createHttpClient(configHttp)
	}

	private JsonObject dataBaseConfig() {
		def json = new JsonBuilder()

		//TODO:FH remove hardcoded. create config-file for connection
		json{
			url "jdbc:hsqldb:file:db/compra"
			driver_class "org.hsqldb.jdbcDriver"
		}

		def config = new JsonObject(json.toString())
		return config
	}
}
