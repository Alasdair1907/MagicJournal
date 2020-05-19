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


let isNumber = function(input){
    return !isNaN(input) || !isNaN(parseInt(input));
};

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
    }
    
    return filter;
};

let getParamsStrFromPagingRequestFilter = function(base, filter) {

    let request = base + "?";

    if (filter.needArticles){
        request += "articles=true&";
    }

    if (filter.needPhotos){
        request += "photos=true&";
    }

    if (filter.needGalleries){
        request += "galleries=true&";
    }

    if (filter.page === null){
        request += "pageNum=0&";
    } else if (isNumber(filter.page)){
        request += "pageNum="+filter.page+"&";
    }

    if (filter.tags){
        let tagsStr = filter.tags.join(",");
        request += "tags="+tagsStr+"&";
    }

    if (filter.authorLogin){
        request += "author=" + filter.authorLogin + "&"
    }

    return request;
};
