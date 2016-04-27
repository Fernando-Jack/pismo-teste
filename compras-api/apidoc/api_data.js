define({ "api": [
  {
    "type": "get",
    "url": "/compra/:id",
    "title": "",
    "group": "Compra",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "optional": false,
            "field": "id",
            "description": "<p>compra unique ID.</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "status",
            "description": "<p>Compra retornada</p>"
          }
        ]
      }
    },
    "version": "0.0.0",
    "filename": "src/main/java/br/com/pismo/compra/api/CompraAPI.groovy",
    "groupTitle": "Compra",
    "name": "GetCompraId"
  },
  {
    "type": "post",
    "url": "/compra",
    "title": "",
    "group": "Compra",
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "status",
            "description": "<p>Compra efetuada</p>"
          }
        ]
      }
    },
    "version": "0.0.0",
    "filename": "src/main/java/br/com/pismo/compra/api/CompraAPI.groovy",
    "groupTitle": "Compra",
    "name": "PostCompra"
  }
] });
