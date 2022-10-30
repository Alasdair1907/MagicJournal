/**
                                        `.------:::--...``.`                                        
                                    `-:+hmmoo+++dNNmo-.``/dh+...                                    
                                   .+/+mNmyo++/+hmmdo-.``.odmo -/`                                  
                                 `-//+ooooo++///////:---..``.````-``                                
                           `````.----:::/::::::::::::--------.....--..`````                         
           ```````````...............---:::-----::::---..------------------........```````          
        `:/+ooooooosssssssyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyysssssssssssssssssssssssssssoo+/:`       
          ``..-:/++ossyhhddddddddmmmmmarea51mbobmlazarmmmmmmmddddddddddddddhhyysoo+//:-..``         
                      ```..--:/+oyhddddmmmmmmmmmmmmmmmmmmmmmmmddddys+/::-..````                     
                                 ``.:oshddmmmmmNNNNNNNNNNNmmmhs+:.`                                 
                                       `.-/+oossssyysssoo+/-.`                                      
                                                                                                     
    
*/

let loadMap = async function(){

    let settingsTO = await getSettingsTO();

    if (settingsTO === undefined){
        return;
    }

    if (!settingsTO.bingApiKey){
        alert("Bing Maps API Key is required for this action. It can be set in Settings.");
        return;
    }

    let mapScriptUrl = 'https://www.bing.com/api/maps/mapcontrol?callback=GetMap&key='+settingsTO.bingApiKey;
    let script = document.createElement("script");
    script.setAttribute('defer', '');
    script.setAttribute('async', '');
    script.setAttribute("type", "text/javascript");
    script.setAttribute("src", mapScriptUrl);
    document.body.appendChild(script);
};

