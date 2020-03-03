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

let loginForm = `
<div class="transparent item-container col-lg-4 center-element login-widget">
<span class="item-container-heading">
Content Management
</span>
<table class="width-100-pc">
<tr>
<td><span class="text fw">login:</span></td>
<td><input type="text" class="form-control" data-role="author_login"></td>
</tr>

<tr>
<td><span class="text fw">password:</span></td>
<td><input type="password" class="form-control" data-role="author_password"></td>
</tr>
</table>
<span class="item-container-heading">
<button type="button" class="btn btn-dark" data-role="perform_login">Log In</button>
</span>
</div>
`;

let administrationRoot = `
<div class="admin-panel-top">
<div class="admin-panel-left-side">
<button type="button" class="btn btn-dark btn-std" data-role="edit-articles">Articles</button>
<button type="button" class="btn btn-dark btn-std" data-role="edit-photos">Photos</button>
<button type="button" class="btn btn-dark btn-std" data-role="edit-galleries">Galleries</button>
<button type="button" class="btn btn-dark btn-std" data-role="edit-authors">Authors</button>
</div>
<div class="admin-panel-right-side">
<span class="text">You are logged in as: {{login}}</span>
<button type="button" class="btn btn-dark btn-std" data-role="logout">Log Out</button>
</div>
</div>

<div data-role="admin-board"></div>
`;
