package br.com.pismo.teste

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import spock.lang.Specification
import spock.util.concurrent.AsyncConditions
import br.com.pismo.produto.AppServer
import br.com.pismo.produto.entity.InventarioItem
import br.com.pismo.produto.entity.Produto

public class InventarioVerticleAPISpec extends Specification{

	private vertx
	private port
	private clientHttp
	private tokenKey

	def setup(){
		vertx = Vertx.vertx()
		ServerSocket socket = new ServerSocket(0)
		port = socket.getLocalPort()
		socket.close()
		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port))
		vertx.deployVerticle(AppServer.class.getName(), options)
		clientHttp = vertx.createHttpClient()		
		sleep(500)
		authenticateHttpClient(clientHttp, port, "localhost", "/api/v1/produto/login",{token -> tokenKey = ("Bearer " + token)})
		sleep(500)
	}

	def cleanup(){
		vertx.close()
	}

	def "should add an item to the inventory" () {

		given: "A product info to be created"
		final def productName = "Playstation"
		final def productPrice = 2000
		final def productCategory = "playstation"

		and: "The product rest URI path"
		final def host = "localhost"
		final def api = "/api/v1/produto"

		and: "The product request header info"
		final String jsonProductToBeCreated = Json.encodePrettily(new Produto(productName, productPrice, productCategory))
		final String lengthJsonProduct = Integer.toString(jsonProductToBeCreated.length())

		and: "The item request header info"
		final String jsonInvetotyToBeCreated = Json.encodePrettily(new InventarioItem(productCategory))
		final String lengthJsonInvetory = Integer.toString(jsonInvetotyToBeCreated.length())

		and:
		def conditions = new AsyncConditions(4)
		def createdProduct = null
		def responseCreateProduct = null
		def createdItem = null
		def responseCreateItem = null

		def inventoryResponseHandler = { response ->
			conditions.evaluate{
				responseCreateItem = response
				response.bodyHandler({ body ->
					conditions.evaluate{
						createdItem = Json.decodeValue(body.toString(), InventarioItem.class)
					}
				})
			}
		}

		def addItemToInventory = {
			def apiInventario = api + "/" + createdProduct.getId() + "/inventario"
			clientHttp.post(port, host, apiInventario)
					.putHeader("content-type", "application/json")
					.putHeader("content-length", lengthJsonInvetory)
					.putHeader(HttpHeaders.AUTHORIZATION, tokenKey)
					.handler(inventoryResponseHandler)
					.write(jsonInvetotyToBeCreated).end()
		}

		def createResponseHandler = { response ->
			conditions.evaluate{
				responseCreateProduct = response
				response.bodyHandler({ body ->
					conditions.evaluate{
						createdProduct = Json.decodeValue(body.toString(), Produto.class)
						addItemToInventory()
					}
				})
			}
		}

		when: "A product is created and an item is add to the inventory of the product"
		clientHttp.post(port, host, api)
				.putHeader("content-type", "application/json")
				.putHeader("content-length", lengthJsonProduct)
				.putHeader(HttpHeaders.AUTHORIZATION, tokenKey)
				.handler(createResponseHandler).write(jsonProductToBeCreated).end()

		and: "Await for results"
		conditions.await()

		then:"The post product response should have the content-type as json and status as 201"
		assert responseCreateProduct.statusCode() == 201
		assert responseCreateProduct.headers().get("content-type").contains("application/json") == true

		and:"The post inventory response should have the content-type as json and status as 201"
		assert responseCreateItem.statusCode() == 201
		assert responseCreateItem.headers().get("content-type").contains("application/json") == true

		and: "The created item info should be equals to the one created"
		assert createdItem.getCategory() == productCategory
		assert createdItem.getId() != null
	}

	def "should consume a item from inventory"(){

		given: "A product info to be created"
		final def productName = "Playstation"
		final def productPrice = 2000
		final def productCategory = "playstation"

		and: "The product rest URI path"
		final def host = "localhost"
		final def api = "/api/v1/produto"

		and: "The product request header info"
		final String jsonProductToBeCreated = Json.encodePrettily(new Produto(productName, productPrice, productCategory))
		final String lengthJsonProduct = Integer.toString(jsonProductToBeCreated.length())

		and: "The item request header info"
		final String jsonInvetotyToBeCreated = Json.encodePrettily(new InventarioItem(productCategory))
		final String lengthJsonInvetory = Integer.toString(jsonInvetotyToBeCreated.length())

		and:
		def conditions = new AsyncConditions(6)
		def createdProduct = null
		def responseCreateProduct = null
		def createdItem = null
		def responseCreateItem = null
		def responseConsumedItem = null
		def consumedItem = null

		def inventoryConsumeResponseHandler = { response ->
			conditions.evaluate{
				responseConsumedItem = response
				response.bodyHandler({ body ->
					conditions.evaluate{
						def jsonSlurper = new JsonSlurper()
						consumedItem = jsonSlurper.parseText(body.toString())
					}
				})
			}
		}
		def consumeItemFromInventory = {
			def apiInventario = api + "/" + createdProduct.getCategory() + "/inventario"
			clientHttp.put(port, host, apiInventario)
					.putHeader(HttpHeaders.AUTHORIZATION, tokenKey)
					.handler(inventoryConsumeResponseHandler).end()
		}

		def inventoryResponseHandler = { response ->
			conditions.evaluate{
				responseCreateItem = response
				response.bodyHandler({ body ->
					conditions.evaluate{
						createdItem = Json.decodeValue(body.toString(), InventarioItem.class)
						consumeItemFromInventory()
					}
				})
			}
		}

		def addItemToInventory = {
			def apiInventario = api + "/" + createdProduct.getId() + "/inventario"
			clientHttp.post(port, host, apiInventario)
					.putHeader("content-type", "application/json")
					.putHeader("content-length", lengthJsonInvetory)
					.handler(inventoryResponseHandler)
					.putHeader(HttpHeaders.AUTHORIZATION, tokenKey)
					.write(jsonInvetotyToBeCreated).end()
		}

		def createResponseHandler = { response ->
			conditions.evaluate{
				responseCreateProduct = response
				response.bodyHandler({ body ->
					conditions.evaluate{
						createdProduct = Json.decodeValue(body.toString(), Produto.class)
						addItemToInventory()
					}
				})
			}
		}

		when: "A product is created. An item is add to the inventory of the product. And the item is consumed"
		clientHttp.post(port, host, api)
				.putHeader("content-type", "application/json")
				.putHeader("content-length", lengthJsonProduct)
				.putHeader(HttpHeaders.AUTHORIZATION, tokenKey)
				.handler(createResponseHandler).write(jsonProductToBeCreated).end()

		and: "Await for results"
		conditions.await()

		then:"The post product response should have the content-type as json and status as 201"
		assert responseCreateProduct.statusCode() == 201
		assert responseCreateProduct.headers().get("content-type").contains("application/json") == true

		and:"The post inventory response should have the content-type as json and status as 201"
		assert responseCreateItem.statusCode() == 201
		assert responseCreateItem.headers().get("content-type").contains("application/json") == true

		and: "The created item info should be equals to the one created"
		assert createdItem.getCategory() == productCategory
		assert createdItem.getId() != null

		and:"The consumed inventory response should have the content-type as json and status as 200"
		assert responseConsumedItem.statusCode() == 200
		assert responseConsumedItem.headers().get("content-type").contains("application/json") == true

		and: "The consumed item should return valid id"
		assert consumedItem.id != -1
		assert consumedItem.id != null
		assert consumedItem.id instanceof Integer

		and: "The consumed item should has the same category of the created item"
		assert consumedItem.category == createdProduct.getCategory()
	}

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
}