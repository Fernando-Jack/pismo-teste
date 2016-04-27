package br.com.pismo.compra.integration

import groovy.json.JsonSlurper
import io.vertx.core.Vertx

class InventarioRestIntegration implements InventarioIntegration {

	def client

	public InventarioRestIntegration(client){
		this.client = client
	}

	@Override
	def int getAvailableItem(productId, handlerCompra){

		final def host = "https://verticle-produto.herokuapp.com"
		final def produtoAPI = "/api/v1/produto"
		final def port = 443		

		def jsonSlurper = new JsonSlurper()

		def inventoryConsumeResponseHandler = { response ->			
			response.bodyHandler({ body ->				
				def inventory = jsonSlurper.parseText(body.toString())
				handlerCompra(inventory.id)
			})
		}

		def consumeItemFromInventory = { category ->
			def apiInventario = produtoAPI + "/" + category + "/inventario"
			client.put(port, host, apiInventario)
					.handler(inventoryConsumeResponseHandler).end()
		}

		client.get(port, host, produtoAPI + "/" + productId)
				.handler({ response ->					
					response.bodyHandler({body ->
						def product = jsonSlurper.parseText(body.toString())
						consumeItemFromInventory(product.category)
					})
				}).end()

		return 0
	}
}
