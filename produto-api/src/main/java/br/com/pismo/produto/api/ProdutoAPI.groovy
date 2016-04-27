package br.com.pismo.produto.api

import io.vertx.core.eventbus.EventBus
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import br.com.pismo.produto.entity.Produto
import br.com.pismo.produto.service.ProdutoService

class ProdutoAPI {

	def private produtoService
	def private router
	def private eventBus

	def ProdutoAPI(
		EventBus eventBus,
		Router router,
		ProdutoService  produtoService
	){
		this.eventBus = eventBus
		this.router = router
		this.produtoService = produtoService
	}

	def	registerInRouter(Router router){		
		router.get('/api/v1/produto').handler(this.&getAll)
		router.post('/api/v1/produto').handler(this.&addOne)
		router.delete('/api/v1/produto/:id').handler(this.&deleteOne)
		router.get('/api/v1/produto/:id').handler(this.&getOne)
		router.put('/api/v1/produto/:id').handler(this.&updateOne)
	}

	/**
	 * @api {get} /produto/
	 * @apiGroup Produto
	 *
	 *
	 * @apiSuccess {String} status Retorna todos os produtos disponiveis
	 *
	 */
	def private void getAll(RoutingContext routingContext) {
		eventBus.send(produtoService.ALL_PRODUCTS_ADDRESS, "", {result->
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
	 * @api {post} /produto/
	 * @apiGroup Produto
	 *
	 *
	 * @apiSuccess {String} status Adiciona um novo produto
	 *
	 */
	def private void addOne(RoutingContext routingContext) {
		def json = routingContext.getBodyAsJson()
		
		eventBus.send(produtoService.CREATE, json, {result->
			if (result.succeeded()) {
				final String returnJson = result.result().body()

				routingContext.response()
						.setStatusCode(201)
						.putHeader("content-type", "application/json; charset=utf-8")
						.putHeader("content-length", Integer.toString(returnJson.length()))
						.end(returnJson);
			}else{
				routingContext.response()
						.setStatusCode(500)
						.end(result.cause().toString())
			}
		})
	}

	/**
	 * @api {delete} /produto/:id
	 * @apiGroup Produto
	 *
	 * @apiParam {Number} produto unique ID.
	 *
	 * @apiSuccess {String} status Remove o produto especificado
	 *
	 */
	def private void deleteOne(RoutingContext routingContext) {
		String id = routingContext.request().getParam("id")
		if (id == null) {
			routingContext.response().setStatusCode(400).end()
			return
		}

		eventBus.send(produtoService.REMOVE_BY_ID, id, {result->
			if (result.succeeded()) {
				routingContext.response().setStatusCode(204).end()
			}else{
				routingContext.response()
						.setStatusCode(500)
						.end(result.cause().toString())
			}
		})
	}

	/**
	 * @api {get} /produto/:id
	 * @apiGroup Produto
	 *
	 * @apiParam {Number} produto unique ID.
	 *
	 * @apiSuccess {String} status Retorna o produto especificado
	 *
	 */
	def private void getOne(RoutingContext routingContext) {

		final String id = routingContext.request().getParam("id")

		if (id == null) {
			routingContext.response().setStatusCode(400).end()
			return
		}

		final Integer idAsInteger = Integer.valueOf(id)

		eventBus.send(produtoService.GET_BY_ID, idAsInteger, {result->
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

	/**
	 * @api {put} /produto/:id
	 * @apiGroup Produto
	 *
	 * @apiParam {Number} produto unique ID.
	 *
	 * @apiSuccess {String} status Atualiza o produto especificado
	 *
	 */
	def private void updateOne(RoutingContext routingContext) {

		final String id = routingContext.request().getParam("id")
		JsonObject json = routingContext.getBodyAsJson()

		if (id == null || json == null) {
			routingContext.response().setStatusCode(400).end()
			return
		}
		
		final Integer idAsInteger = Integer.valueOf(id)
		def produto = new Produto()
		produto.with{
			it.id 			= idAsInteger
			name 		= json.getString("name")
			price 		= json.getInteger("price")
			category	= json.getString("category")
		}		

		eventBus.send(produtoService.UPDATE, Json.encodePrettily(produto), {result->
			if (result.succeeded()) {
				final String returnJson = result.result().body()

				if (returnJson && !returnJson.allWhitespace){
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
