package br.com.pismo.compra

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.auth.jwt.JWTOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.JWTAuthHandler
import io.vertx.ext.web.handler.StaticHandler
import br.com.pismo.compra.api.CompraAPI

public class AppServer extends AbstractVerticle {

	@Override
	public void start(Future<Void> fut) {

		def router = Router.router(vertx)
		def eventBus = vertx.eventBus()

		def CompraAPI compraAPI = new CompraAPI(router, eventBus)
		compraAPI.registerInRouter(router)

		defineRouterAuth(router)
		defineResponseForBaseURL(router)
		createServerInstace(router, fut)
	}

	private defineResponseForBaseURL(router) {
		def handler = StaticHandler.create()
		router.route("/apidoc/*").handler(handler)
		router.route('/').handler( {routingContext ->
			HttpServerResponse response = routingContext.response()
			response
					.putHeader("content-type", "text/html")
					.end("<h1>Compras API - access /apidoc/ for more information </h1>")
		})
	}

	private createServerInstace(Router router, Future fut) {
		vertx
				.createHttpServer()
				.requestHandler(router.&accept)
				.listen(
				config().getInteger("http.port", 8081),
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
	 * @api {post} /compra/login
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