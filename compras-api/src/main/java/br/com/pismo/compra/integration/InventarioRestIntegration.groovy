package br.com.pismo.compra.integration

import groovy.json.JsonSlurper
import io.vertx.core.Vertx

class InventarioRestIntegration implements InventarioIntegration {

	def vertx

	public InventarioRestIntegration(Vertx vertx){
		this.vertx = vertx
	}

	@Override
	def int getAvailableItem(productId, handlerCompra){

		final def host = "localhost"
		final def produtoAPI = "/api/v1/produto"
		final def port = 8080

		def inventoryItemId = -2;

		def jsonSlurper = new JsonSlurper()

		def inventoryConsumeResponseHandler = { response ->
			response.bodyHandler({ body ->
				def inventory = jsonSlurper.parseText(body.toString())
				inventoryItemId = inventory.id		
				print inventory.id
				handlerCompra(inventory.id)
			})
		}

		def consumeItemFromInventory = { category ->
			def apiInventario = produtoAPI + "/" + category + "/inventario"
			vertx.createHttpClient().put(port, host, apiInventario)
					.handler(inventoryConsumeResponseHandler).end()
		}

		vertx.createHttpClient().get(port, host, produtoAPI + "/" + productId)
				.handler({ response ->
					response.bodyHandler({body ->
						def product = jsonSlurper.parseText(body.toString())
						consumeItemFromInventory(product.category)
					})
				}).end()

		def client = vertx.createHttpClient()

		// Specify both port and host name
		client.getNow(8080, "myserver.mycompany.com", "/some-uri", { response ->
			println("Received response with status code ${response.statusCode()}")
		})

		return inventoryItemId
	}
}
