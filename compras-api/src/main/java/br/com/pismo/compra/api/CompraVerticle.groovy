package br.com.pismo.compra.api

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import br.com.pismo.compra.entity.Compra
import br.com.pismo.compra.service.CompraService


class CompraVerticle {
	
	def private CompraService compraService
	def private router
	
	def ComprasVerticle(
			Router router,
			CompraService  compraService
		){
		this.router = router
		this.compraService = compraService
	}
		
	def	registerInRouter(Router router){
		router.route('/api/v1/compra*').handler(BodyHandler.create())
		router.get('/api/v1/compra/:id').handler(this.&getAll)
		router.post('/api/v1/compra/:id').handler(this.&addOne)		
	}

	def private void addOne(RoutingContext routingContext) {
		JsonObject json = routingContext.getBodyAsJson()
		
		def id = compraService.comprar(json.getString("productId"), json.getInteger("price"), json.getString("userId"))
		final Compra compra = compraService.getCompraById(id)
		
		final String returnJson = Json.encodePrettily(compra)
		
		routingContext.response()
				.setStatusCode(201)
				.putHeader("content-type", "application/json; charset=utf-8")
				.putHeader("content-length", Integer.toString(returnJson.length()))
				.end(returnJson);
	}

	def private void getOne(RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id")
		if (id == null) {
			routingContext.response().setStatusCode(400).end()
		} else {
			final Integer idAsInteger = Integer.valueOf(id)
			Compra produto = compraService.getCompraById(idAsInteger)
			if (produto == null) {
				routingContext.response().setStatusCode(404).end()
			} else {
				routingContext.response()
						.putHeader("content-type", "application/json;  charset=utf-8")
						.end(Json.encodePrettily(produto))
			}
		}
	}

}
