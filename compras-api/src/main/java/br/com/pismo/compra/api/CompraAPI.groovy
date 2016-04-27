package br.com.pismo.compra.api

import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.Json
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import br.com.pismo.compra.entity.Compra
import br.com.pismo.compra.service.CompraService


class CompraAPI {

	private compraService
	private router
	private eventBus

	def CompraAPI(Router router, EventBus eventBus){
		this.router = router
		this.eventBus = eventBus
	}

	def	registerInRouter(Router router){
		router.route('/api/v1/compra*').handler(BodyHandler.create())
		router.get('/api/v1/compra/:id').handler(this.&getOne)
		router.post('/api/v1/compra/').handler(this.&addOne)
	}

	/**
	 * @api {post} /compra 
	 * @apiGroup Compra
	 *
	 * 
	 * @apiSuccess {String} status Compra efetuada
	 * 
	 */
	def private void addOne(RoutingContext routingContext) {

		def json = routingContext.getBodyAsJson()

		eventBus.send(CompraService.CREATE, json, {result->
			if (result.succeeded()) {
				final String returnJson = result.result().body()

				routingContext.response()
						.setStatusCode(201)
						.putHeader("content-type", "application/json; charset=utf-8")
						.putHeader("content-length", Integer.toString(returnJson.length()))
						.end(returnJson)
			}else{
				routingContext.response()
						.setStatusCode(500)
						.end(result.cause().toString())
			}
		})
	}

	/**
	 * @api {get} /compra/:id 
	 * @apiGroup Compra
	 * 
	 * @apiParam {Number} id compra unique ID.
	 * 
	 * @apiSuccess {String} status Compra retornada
	 * 
	 */
	def private void getOne(RoutingContext routingContext) {

		final String id = routingContext.request().getParam("id")

		if (id == null) {
			routingContext.response().setStatusCode(400).end()
			return
		}

		final Integer idAsInteger = Integer.valueOf(id)

		eventBus.send(CompraService.GET_BY_ID, idAsInteger, {result->
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
