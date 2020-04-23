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

let postFilter = async function($element, basicPostFilterTO, displayCallback, self) {
    $element.html(postFilterTemplate);

    let $filterButton = $element.find('[data-role="filter-search"]');
    let $clearButton = $element.find('[data-role="filter-clear"]');

    let $authorLoginFilterInput = $element.find('[data-role="author-login-filter"]');
    let $titleContainsInput = $element.find('[data-role="title-contains-filter"]');
    let $creationDateFrom = $element.find('[data-role="date-from-filter"]');
    let $creationDateTo = $element.find('[data-role="date-from-to"]');

    if (basicPostFilterTO){
        if (basicPostFilterTO.login){
            $authorLoginFilterInput.val(basicPostFilterTO.login);
        }
        if (basicPostFilterTO.from){
            $creationDateFrom.val(basicPostFilterTO.from);
        }
        if (basicPostFilterTO.to){
            $creationDateTo.val(basicPostFilterTO.to);
        }
        if (basicPostFilterTO.titleContains){
            $titleContainsInput.val(basicPostFilterTO.titleContains);
        }
    }

    $clearButton.click(function(){
        $authorLoginFilterInput.val("");
        $titleContainsInput.val("");
        $creationDateFrom.val("");
        $creationDateTo.val("");
    });

    $filterButton.click(await async function(){

        // TODO add regex verification & alert

        let basicPostFilterTO = {
            login: $authorLoginFilterInput.val(),
            from: $creationDateFrom.val(),
            to: $creationDateTo.val(),
            titleContains: $titleContainsInput.val()
        };

        await displayCallback(self, basicPostFilterTO);
    });
};

let postFilterTemplate = `<div class="width-100-pc transparent item-container">
    <table class="width-100-pc">
        <tr>
            <td width="50%;"><span class="text">Author login: </span></td>
            <td width="50%;"><span class="text">Title contains: </span></td>
        </tr>
        <tr>
            <td class="td-search"><input type="text" class="form-control input" size="10" data-role="author-login-filter"></td>
            <td class="td-search"><input type="text" class="form-control input" size="15" data-role="title-contains-filter"></td>
        </tr>
        <tr>
            <td><span class="text">Date from: (yyyy-MM-dd) </span></td>
            <td><span class="text">Date to: (yyyy-MM-dd)</span></td>
        </tr>
        <tr>
            <td class="td-search"><input type="text" class="form-control input" size="10" data-role="date-from-filter"></td>
            <td class="td-search"><input type="text" class="form-control input" size="10" data-role="date-from-to"></td>
        </tr>
        <tr>
            <td class="td-search" style="text-align: right;"><button type="button" class="btn btn-light btn-std" data-role="filter-search" style="width:30%; margin-top:1vh !important;">Search</button></td>
            <td class="td-search" style="text-align: left;"><button type="button" class="btn btn-light btn-std" data-role="filter-clear" style="width:30%; margin-top:1vh !important;">Clear parameters</button></td>
        </tr>
    </table>
</div>`;


