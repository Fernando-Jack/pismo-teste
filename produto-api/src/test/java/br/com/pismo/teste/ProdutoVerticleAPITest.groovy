package br.com.pismo.teste 

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
import br.com.pismo.produto.entity.Produto

@RunWith(VertxUnitRunner.class)
public class ProdutoVerticleAPITest {

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
	public void shouldBeAbleToAddProduct(TestContext context) {

		Async async = context.async()

		final def host = "localhost"
		final def api = "/api/v1/produto"

		final def productName = "Playstation"
		final def productPrice = 2000
		final def productCategory = "playstation"

		final String jsonProductToBeCreated = Json.encodePrettily(new Produto(productName, productPrice, productCategory))
		final String length = Integer.toString(jsonProductToBeCreated.length())

		def postAssertions = { body ->
			final Produto produto = Json.decodeValue(body.toString(), Produto.class)
			context.assertEquals(produto.getName(), productName)
			context.assertEquals(produto.getPrice(), productPrice)
			context.assertNotNull(produto.getId())
			
			async.complete()
		}

		def responseHandler = { response ->
			context.assertEquals(response.statusCode(), 201)
			context.assertTrue(response.headers().get("content-type").contains("application/json"))
			response.bodyHandler(postAssertions) 
		}

		vertx.createHttpClient().post(port, host, api)
			.putHeader("content-type", "application/json")
			.putHeader("content-length", length)
			.handler(responseHandler).write(jsonProductToBeCreated).end()
	}
	
	@Test
	public void shouldBeAbleToAddAndRemoveProduct(TestContext context) {

		Async async = context.async()

		final def host = "localhost"
		final def api = "/api/v1/produto"

		final def productName = "Playstation"
		final def productPrice = 2000
		final def productCategory = "playstation"

		final String json = Json.encodePrettily(new Produto(productName, productPrice, productCategory))
		final String length = Integer.toString(json.length())
		
		def client = vertx.createHttpClient()
		def createdProduct = null;
		
		def verifyExistenceOfCretedProductRequest = {			
			client.get(port, host, api + "/" + createdProduct.getId())			
			.handler({ response ->
				context.assertEquals(response.statusCode(), 404)	
				async.complete();			
			})
			.end()			
		}
		
		def deleteCreatedProductRequest = {			
			client.delete(port, host, api + "/" + createdProduct.getId())						
			.handler({ response ->
				context.assertEquals(response.statusCode(), 204)
				verifyExistenceOfCretedProductRequest()				
			})
			.end()	
		}		

		def createResponseHandler = { response ->					
			context.assertEquals(response.statusCode(), 201)
			context.assertTrue(response.headers().get("content-type").contains("application/json"))
			response.bodyHandler({ body ->
				createdProduct = Json.decodeValue(body.toString(), Produto.class)
				context.assertEquals(createdProduct.getName(), productName)
				context.assertEquals(createdProduct.getPrice(), productPrice)
				context.assertNotNull(createdProduct.getId())
				
				deleteCreatedProductRequest()
			})			
		}		

		client.post(port, host, api)
			.putHeader("content-type", "application/json")
			.putHeader("content-length", length)
			.handler(createResponseHandler)
			.write(json)			
			.end()
	}
	
	@Test
	public void shouldBeAbleToAddAndUpdateProduct(TestContext context) {

		Async async = context.async()

		final def host = "localhost"
		final def api = "/api/v1/produto"

		final def productName = "Playstation"
		final def productPrice = 2000
		final def productCategory = "playstation"
		
		final def updateProductName = "Playstation2"
		final def updateProductPrice = 3000
		final def updateProductCategory = "playstation2"

		final String json = Json.encodePrettily(new Produto(productName, productPrice, productCategory))
		final String updateJson = Json.encodePrettily(new Produto(updateProductName, updateProductPrice, updateProductCategory))
		
		final String length = Integer.toString(json.length())
		final String updateLength = Integer.toString(updateJson.length())
		
		def client = vertx.createHttpClient()
		def createdProduct = null;	
		
		def updateCreatedProductRequest = {
			client.put(port, host, api + "/" + createdProduct.getId())
			.putHeader("content-type", "application/json")
			.putHeader("content-length", updateLength)
			.handler({ response ->
				context.assertEquals(response.statusCode(), 200)
				context.assertTrue(response.headers().get("content-type").contains("application/json"))
				response.bodyHandler({ body ->					
					Produto updatedProduct = Json.decodeValue(body.toString(), Produto.class)					
					context.assertEquals(updatedProduct.getName(), updateProductName)
					context.assertEquals(updatedProduct.getPrice(), updateProductPrice)
					context.assertEquals(updatedProduct.getId(), createdProduct.getId())
					
					async.complete()
				})
			})
			.write(updateJson)
			.end()
		}

		def createResponseHandler = { response ->
			context.assertEquals(response.statusCode(), 201)
			context.assertTrue(response.headers().get("content-type").contains("application/json"))
			response.bodyHandler({ body ->
				createdProduct = Json.decodeValue(body.toString(), Produto.class)
				context.assertEquals(createdProduct.getName(), productName)
				context.assertEquals(createdProduct.getPrice(), productPrice)
				context.assertNotNull(createdProduct.getId())
				
				updateCreatedProductRequest()
			})
		}

		client.post(port, host, api)
			.putHeader("content-type", "application/json")
			.putHeader("content-length", length)
			.handler(createResponseHandler)
			.write(json)
			.end()
	}
}