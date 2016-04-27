package br.com.pismo.produto.api

import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import br.com.pismo.produto.entity.InventarioItem
import br.com.pismo.produto.service.InventarioService


class InventarioAPI {

	private InventarioService inventarioService
	private router
	private eventBus

	def InventarioAPI(
	EventBus eventBus,
	Router router,
	InventarioService  inventarioService
	){
		this.eventBus = eventBus
		this.router = router
		this.inventarioService = inventarioService
	}

	def	registerInRouter(Router router){
		//TODO:FH Implementar getOne para inventario
		router.get('/api/v1/produto/:produto/inventario').handler(this.&getAll)
		router.post('/api/v1/produto/:produto/inventario').handler(this.&addOne)
		router.put('/api/v1/produto/:produto/inventario').handler(this.&consumeOne)
	}

	/**
	 * @api {get} /produto/:produto-id/inventario
	 * @apiGroup Inventario
	 *
	 * @apiParam {Number} produto unique ID.
	 *
	 * @apiSuccess {String} status Todo o inventario disponível retornado
	 *
	 */
	def private void getAll(RoutingContext routingContext) {
		eventBus.send(inventarioService.GET_ALL, "", {result->
			if (result.succeeded()) {
				routingContext.response()
						.putHeader("content-type", "application/json;  charset=utf-8")
						.setStatusCode(200)
						.end(result.result().body())
			}else{
				routingContext.response()
						.setStatusCode(500)
						.end(result.cause().toString())
			}
		})
	}

	/**
	 * @api {post} /produto/:produto-id/inventario
	 * @apiGroup Inventario
	 *
	 * @apiParam {Number} produto unique ID.
	 *
	 * @apiSuccess {String} status Adiciona um item ao inventario do produto
	 *
	 */
	def private void addOne(RoutingContext routingContext) {

		JsonObject json = routingContext.getBodyAsJson()

		eventBus.send(inventarioService.CREATE, json, {result->
			if (result.succeeded()) {
				final String returnJson = result.result().body()

				routingContext.response()
						.setStatusCode(201)
						.putHeader("content-type", "application/json; charset=utf-8")
						.putHeader("content-length", returnJson.length().toString())
						.end(returnJson);
			}else{
				routingContext.response()
						.setStatusCode(500)
						.end(result.cause().toString())
			}
		})
	}
	
	/**
	 * @api {put} /produto/:produto-id/inventario
	 * @apiGroup Inventario
	 *
	 * @apiParam {Number} produto unique ID.
	 *
	 * @apiSuccess {String} status Consome um item do inventario do produto
	 *
	 */
	def private void consumeOne(RoutingContext routingContext) {
		def category = routingContext.request().getParam("produto")

		eventBus.send(inventarioService.CONSUME, category, {result->
			if (result.succeeded()) {
				final String returnJson = result.result().body()

				if (returnJson && !returnJson.allWhitespace && returnJson != "-1"){
					routingContext.response()
							.putHeader("content-type", "application/json; charset=utf-8")
							.putHeader("content-length", Integer.toString(returnJson.length()))
							.end(returnJson)
				} else {
					routingContext.response().setStatusCode(404).end()
				}
			}else{
				routingContext.response()
						.setStatusCode(500)
						.end(result.cause().toString())
			}
		})
	}
}
