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

let boldOpenTag = new RegExp(/\[\s*b\s*\]/, 'g'); // [b]
let boldCloseTag = new RegExp(/\[\s*\/\s*b\s*\]/, 'g'); // [/b]

let italicOpenTag = new RegExp(/\[\s*i\s*\]/, 'g'); // [i]
let italicCloseTag = new RegExp(/\[\s*\/\s*i\s*\]/, 'g'); // [/i]

let subHeadingOpenTag = new RegExp(/\[\s*h\s*\]/, 'g'); // [h]
let subHeadingCloseTag = new RegExp(/\[\s*\/\s*h\s*\]\s*(?:<br>)*/, 'g'); // [/h] that might be followed by <br>

let youtubeVideoCode = new RegExp(/\[\s*youtube\s*video\s*\=\s*([a-zA-Z0-9\-_]+)\s*\]\s*(?:<br>)*/, 'g'); // [youtube video=xOnRlsd0558] that might be followed by <br>

let quoteOpenTag = new RegExp(/\[\s*quote\s*\]\s*(?:<br>)*/, 'g'); // [quote] that might be followed by <br>
let quoteCloseTag = new RegExp(/\[\s*\/\s*quote\s*\]\s*(?:<br>)*/, 'g'); // [/quote] that might be followed by <br>

let bulletListOpenTag = new RegExp(/\[\s*(x+)\s*\]\s*(?:<br>)*/, 'g'); // [x]
let bulletListCloseTag = new RegExp(/\[\s*\/\s*x+\s*\]\s*(?:<br>)*/, 'g'); // [/x]


/*
Transforming data divs into what we want to see
element - element into which the article has been loaded
 */
let postRender = function(element){

    // images
    let $imageDivs = element.find('[data-type=image]');
    $imageDivs.each(function(){
        let imageID = $(this).data("id");
        let preview = $(this).data("preview");
        let image = $(this).data("image");
        let title = $(this).data("title");

        let hBbInlineImage = Handlebars.compile(bbInlineImage);
        $(this).html(hBbInlineImage({imageSrc: image, title: title, preview: preview}));
    });

    // files
    let $fileDivs = element.find('[data-type=file]');
    $fileDivs.each(function(){
        let fileId = $(this).data("id");
        let displayName = $(this).data("displayname");
        let description = $(this).data("description");

        let hBbInlineFile = Handlebars.compile(bbInlineFile);
        $(this).html(hBbInlineFile({fileId: fileId, displayName: displayName, description: description}));
    });
};

/*
Full render for article texts and similar content (e.g. about page)
 */
let render = function(text){

    text = basicRender(text);

    // embedded youtube videos
    let youtubeBBElements = [];
    let youtubeVideoIds = [];

    let youtubeMatch = youtubeVideoCode.exec(text);
    while (youtubeMatch != null){
        youtubeBBElements.push(youtubeMatch[0]);
        youtubeVideoIds.push(youtubeMatch[1]);
        youtubeMatch = youtubeVideoCode.exec(text);
    }

    for (let i = 0; i < youtubeBBElements.length; i++) {
        let ytElement = youtubeBBElements[i];
        let hBbYouTube = Handlebars.compile(bbYouTube);
        text = text.split(ytElement).join(hBbYouTube({videoCode: youtubeVideoIds[i]}));
    }

    // quotes

    text = text.replace(quoteOpenTag, "<div class='bb-quote bb-quote-size'>");
    text = text.replace(quoteCloseTag, "</div>");

    // bullet lists

    let bulletListTags = [];
    let bulletCounts = [];

    let bulletOpen = bulletListOpenTag.exec(text);
    while (bulletOpen != null){
        bulletListTags.push(bulletOpen[0]);
        bulletCounts.push(bulletOpen[1].length);
        bulletOpen = bulletListOpenTag.exec(text);
    }

    for (let i = 0; i < bulletListTags.length; i++){
        let bulletTag = bulletListTags[i];
        let count = bulletCounts[i];

        let hBulletListOpen = Handlebars.compile(bulletListOpen);
        text = text.split(bulletTag).join(hBulletListOpen({count: count}));
    }

    text = text.replace(bulletListCloseTag, "</span></div>");

    return text;
};

/*
basic render - italics, bold, newlines, links - used in descriptions as well
 */
let basicRender = function(text){
    text = newlineToBr(text);
    text = clearExcessiveLineBreaks(text);

    text = text.replace(boldOpenTag, "<span class='bb-bold'>");
    text = text.replace(boldCloseTag, "</span>");

    text = text.replace(italicOpenTag, "<span class='bb-italic'>");
    text = text.replace(italicCloseTag, "</span>");

    text = text.replace(subHeadingOpenTag, "<span class='bb-heading'>");
    text = text.replace(subHeadingCloseTag, "</span>");

    return text;
};

/*
we dont want to render basic bbcode when descriptions are being displayed in the lists - so we just hide the code
 */
let basicNotRender = function (text){
    // todo
    return text;
};

let newlineToBr = function(input){
    return input.replace(/(?:\r\n|\r|\n)/g, '<br>');
};

let clearExcessiveLineBreaks = function(input){
    return input.replace(/(?:<br>)+/g, '<br>');
};