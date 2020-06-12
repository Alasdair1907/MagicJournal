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

/*
Transforming data divs into what we want to see
element - element into which the article has been loaded
 */
let postRender = function(element){

    let $imageDivs = element.find('[data-type=image]');
    $imageDivs.each(function(){
        let imageID = $(this).data("id");
        let preview = $(this).data("preview");
        let image = $(this).data("image");
        let title = $(this).data("title");

        let hBbInlineImage = Handlebars.compile(bbInlineImage);
        $(this).html(hBbInlineImage({imageSrc: image, title: title, preview: preview}));
    });

    let $fileDivs = element.find('[data-type=file]');
    $fileDivs.each(function(){
        let fileId = $(this).data("id");
        let displayName = $(this).data("displayname");
        let description = $(this).data("description");

        // todo make a template
        $(this).html(``)
    });
};

/*
Full render for article texts and similar content (e.g. about page)
 */
let render = function(text){

    text = basicRender(text);

    return text;
};

/*
basic render - italics, bold, newlines, links - used in descriptions as well
 */
let basicRender = function(text){
    text = newlineToBr(text);

    text = text.replace(boldOpenTag, "<b>");
    text = text.replace(boldCloseTag, "</b>");

    return text;
};

/*
we dont want to render basic bbcode when descriptions are being displayed in the lists - so we just hide the code
 */
let basicNotRender = function (text){
    // todo
    return text;
}

let newlineToBr = function(input){
    return input.replace(/(?:\r\n|\r|\n)/g, '<br>');
};