package br.com.pismo.test.compra

import groovy.json.JsonBuilder
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import spock.lang.Specification
import spock.lang.Stepwise
import spock.util.concurrent.AsyncConditions
import br.com.pismo.compra.AppServer
import br.com.pismo.compra.entity.Compra
import br.com.pismo.compra.integration.InventarioRestIntegration
import br.com.pismo.compra.repository.CompraRepositoryJDBCSQL
import br.com.pismo.compra.service.CompraService


@Stepwise
public class CompraVerticleAPISpec extends Specification {

	private Vertx vertx
	private int port
	def clientHttp	

	def setup(){
		vertx = Vertx.vertx()
		startCompraAPI()
		clientHttp = vertx.createHttpClient()
		sleep(1000)
	}

	def cleanup(){
		vertx.close()
	}

	def "should make a purchase"(){

		given: "A purchase info to be created"
		final def expectedItemId = 10
		final def expectedPrice = 2000
		final def expectedUserId = 15
		final def expectedProductId = 20		
		def compraRequestJson = "{\"productId\":10 ,\"userId\":15 , \"price\":2000}"

		and: "The purchase rest URI path"
		final def host = "localhost"
		final def api = "/api/v1/compra"

		and: "The purchase request header info"
		final String jsonPurchaseToBeCreated = compraRequestJson
		final String length = Integer.toString(compraRequestJson.length())

		and:
		def conditions = new AsyncConditions(2)
		def responseHelper = null
		def responseCompra = null
		def responseHandler = { response ->
			conditions.evaluate{
				responseHelper = response
				response.bodyHandler({ body ->
					conditions.evaluate{
						responseCompra = Json.decodeValue(body.toString(), Compra.class)
					}
				})
			}
		}

		when: "The purchase creation is requested"
		clientHttp.post(port, host, api)
				.putHeader("content-type", "application/json")
				.putHeader("content-length", length)
				.handler(responseHandler).write(jsonPurchaseToBeCreated).end()

		and: "await for responses"
		conditions.await()


		then:"The request should have the content-type as json and status as 201"
		assert responseHelper.statusCode() == 201
		assert responseHelper.headers().get("content-type").contains("application/json") == true


		and: "The purchase should be equals as the one created "
		assert responseCompra != null
		assert responseCompra.getItemId() == expectedItemId
		assert responseCompra.getUserId() == expectedUserId
		assert responseCompra.getPrice() == expectedPrice
		assert responseCompra.getId() != null
	}

	private startCompraAPI() {

		def options = getServerVerticleOptions()
		def config = dataBaseConfig()
		def jdbc = JDBCClient.createShared(vertx, config, "compra-api")
		def serverVerticle = new AppServer()
		def compraVerticle = compraVerticleWithMockIntegration(jdbc)

		vertx.deployVerticle(compraVerticle)
		vertx.deployVerticle(serverVerticle, options)
	}

	private compraVerticleWithMockIntegration(jdbc) {
		def expectedItemId = 10

		def integration = Mock(InventarioRestIntegration)
		1 * integration.getAvailableItem(_, _) >> { productId, callback ->
			callback(expectedItemId)
		}

		def compraVerticle = new CompraService(new CompraRepositoryJDBCSQL(jdbc), integration)

		return compraVerticle
	}

	private JsonObject dataBaseConfig() {
		def json = new JsonBuilder()

		json{
			url "jdbc:hsqldb:file:db/compra"
			driver_class "org.hsqldb.jdbcDriver"
		}

		def config = new JsonObject(json.toString())
		return config
	}

	private DeploymentOptions getServerVerticleOptions() {
		ServerSocket socket = new ServerSocket(0)
		port = socket.getLocalPort()
		socket.close()
		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port))
		return options
	}
}