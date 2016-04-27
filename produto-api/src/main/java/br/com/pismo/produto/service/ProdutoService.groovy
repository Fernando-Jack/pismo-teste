package br.com.pismo.produto.service

import groovy.json.JsonBuilder
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import br.com.pismo.produto.entity.Produto
import br.com.pismo.produto.repository.ProdutoRepository

import com.fasterxml.jackson.databind.ObjectMapper

public class ProdutoService extends AbstractVerticle {

	def ProdutoRepository repository
	def static public final String ALL_PRODUCTS_ADDRESS = "pismo.api.products.all.products"
	def static public final String CREATE 				= "pismo.api.products.create.product"
	def static public final String GET_BY_ID 			= "pismo.api.products.getById.product"
	def static public final String REMOVE_BY_ID			= "pismo.api.products.removeById.product"
	def static public final String UPDATE				= "pismo.api.products.update.product"
	def static public final String VERTICLE_ADDRESS 	= "groovy:br.com.pismo.produto.service.ProdutoService"
	
	private final ObjectMapper mapper = new ObjectMapper()
	
	def ProdutoService(ProdutoRepository repository){
		this.repository = repository
	}

	@Override
	public void start(Future<Void> fut) throws Exception {		
		
		vertx.eventBus().consumer(ALL_PRODUCTS_ADDRESS, { message ->
			this.getAllProdutos({results ->
				def response = mapper.writeValueAsString(results)
				message.reply(response)
			})
		})
		
		vertx.eventBus().consumer(CREATE, { message ->
			def body =  message.body()
			def id = this.createProduto(
					body.getString("name"),
					body.getInteger("price"),
					body.getString("category"),
					{result ->
						def response = Json.encodePrettily(result)			
						message.reply(response)
					}
				)
		})
		
		vertx.eventBus().consumer(GET_BY_ID, { message ->
			def body =  message.body()

			def id = body.toInteger()
			
			this.getProdutoById(id,{result ->
				def response
				
				if(result){
					response = Json.encodePrettily(result)
					message.reply(response)
				}
	
				if(!response)
					message.reply("")								
			})
		})
		
		vertx.eventBus().consumer(REMOVE_BY_ID, { message ->

			def body =  message.body()
			def id = body.toInteger()

			this.removeById(id, {result ->
				message.reply(result)
			})			
		})

		vertx.eventBus().consumer(UPDATE, { message ->

			def body =  new JsonObject(message.body())			
			def produto = new Produto()			
			
			produto.with{
				id 			= body.getInteger("id")
				name 		= body.getString("name")
				price 		= body.getInteger("price")
				category	= body.getString("category")
			}
			
			this.update(produto,{result ->
				def response = Json.encodePrettily(result)
				message.reply(response)
			})
			
			
		})
		
		fut.succeeded()		
	}

	@Override
	public void stop() throws Exception {
	}
	
	def  int createProduto(expectedName, expectedPrice, expectedCategory, nextHandler){
		repository.createProduto(expectedName, expectedPrice, expectedCategory, nextHandler)
	}

	def  Produto getProdutoById(id, nextHandler){
		repository.getProdutoById(id, nextHandler)
	}

	def  removeAll(nextHandler){
		repository.removeAll(nextHandler)
	}

	def  removeById(id, nextHandler){
		repository.removeById(id, nextHandler)
	}

	def  List<Produto> getAllProdutos(nextHandler){
		repository.getAll(nextHandler)
	}
	
	def Produto update(produto, nextHandler){
		repository.update(produto, nextHandler)
	}
}
