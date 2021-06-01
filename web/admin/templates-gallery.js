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

let galleryEditSelect = `

<div data-role="search-anchor" class="width-full"></div>

<div class="item-container transparent width-full center-text">
<button type="button" class="btn btn-primary btn-std btn-vertical" data-id="{{this.id}}" data-role="gallery-new">Create new gallery</button>
</div>

<div class="item-container transparent width-full">
<span class="item-container-heading">Existing Galleries</span>
<table class="width-100-pc">

<tr class="list-tr">
<td class="list-entry center-text" style="width: 25%;"><span class="text bold">Info</span></td>
<td class="list-entry center-text" style="width: 40%;"><span class="text bold">Preview</span></td>
<td class="list-entry center-text" style="width: 15%;"><span class="text bold">Status</span></td>
<td class="list-entry center-text" style="width: 20%;"><span class="text bold">Action</span></td>
</tr>

{{#each galleryVOs}}
<tr class="list-tr">

<td class="list-entry structure">
<span class="text">Title: {{this.title}}</span>
<span class="text">Author: {{this.authorVO.displayName}} ({{this.authorVO.login}})</span>
<span class="text">Created: {{this.creationDateStr}}</span>
</td>

<td class="list-entry center-text">
{{#each this.galleryRepresentation}}
<img src="../getImage.jsp?filename={{this.thumbnail}}" class="gallery-image-list-item" >
{{/each}}
</td>

<td class="list-entry center-text">
{{#if this.published}}
<span class="text">published</span>
{{else}}
<span class="text">NOT published</span>
{{/if}}

<button type="button" class="btn btn-light btn-std" data-id="{{this.id}}" data-owner="{{this.authorVO.login}}" data-role="gallery-publish-toggle" {{#if demoUser}}disabled="disabled"{{/if}}>Toggle</button>
</td>

<td class="list-entry center-text">

<button type="button" class="btn btn-light btn-std" data-id="{{this.id}}" data-owner="{{this.authorVO.login}}" data-role="gallery-edit">Edit</button>
<button type="button" class="btn btn-danger btn-std" data-id="{{this.id}}" data-owner="{{this.authorVO.login}}" data-role="gallery-delete">Delete</button>

</td>
</tr>

{{/each}}
</table>


</div>

<div data-role="gallery-new-edit"></div>
`;

/** extends photoEditSelect **/
let confirmDeleteGallery = `
<div class="modal" data-role="delete-gallery-confirm">
<div class="modal-dialog">
  <div class="modal-content">
  
    <!-- Modal Header -->
    <div class="modal-header">
      <h4 class="modal-title">Confirm Deletion</h4>
      <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
    </div>
    
    <!-- Modal body -->
    <div class="modal-body">
      <span>This will permanently delete the gallery and all its photos. Continue?</span>
    </div>
    
    <!-- Modal footer -->
    <div class="modal-footer">
      <button type="button" class="btn btn-danger btn-std" data-role="delete-confirm" {{#if demoUser}}disabled="disabled"{{/if}}>Yes, delete it all!</button>
      <button type="button" class="btn btn-primary btn-std" data-bs-dismiss="modal" {{#if demoUser}}disabled="disabled"{{/if}}>No</button>
    </div>
    
  </div>
</div>
</div>
`;

galleryEditSelect += confirmDeleteGallery;


let galleryNewOrEdit = `
<div class="flex-main">

    <div class="item-container transparent width-medium highres-line-item item-left">
        <span class="item-container-heading">Edit</span>

        <input type="hidden" data-role="data-id" value="{{galleryVO.id}}">

        <span class="text">Title:</span>
        <input type="text" class="form-control input width-100-pc" data-role="data-title" value="{{galleryVO.title}}"><br/>
        
        <span class="text">Tiny description:</span><br />
        <span class="smalltext">Brief description for social media posts</span><br />
        <textarea class="input-textarea-tiny-description width-100-pc" maxlength="256" data-role="data-tinydescription">{{galleryVO.tinyDescription}}</textarea><br />

        <span class="text">Description:</span><br />
        <span class="smalltext">Basic BB code can be used. Max. 1000 characters, will be trimmed to 300 in listings.</span>
        <textarea class="input-textarea-description width-100-pc" maxlength="1000" rows="20" data-role="data-description">{{galleryVO.description}}</textarea><br />

        <span class="text">Coordinates: (latitude, longitude)</span> <span data-role="select-on-map" class="link"> select on map</span>
        <input type="text" class="form-control input width-100-pc" data-role="data-gps-coordinates" value="{{galleryVO.gpsCoordinates}}"><br />

        <button type="button" class="btn btn-light btn-std btn-vertical" data-role="data-gallery-save-or-update" {{#if demoUser}}disabled="disabled"{{/if}}>Save & Close</button>
    </div>

    <div class="highres-line-item width-medium item-right">
        <div data-role="gallery-tag-editor"></div>
        <div data-role="gallery-relation-editor"></div>
    </div>

</div>

<div data-role="modal-anchor"></div>
<div data-role="gallery-image-manager" class="width-full"></div>
`;
