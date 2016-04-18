package br.com.pismo.produto.api

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import br.com.pismo.produto.entity.InventarioItem;
import br.com.pismo.produto.entity.Produto
import br.com.pismo.produto.service.InventarioService


class InventarioVerticle {
	
	private InventarioService inventarioService
	private router
	
	def InventarioVerticle(
			Router router,
			InventarioService  inventarioService
		){
		this.router = router
		this.inventarioService = inventarioService
	}
		
	def	registerInRouter(Router router){
		//TODO:FH Implementar getOne para inventario
		//router.get('/api/v1/produto/:produto/inventario/:id').handler(this.&getOne)
		router.post('/api/v1/produto/:produto/inventario').handler(this.&addOne)
		router.put('/api/v1/produto/:produto/inventario').handler(this.&updateOne)
	}

	def private void addOne(RoutingContext routingContext) {
		JsonObject json = routingContext.getBodyAsJson()		
		
		def id = inventarioService.addItem(json.getString("category"))
		final InventarioItem item = inventarioService.getInventarioItemById(id)
		
		final String returnJson = Json.encodePrettily(item)
		
		routingContext.response()
				.setStatusCode(201)
				.putHeader("content-type", "application/json; charset=utf-8")
				.putHeader("content-length", Integer.toString(returnJson.length()))
				.end(returnJson);
	}

	def private void updateOne(RoutingContext routingContext) {
		final String category = routingContext.request().getParam("produto")		

		if (category == null) {
			routingContext.response().setStatusCode(400).end()
		} else {			
			int idItem = inventarioService.consumeInventarioItem(category)
			
			if (idItem == null) {
				routingContext.response().setStatusCode(404).end()
			} else {			
				//TODO:FH implementar retorno do item ao invés de id	
				routingContext.response()
						.putHeader("content-type", "application/json; charset=utf-8")
						.end("{\"id\": " + idItem +"}")
			}
		}
	}

	

}
