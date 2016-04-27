package br.com.pismo.teste

import groovy.json.JsonBuilder
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import spock.lang.Specification
import spock.lang.Stepwise
import br.com.pismo.produto.repository.InventarioRepositoryJDBCSQL
import br.com.pismo.produto.service.InventarioService

@Stepwise
class InventarioServiceSpec extends Specification {

	private Vertx vertx
	private int port
	def clientHttp
	def jdbc

	def setup(){
		vertx = Vertx.vertx()
		def json = new JsonBuilder()

		json{
			url "jdbc:hsqldb:file:db/inventario"
			driver_class "org.hsqldb.jdbcDriver"
		}

		def config = new JsonObject(json.toString())
		jdbc = JDBCClient.createShared(vertx, config, "produtos-api")
		sleep(1000)
	}

	def cleanup(){
		vertx.close()
	}

	def "should retrieve an item from inventory"() {

		given: "Given a product to be added"
		def expectedCategory = "playstation"

		and: "An empty invertory service"
		def invetarioService = new InventarioService(new InventarioRepositoryJDBCSQL(jdbc))
		sleep(2000)

		when: "A new item is added"
		def invetarioIdCreated
		invetarioService.addItem(expectedCategory,{result ->
			invetarioIdCreated = result.getId()
		})
		sleep(2000)

		and: "The same item added is retrieved"
		def item
		invetarioService.getInventarioItemById(invetarioIdCreated,{result -> item = result})
		sleep(500)

		then: "The item info must be the used to create it"
		assert expectedCategory == item.getCategory()
		assert invetarioIdCreated == item.getId()
	}

	def "should retrieve all item from inventory"() {

		given: "Given a product to be added"
		def expectedCategory = "playstation"
		def expectedNumberOfItems = 1

		and: "An empty invertory service"
		def invetarioService = new InventarioService(new InventarioRepositoryJDBCSQL(jdbc))
		sleep(2000)

		when: "A new item is added"
		def invetarioIdCreated
		invetarioService.addItem(expectedCategory,{result ->
			invetarioIdCreated = result.getId()
		})
		sleep(2000)

		and: "The same item added is retrieved"
		def items
		invetarioService.getAll({result -> items = result})
		sleep(500)

		then: "The item info must be the used to create it"
		assert expectedNumberOfItems == items.size()
		assert expectedCategory == items[0].getCategory()
		assert invetarioIdCreated == items[0].getId()
	}


	def "should consume an item from inventory"(){

		given: "Given a product to be added"
		def expectedCategory = "playstation2"

		and: "An empty invertory service"
		def invetarioService = new InventarioService(new InventarioRepositoryJDBCSQL(jdbc))
		sleep(500)

		and: "Only one item is added"
		def invetarioIdCreated
		invetarioService.addItem(expectedCategory,{result -> invetarioIdCreated = result.getId()})
		sleep(500)

		when: "An item is consumed from the inventory"
		def item
		invetarioService.consumeInventarioItem(expectedCategory ,{result -> item = result})
		sleep(500)

		and: "And a new item is consumed "
		def itemNullId
		invetarioService.consumeInventarioItem(expectedCategory ,{result -> itemNullId = result})
		sleep(500)

		then: "The itemId returned when the new item was created must be the same of the item consumed"
		assert item.getId() == invetarioIdCreated

		and: "The second attempt to retrieve an item should return -1"
		assert itemNullId == -1
	}
}
