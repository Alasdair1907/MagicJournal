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


let photostoryEditSelect = `

<div data-role="search-anchor" class="width-large"></div>

<div class="item-container transparent width-large center-text">
<button type="button" class="btn btn-primary btn-std btn-vertical" data-id="{{this.id}}" data-role="photostory-post-new">Create new photostory</button>
</div>

<div class="item-container transparent width-large">
<span class="item-container-heading">Existing Collage Posts</span>
<table class="width-100-pc">

<tr class="list-tr">
<td class="list-entry center-text" style="width: 35%;"><span class="text bold">Info</span></td>
<td class="list-entry center-text" style="width: 15%;"><span class="text bold">Image</span></td>
<td class="list-entry center-text" style="width: 20%;"><span class="text bold">Status</span></td>
<td class="list-entry center-text" style="width: 30%;"><span class="text bold">Action</span></td>
</tr>

{{#each photostoryPosts}}
<tr class="list-tr">

<td class="list-entry structure">
<span class="text">Title: {{this.title}}</span>
<span class="text">Author: {{this.authorVO.displayName}} ({{this.authorVO.login}})</span>
<span class="text">Created: {{this.creationDateStr}}</span>
</td>

<td class="list-entry"><img src="../getImage.jsp?filename={{this.titleImageVO.thumbnail}}" alt="[no image]" class="photo-entry"></td>

<td class="list-entry center-text">
{{#if this.published}}
<span class="text">published</span>
{{else}}
<span class="text">NOT published</span>
{{/if}}
<button type="button" class="btn btn-light btn-std" data-id="{{this.id}}" data-owner="{{this.authorVO.login}}" data-role="photostory-publish-toggle" {{#if demoUser}}disabled="disabled"{{/if}}>Toggle</button>
</td>

<td class="list-entry center-text">

<button type="button" class="btn btn-light btn-std" data-id="{{this.id}}" data-owner="{{this.authorVO.login}}" data-role="photostory-post-edit">Edit</button>
<button type="button" class="btn btn-danger btn-std" data-id="{{this.id}}" data-owner="{{this.authorVO.login}}" data-role="photostory-post-delete">Delete</button>

</td>
</tr>
{{/each}}
</table>

</div>

`;

let photostoryNewOrEdit = `
<div class="item-container transparent width-large">
<span class="item-container-heading">Edit</span>

<input type="hidden" data-role="data-id" value="{{photostoryVO.id}}">

<span class="text">Title:</span>
<input type="text" class="form-control input" data-role="data-title" value="{{photostoryVO.title}}"><br/>

<span class="text">Tiny description:</span><br />
<span class="smalltext">Brief description for social media posts</span><br />
<textarea class="input-textarea-tiny-description width-100-pc" maxlength="256" data-role="data-tinydescription">{{photostoryVO.tinyDescription}}</textarea><br />

<span class="text">Description:</span><br />
<span class="smalltext">max. 300 characters</span><br />
<textarea class="input-textarea-description-photostory width-100-pc" maxlength="300" data-role="data-description">{{photostoryVO.description}}</textarea><br />
<br />
<span class="text">Photostory Text:</span><br />

<!-- TODO: photostory content editor goes here -->

<button type="button" class="btn btn-light btn-std btn-vertical" data-role="add-title-block-button"><i class="fa fa-heading"></i> Add title block</button>
<button type="button" class="btn btn-light btn-std btn-vertical" data-role="add-image-block-button"><i class="fa fa-image"></i> Add image block</button>
<button type="button" class="btn btn-light btn-std btn-vertical" data-role="add-text-block-button"><i class="fa fa-text-width"></i> Add text block</button>
<button type="button" class="btn btn-light btn-std btn-vertical" data-role="bb-code-hint-button"><i class="fas fa-pencil-alt"></i> BBCode hint</button>

<div class="photostory-editor-board" data-role="photostory-editor-board">

</div>

<span class="text">Coordinates: (latitude, longitude)</span> <span data-role="select-on-map" class="link"> select on map</span>
<input type="text" class="form-control input" data-role="data-gps-coordinates" value="{{photostoryVO.gpsCoordinates}}"><br />

<div style="display: flex">
<button type="button" class="btn btn-light btn-std btn-vertical" data-role="data-photostory-save-or-update" {{#if demoUser}}disabled="disabled"{{/if}}>Save photostory</button><br />
<button type="button" class="btn btn-light btn-std btn-vertical" data-role="data-photostory-save-or-update-close" {{#if demoUser}}disabled="disabled"{{/if}}>Save photostory & Close</button><br />
<button type="button" class="btn btn-light btn-std btn-vertical" data-role="data-photostory-save-and-preview" {{#if demoUser}}disabled="disabled"{{/if}}>Save photostory & preview</button><br />
</div>

<hr class="hr-white">

<span class="text">Title Image:</span><br />
<img src="../getImage.jsp?filename={{photostoryVO.titleImageVO.preview}}" class="photo-edit-image" data-role="photostory-title-image" alt="Photostory Title Image"><br />
<button type="button" class="btn btn-light btn-std btn-vertical" data-role="data-photostory-update-image">Change image</button><br />

</div>

<div data-role="photostory-tag-editor" class="width-large"></div>
<div data-role="photostory-relation-editor" class="width-large"></div>
<div data-role="photostory-helper-image-manager" class="width-large"></div>

<div data-role="modal-anchor"></div>
`;

