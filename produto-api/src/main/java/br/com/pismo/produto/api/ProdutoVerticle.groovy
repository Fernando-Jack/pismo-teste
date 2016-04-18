package br.com.pismo.produto.api

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import br.com.pismo.produto.entity.Produto
import br.com.pismo.produto.service.ProdutoService

class ProdutoVerticle {
	
	def private produtoService
	def private router
	
	def ProdutoVerticle(
		Router router,
		ProdutoService  produtoService
		){
		this.router = router
		this.produtoService = produtoService
	}
		
	def	registerInRouter(Router router){
		router.route('/api/v1/produto*').handler(BodyHandler.create())
		router.get('/api/v1/produto').handler(this.&getAll)
		router.post('/api/v1/produto').handler(this.&addOne)
		router.delete('/api/v1/produto/:id').handler(this.&deleteOne)
		router.get('/api/v1/produto/:id').handler(this.&getOne)
		router.put('/api/v1/produto/:id').handler(this.&updateOne)
	}
	
	def private void getAll(RoutingContext routingContext) {
		routingContext.response()
				.putHeader("content-type", "application/json;  charset=utf-8")
				.end(Json.encodePrettily(produtoService.getAllProdutos()))
	}

	def private void addOne(RoutingContext routingContext) {
		JsonObject json = routingContext.getBodyAsJson()
		
		def id = produtoService.createProduto(json.getString("name"), json.getInteger("price"), json.getString("category"))
		final Produto produto = produtoService.getProdutoById(id)
		
		final String returnJson = Json.encodePrettily(produto)
		
		routingContext.response()
				.setStatusCode(201)
				.putHeader("content-type", "application/json; charset=utf-8")
				.putHeader("content-length", Integer.toString(returnJson.length()))
				.end(returnJson);
	}

	def private void deleteOne(RoutingContext routingContext) {
		String id = routingContext.request().getParam("id")
		if (id == null) {
			routingContext.response().setStatusCode(400).end()
		} else {
			Integer idAsInteger = Integer.valueOf(id)
			produtoService.removeById(idAsInteger)
		}
		routingContext.response().setStatusCode(204).end()
	}

	def private void getOne(RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id")
		if (id == null) {
			routingContext.response().setStatusCode(400).end()
		} else {
			final Integer idAsInteger = Integer.valueOf(id)
			Produto produto = produtoService.getProdutoById(idAsInteger)
			if (produto == null) {
				routingContext.response().setStatusCode(404).end()
			} else {
				routingContext.response()
						.putHeader("content-type", "application/json;  charset=utf-8")
						.end(Json.encodePrettily(produto))
			}
		}
	}

	def private void updateOne(RoutingContext routingContext) {
		final String id = routingContext.request().getParam("id")
		JsonObject json = routingContext.getBodyAsJson()

		if (id == null || json == null) {
			routingContext.response().setStatusCode(400).end()
		} else {
			final Integer idAsInteger = Integer.valueOf(id)
			def produto = produtoService.getProdutoById(idAsInteger)
			if (produto == null) {
				routingContext.response().setStatusCode(404).end()
			} else {
				produto.setName(json.getString("name"))
				produto.setPrice(json.getInteger("price"))
				produto.setCategory(json.getString("category"))
				routingContext.response()
						.putHeader("content-type", "application/json; charset=utf-8")
						.end(Json.encodePrettily(produto))
			}
		}
	}

	

}
