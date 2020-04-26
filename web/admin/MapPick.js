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

let mapPickTemplate = `
<div class="modal" data-role="map-pick-modal">
    <div class="modal-dialog modal-window-huge">
        <div class="modal-content">

            <!-- Modal Header -->
            <div class="modal-header">
                <span class="modal-h1">Choose location</span>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>

            <!-- Modal body -->
            <div class="modal-body">
            
            <div style="display: flex; justify-content: space-between;">
                <input type="text" class="input form-control" style="width:77%;" data-role="mapPickSearch">
                <button type="button" class="btn btn-success btn-std" style="width:20%;" data-role="search-submit">Search</button>
            </div>
            <div data-role="mapPickMain" class="width-100-pc" style="height: 60vh; margin-top: 1vh;"></div>
            
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-success btn-std" data-role="location-insert" disabled="disabled">Use selected location</button>
                <button type="button" class="btn btn-primary btn-std" data-dismiss="modal">Cancel</button>
            </div>

        </div>
    </div>
</div>
`;

let mapPick = async function($modalAnchor, $inputElem, currentCoordinates){

    $modalAnchor.html(mapPickTemplate);

    try {
        let $mapAnchor = $modalAnchor.find('[data-role="mapPickMain"]');
        let $modal = $modalAnchor.find('[data-role="map-pick-modal"]');
        let $locationInsertButton = $modalAnchor.find('[data-role="location-insert"]');

        let $searchInput = $modalAnchor.find('[data-role="mapPickSearch"]');
        let $searchSubmit = $modalAnchor.find('[data-role="search-submit"]');

        $modal.modal();
        let coordinates = null;

        $locationInsertButton.click(function () {
            $inputElem.val(coordinates);
            $modal.modal('hide');
            $modalAnchor.html('');
        });

        let f = function (ev) {

            let searchManager = null;
            let location = null;
            if (currentCoordinates && checkCoordinates(currentCoordinates)) {
                location = new Microsoft.Maps.Location.parseLatLong(currentCoordinates);
            } else {
                location = new Microsoft.Maps.Location(48.57, 7.75);
            }

            let mapTypeId = Microsoft.Maps.MapTypeId.aerial;
            let map = new Microsoft.Maps.Map($mapAnchor.get(0), {
                center: location,
                zoom: 4,
                mapTypeId: mapTypeId
            });

            let putPin = function (e) {
                removePins();

                let point = new Microsoft.Maps.Point(e.getX(), e.getY());
                let location = e.target.tryPixelToLocation(point);
                let pin = new Microsoft.Maps.Pushpin(location, {text: '+', color: 'red'});

                $locationInsertButton.prop("disabled", false);
                coordinates = location.latitude + ", " + location.longitude;

                map.entities.push(pin);
            };

            let removePins = function () {
                for (var i = map.entities.getLength() - 1; i >= 0; i--) {
                    var pushpin = map.entities.get(i);
                    if (pushpin instanceof Microsoft.Maps.Pushpin) {
                        map.entities.removeAt(i);
                    }
                }
            };

            let geocode = function (query) {
                let searchRequest = {
                    where: query,
                    callback: function (r) {
                        if (r && r.results && r.results.length > 0) {
                            map.setView({
                                center: r.results[0].location,
                                zoom: 15
                            });
                        }
                    },
                    errorCallback: function (t) {
                        alert("No results found.");
                    }
                };

                searchManager.geocode(searchRequest);
            };

            let search = function () {
                if (!searchManager) {
                    Microsoft.Maps.loadModule('Microsoft.Maps.Search', function () {
                        searchManager = new Microsoft.Maps.Search.SearchManager(map);
                        search();
                    });
                } else {
                    let query = $searchInput.val();
                    geocode(query);
                }
            };

            $searchSubmit.click(function () {
                search();
            });
            Microsoft.Maps.Events.addHandler(map, 'click', putPin);

            if (currentCoordinates && checkCoordinates(currentCoordinates)) {
                map.entities.push(new Microsoft.Maps.Pushpin(location, {text: '+', color: 'red'}));
            }
        };

        if (func){
            document.removeEventListener('bingMapApiLoaded', func);
        }
        func = f;
        document.addEventListener('bingMapApiLoaded', f);

        let settingsTO = await ajax({action: "getSettingsNoAuth"},"error loading settings");
        if (settingsTO === undefined){
            return;
        }

        if (!settingsTO.bingApiKey){
            alert("Bing API Key is required for this action. It can be set in Settings.");
            return;
        }

        var mapScriptUrl = 'https://www.bing.com/api/maps/mapcontrol?callback=GetMap&key='+settingsTO.bingApiKey;
        var script = document.createElement("script");
        script.setAttribute('defer', '');
        script.setAttribute('async', '');
        script.setAttribute("type", "text/javascript");
        script.setAttribute("src", mapScriptUrl);
        document.body.appendChild(script);

    } catch (e){
        console.log(e);
    }
};