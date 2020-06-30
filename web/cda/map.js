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
    let settingsTO = await ajaxCda({action: "getSettingsNoAuth"},"error loading settings");
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

        let location = new Microsoft.Maps.Location(48.57, 7.75);
        let mapTypeId = Microsoft.Maps.MapTypeId.aerial;
        let map = new Microsoft.Maps.Map($mapAnchor.get(0), {
            center: location,
            zoom: 4,
            mapTypeId: mapTypeId
        });

        let pagingRequestFilter = getPagingRequestFilterFromParams();
        $controllerAnchor.DynamicSearchCda({pagingRequestFilter: pagingRequestFilter, geo: true, callback: async function(){
                pagingRequestFilter = $controllerAnchor.DynamicSearchCda("getPagingRequestFilter");
                pagingRequestFilter.itemsPerPage = 10000;
                let postVOList = await ajaxCda({action: "processPagingRequestUnified", data: JSON.stringify(pagingRequestFilter) });

                $.each(postVOList.posts, function(index, postVO){
                    let location = new Microsoft.Maps.Location.parseLatLong(postVO.gpsCoordinates);

                    let color = 'red';
                    if (postVO.isArticle){
                        color = 'blue';
                    } else if (postVO.isPhoto){
                        color = 'green';
                    }

                    let pin = new Microsoft.Maps.Pushpin(location, {text: '+', color: color});
                    map.entities.push(pin);
                });
            }});

    }
});