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

var POST_ATTRIBUTION_GALLERY = 0;
var POST_ATTRIBUTION_PHOTO = 1;
var POST_ATTRIBUTION_ARTICLE = 2;
var POST_ATTRIBUTION_PROFILE = 3;

let isNumber = function(input){
    return !isNaN(input) || !isNaN(parseInt(input));
};

let getSidepanelData = async function(latestPostsCount, postAttributionClass, postId){
    let sidePanelRequestTO = {
        postAttribution: postAttributionClass,
        postId: postId,
        limitLatest: latestPostsCount
    };

    let sidePanelPostsTO = await ajaxCda({data: JSON.stringify(sidePanelRequestTO), action: "getSidePanelPosts"}, "error fetching side panel posts");

    // these both set hasRelated, because this is used in the newer bottom panel
    // that doesn't distinguish between related and associated
    if (sidePanelPostsTO.associated && sidePanelPostsTO.associated.length > 0){
        sidePanelPostsTO.hasRelated = true;
    }
    if (sidePanelPostsTO.related && sidePanelPostsTO.related.length > 0){
        sidePanelPostsTO.hasRelated = true;
    }

    return sidePanelPostsTO;
}

let getPagingRequestFilterFromParams = function(){

    let params = new URLSearchParams(window.location.search);

    let filter = {};

    let postsRequested = false;
    
    if (params.has("articles") && params.get("articles") === "true"){
        filter.needArticles = true;
        postsRequested = true;
    }

    if (params.has("photos") && params.get("photos") === "true"){
        filter.needPhotos = true;
        postsRequested = true;
    }

    if (params.has("galleries") && params.get("galleries") === "true"){
        filter.needGalleries = true;
        postsRequested = true;
    }

    if (params.has("collages") && params.get("collages") === "true"){
        filter.needPhotostories = true;
        postsRequested = true;
    }

    if (params.has("pageNum") && isNumber(params.get("pageNum"))){
        filter.page = parseInt(params.get("pageNum"));
    } else {
        filter.page = 0;
    }

    if (params.has("tags")){
        filter.tags = params.get("tags").split(",");
    }

    if (params.has("author")){
        filter.authorLogin = params.get("author");
    }

    if (!postsRequested){
        filter.needArticles = true;
        filter.needPhotos = true;
        filter.needGalleries = true;
        filter.needPhotostories = true;
    }
    
    return filter;
};

let getParamsStrFromPagingRequestFilter = function(base, filter, advanced = false) {

    let request = base + "?";

    if (advanced){
        request += "advanced=true&";
    }

    if (filter.needArticles){
        request += "articles=true&";
    }

    if (filter.needPhotos){
        request += "photos=true&";
    }

    if (filter.needGalleries){
        request += "galleries=true&";
    }

    if (filter.needPhotostories){
        request += "collages=true&";
    }

    if (filter.page === null){
        request += "pageNum=0&";
    } else if (isNumber(filter.page)){
        request += "pageNum="+filter.page+"&";
    }

    if (filter.tags && filter.tags.length){
        let tagsStr = filter.tags.join(",");
        request += "tags="+tagsStr+"&";
    }

    if (filter.authorLogin){
        request += "author=" + filter.authorLogin + "&"
    }

    return request;
};

let getLocationHeaderByFilter = function(filter) {
    if (filter.tags){
        return "Tags: "+filter.tags.join(", ");
    }
    if (filter.authorLogin){
        return "Posts by Author";
    }

    let needs = [!!filter.needArticles, !!filter.needPhotos, !!filter.needGalleries, !!filter.needPhotostories];

    if (arrEqual(needs, [true, false, false, false])) {
        return "Articles";
    }

    if (arrEqual(needs, [false, true, false, false])) {
        return "Photos";
    }
    if (arrEqual(needs, [false, false, true, false])) {
        return "Galleries";
    }
    if (arrEqual(needs, [false, false, false, true])) {
        return "Collages";
    }

    return "";
};

let arrEqual = function(a, b){
    return JSON.stringify(a) === JSON.stringify(b);
};

let getSettingsTO = async function(){

    let settingsTOJson = await fromBase64Cda($("meta[name='settingsTOCache']").attr("content"));
    let settingsTO = JSON.parse(settingsTOJson);

    return settingsTO;
};


let toBase64Cda = async function(input){
    return await ajaxCda({action: "toBase64Utf8", data: input});
};

let fromBase64Cda = async function(input){
    return await ajaxCda({action: "fromBase64Utf8", data: input});
};