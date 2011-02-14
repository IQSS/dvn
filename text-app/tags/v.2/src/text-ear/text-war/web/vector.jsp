<%-- 
    Document   : index
    Created on : Oct 15, 2010, 11:11:43 AM
    Author     : ekraffmiller
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <title>OpenLayers Vector Example</title>

        <link rel="stylesheet" href="../theme/default/style.css" type="text/css" />
        <link rel="stylesheet" href="style.css" type="text/css" />
        <style type="text/css">
            #map {
                width: 600px;
                height: 300px;
                border: 1px solid black;
                float:left;
            }
            #map2 {
                width: 400px;
                height: 400px;
                border: 1px solid black;
                float:left;
            }
        </style>
        <script src="http://openlayers.org/api/OpenLayers.js"></script>

        <script src="http://proj4js.org/lib/proj4js-compressed.js"></script>
     <script type="text/javascript">
            var map;

            function init(){
                map = new OpenLayers.Map('map');
                var wms = new OpenLayers.Layer.WMS(
                    "OpenLayers WMS", "http://vmap0.tiles.osgeo.org/wms/vmap0",
                    {layers: 'basic'}
                );

                var layer = new OpenLayers.Layer.Vector("GML", {
                    strategies: [new OpenLayers.Strategy.Fixed()],
                    protocol: new OpenLayers.Protocol.HTTP({
                        url: "polygon.xml",
                        format: new OpenLayers.Format.GML()
                    })
                });
               // map.addLayer(layer);
                map.addLayers([wms, layer]);
                map.zoomToExtent(new OpenLayers.Bounds(
                    -2.0, -2.0, 2.0, 2.0
                ));
            }
        </script>
    </head>

    <body onload="init()">
        <h1 id="title">Vector Behavior Example (Fixed/HTTP/GML)</h1>
        <div id="tags">
            vector, strategy, strategies, protocoll, advanced, gml, http, fixed
        </div>
        <p id="shortdesc">
            Vector layer with a Fixed strategy, HTTP protocol, and GML format.
        </p>
        <div id="map" class="smallmap"></div>

        <div id="docs">
            The vector layer shown uses the Fixed strategy, the HTTP protocol,
            and the GML format.
            The Fixed strategy is a simple strategy that fetches features once
            and never re-requests new data.
            The HTTP protocol makes requests using HTTP verbs.  It should be
            constructed with a url that corresponds to a collection of features
            (a resource on some server).
            The GML format is used to serialize features.
        </div>
    </body>

</html>