$.widget("magic.CdaMap", {
    _create: async function(){
        let self = this;

        self.element.html(cdaMapTemplate);

        let $mapAnchor = self.element.find('[data-role="cda-map"]');
        let $controllerAnchor = self.element.find('[data-role="map-search-controller"]');

        let settingsTO = await getSettingsTO();

        let mapTypeId = Microsoft.Maps.MapTypeId.aerial;
        if (settingsTO && settingsTO.mapTypeIdStr){
            let typeId = settingsTO.mapTypeIdStr;
            if (typeId == "canvasDark"){
                mapTypeId = Microsoft.Maps.MapTypeId.canvasDark;
            } else if (typeId == "canvasLight"){
                mapTypeId = Microsoft.Maps.MapTypeId.canvasLight;
            } else if (typeId == "grayscale"){
                mapTypeId = Microsoft.Maps.MapTypeId.grayscale;
            }
        }

        let location = new Microsoft.Maps.Location(50.08962962831929, 14.398006198883046);
        let map = new Microsoft.Maps.Map($mapAnchor.get(0), {
            center: location,
            zoom: 3,
            mapTypeId: mapTypeId
        });

        let infobox = new Microsoft.Maps.Infobox(map.getCenter(), {
            visible: false
        });
        infobox.setMap(map);

        let pagingRequestFilter = getPagingRequestFilterFromParams();
        $controllerAnchor.DynamicSearchCda({pagingRequestFilter: pagingRequestFilter, geo: true, clickOnCreate: true, callback: async function(pagingRequestFilter){

            // load posts that match the criteria from the search
            pagingRequestFilter.itemsPerPage = 10000;
            let postVOList = await ajaxCda({action: "processPagingRequestUnified", data: JSON.stringify(pagingRequestFilter) });

            // remove existing pushpins if any
            for (var i = map.entities.getLength() - 1; i >= 0; i--) {
                var pushpin = map.entities.get(i);
                if (pushpin instanceof Microsoft.Maps.Pushpin) {
                    map.entities.removeAt(i);
                }
            }

            // remove any existing layers
            map.layers.clear();

            // make pushpins from the posts
            var newPushpins = [];
            var newLocations = [];
            $.each(postVOList.posts, function(index, postVO){
                let location = new Microsoft.Maps.Location.parseLatLong(postVO.gpsCoordinates);
                newLocations.push(location);

                let color = 'red';
                if (postVO.isArticle){
                    color = 'blue';
                } else if (postVO.isPhoto){
                    color = '#9AE620';
                }

                let pin = new Microsoft.Maps.Pushpin(location, {text: '+', color: color});
                pin.metadata = {postVO: postVO};
                //map.entities.push(pin);
                Microsoft.Maps.Events.addHandler(pin, 'click', pushpinClickedHandler);
                newPushpins.push(pin);
            });


            // add cluster layer with pushpins
            clusterLayer = new Microsoft.Maps.ClusterLayer(newPushpins, {
                clusteredPinCallback: customizeClusteredPin
            });
            map.layers.insert(clusterLayer);

            // set map to fit the pushpins
            var bounds = Microsoft.Maps.LocationRect.fromLocations(newLocations);
            var diagonal = getRectDiagonalMeters(bounds);
            if (diagonal < 10_000){
                map.setView({ center: bounds.center, zoom: 10});
            } else {
                map.setView({bounds: bounds, padding: 100});
            }

        }});


        let customizeClusteredPin = function(cluster) {
            //Add click event to clustered pushpin
            Microsoft.Maps.Events.addHandler(cluster, 'click', clusterClicked);
        }


        let clusterClicked = function(e) {
            if (e.target.containedPushpins) {

                var pushpinCount = e.target.containedPushpins.length;
                var locs = [];
                for (var i = 0, len = pushpinCount; i < len; i++) {
                    //Get the location of each pushpin.
                    locs.push(e.target.containedPushpins[i].getLocation());
                }

                //Create a bounding box for the pushpins.
                var bounds = Microsoft.Maps.LocationRect.fromLocations(locs);

                // are pushpins too close? then show the list of posts in the infobox
                var diagonal = getRectDiagonalMeters(bounds);
                if (diagonal < 300){

                    var descriptionText = '';
                    var title = pushpinCount + ' items in this area';
                    for (var i = 0; i < pushpinCount; i++){
                        var pushpin = e.target.containedPushpins[i];

                        var postVO = pushpin.metadata.postVO;
                        var postClass = postVO.isArticle ? 'Article' : ( postVO.isPhoto ? 'Photo' : 'Gallery' );

                        descriptionText += '[' + postClass + '] <a href="posts.jsp?' + postClass.toLowerCase() + '=' + postVO.id + '" target="_blank">'+ postVO.title + '</a><br />'
                    }

                    // if the user clicks tiny cluster from zoomed-out map, we zoom into defaultZoom before showing the infobox
                    var defaultZoom = 13;
                    var maxZoom = map.getZoomRange().max;
                    if (map.getZoom() < defaultZoom){
                        map.setView({ center: bounds.center, zoom: maxZoom < defaultZoom ? maxZoom : defaultZoom});
                    }

                    infobox.setOptions({
                        location: e.target.getLocation(),
                        title: title,
                        maxHeight: 400,
                        maxWidth: 400,
                        showPointer: false,
                        description: descriptionText,
                        visible: true
                    });

                } else {
                    //Zoom into the bounding box of the cluster.
                    //Add a padding to compensate for the pixel area of the pushpins.
                    map.setView({ bounds: bounds, padding: 100 });
                }
            }
        }

        let getRectDiagonalMeters = function(bounds){
            var northWest = bounds.getNorthwest();
            var southEast = bounds.getSoutheast();
            var diagonal = Microsoft.Maps.SpatialMath.getLengthOfPath([northWest, southEast], Microsoft.Maps.SpatialMath.DistanceUnits.Meters, true);
            return diagonal
        }

        let pushpinClickedHandler = function(e){
            let postVO = e.target.metadata.postVO;

            let postClass = postVO.isArticle ? 'Article' : ( postVO.isPhoto ? 'Photo' : 'Gallery' );
            let mainImageVO = postVO.mainImageVO

            infobox.setOptions({
                location: e.target.getLocation(),
                title: postClass,
                maxHeight: 400,
                maxWidth: 400,
                showPointer: false,
                description: postVO.title + '<br /><div style="width:150px; height:100px;"><img src="getImage.jsp?filename=' + mainImageVO.thumbnail + '" style="max-width: 150px; max-height: 100px;"/></div><br /><a href="posts.jsp?' + postClass.toLowerCase() + '=' + postVO.id + '" target="_blank">View post</a>',
                visible: true
            });

        };

        document.title = "Map";
    }
});
