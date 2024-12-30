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

let lineBreakTag = new RegExp(/\[\s*br\s*\]\s*(?:<br>)*/, 'g'); // [br]

let emptyParagraphs = new RegExp(/<p class=\"paragraph\">(\s*<br\s*\/?>)*\s*<\/p>/, 'g');  // empty paragraphs (also with whitespaces and <br>'s)


//[url=http://www.example.org?var=foo&bar=23&q= W00t123]Blah blah[/url]
// group 1 - link
// group 2 - link text
let hypertextLinkTag = new RegExp(/\[\s*url\s*=\s*(.+?)\s*\](.+?)(?:\[\s*\/\s*url\s*])/, 'g');

let pOpen = "<p class=\"paragraph\">";
let pClose = "</p>";

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

    // headings (used to be in basicRender, but that was not a good idea)
    text = text.replace(subHeadingOpenTag, pClose+"<span class='bb-heading'>");
    text = text.replace(subHeadingCloseTag, "</span>"+pOpen);

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
        text = text.split(ytElement).join(pClose + hBbYouTube({videoCode: youtubeVideoIds[i]}) + pOpen);
    }

    // quotes

    text = text.replace(quoteOpenTag, pClose + "<div class='bb-quote bb-quote-size'>");
    text = text.replace(quoteCloseTag, "</div>" + pOpen);

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
        text = text.split(bulletTag).join(pClose + hBulletListOpen({count: count}));
    }

    text = text.replace(bulletListCloseTag, "</span></div>"+pOpen);
    text = text.replace(lineBreakTag, "<br>" );
    text = text.replace(emptyParagraphs, "");

    return text;
};

/*
basic render - italics, bold, newlines, links - used in descriptions as well
 */
let basicRender = function(text, useParagraph = true){
    if (useParagraph) {
        text = pOpen + text + pClose;
    }
    text = newlineToBr(text);
    text = clearExcessiveLineBreaks(text);

    text = text.replace(boldOpenTag, "<span class='bb-bold'>");
    text = text.replace(boldCloseTag, "</span>");

    text = text.replace(italicOpenTag, "<span class='bb-italic'>");
    text = text.replace(italicCloseTag, "</span>");

    // links
    let urlTags = [];
    let urls = [];
    let texts = [];

    let urlMatch = hypertextLinkTag.exec(text);
    while (urlMatch != null){
        urlTags.push(urlMatch[0]);
        urls.push(urlMatch[1]);
        texts.push(urlMatch[2]);

        urlMatch = hypertextLinkTag.exec(text);
    }

    for (let i = 0; i < urlTags.length; i++){
        let hHyperLinkTemplate = Handlebars.compile(hyperLinkTemplate);
        text = text.split(urlTags[i]).join(hHyperLinkTemplate({url: urls[i], text: texts[i]}));
    }

    return text;
};

/*
we dont want to render basic bbcode when descriptions are being displayed in the lists - so we just hide the code
 */

let basicNotRender = function(text){

    if (!text){
        return "";
    }

    text = text.replace(boldOpenTag, "");
    text = text.replace(boldCloseTag, "");

    text = text.replace(italicOpenTag, "");
    text = text.replace(italicCloseTag, "");

    text = text.replace(subHeadingOpenTag, "");
    text = text.replace(subHeadingCloseTag, " ");

    // links
    let urlTags = [];
    let texts = [];

    let urlMatch = hypertextLinkTag.exec(text);
    while (urlMatch != null){
        urlTags.push(urlMatch[0]);
        texts.push(urlMatch[2]);

        urlMatch = hypertextLinkTag.exec(text);
    }

    for (let i = 0; i < urlTags.length; i++){
        text = text.split(urlTags[i]).join(texts[i]);
    }
    return text;
};

let newlineToBr = function(input){
    if (!input){
        return "";
    }

    return input.replace(/(?:\r\n|\r|\n)/g, '<br>');
};

let clearExcessiveLineBreaks = function(input){
    return input.replace(/(?:<br>)+/g, '<br>');
};

let shrinkDescription = function(description){

    if (!description){
        return "";
    }

    if (description.length > 300){
        description = description.substring(0,297).trim() + "...";
    }
    return description;
};

let prepareCompactDescription = function(description){
    description = basicNotRender(description);
    description = shrinkDescription(description);
    return description;
};