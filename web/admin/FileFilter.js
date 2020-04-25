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


let fileFilter = async function($element, basicFileFilter, displayCallback, self, click) {
    $element.html(fileFilterTemplate);

    let $filterButton = $element.find('[data-role="filter-search"]');
    let $clearButton = $element.find('[data-role="filter-clear"]');

    let $searchDisplayNameInput = $element.find('[data-role="file-search-display-name"]');
    let $searchOriginalNameInput = $element.find('[data-role="file-search-original-name"]');
    let $searchAuthorLoginInput = $element.find('[data-role="file-search-author-login"]');

    let $meButton = $element.find('[data-role="me"]');
    $meButton.click(function () {
        $searchAuthorLoginInput.val(Cookies.get("login"));
    });

    if (basicFileFilter){
        if (basicFileFilter.authorLogin){
            $searchAuthorLoginInput.val(basicFileFilter.authorLogin);
        }
        if (basicFileFilter.fileDisplayName){
            $searchDisplayNameInput.val(basicFileFilter.fileDisplayName);
        }
        if (basicFileFilter.fileOriginalName){
            $searchOriginalNameInput.val(basicFileFilter.fileOriginalName);
        }
    }



    $clearButton.click(function(){
        $searchDisplayNameInput.val("");
        $searchOriginalNameInput.val("");
        $searchAuthorLoginInput.val("");
    });

    $filterButton.click(await async function(){

        let basicFileFilter = {
            authorLogin: $searchAuthorLoginInput.val(),
            fileDisplayName: $searchDisplayNameInput.val(),
            fileOriginalName: $searchOriginalNameInput.val()
        };

        await displayCallback(self, basicFileFilter);
    });

    if (click){
        $filterButton.click();
    }
};

let fileFilterTemplate = `<div class="width-100-pc transparent item-container">
    <table class="width-100-pc">
        <tr>
            <td width="33%;"><span class="text">Display name contains:</span></td>
            <td width="33%;"><span class="text">Orig. name contains:</span></td>
            <td width="33%;"><span class="text">Author login:</span></td></td>
        </tr>
        <tr>
            <td class="td-search"><input type="text" class="form-control input width-100-pc" data-role="file-search-display-name"></td>
            <td class="td-search"><input type="text" class="form-control input  width-100-pc" data-role="file-search-original-name"></td>
            <td class="td-search"><input type="text" class="form-control input  width-100-pc" data-role="file-search-author-login"></td>
        </tr>
        <tr>
            <td class="td-search">&nbsp;</td>
            <td class="td-search" style="text-align: center;">
                <button type="button" class="btn btn-light btn-std" data-role="filter-search" style="margin-top:1vh !important;">Search</button>
                <button type="button" class="btn btn-light btn-std" data-role="filter-clear" style="margin-top:1vh !important;">Clear parameters</button>
                <button type="button" class="btn btn-light btn-std" data-role="me" style="margin-top:1vh !important;">Me</button>
            </td>
            <td class="td-search">&nbsp;</td>
        </tr>
    </table>
</div>`;


