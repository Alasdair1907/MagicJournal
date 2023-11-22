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

let showBBCodeHintModal = function($modalAnchor){
    $modalAnchor.html(bbCodeHintModal);

    let $modalElem = $modalAnchor.find('[data-role="bb-code-hint-modal"]');
    $modalElem.modal('show');
};

let bbCodeHintModal = `
<div class="modal" data-role="bb-code-hint-modal">
    <div class="modal-dialog">
        <div class="modal-content">

            <!-- Modal Header -->
            <div class="modal-header">
                <h4 class="modal-title">BBCode Info</h4>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>

            <!-- Modal body -->
            <div class="modal-body">
                <span style="text-align: center; display: block; text-decoration: underline">Full code (articles, About page)</span>
                
                <b>[br]</b><br />
                This will create a line break (newline)<br />
                <br />
                <b>[b]bold[/b]</b><br />
                <b>[i]italics[/i]</b><br />
                <b>[h]header[/h]</b><br />
                <br />
                <b>[youtube video=BEWz4SXfyCQ]</b><br />
                (in this example, the address of the video would be https://www.youtube.com/watch?v=BEWz4SXfyCQ)<br />
                <br />
                <b>[quote]This text will be displayed in a special quote paragraph[/quote]</b><br />
                <br />
                <b>[x]Bullet list[/x]</b><br />
                There can be five levels of bullet lists. Top level is marked as [xxxxx][/xxxxx]<br />
                <br />
                <b>[url=http://www.example.org]Example link[/url]</b><br />
                Will create a hyperlink with the text 'Example link' pointing to 'http://www.example.org'<br />
                <br />
                <span style="text-align: center; display: block; text-decoration: underline">Basic BBCode (photo and gallery descriptions)</span>
                <b>[b]bold[/b]</b><br />
                <b>[i]italics[/i]</b><br />
                <br />
                <b>[url=http://www.example.org]Example link[/url]</b><br />
                Will create a hyperlink with the text 'Example link' pointing to 'http://www.example.org'<br />
                
            </div>

            <!-- Modal footer -->
            <div class="modal-footer">
                <button type="button" class="btn btn-primary btn-std" data-bs-dismiss="modal">Close</button>
            </div>
            
        </div>
    </div>
</div>
`;
