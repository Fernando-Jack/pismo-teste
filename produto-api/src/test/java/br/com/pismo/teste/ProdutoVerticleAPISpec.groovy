package br.com.pismo.teste 

import groovy.json.JsonOutput
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import spock.lang.Specification
import spock.lang.Stepwise
import spock.util.concurrent.AsyncConditions
import br.com.pismo.produto.AppServer
import br.com.pismo.produto.entity.Produto

@Stepwise
public class ProdutoVerticleAPISpec extends Specification {

	private vertx
	private port
	private clientHttp
	private tokenKey
	
	def setup(){
		vertx = Vertx.vertx()
		startServerVerticle()
		clientHttp = vertx.createHttpClient()
		sleep(500)
		authenticateHttpClient(clientHttp, port, "localhost", "/api/v1/produto/login",{token -> tokenKey = ("Bearer " + token)})
		sleep(500)
	}	
	
	def cleanup(){
		vertx.close()
	}

	def "should add a product"(){	
			
		given: "A product info to be created"
			final def productName = "Playstation"
			final def productPrice = 2000
			final def productCategory = "playstation"
			
		and: "The product rest URI path"
			final def host = "localhost"
			final def api = "/api/v1/produto"
			
		and: "The product request header info"
			final String jsonProductToBeCreated = Json.encodePrettily(new Produto(productName, productPrice, productCategory))
			final String length = Integer.toString(jsonProductToBeCreated.length())
			
		and: 
			def conditions = new AsyncConditions(2)
			def responseHelper = null
			def responseProduct = null
			def responseHandler = { response ->
				conditions.evaluate{	
					responseHelper = response
					response.bodyHandler({ body ->
						conditions.evaluate{
							responseProduct = Json.decodeValue(body.toString(), Produto.class)
						}
					})
				}
			}			

		when: "The product creation is requested"
			clientHttp.post(port, host, api)
				.putHeader("content-type", "application/json")
				.putHeader("content-length", length)
				.putHeader(HttpHeaders.AUTHORIZATION, tokenKey)
				.handler(responseHandler).write(jsonProductToBeCreated).end()
		
		and: "await for responses"			
			conditions.await()	
			
			
		then:"The request should have the content-type as json and status as 201"			
			assert responseHelper.statusCode() == 201
			assert responseHelper.headers().get("content-type").contains("application/json") == true
			
		 
		and: "The product should be equals as the one created "
			assert responseProduct != null
			assert responseProduct.getName() == productName
			assert responseProduct.getPrice() == productPrice
			assert responseProduct.getId() != null
		
	}
	
	
	def "should remove a product"(){

		given: "A product info to be created"
			final def productName = "Playstation"
			final def productPrice = 2000
			final def productCategory = "playstation"
		
		and: "The product rest URI path"
			final def host = "localhost"
			final def api = "/api/v1/produto"

		and: "The product request header info"
			final String json = Json.encodePrettily(new Produto(productName, productPrice, productCategory))
			final String length = Integer.toString(json.length())
				
		and:
			def conditions = new AsyncConditions(4)
			def responseCreateHelper = null
			def responseDeleteHelper = null
			def responseGetHelper = null
			def responseCreatedProduct = null
			
			def verifyExistenceOfCretedProductRequest = {
				clientHttp.get(port, host, api + "/" + responseCreatedProduct.getId())
				.putHeader(HttpHeaders.AUTHORIZATION, tokenKey)
				.handler({ response ->
					conditions.evaluate{
						responseGetHelper = response						
					}			
				})
				.end()
			}
						
			def deleteCreatedProductRequest = {
				clientHttp.delete(port, host, api + "/" + responseCreatedProduct.getId())
				.putHeader(HttpHeaders.AUTHORIZATION, tokenKey)
				.handler({ response ->
					conditions.evaluate{
						responseDeleteHelper = response						
						verifyExistenceOfCretedProductRequest()
					}
				})
				.end()
			}
			
			def createResponseHandler = { response ->
				conditions.evaluate{
					responseCreateHelper = response
					response.bodyHandler({ body ->
						conditions.evaluate{
							responseCreatedProduct = Json.decodeValue(body.toString(), Produto.class)
							deleteCreatedProductRequest()
						}
					})
				}
			}
		
		when: "The product creation is requested"
			clientHttp.post(port, host, api)
				.putHeader("content-type", "application/json")
				.putHeader("content-length", length)
				.putHeader(HttpHeaders.AUTHORIZATION, tokenKey)
				.handler(createResponseHandler)
				.write(json)
				.end()			
		
				
		and: "Await for results"
			conditions.await()
		
		then:"The creation response should have the content-type as json and status as 201"
			assert responseCreateHelper.statusCode() == 201
			assert responseCreateHelper.headers().get("content-type").contains("application/json") == true
		
		and: "The product info should be equals as the one created "
			assert responseCreatedProduct != null
			assert responseCreatedProduct.getName() == productName
			assert responseCreatedProduct.getPrice() == productPrice
			assert responseCreatedProduct.getId() != null
			
		and: "The created product should be deleted"
			assert responseDeleteHelper.statusCode() == 204	
			
		and: "The deleted producted should not be available"
			assert responseGetHelper.statusCode() == 404		
		
	}		
	
