package br.com.pismo.compra.integration

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import io.vertx.core.http.HttpHeaders
 
class InventarioRestIntegration implements InventarioIntegration {

	private client
	private tokenKey

	public InventarioRestIntegration(client){
		this.client = client
	}

	@Override
	def int getAvailableItem(productId, handlerCompra){

		//TODO:FH remove hardcoded. create config-file for end-points		
		final def produtoAPI = "/api/v1/produto"

		def jsonSlurper = new JsonSlurper()

		def inventoryConsumeResponseHandler = { response ->
			response.bodyHandler({ body ->
				def inventory = jsonSlurper.parseText(body.toString())
				handlerCompra(inventory.id)
			})
		}

		def consumeItemFromInventory = { category ->
			def apiInventario = produtoAPI + "/" + category + "/inventario"
			client.put(apiInventario)
			.putHeader(HttpHeaders.AUTHORIZATION, tokenKey)
					.handler(inventoryConsumeResponseHandler).end()
		}

		def getProduct = { 
			client.get(produtoAPI + "/" + productId)
			.putHeader(HttpHeaders.AUTHORIZATION, tokenKey)
					.handler({ response ->
						response.bodyHandler({body ->
							def product = jsonSlurper.parseText(body.toString())
							consumeItemFromInventory(product.category)
						})
					}).end()
		}
		
		//TODO:FH insert http client already configured port and host.
		authenticateHttpClient(client, "/api/v1/produto/login",{token ->
			tokenKey = ("Bearer " + token)
			getProduct()
		})

		return 0
	}

	def authenticateHttpClient(httpClient, api ,tokenHandler){
		//TODO:FH move hardcoded. create a user api auth
		//TODO:FH remove hardcoded. create config-file for login info
		def userName = 'admin'
		def password = '123'
		def jsonAuth =  JsonOutput.toJson([username: userName, password: password]).toString()		
		
		httpClient.post(api)
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