/** extends photostoryEditSelect **/
let confirmDeletePhotostory = `
<div class="modal" data-role="delete-photostory-confirm">
<div class="modal-dialog">
  <div class="modal-content">
  
    <!-- Modal Header -->
    <div class="modal-header">
      <h4 class="modal-title">Confirm Deletion</h4>
      <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
    </div>
    
    <!-- Modal body -->
    <div class="modal-body">
      <span>This will permanently delete the photostory. Continue?</span>
    </div>
    
    <!-- Modal footer -->
    <div class="modal-footer">
      <button type="button" class="btn btn-danger btn-std" data-role="delete-confirm" {{#if demoUser}}disabled="disabled"{{/if}}>Yes, delete!</button>
      <button type="button" class="btn btn-primary btn-std" data-bs-dismiss="modal" {{#if demoUser}}disabled="disabled"{{/if}}>No</button>
    </div>
    
  </div>
</div>
</div>
`;

photostoryEditSelect += confirmDeletePhotostory;

let PSItemTemplate = `
<div class="psitem-template" data-order="{{order}}" data-role="psitem-template">
<div class="psitem-main-container" data-role="psitem-container">{{{psitemCode}}}</div>
<div class="psitem-order-container" data-role="psitem-order">{{order}}</div>
<div class="psitem-trashcan-container"><i data-role="psitem-trashcan" data-order="{{order}}" class="fa-regular fa-trash-can psitem-control-icons psitem-control-trashcan"></i></div>
<div class="psitem-arrows-container">
    <div class="psitem-arrows-subcontainer">
        <i class="fa-solid fa-chevron-up psitem-control-icons" data-role="psitem-order-moveup" data-order="{{order}}"></i>
        <i class="fa-solid fa-chevron-down psitem-control-icons" data-role="psitem-order-movedown" data-order="{{order}}"></i>
    </div>
</div>


</div>
</div>
`;

let PSItemTitle = `
<div class="psitem-heading">Title</div>
<div class="psitem-title-editor">
    <input type="text" class="psitem-title-text" data-role="title-text" value="{{titleText}}" data-order="{{order}}" readonly>
     <i class="fa-solid fa-pen psitem-control-icons psitem-control-edit" data-role="edit-title-button" data-order="{{order}}"></i>
</div>
`

let PSItemImage = `
<div class="psitem-image-editor">
    <div class="psitem-image-editor-image">
        <div class="psitem-image-placeholder">
            <img src="../getImage.jsp?filename={{thumbnailFile}}" class="psitem-image-img" data-role="psitem-image" data-order="{{order}}" {{#if noFile}} style="display: none;"{{/if}}>
        </div>
       
    </div>
    
    <div class="psitem-image-editor-caption">
        <div class="psitem-image-editor-textarea-div"><textarea cols="2" class="psitem-image-editor-textarea" data-role="psitem-image-caption" data-order="{{order}}" readonly>{{imageCaption}}</textarea></div>
        <div class="psitem-image-editor-buttons-div">
            <button type="button" class="btn btn-primary btn-std psitem-image-editor-button" data-role="psitem-change-image" data-order="{{order}}">Change image</button>
            <button type="button" class="btn btn-primary btn-std psitem-image-editor-button" data-role="psitem-change-caption" data-order="{{order}}">Edit caption</button>
        </div>
    </div>
</div>
`;

let PSItemText = `
<div class="psitem-heading">Text block</div>
<div class="psitem-text-editor-container">
    <div class="psitem-text-textarea-div">
        <textarea class="psitem-text-textarea" rows="6" data-order="{{order}}" data-role="psitem-text-textarea" readonly>{{text}}</textarea>
    </div>
    <div class="psitem-text-edit-button-div">
        <i class="fa-solid fa-pen psitem-control-icons psitem-control-edit" data-order="{{order}}" data-role="psitem-text-edit"></i>
    </div>
</div>

`