	def "should update a product"(){
		
		given: "A product info to be created"
			final def productName = "Playstation"
			final def productPrice = 2000
			final def productCategory = "playstation"
		
		and: "A product info to be updated"
			final def updateProductName = "Playstation2"
			final def updateProductPrice = 3000
			final def updateProductCategory = "playstation2"
		
		and: "The product rest URI path"
			final def host = "localhost"
			final def api = "/api/v1/produto"
		
		and: "The product request header info"
			final String json = Json.encodePrettily(new Produto(productName, productPrice, productCategory))
			final String updateJson = Json.encodePrettily(new Produto(updateProductName, updateProductPrice, updateProductCategory))		
			final String length = Integer.toString(json.length())
			final String updateLength = Integer.toString(updateJson.length())
		
		and: 
			def conditions = new AsyncConditions(4)
			def responseCreateHelper = null
			def responseUpdateHelper = null
			def responseGetHelper = null
			def responseCreatedProduct = null	
			def responseUpdatedProduct = null
			
			def updateCreatedProductRequest = {
				clientHttp.put(port, host, api + "/" + responseCreatedProduct.getId())
				.putHeader("content-type", "application/json")
				.putHeader("content-length", updateLength)
				.putHeader(HttpHeaders.AUTHORIZATION, tokenKey)
				.handler({ response ->
					responseUpdateHelper = response
					conditions.evaluate{
						response.bodyHandler({ body ->
							conditions.evaluate{
								responseUpdatedProduct = Json.decodeValue(body.toString(), Produto.class)								
							}
						})
					}
				})
				.write(updateJson)
				.end()
			}
			
			def createResponseHandler = { response ->
				conditions.evaluate{
					responseCreateHelper = response
					response.bodyHandler({ body ->
						conditions.evaluate{
							responseCreatedProduct = Json.decodeValue(body.toString(), Produto.class)
							updateCreatedProductRequest()
						}
					})
				}
			}
		
		when: "A product is created"
		clientHttp.post(port, host, api)
			.putHeader("content-type", "application/json")
			.putHeader("content-length", length)
			.putHeader(HttpHeaders.AUTHORIZATION, tokenKey)
			.handler(createResponseHandler)
			.write(json)
			.end()
			
		and: "Await for results"
			conditions.await()
			
		then:"The creation response should have the content-type as json and status as 201"
			assert responseCreateHelper.statusCode() == 201
			assert responseCreateHelper.headers().get("content-type").contains("application/json") == true
			
		and: "The product info should be equals as the one created "
			assert responseCreatedProduct != null
			assert responseCreatedProduct.getName() == productName
			assert responseCreatedProduct.getPrice() == productPrice
			assert responseCreatedProduct.getId() != null
			
		and: "The update response should return code 200 and json content"
			assert responseUpdateHelper.statusCode() == 200
			assert responseUpdateHelper.headers().get("content-type").contains("application/json") == true
			
		and: "The product updated should have the expected info"
			assert responseUpdatedProduct.getName() == updateProductName
			assert responseUpdatedProduct.getPrice() == updateProductPrice
			assert responseUpdatedProduct.getId() == responseCreatedProduct.getId()	
	}
	
	def authenticateHttpClient(httpClient, port, host, api ,tokenHandler){
		def userName = 'admin'
		def password = '123'
		def jsonAuth =  JsonOutput.toJson([username: userName, password: password]).toString()
		
		httpClient.post(port, host, api)
				.putHeader("content-type", "application/json")
				.putHeader("content-length", jsonAuth.length().toString())
				.handler({ response ->
					response.bodyHandler({ body ->					
							def responseToken = body.toString()
							tokenHandler(responseToken)
					})
				})
				.write(jsonAuth)				
				.end()
	}
	
	private startServerVerticle() {
		def options = getServerVerticleOptions()
		vertx.deployVerticle(AppServer.class.getName(), options)
	}

	private DeploymentOptions getServerVerticleOptions() {
		ServerSocket socket = new ServerSocket(0)
		port = socket.getLocalPort()
		socket.close()
		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port))
		return options
	}
}