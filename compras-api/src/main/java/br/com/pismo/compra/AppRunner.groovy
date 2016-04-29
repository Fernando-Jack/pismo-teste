package br.com.pismo.compra;

import groovy.json.JsonBuilder
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClientOptions
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.web.Router
import br.com.pismo.compra.integration.InventarioRestIntegration
import br.com.pismo.compra.repository.CompraRepositoryJDBCSQL
import br.com.pismo.compra.service.CompraService

public class AppRunner {

	public static void main(String[] args){
		deployVerticles()
	}

	def static void deployVerticles(){
		def vertx = Vertx.vertx()
		def router = Router.router(vertx)

		def compraVerticle = configureCompraVerticle(router, vertx)
		def server = new AppServer()

		def options = new DeploymentOptions().setWorker(true);
		vertx.deployVerticle(compraVerticle, options)
		vertx.deployVerticle(server)
	}

	private static configureCompraVerticle(Router router, Vertx vertx) {
		def config = dataBaseConfig()
		def jdbc = JDBCClient.createShared(vertx, config, "compra-api")
		
		//TODO:FH remove hardcoded. create config-file for port and host
		def configHttp = new HttpClientOptions()
				.setDefaultHost("https://verticle-produto.herokuapp.com")
				.setDefaultPort(443)
				
		def httpClient = vertx.createHttpClient(configHttp)

		def compraVerticle = new CompraService(
				new CompraRepositoryJDBCSQL(jdbc),
				new InventarioRestIntegration(httpClient))

		return compraVerticle
	}

	private static JsonObject dataBaseConfig() {
		def json = new JsonBuilder()

		//TODO:FH remove hardcoded. create config-file for connection
		json{
			url "jdbc:hsqldb:file:db/compra"
			driver_class "org.hsqldb.jdbcDriver"
		}

		def config = new JsonObject(json.toString())
		return config
	}
}
