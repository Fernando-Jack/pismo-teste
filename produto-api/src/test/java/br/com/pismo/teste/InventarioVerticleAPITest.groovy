package br.com.pismo.teste 

import groovy.json.JsonSlurper
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.unit.Async
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import br.com.pismo.produto.AppServer
import br.com.pismo.produto.entity.InventarioItem
import br.com.pismo.produto.entity.Produto

@RunWith(VertxUnitRunner.class)
public class InventarioVerticleAPITest {

	private Vertx vertx
	private int port

	@Before
	public void setUp(TestContext context) throws IOException {
		vertx = Vertx.vertx()
		ServerSocket socket = new ServerSocket(0)
		port = socket.getLocalPort()
		socket.close()
		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port))
		vertx.deployVerticle(AppServer.class.getName(), options, context.asyncAssertSuccess())
	}

	@After
	public void tearDown(TestContext context) {
		vertx.close(context.asyncAssertSuccess())
	}

	@Test
	public void shouldBeAbleToAddItemToInventario(TestContext context) {

		Async async = context.async()

		final def host = "localhost"
		final def api = "/api/v1/produto"

		final def productName = "Playstation"
		final def productPrice = 2000
		final def productCategory = "playstation"

		final String jsonProductToBeCreated = Json.encodePrettily(new Produto(productName, productPrice, productCategory))
		final String lengthJsonProduct = Integer.toString(jsonProductToBeCreated.length())
		
		final String jsonInvetotyToBeCreated = Json.encodePrettily(new InventarioItem(productCategory))
		final String lengthJsonInvetory = Integer.toString(jsonInvetotyToBeCreated.length())
		
		Produto produto;
		
		def inventoryResponseHandler = { response ->
			context.assertEquals(response.statusCode(), 201)
			context.assertTrue(response.headers().get("content-type").contains("application/json"))
			response.bodyHandler({ body ->
				InventarioItem item = Json.decodeValue(body.toString(), InventarioItem.class)				
				context.assertEquals(item.getCategory(), productCategory)
				context.assertNotNull(item.getId())
				async.complete()
			})
		}

		def addItemToInventory = {
			def apiInventario = api + "/" + produto.getId() + "/inventario"
			vertx.createHttpClient().post(port, host, apiInventario)
				.putHeader("content-type", "application/json")
				.putHeader("content-length", lengthJsonInvetory)
				.handler(inventoryResponseHandler).write(jsonInvetotyToBeCreated).end()
		}

		def productResponseHandler = { response ->
			context.assertEquals(response.statusCode(), 201)
			context.assertTrue(response.headers().get("content-type").contains("application/json"))
			response.bodyHandler({ body ->
				produto = Json.decodeValue(body.toString(), Produto.class)
				context.assertEquals(produto.getName(), productName)
				context.assertEquals(produto.getPrice(), productPrice)
				context.assertEquals(produto.getCategory(), productCategory)
				context.assertNotNull(produto.getId())
				addItemToInventory()
			}) 
		}

		vertx.createHttpClient().post(port, host, api)
			.putHeader("content-type", "application/json")
			.putHeader("content-length", lengthJsonProduct)
			.handler(productResponseHandler).write(jsonProductToBeCreated).end()
	}
	
	@Test
	public void shouldBeAbleToConsumeItemFromInventario(TestContext context) {

		Async async = context.async()

		final def host = "localhost"
		final def api = "/api/v1/produto"

		final def productName = "Playstation"
		final def productPrice = 2000
		final def productCategory = "playstation"

		final String jsonProductToBeCreated = Json.encodePrettily(new Produto(productName, productPrice, productCategory))
		final String lengthJsonProduct = Integer.toString(jsonProductToBeCreated.length())
		
		final String jsonInvetotyToBeCreated = Json.encodePrettily(new InventarioItem(productCategory))
		final String lengthJsonInvetory = Integer.toString(jsonInvetotyToBeCreated.length())
		
		Produto produto;		
		
		def inventoryConsumeResponseHandler = { response ->
			context.assertEquals(response.statusCode(), 200)
			context.assertTrue(response.headers().get("content-type").contains("application/json"))
			response.bodyHandler({ body ->								
				def jsonSlurper = new JsonSlurper()
				def object = jsonSlurper.parseText(body.toString())				
				context.assertNotEquals(object.id, -1)
				context.assertNotNull(object.id)
				context.assertTrue(object.id instanceof Integer)
				async.complete()
			})
		}
		
		def consumeItemFromInventory = {			
			def apiInventario = api + "/" + produto.getCategory() + "/inventario"
			vertx.createHttpClient().put(port, host, apiInventario)			
				.handler(inventoryConsumeResponseHandler).end()
		}
		
		def inventoryResponseHandler = { response ->
			context.assertEquals(response.statusCode(), 201)
			context.assertTrue(response.headers().get("content-type").contains("application/json"))
			response.bodyHandler({ body ->
				InventarioItem item = Json.decodeValue(body.toString(), InventarioItem.class)
				context.assertEquals(item.getCategory(), productCategory)
				context.assertNotNull(item.getId())				
				consumeItemFromInventory()
			})
		}

		def addItemToInventory = {
			def apiInventario = api + "/" + produto.getCategory() + "/inventario"
			vertx.createHttpClient().post(port, host, apiInventario)
				.putHeader("content-type", "application/json")
				.putHeader("content-length", lengthJsonInvetory)
				.handler(inventoryResponseHandler).write(jsonInvetotyToBeCreated).end()
		}

		def productResponseHandler = { response ->
			context.assertEquals(response.statusCode(), 201)
			context.assertTrue(response.headers().get("content-type").contains("application/json"))
			response.bodyHandler({ body ->
				produto = Json.decodeValue(body.toString(), Produto.class)
				context.assertEquals(produto.getName(), productName)
				context.assertEquals(produto.getPrice(), productPrice)
				context.assertEquals(produto.getCategory(), productCategory)
				context.assertNotNull(produto.getId())
				addItemToInventory()
			})
		}

		vertx.createHttpClient().post(port, host, api)
			.putHeader("content-type", "application/json")
			.putHeader("content-length", lengthJsonProduct)
			.handler(productResponseHandler).write(jsonProductToBeCreated).end()
	}
}