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


let authorsEditRoot = `
<div data-role="registeredAuthorsPanel"></div>
<div data-role="addNewAuthorPanel"></div>
`;

let registeredAuthorsPanel = `
<div class="item-container transparent width-full">
<span class="item-container-heading">Registered Authors</span>
<table class="width-100-pc">
<tr class="list-line list-tr">
<td class="list-entry" style="width: 20%;"><span class="text bold center-text">login</span></td>
<td class="list-entry" style="width: 20%;"><span class="text bold center-text">display name</span></td>
<td class="list-entry" style="width: 20%;"><span class="text bold center-text">access level</span></td>
<td class="list-entry center-text" style="width: 40%;"><span class="text bold center-text">change</span></td>
</tr>
{{#each authorVOs}}
  <tr class="list-line list-tr">
    <td class="list-entry" style="width: 20%;"><span class="text">{{this.login}}</span></td>
    <td class="list-entry" style="width: 20%;"><span class="text">{{this.displayName}}</span></td>
    
    <td class="list-entry" style="width: 20%;">
    <span class="text">
    {{this.privilegeLevelName}}
    <i class="fas fa-info-circle" data-toggle="tooltip" data-placement="top" title="{{this.privilegeLevelDescription}}"></i>
    </span>
    </td>
    
    <td class="list-entry center-text" style="width: 40%; text-align: left !important;">
    <button type="button" class="btn btn-light btn-std" disabled="disabled" data-id="{{this.id}}" data-role="change-display-name">display name</button>
    <button type="button" class="btn btn-light btn-std" disabled="disabled" data-id="{{this.id}}" data-role="change-password">password</button>
    <button type="button" class="btn btn-light btn-std" disabled="disabled" data-id="{{this.id}}" data-role="change-privilege">privilege level</button>
    <button type="button" class="btn btn-danger btn-std" disabled="disabled" data-id="{{this.id}}" data-role="delete-user" {{#if this.hasPosts}}style="display:none"{{/if}}>delete</button>
    </td>
  </tr>
{{/each}}
</table>
</div>
`;

let addNewAuthorPanel = `
<div class="item-container transparent width-medium" data-role="add-new-author-panel">
<span class="item-container-heading">Add New Author</span>
<table class="width-100-pc">

<tr class="list-line">
<td><span class="text">Display Name:</span></td>
<td><input type="text" class="form-control input" data-user="super" disabled="disabled" data-role="new-author-display-name"></td>
</tr>

<tr class="list-line">
<td><span class="text">Login:</span></td>
<td><input type="text" class="form-control input" data-user="super" disabled="disabled" data-role="new-author-login"></td>
</tr>

<tr class="list-line">
<td><span class="text">Password:</span></td>
<td><input type="password" class="form-control input" data-user="super" disabled="disabled" data-role="new-author-password"></td>
</tr>

<tr class="list-line">
<td><span class="text">Privilege Level:</span></td>
<td class="text">

<div class="btn-group btn-group-toggle" data-toggle="buttons">
{{#each privilegeVOs}}
<label class="btn btn-std btn-secondary">
<input type="radio" name="privileges" data-user="super" disabled="disabled" data-role="new-author-privilege-level-id" data-id="{{this.id}}"> {{this.name}} 
</label>
{{/each}}
</div>

</td>
</tr>

</table>

<span class="item-container-heading">
<button type="button" class="btn btn-light btn-std" data-user="super" disabled="disabled" data-role="new-author-create">Create</button>
</span>
</div>

<!-- password changing modal -->

<div class="modal" data-role="change-password-modal">
<div class="modal-dialog">
  <div class="modal-content">
  
    <!-- Modal Header -->
    <div class="modal-header">
      <h4 class="modal-title">Enter New Password</h4>
      <button type="button" class="close" data-dismiss="modal">&times;</button>
    </div>
    
    <!-- Modal body -->
    <div class="modal-body">
      <input type="password" class="form-control input" data-role="new-user-password" style="width:80%;">
    </div>
    
    <!-- Modal footer -->
    <div class="modal-footer">
      <button type="button" class="btn btn-light btn-std" data-role="update-user-password">Update</button>
      <button type="button" class="btn btn-danger btn-std" data-dismiss="modal" onclick="$('[data-role=new-user-password]').val('');">Close</button>
    </div>
    
  </div>
</div>
</div>


<!-- display name changing modal -->

<div class="modal" data-role="change-displayname-modal">
<div class="modal-dialog">
  <div class="modal-content">
  
    <!-- Modal Header -->
    <div class="modal-header">
      <h4 class="modal-title">Enter New Display Name</h4>
      <button type="button" class="close" data-dismiss="modal">&times;</button>
    </div>
    
    <!-- Modal body -->
    <div class="modal-body">
      <input type="text" class="form-control input" data-role="new-user-displayname" style="width:80%;">
    </div>
    
    <!-- Modal footer -->
    <div class="modal-footer">
      <button type="button" class="btn btn-light btn-std" data-role="update-user-displayname">Update</button>
      <button type="button" class="btn btn-danger btn-std" data-dismiss="modal" onclick="$('[data-role=new-user-displayname]').val('');">Close</button>
    </div>
    
  </div>
</div>
</div>

<!-- privilege level changing modal -->

<div class="modal" data-role="change-access-level-modal">
<div class="modal-dialog">
  <div class="modal-content">
  
    <!-- Modal Header -->
    <div class="modal-header">
      <h4 class="modal-title">Select New Access Level</h4>
      <button type="button" class="close" data-dismiss="modal">&times;</button>
    </div>
    
    <!-- Modal body -->
    <div class="modal-body">
{{#each privilegeVOs}}
<input type="radio" name="privileges" data-role="update-author-privilege-level-id" data-id="{{this.id}}"> {{this.name}} 
{{/each}}
    </div>
    
    <!-- Modal footer -->
    <div class="modal-footer">
      <button type="button" class="btn btn-light btn-std" data-role="update-user-access-level">Update</button>
      <button type="button" class="btn btn-danger btn-std" data-dismiss="modal" onclick="$('[data-role=new-user-password]').val('');">Close</button>
    </div>
    
  </div>
</div>
</div>


<!-- user deletion confirmation -->

<div class="modal" data-role="delete-user-confirm">
<div class="modal-dialog">
  <div class="modal-content">
  
    <!-- Modal Header -->
    <div class="modal-header">
      <h4 class="modal-title">Confirm Deletion</h4>
      <button type="button" class="close" data-dismiss="modal">&times;</button>
    </div>
    
    <!-- Modal body -->
    <div class="modal-body">
      <span>This will permanently delete the user. Continue?</span>
    </div>
    
    <!-- Modal footer -->
    <div class="modal-footer">
      <button type="button" class="btn btn-danger btn-std" data-role="delete-confirm">Yes, delete!</button>
      <button type="button" class="btn btn-primary btn-std" data-dismiss="modal">No</button>
    </div>
    
  </div>
</div>
</div>
`;
