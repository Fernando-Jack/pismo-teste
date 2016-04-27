package br.com.pismo.compra.service

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future;
import io.vertx.core.json.Json
import br.com.pismo.compra.entity.Compra
import br.com.pismo.compra.integration.InventarioIntegration
import br.com.pismo.compra.repository.CompraRepository


class CompraService extends AbstractVerticle {

	def static public final String CREATE 				= "pismo.api.products.create.compra"
	def static public final String GET_BY_ID 			= "pismo.api.products.getById.compra"
	def static public final String VERTICLE_ADDRESS 	= "groovy:br.com.pismo.compra.service.CompraService"

	def repository
	def inventario

	public CompraService(
	CompraRepository repository,
	InventarioIntegration inventario
	){
		this.repository = repository
		this.inventario = inventario
	}

	@Override
	public void start(Future<Void> fut){
		vertx.eventBus().consumer(CREATE, { message ->
			def body =  message.body()
			def id = this.comprar(
						body.getInteger("productId"),
						body.getInteger("userId"),
						body.getInteger("price"), {result ->
							def response = Json.encodePrettily(result)
							message.reply(response)
						}
					)
		})

		vertx.eventBus().consumer(GET_BY_ID, { message ->
			def body =  message.body()

			def id = body.toInteger()

			this.getCompraById(id,{result ->
				def response

				if(result){
					response = Json.encodePrettily(result)
					message.reply(response)
				}

				if(!response)
					message.reply("-1")
			})
		})
		
		fut.complete()
	}

	def int comprar(productId, userId, price, nextHandler){
		inventario.getAvailableItem(productId,{ result ->
			repository.createCompra(result, userId, price, nextHandler)
		})
		return 0
	}

	def Compra getCompraById(id, nextHandler){
		repository.getCompraById(id, nextHandler)
	}
}
