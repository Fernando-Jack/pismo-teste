package br.com.pismo.produto.service

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.pismo.produto.entity.InventarioItem
import br.com.pismo.produto.repository.InventarioRepository

class InventarioService extends AbstractVerticle {
	
	def private InventarioRepository inventarioRepository;	
	def static public final String CREATE 				= "pismo.api.products.create.inventory"
	def static public final String GET_BY_ID 			= "pismo.api.products.getById.inventory"	
	def static public final String GET_ALL 			= "pismo.api.products.getAll.inventory"
	def static public final String CONSUME				= "pismo.api.products.consume.inventory"
	def static public final String VERTICLE_ADDRESS 	= "groovy:br.com.pismo.produto.service.InventarioService"
	
	private final ObjectMapper mapper = new ObjectMapper()
	
	def InventarioService(InventarioRepository inventarioRepository){
		this.inventarioRepository = inventarioRepository
	}
	
	@Override
	public void start(Future<Void> fut) throws Exception {
		vertx.eventBus().consumer(GET_ALL, { message ->
			this.getAll({results ->
				def response = mapper.writeValueAsString(results)
				message.reply(response)
			})
		})
		
		vertx.eventBus().consumer(CREATE, { message ->
			def category =  message.body().getString("category")			
			this.addItem(category,{result ->
				def response = Json.encodePrettily(result)
				message.reply(response)
			})
		})
		
		vertx.eventBus().consumer(GET_BY_ID, { message ->
			def body =  message.body()
			def id = body.toInteger()
			
			this.getInventarioItemById(id,{result ->
				def response			
				if(result){
					response = Json.encodePrettily(result)
					message.reply(response)
				}
				if(!response)
					message.reply("")
			})
		})
		

		vertx.eventBus().consumer(CONSUME, { message ->
			def category =  message.body()			
			this.consumeInventarioItem(category,{result ->
				def response = Json.encodePrettily(result)
				message.reply(response)
			})
		})
		
		fut.succeeded()
	}

	@Override
	public void stop() throws Exception {
	}
	
	public int addItem(String category, nextHandler) {
		inventarioRepository.addItem(category, nextHandler);
	}
	
	public InventarioItem getInventarioItemById(int id, nextHandler) {
		inventarioRepository.getInventarioItemById(id, nextHandler)
	}
	
	public int consumeInventarioItem(String category, nextHandler) {
		inventarioRepository.consumeInventarioItemByCategory(category, nextHandler)
	}
	
	public int getAll(nextHandler) {
		inventarioRepository.getAll(nextHandler)
		return 0
	}

}
