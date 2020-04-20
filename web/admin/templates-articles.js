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


let articleEditSelect = `
<div class="item-container transparent width-large">
<span class="item-container-heading">Existing Article Posts</span>
<table class="width-100-pc">

<tr class="list-tr">
<td class="list-entry center-text" style="width: 35%;"><span class="text bold">Info</span></td>
<td class="list-entry center-text" style="width: 15%;"><span class="text bold">Image</span></td>
<td class="list-entry center-text" style="width: 20%;"><span class="text bold">Status</span></td>
<td class="list-entry center-text" style="width: 30%;"><span class="text bold">Action</span></td>
</tr>

{{#each articlePosts}}
<tr class="list-tr">

<td class="list-entry structure">
<span class="text">Title: {{this.title}}</span>
<span class="text">Author: {{this.authorVO.displayName}}</span>
<span class="text">Created: {{this.creationDateStr}}</span>
</td>

<td class="list-entry"><img src="/getImage.jsp?filename={{this.titleImageVO.thumbnail}}" alt="[no image]" class="photo-entry"></td>

<td class="list-entry center-text">
{{#if this.published}}
<span class="text">published</span>
{{else}}
<span class="text">NOT published</span>
{{/if}}
<button type="button" class="btn btn-light btn-std" data-id="{{this.id}}" data-role="article-publish-toggle" {{#if testUser}}disabled="disabled"{{/if}}>Toggle</button>
</td>

<td class="list-entry center-text">

<button type="button" class="btn btn-light btn-std" data-id="{{this.id}}" data-role="article-post-edit">Edit</button>
<button type="button" class="btn btn-danger btn-std" data-id="{{this.id}}" data-role="article-post-delete">Delete</button>

</td>
</tr>
{{/each}}
</table>

<button type="button" class="btn btn-light btn-std btn-vertical" data-id="{{this.id}}" data-role="article-post-new">Create new article</button>
</div>

<div data-role="article-post-new-edit"></div>
`;

let articleNewOrEdit = `
<div class="item-container transparent width-large">
<span class="item-container-heading">Edit</span>

<input type="hidden" data-role="data-id" value="{{articleVO.id}}">

<span class="text">Title:</span>
<input type="text" class="form-control input" data-role="data-title" value="{{articleVO.title}}"><br/>

<span class="text">Description:</span><br />
<textarea class="input-textarea-description width-100-pc" maxlength="1000" data-role="data-description">{{articleVO.description}}</textarea><br />

<span class="text">Article Text:</span><br />

<button type="button" class="btn btn-light btn-std btn-vertical" data-role="image-insert-button"><i class="fa fa-image"></i> Insert image</button>
<button type="button" class="btn btn-light btn-std btn-vertical" data-role="file-insert-button"><i class="fa fa-file"></i> Insert file</button>

<textarea class="input-textarea-text width-100-pc" data-role="data-article-text">{{articleVO.articleText}}</textarea>

<span class="text">GPS coordinates:</span>
<input type="text" class="form-control input" data-role="data-gps-coordinates" value="{{articleVO.gpsCoordinates}}"><br />

<div style="display: flex">
<button type="button" class="btn btn-light btn-std btn-vertical" data-role="data-article-save-or-update" {{#if testUser}}disabled="disabled"{{/if}}>Save article</button><br />
<button type="button" class="btn btn-light btn-std btn-vertical" data-role="data-article-save-or-update-close" {{#if testUser}}disabled="disabled"{{/if}}>Save article & Close</button><br />
</div>

<hr class="hr-white">

<span class="text">Title Image:</span><br />
<img src="/getImage.jsp?filename={{articleVO.titleImageVO.preview}}" class="photo-edit-image" data-role="article-title-image" alt="Article Title Image"><br />
<button type="button" class="btn btn-light btn-std btn-vertical" data-role="data-article-update-image">Change image</button><br />

</div>

<div data-role="article-tag-editor" class="width-large"></div>
<div data-role="article-relation-editor" class="width-large"></div>
<div data-role="article-helper-image-manager" class="width-large"></div>

<div data-role="modal-anchor"></div>
`;

/** extends articleEditSelect **/
let confirmDeleteArticle = `
<div class="modal" data-role="delete-article-confirm">
<div class="modal-dialog">
  <div class="modal-content">
  
    <!-- Modal Header -->
    <div class="modal-header">
      <h4 class="modal-title">Confirm Deletion</h4>
      <button type="button" class="close" data-dismiss="modal">&times;</button>
    </div>
    
    <!-- Modal body -->
    <div class="modal-body">
      <span>This will permanently delete the article. Continue?</span>
    </div>
    
    <!-- Modal footer -->
    <div class="modal-footer">
      <button type="button" class="btn btn-danger btn-std" data-role="delete-confirm" {{#if testUser}}disabled="disabled"{{/if}}>Yes, delete!</button>
      <button type="button" class="btn btn-primary btn-std" data-dismiss="modal" {{#if testUser}}disabled="disabled"{{/if}}>No</button>
    </div>
    
  </div>
</div>
</div>
`;

articleEditSelect += confirmDeleteArticle;

