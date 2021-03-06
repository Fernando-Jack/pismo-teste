package br.com.pismo.produto

import groovy.json.JsonBuilder
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.auth.jwt.JWTOptions
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.JWTAuthHandler
import io.vertx.ext.web.handler.StaticHandler
import br.com.pismo.produto.api.InventarioAPI
import br.com.pismo.produto.api.ProdutoAPI
import br.com.pismo.produto.repository.InventarioRepositoryJDBCSQL
import br.com.pismo.produto.repository.ProdutoRepositoryJDBCSQL
import br.com.pismo.produto.service.InventarioService
import br.com.pismo.produto.service.ProdutoService

public class AppServer extends AbstractVerticle {

	@Override
	public void start(Future<Void> fut) {

		def jdbc = configJDBCConnetion()

		def router = Router.router(vertx)		
		def eventBus = vertx.eventBus()

		defineRouterBodyHandler(router)
		defineRouterAuth(router)
		configProductAPI(jdbc, eventBus, router)
		configInventoryAPI(jdbc, eventBus, router);

		defineResponseForBaseURL(router)
		createServerInstace(router, fut)
		
	}

	private defineRouterBodyHandler(Router router) {
		router.route('/api/v1/produto*').handler(BodyHandler.create())		
	}

	private configInventoryAPI(JDBCClient jdbc, EventBus eventBus, Router router) {
		def inventoryVerticle = new InventarioService(
				new InventarioRepositoryJDBCSQL(jdbc))
		
		def options = new DeploymentOptions().setWorker(true);
		vertx.deployVerticle(inventoryVerticle, options)

		def InventarioAPI inventarioAPI =
				new InventarioAPI(
				eventBus,
				router,
				inventoryVerticle)


		inventarioAPI.registerInRouter(router)
	}

	private configProductAPI(JDBCClient jdbc, EventBus eventBus, Router router) {
		def produtoVerticle = new ProdutoService(
				new ProdutoRepositoryJDBCSQL(jdbc))
		
		def options = new DeploymentOptions().setWorker(true);
		vertx.deployVerticle(produtoVerticle, options)

		def ProdutoAPI produtoAPI =
				new ProdutoAPI(
				eventBus,
				router,
				produtoVerticle
				)

		produtoAPI.registerInRouter(router)
	}

	private JDBCClient configJDBCConnetion() {
		def json = new JsonBuilder()

		json{
			url "jdbc:hsqldb:file:db/produto"
			driver_class "org.hsqldb.jdbcDriver"
		}

		def config = new JsonObject(json.toString())
		def jdbc = JDBCClient.createShared(vertx, config, "produtos-api")
		return jdbc
	}

	private defineResponseForBaseURL(router) {
		def handler = StaticHandler.create()
		router.route("/apidoc/*").handler(handler)
		router.route('/').handler( {routingContext ->
			HttpServerResponse response = routingContext.response()
			response
					.putHeader("content-type", "text/html")
					.end("<h1>Produto API - access /apidoc/ for more information </h1>")
		})
	}

	private createServerInstace(Router router, Future fut) {
		vertx
				.createHttpServer()
				.requestHandler(router.&accept)
				.listen(
				config().getInteger("http.port", 8080),
				//System.getenv('PORT') as int, '0.0.0.0' ,
				{result ->
					if (result.succeeded()) {
						fut.complete()
					} else {
						fut.fail(result.cause())
					}
				}
				)
	}

	/**
	 * @api {post} /produto/login
	 * @apiGroup Autenticação	 
	 *
	 * @apiSuccess {String} status Retorna um token de acesso
	 *
	 */
	private defineRouterAuth(Router router) {
		def config = new JsonObject().put("keyStore", new JsonObject()
				.put("path", "keystore.jceks")
				.put("type", "jceks")
				.put("password", "secret"))

		def provider = JWTAuth.create(vertx, config)		
		
		router.route("/api/v1/produto/*").handler(JWTAuthHandler.create(provider,"/api/v1/produto/login"))
		
		router.post("/api/v1/produto/login").handler({ ctx ->
			def json = ctx.getBodyAsJson()
			if ("admin" == json.getString("username") && "123" == json.getString("password")) {
				ctx.response().putHeader("Content-Type", "text/plain");
				ctx.response().end(provider.generateToken(new JsonObject(), new JWTOptions().setExpiresInSeconds(120L)))
			} else {
				ctx.fail(401)
			}
		})		
	}

}