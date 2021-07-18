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
        alert("Bing API Key is required for this action. It can be set in Settings.");
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

        let location = new Microsoft.Maps.Location(50.08962962831929, 14.398006198883046);
        let mapTypeId = Microsoft.Maps.MapTypeId.aerial;
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

            pagingRequestFilter.itemsPerPage = 10000;
            let postVOList = await ajaxCda({action: "processPagingRequestUnified", data: JSON.stringify(pagingRequestFilter) });

            for (var i = map.entities.getLength() - 1; i >= 0; i--) {
                var pushpin = map.entities.get(i);
                if (pushpin instanceof Microsoft.Maps.Pushpin) {
                    map.entities.removeAt(i);
                }
            }

            $.each(postVOList.posts, function(index, postVO){
                let location = new Microsoft.Maps.Location.parseLatLong(postVO.gpsCoordinates);

                let color = 'red';
                if (postVO.isArticle){
                    color = 'blue';
                } else if (postVO.isPhoto){
                    color = 'green';
                }

                let pin = new Microsoft.Maps.Pushpin(location, {text: '+', color: color});
                pin.metadata = {postVO: postVO};
                map.entities.push(pin);

                Microsoft.Maps.Events.addHandler(pin, 'click', pushpinClickedHandler);
            });

        }});

        let pushpinClickedHandler = function(e){
            let postVO = e.target.metadata.postVO;

            let postClass = postVO.isArticle ? 'Article' : ( postVO.isPhoto ? 'Photo' : 'Gallery' );
            let mainImageVO = postVO.mainImageVO

            infobox.setOptions({
                location: e.target.getLocation(),
                title: postClass,
                maxHeight: 300,
                maxWidth: 300,
                showPointer: false,
                description: postVO.title + '<br /><img src="getImage.jsp?filename=' + mainImageVO.thumbnail + '" style="max-width: 150px; max-height: 150px;"/><br /><a href="posts.jsp?' + postClass.toLowerCase() + '=' + postVO.id + '" target="_blank">View post</a>',
                visible: true
            });

        };

        document.title = "Map";
    }
});
