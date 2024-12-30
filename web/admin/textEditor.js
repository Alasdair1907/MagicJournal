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

$.widget("admin.TextEditor", {

    _textEditorModal: `

<div class="modal" data-role="text-edit-modal">
    <div class="modal-dialog">
        <div class="modal-content">

            <!-- Modal Header -->
            <div class="modal-header">
                <span class="modal-h1">Text Editor</span>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>

            <!-- Modal body -->
            <div class="modal-body">
                <span data-role="char-count">0</span>/{{charLimit}}<br />
                <textarea rows="10" style="width:100%;resize:none;" data-role="editor-textarea">{{textToEdit}}</textarea>
            </div>


            <!-- Modal footer -->
            <div class="modal-footer">
                <button type="button" class="btn btn-success btn-std" data-role="save-edits">Save edits</button>
                <button type="button" class="btn btn-primary btn-std" data-bs-dismiss="modal">Cancel</button>
            </div>

        </div>
    </div>
</div>
`,

    _create: function(){

    },
    _init: function(){
        this._display(this, this.options);
    },

    _display: function(self, ops){

        let textToEdit = self.element.val();

        let modalDiv = document.createElement("div");
        modalDiv.setAttribute("display","none");
        let $modalDiv = $(modalDiv);
        let hModal = Handlebars.compile(self._textEditorModal);
        $modalDiv.html(hModal({textToEdit: textToEdit, charLimit: ops.charLimit}));

        let $modalDialog = $modalDiv.find('[data-role="text-edit-modal"]');
        $modalDialog.modal('show');

        let $textarea = $modalDialog.find('[data-role="editor-textarea"]');
        let $counter = $modalDialog.find('[data-role="char-count"]');

        $textarea.unbind();
        $textarea.bind('input propertychange', function() {
            let textLength = $textarea.val().length;
            let html = textLength;
            if (textLength > ops.charLimit){
                html = '<span style="color:red">'+textLength+'</span>';
            }
            $counter.html(html);
        });
        $textarea.trigger("propertychange");

        let $saveEdits = $modalDialog.find('[data-role="save-edits"]');
        $saveEdits.unbind();
        $saveEdits.click(function(){
            let textValue = $textarea.val();
            self.element.val(textValue);
            $modalDialog.modal('hide');
            self.element.trigger("editcomplete");
        });
    }

})