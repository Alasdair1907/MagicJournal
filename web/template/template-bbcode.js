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

let bbInlineImage = `
<img src="getImage.jsp?filename={{preview}}" class="article-image" data-role="inline-image" data-image="{{imageSrc}}" data-title="{{title}}">
<span class="article-image-title">{{title}}</span>
`;

let bbInlineFile = `

<div class="attached-file-container attached-file-container-size">

    <a href="getFile.jsp?id={{fileId}}" class="general-a" download>
        <i class="fas fa-file-download attached-file-icon"></i>
    </a>
    
    <a href="getFile.jsp?id={{fileId}}" class="general-a" download>
        <div class="attached-file-info-div">
            <span class="text-main">{{displayName}}</span>
            <span class="attached-file-description">{{description}}</span>
        </div>
    </a>
    

</div>
`;

let bbYouTube = `
<iframe class="bb-youtube-embed-size bb-youtube-embed" src="https://www.youtube.com/embed/{{videoCode}}" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
`;

let bulletListOpen = `
<div class="bb-bullet-list bb-bullet-{{count}}"><i class="fas fa-dot-circle bb-bullet"></i><span class="text-main">
`;